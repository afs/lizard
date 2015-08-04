/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  See the NOTICE file distributed with this work for additional
 *  information regarding copyright ownership.
 */

package lizard.cluster ;

import static java.lang.String.format ;

import java.util.HashSet ;
import java.util.List ;
import java.util.Set ;
import java.util.concurrent.TimeUnit ;
import java.util.concurrent.atomic.AtomicBoolean ;

import lizard.system.LizardException ;

import org.apache.curator.RetryPolicy ;
import org.apache.curator.framework.CuratorFramework ;
import org.apache.curator.framework.CuratorFrameworkFactory ;
import org.apache.curator.framework.recipes.atomic.AtomicValue ;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong ;
import org.apache.curator.framework.recipes.locks.InterProcessLock ;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex ;
import org.apache.curator.retry.ExponentialBackoffRetry ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.zookeeper.CreateMode ;
import org.apache.zookeeper.data.Stat ;
import org.seaborne.dboe.transaction.txn.TxnIdGenerator ;
import org.seaborne.dboe.transaction.txn.TxnIdSimple ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
/** Cluster management.
 * One instance of this class per JVM.
 */
public class Cluster {
    /** Create this JVM cluster management instance */
    public static synchronized void createSystem(String connectString) {
        if ( instance != null )
            return ;

        try {
            instance = new Cluster$(connectString) ;
            log.info(format("Zookeeper %s", connectString)) ;
        } 
        catch (Exception e) {
            log.error("Failed: "+connectString, e) ;
        }
    }

    /** Start a monitor - this logs changes to the membership */  
    public static void monitor() { ClusterMonitor.start(); }

    /** Add member : the servce name is a path component (no "/")
     * identifiying the service type.  
     */
    public static String addMember(String serviceName) {
        checkActive() ;
        synchronized(instance) {
            String x = instance.addMember(serviceName) ;
            return x ; 
            // record registrations?
        }
    }

    /** Remove a member - pass in the string returned by {@link #addMember} */
    public static void removeMember(String serviceName) {
        checkActive() ;
        synchronized(instance) {
            instance.removeMember(serviceName) ;
        }
    }

    /** Return the current membership.
     * This contacts the zookeeper service.
     * See {@link ClusterMonitor} 
     */
    public static List<String> members() {
        checkActive() ;
        return instance.members() ;
    }
    
//    public static void watch(String path) {
//        checkActive() ;
//        instance.watch(path, new ClusterWatcher());
//    }

    private static void checkActive() {
        if ( instance == null )
            log.error("Not initialized") ;
    }

    public static synchronized void close() {
        if ( instance != null ) {
            instance.close() ;
        }
    }
    
    /** A cluster-wide unique value. */ 
    public static synchronized long uniqueNumber() {
        checkActive() ;
        // Allocate in blocks.
        return instance.uniqueNumber() ;
    }
    
    public static TxnIdGenerator getTxnIdGenerator() {
        return ()-> new TxnIdSimple(uniqueNumber()) ;
    }

    /** The underlying CuratorFramework */ 
    public static synchronized CuratorFramework getClient() {
        checkActive() ;
        return instance.client ;
    }
    
//    /** Watch a key's children */ 
//    public static void watch(CuratorWatcher watcher, String key) {
//        //log.info("Watch: "+key) ;
//        checkActive() ;
//        try {
//            List<String> children = instance.client.getChildren().usingWatcher(watcher).forPath(key) ;
//        }
//        catch (Exception e) {
//            log.error("Failed: watch("+key+")", e) ;
//        } 
//    }

    public static void acquireWriteLock() { instance.acquireWriteLock() ; }

    public static void releaseWriteLock() { instance.releaseWriteLock() ; }

    // ----------------------------------
    // Public interface
    private static Cluster$ instance = null ;

    private static Logger log = LoggerFactory.getLogger(Cluster.class) ;

    private static byte[] zeroBytes = new byte[0] ;

    static class Cluster$ {

        private CuratorFramework client = null ;
        private Set<String> registrations = new HashSet<>() ;
        private AtomicBoolean active = new AtomicBoolean(false) ;
        private DistributedAtomicLong globalCounter ;
        private InterProcessLock globalWriteLock ;
        
