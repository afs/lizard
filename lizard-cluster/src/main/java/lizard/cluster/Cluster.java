/*
 *  Copyright 2013, 2014 Andy Seaborne
 *
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
 */

package lizard.cluster ;

import java.util.List ;
import java.util.concurrent.atomic.AtomicBoolean ;

import org.apache.curator.RetryPolicy ;
import org.apache.curator.framework.CuratorFramework ;
import org.apache.curator.framework.CuratorFrameworkFactory ;
import org.apache.curator.framework.api.CuratorListener ;
import org.apache.curator.framework.api.CuratorWatcher ;
import org.apache.curator.retry.ExponentialBackoffRetry ;
import org.apache.jena.atlas.lib.Lib ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.apache.zookeeper.CreateMode ;
import org.apache.zookeeper.Watcher.Event.EventType ;
import org.apache.zookeeper.data.Stat ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

/** Cluster management */
public class Cluster {
    static { LogCtl.setLog4j(); }
    private static Logger log = LoggerFactory.getLogger(Cluster.class) ;
    
    private static String namespace = "/lizard" ;
    public static String keyMembers = "/lizard/members" ;
    //private static String keyElements = "/lizard/members/active" ;
    
    private static byte[] zeroBytes = new byte[0] ;
    
    private CuratorFramework client = null ;
    private String self = null ;
    private AtomicBoolean active = new AtomicBoolean(false) ;
    
    private CuratorListener listener = (client, event) -> {
        switch (event.getType()) {
            case CHILDREN :
                break ;
            case CLOSING :
                break ;
            case CREATE :
                break ;
            case DELETE :
                break ;
            case EXISTS :
                break ;
            case GET_ACL :
                break ;
            case GET_DATA :
                break ;
            case SET_ACL :
                break ;
            case SET_DATA :
                break ;
            case SYNC :
                break ;
            case WATCHED :
                break ;
            default :
                break ;
        }            
        System.out.println("LISTEN: type = " + event.getType()) ;
        System.out.println("LISTEN: name = " + event.getName()) ;
        System.out.println("LISTEN: path = " + event.getPath()) ;
        // reload.
        if ( active.get() )
            watch(keyMembers) ;
    } ;

    public static void main(String... argv) throws Exception {
        String connect = "localhost:2181" ;
        log.info(connect) ;
        Cluster cluster1 = Cluster.createSystem(connect) ;
        log.info("add") ;
        cluster1.addMember("system") ;
        log.info("members") ;
        cluster1.members().forEach(System.out::println) ;
        
        cluster1.members().forEach( x -> {
            String y = keyMembers+"/"+x ;
            cluster1.watch(y) ;
        }) ;

//        Cluster cluster2 = Cluster.createSystem(connect) ;
//        cluster2.addMember() ;
//        cluster2.members().forEach(System.out::println) ;
        
        log.info("sleep") ;
        Lib.sleep(5000) ;
        log.info("close") ;
        cluster1.close() ;
    }

    public void close() {
        log.info("Close : "+self) ;
        active.set(false) ;
        delete(self) ;
        client.close() ;
        log.info("Closed") ;
    }
    
    public String addMember(String baseName) {
        return addMember(baseName, zeroBytes) ; 
    }
    
    /** Add this into the zookeep pool using the given string as base name.
     * Only the first registration causes a change.
     * Returns the actual ephemeral sequentional znode name.
     */  
    public String addMember(String baseName, byte[] data) {
        if ( baseName.startsWith("/") )
            throw new IllegalArgumentException("Base name starts with '/': " +baseName) ;
        String k = keyMembers+"/"+baseName ;
        if ( ! k.endsWith("-") )
            k = k + "-" ;
        if ( data == null )
            data = zeroBytes ;
        
        synchronized(this) {
            if ( self != null ) {
                log.info("Existing registration: "+self) ;
                return self ;
            }
            try {
                if ( ! baseName.startsWith("/") )
                    
                self = client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(k, data) ;
                log.info("Registered as: "+self) ;
            } catch (Exception e) {
                log.error("Failed: addMember("+k+")") ;
            }
        }
        return self ;
    }

    public List<String> members() {
        try {
            return client.getChildren().forPath(keyMembers) ;
            // Filter for key3?
        } catch (Exception e) {
            log.error("Failed: members("+keyMembers+")") ;
            return null ;
        }
    }
    
//    @SuppressWarnings("resource")
//    public static Cluster createLocal() {
//        TestingServer server ;
//        String connectString = null ;
//        
//        try {
//            server = new TestingServer() ;
//        } catch (Exception e) {
//            log.error("Failed: "+connectString, e) ;
//            return null ;
//        }
//    
//        return createSystem(server.getConnectString()) ;
//    }

