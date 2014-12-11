/*
 *  Copyright 2014 Andy Seaborne
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

package lz_dev;

import java.util.List ;

import lizard.cluster.Cluster ;
import org.apache.curator.RetryPolicy ;
import org.apache.curator.framework.CuratorFramework ;
import org.apache.curator.framework.CuratorFrameworkFactory ;
import org.apache.curator.framework.api.CuratorListener ;
import org.apache.curator.retry.ExponentialBackoffRetry ;
import org.apache.jena.atlas.lib.Lib ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

public class MainZkWatcher {
    static { LogCtl.setLog4j(); }
    
    public static String keyMembers = "/lizard/members" ;
    private static Logger log = LoggerFactory.getLogger(MainZkWatcher.class) ;
    
    public static void main(String...argv) throws Exception {
        String connectString = "localhost:2181" ;
        Cluster.createSystem(connectString) ;
        Cluster.monitor() ; 
        Lib.sleep(60000) ;
        Cluster.close();
        Lib.sleep(1000) ;
        System.out.println("DONE");
    }
    
    public static void run(String connectString) {
        CuratorFramework client = null ;
        try {
            RetryPolicy policy = new ExponentialBackoffRetry(10000, 3) ;
            client = CuratorFrameworkFactory.builder()
                /*.namespace(namespace)*/
                .connectString(connectString)
                .retryPolicy(policy)
                .build() ;
            client.start() ;
            
            client.blockUntilConnected() ;
            client.getCuratorListenable().addListener(listener) ;
            
        }
        catch (Exception e) {
            log.error("Failed: "+connectString, e) ;
            client = null ;
        }
        
        
        
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
