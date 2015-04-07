/**
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

package conf2.build;

import lizard.conf.Config ;
import org.apache.curator.test.TestingServer ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.zookeeper.server.ServerConfig ;
import org.apache.zookeeper.server.ZooKeeperServerMain ;
import org.seaborne.dboe.migrate.L ;
import org.slf4j.Logger ;
import conf2.conf.ConfCluster ;
import conf2.conf.ConfZookeeper ;
import conf2.conf.NetHost ;

public class Lz2BuildZk {
    private static Logger logConf = Config.logConf ;
    
    static TestingServer zkTestServer;
    
    public static String zookeeper(ConfCluster confCluster, NetHost here) {
        // @@
        if ( confCluster.zkServer.size() == 0 ) {}
        if ( confCluster.zkServer.size() > 1 ) {}
        ConfZookeeper confZookeeper = confCluster.zkServer.get(0) ;
        zookeeper(confZookeeper);
        return "localhost:"+confZookeeper.port  ;
    }

    public static void zookeeper(ConfZookeeper confZookeeper) {
        if ( confZookeeper.isEphemeral() ) {
            FmtLog.info(logConf, "Zookeeper (ephemeral): %d", confZookeeper.port) ;
            zookeeperSimple(confZookeeper.port) ;
            return ;
        }

        FmtLog.info(logConf, "Zookeeper %s : %d", confZookeeper.zkConfDir, confZookeeper.port) ;
        ServerConfig config = new ServerConfig();
        config.parse(new String[] {Integer.toString(confZookeeper.port), confZookeeper.zkConfDir}) ;
        ZooKeeperServerMain zk = new ZooKeeperServerMain();
        L.async(()-> {
            try { zk.runFromConfig(config) ; }
            catch (Exception e) { FmtLog.warn(logConf, "Failed to run zookeeper: "+e.getMessage(), e); }
        }) ;
    }
    
    public static void zookeeperSimple(int port) {
        try { zkTestServer = new TestingServer(port) ; }
        catch (Exception e) { e.printStackTrace(); }
    }



}