    public static Cluster createSystem(String connectString) {
        try {
            return new Cluster(connectString) ; 
        } catch (Exception e) {
            log.error("Failed: "+connectString, e) ;
            return null ;
        }
    }

    private Cluster(String connectString) {
        try {
            RetryPolicy policy = new ExponentialBackoffRetry(1000, 3) ;
            client = CuratorFrameworkFactory.builder()/*.names    private void watch(String key) {
    
pace(namespace)*/.connectString(connectString).retryPolicy(policy).build() ;
            client.start() ;
            
            client.blockUntilConnected() ;
            client.getCuratorListenable().addListener(listener) ;
            
        }
        catch (Exception e) {
            log.error("Failed: "+connectString, e) ;
            client = null ;
        }
        ensure(namespace) ;
        ensure(keyMembers) ;
        watch(keyMembers) ;
        ///watch(key) ;
    }
    
    private CuratorWatcher w = (x) -> {
        if ( x.getType() == EventType.NodeChildrenChanged )
            System.out.println("WATCH: " + x) ;
    } ;
    
    
    
    private void watch(String key) {
        log.info("W: "+key) ;
        try {
            List<String> x = client.getChildren().watched().inBackground().forPath(key) ;
            log.info("Watch: "+x) ;
        }
        catch (Exception e) {
            log.error("Failed: watch("+key+")", e) ;
        } 
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
    
    private void delete(String key) {
        try {
            client.delete().forPath(key) ;
            log.info("Delete "+key) ;
        } catch (Exception e) {
            log.error("Failed: delete("+key+")") ;
        }
    }

    //    public static void init() throws Exception {
    //        // TestingServer server = new TestingServer();
    //
    ////        TestingServer server = new TestingServer() ;
    ////        server.start(); 
    //        try {
    //            //String connect = server.getConnectString() ;
    //            String connect = "localhost:2181" ;
    //            RetryPolicy policy = new ExponentialBackoffRetry(1000, 3) ;
    //            CuratorFramework client = CuratorFrameworkFactory.newClient(connect, policy) ;
    //            client.start() ;
    //            client.blockUntilConnected(); 
    //            System.out.println() ;
    //            
    //            list(client, key) ;
    //            
    //            //client.blockUntilConnected();
    //            byte[] data = Bytes.string2bytes("AFS:"+new Date()) ;
    //            client.create()
    //                .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
    //                .forPath(key2, data) ;
    //            
    //            System.out.println() ;
    //
    //            list(client, key) ;
    ////            
    ////            byte[] data2 = client
    ////                .getData()
    ////                .forPath(key) ;
    ////            String x = Bytes.bytes2string(data2) ;
    ////            System.out.println(":"+x+":") ;
    //            Lib.sleep(10*1000) ;
    //            System.exit(0) ;
    //
    //        }
    //        catch (Exception ex) {
    //            ex.printStackTrace(System.err) ;
    //        }
    //    }

//    public static void init() throws Exception {
//        // TestingServer server = new TestingServer();
//
////        TestingServer server = new TestingServer() ;
////        server.start(); 
//        try {
//            //String connect = server.getConnectString() ;
//            String connect = "localhost:2181" ;
//            RetryPolicy policy = new ExponentialBackoffRetry(1000, 3) ;
//            CuratorFramework client = CuratorFrameworkFactory.newClient(connect, policy) ;
//            client.start() ;
//            client.blockUntilConnected(); 
//            System.out.println() ;
//            
//            list(client, key) ;
//            
//            //client.blockUntilConnected();
//            byte[] data = Bytes.string2bytes("AFS:"+new Date()) ;
//            client.create()
//                .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
//                .forPath(key2, data) ;
//            
//            System.out.println() ;
//
//            list(client, key) ;
////            
////            byte[] data2 = client
////                .getData()
////                .forPath(key) ;
////            String x = Bytes.bytes2string(data2) ;
////            System.out.println(":"+x+":") ;
//            Lib.sleep(10*1000) ;
//            System.exit(0) ;
//
//        }
//        catch (Exception ex) {
//            ex.printStackTrace(System.err) ;
//        }
//    }


}
