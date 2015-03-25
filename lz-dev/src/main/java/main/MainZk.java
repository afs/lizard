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

package main;

import java.util.List ;
import java.util.concurrent.atomic.AtomicLong ;

import lizard.cluster.Cluster ;

import org.apache.curator.RetryPolicy ;
import org.apache.curator.framework.CuratorFramework ;
import org.apache.curator.framework.CuratorFrameworkFactory ;
import org.apache.curator.framework.api.CuratorListener ;
import org.apache.curator.framework.recipes.atomic.AtomicValue ;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong ;
import org.apache.curator.framework.recipes.shared.SharedCount ;
import org.apache.curator.retry.ExponentialBackoffRetry ;
import org.apache.jena.atlas.lib.Lib ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.seaborne.dboe.transaction.txn.TxnIdFactory ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

public class MainZk {
    static { LogCtl.setLog4j(); }
    
    public static String keyMembers = "/lizard/members" ;
    private static Logger log = LoggerFactory.getLogger(MainZk.class) ;
    
    public static void main(String...argv) throws Exception {
        
        {
            long x1 = TxnIdFactory.create().runtime() ; 
            long x2 = TxnIdFactory.create().runtime() ;
            System.out.printf("0x%016x\n", x1) ;
            System.out.printf("0x%016x\n", x2) ;
            System.exit(0) ;
        }
        
        
        String connectString = "localhost:2181" ;
        run(connectString) ;
        System.exit(0) ;
//        Runnable r = () -> run(connectString) ;
//        new Thread(r).start() ;
        
        Cluster.createSystem(connectString) ;
        Cluster.members().forEach(System.out::println);
        String x2 = Cluster.addMember("MySelf") ;
        log.info("Self :: "+x2);
        //Lib.sleep(2000);
        String x1 = Cluster.addMember("me!") ;
        log.info("Self :: "+x1);
        Lib.sleep(2000);
        Cluster.members().forEach(System.out::println);
        Cluster.removeMember(x1);
        Cluster.members().forEach(System.out::println);
        Cluster.removeMember(x2);
        Cluster.members().forEach(System.out::println);
        Cluster.close();
        Lib.sleep(1000) ;
        System.out.println("DONE");
    }
    
    public static void run(String connectString) {
        CuratorFramework client = null ;
        RetryPolicy policy = new ExponentialBackoffRetry(10000, 3) ;
        try {
            client = CuratorFrameworkFactory.builder()
                /*.namespace(namespace)*/
                .connectString(connectString)
                .retryPolicy(policy)
                .build() ;
            client.start() ;
            
            client.blockUntilConnected() ;
            //client.getCuratorListenable().addListener(listener) ;
            
        }
        catch (Exception e) {
            log.error("Failed: "+connectString, e) ;
            client = null ;
        }
        
        //SharedCount sc = new SharedCount(client, "COUNTER", 0) ;
        DistributedAtomicLong x = new DistributedAtomicLong(client,"/COUNTER", policy) ;
        try {
            AtomicValue<Long> along = x.increment() ;
            if ( ! along.succeeded() )
                System.err.println("Did not succeed") ;
            System.out.println(along.postValue()) ;
            along = x.increment() ;
            if ( ! along.succeeded() )
                System.err.println("Did not succeed") ;
            System.out.println(along.postValue()) ;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        if ( true ) return ;
        
        log.info("sleep") ;
        Lib.sleep(10000) ;
        log.info("sleep") ;
        try {
            while(true) { Lib.sleep(1000) ; }
        } catch (Exception ex) {}
        client.close() ;
    }

    private static CuratorListener listener = (client, event) -> {
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
        
        log.info("LISTEN: kids = " + event.getChildren()) ;
        log.info("LISTEN: type = " + event.getType()) ;
        log.info("LISTEN: name = " + event.getName()) ;
        log.info("LISTEN: path = " + event.getPath()) ;
        log.info("LISTEN: rc   = " + event.getResultCode()) ;
        //log.info("LISTEN: stat = " + event.getStat()) ;
        log.info("LISTEN: watched = " + event.getWatchedEvent()) ;
        // reload.
        watch(client, keyMembers) ;
    } ;
    
    private static void watch(CuratorFramework client, String key) {
        log.info("W: "+key) ;
        try {
            //client.getZookeeperClient().getZooKeeper().exists(key, true, listener, null); 
            List<String> x = client.getChildren().watched().inBackground().forPath(key) ;
            log.info("Watch: "+x) ;
        }
        catch (Exception e) {
            log.error("Failed: watch("+key+")", e) ;
        } 
    }
}