        private Cluster$(String connectString) {
            RetryPolicy policy = new ExponentialBackoffRetry(10000, 5) ;
            try {
                client = CuratorFrameworkFactory.builder()
                    /*.namespace(namespace)*/
                    .connectString(connectString)
                    .retryPolicy(policy)
                    .build() ;
                client.start() ;
        
                client.blockUntilConnected() ;
                
            }
            catch (Exception e) {
                log.error("Failed: "+connectString, e) ;
                client = null ;
            }
            ensure(ClusterCtl.namespace) ;
            ensure(ClusterCtl.members) ;
            active.set(true) ;
            globalCounter = new DistributedAtomicLong(client,"/COUNTER", policy) ;
            try {
                log.info("/COUNTER = "+globalCounter.get().postValue());
            } catch (Exception ex) {}
            globalWriteLock = new InterProcessSemaphoreMutex(client, "/WriteLock") ; 
        }
        
        private static int LOCK_ACQUIRE_TIMEOUT = 5000 ;    // Milliseconds.  

        public void acquireWriteLock() {
            boolean b = false ;
            try { b = globalWriteLock.acquire(LOCK_ACQUIRE_TIMEOUT, TimeUnit.MILLISECONDS) ; }
            catch (Exception ex) { throw new LizardException("globalWriteLock.acquire", ex) ; }
            if ( ! b ) {
                FmtLog.info(log, "Timeout getting write lock: (%d ms)", LOCK_ACQUIRE_TIMEOUT );
                throw new LizardException("Timeout getting write lock") ;
            }
        }

        public void releaseWriteLock() {
            try { globalWriteLock.release(); }
            catch (Exception ex) { throw new LizardException("globalWriteLock.acquire", ex) ; }
        }


        public String addMember(String baseName) {
            return addMember(baseName, zeroBytes) ; 
        }

        /** Add this into the zookeeper pool using the given string as base name.
         * Only the first registration causes a change.
         * Returns the actual ephemeral sequentional znode name.
         */  
        public String addMember(String baseName, byte[] data) {
            if ( baseName.startsWith("/") )
                throw new IllegalArgumentException("Base name starts with '/': " +baseName) ;
            String k = ClusterCtl.members+"/"+baseName ;
            if ( ! k.endsWith("-") )
                k = k + "-" ;
            if ( data == null )
                data = zeroBytes ;

            try {
                String x = client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(k, data) ;
                log.info("Registered as: "+x) ;
                registrations.add(x) ;
                return x ; 
            } catch (Exception e) {
                log.error("Failed: addMember("+k+")") ;
                return null ; 
            }
        }

        public List<String> members() {
            try {
                return client.getChildren().forPath(ClusterCtl.members) ;
                // Filter for key3?
            } catch (Exception e) {
                log.error("Failed: members("+ClusterCtl.members+")") ;
                return null ;
            }
        }
        
        public long uniqueNumber() {
            // XXX Blocks of numbers.
            try {
                AtomicValue<Long> along = globalCounter.increment() ;
                if ( ! along.succeeded() ) {
                    log.error("Failed: uniqueNumber") ;
                    throw new LizardException("Failed to allocate a unique number") ;
                }
                //FmtLog.info(log, "Unique: %d -> %d", along.preValue(), along.postValue()) ;
                return along.postValue() ;
            }
            catch (Exception e) {
                throw new LizardException("Exception allocating a unique number", e) ;
            }
        }

        public void close() {
            log.info("Close") ;
            active.set(false) ;
            registrations.forEach(x -> removeMember(x)) ;
            client.close() ;
            client = null ;
            log.info("Closed") ;
        }

        private void ensure(String key) {
            try {
                Stat stat = client.checkExists().forPath(key) ;
                if ( stat == null )
                    create(key, new byte[0]) ;
            }
            catch (Exception e) {
                log.error("Failed: ensure("+key+")", e) ;
            } 
        }

        private void create(String key, byte[] data) {
            // Ignore if present.
            try {
                String x = client.create().withMode(CreateMode.PERSISTENT).forPath(key, data) ;
                log.info(x) ;
            } catch (Exception e) {
                log.error("Failed: create("+key+")") ;
            }
        }

        private void removeMember(String key) {
            try {
                client.delete().forPath(key) ;
                registrations.remove(key) ;
                log.info("Delete "+key) ;
            } catch (Exception e) {
                log.error("Failed: delete("+key+")") ;
            }
        }
    }
}
