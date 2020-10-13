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

package lizard.build;

import lizard.conf.Config ;
import lizard.system.LizardException ;
import org.apache.curator.test.TestingServer ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.jena.dboe.migrate.L ;
import org.apache.zookeeper.server.ServerConfig ;
import org.apache.zookeeper.server.ZooKeeperServerMain ;
import org.slf4j.Logger ;

public class LzBuildZk {
    private static Logger logConf = Config.logConf ;
    
    static TestingServer zkTestServer;
    static String zkConnectString ;
    
    /** Run a full Zookeepr here */
    public static void zookeeper(int port, String zkConfDir) {
        FmtLog.info(logConf, "Start Zookeeper %s : %d", zkConfDir, port) ;
        ServerConfig config = new ServerConfig();
        config.parse(new String[] {Integer.toString(port), zkConfDir}) ;
        ZooKeeperServerMain zk = new ZooKeeperServerMain();
        L.async(()-> {
            try { zk.runFromConfig(config) ; }
            catch (Exception e) { FmtLog.warn(logConf, "Failed to run zookeeper: "+e.getMessage(), e); }
        }) ;
    }
    
    /** Run an ephemeral zookeeper */
    public static void zookeeperSimple(int port) {
        FmtLog.info(logConf, "Start Zookeeper (ephemeral): %d", port) ;
        try { zkTestServer = new TestingServer(port) ; }
        catch (Exception e) { throw new LizardException(e) ; }
    }
}

