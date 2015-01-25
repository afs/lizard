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

package lizard.cluster;

import java.util.HashSet ;
import java.util.List ;
import java.util.Set ;

import org.apache.curator.framework.api.CuratorWatcher ;
import org.apache.jena.atlas.lib.SetUtils ;
import org.apache.zookeeper.WatchedEvent ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

/** Utility to watch the cluster membership and log changes.
 * This class does <b>not</b> promise an up-to-date or consistent view of
 * the cluster.  A consistent snapshot can be obtained by
 * {@link Cluster#members} which directly contacts the
 * zookeeper service. 
 */ 
public class ClusterMonitor {
    
    private static Logger log = LoggerFactory.getLogger(ClusterMonitor.class) ;
    private static boolean watching = true ;

    /** Start monitoring */ 
    public static void start() {
        log.info("Watching ...") ;
        new Watcher().arm() ;
    }
    
    /** Stop monitoring (actually switches off at the next watch event) */ 
    public static void stop() {
        watching = false ;
    }

    static class Watcher implements CuratorWatcher {
        private Set<String> current = new HashSet<>() ; 

        private Watcher() {
            List<String> x = Cluster.members() ;
            current.addAll(x) ;
        }

        private void arm() {
            try {
                Cluster.getClient().getChildren().usingWatcher(this).forPath(ClusterCtl.members) ;
            } catch (Exception ex) {
                log.error("Failed to arm the cluster monitor", ex) ;
            }
        }
        
        @Override
        public void process(WatchedEvent event) throws Exception {
            if ( ! watching )
                // and don't rearm.
                return ;
            log.debug("WATCH: type = " + event.getType()) ;
            log.debug("WATCH: path = " + event.getPath()) ;
            // reload if not closing
            List<String> x = Cluster.members() ;

            Set<String> current2 = new HashSet<>() ;
            current2.addAll(x) ;

            Set<String> leavers = SetUtils.difference(current, current2) ;
            Set<String> arrivals = SetUtils.difference(current2, current) ;

            leavers.forEach(s ->  log.info("Leave:   " +s));
            arrivals.forEach(s -> log.info("Arrival: " +s));
            current = current2 ;
            arm() ;
        }
        
    }
}
