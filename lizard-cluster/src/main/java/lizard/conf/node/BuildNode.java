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

package lizard.conf.node;

import lizard.cluster.Platform ;
import lizard.conf.Config ;
import lizard.conf.LzBuild ;
import lizard.node.TServerNode ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.slf4j.Logger ;

import com.hp.hpl.jena.tdb.base.file.Location ;
import com.hp.hpl.jena.tdb.store.nodetable.NodeTable ;

public class BuildNode {
    static Logger logConf = Config.logConf ;
    
    public static void setupNodeServers(String conffile) {
        Runnable r = confServers(conffile) ;
        r.run() ;
        //Lib.sleep(1000) ;
    }
    
    public static Runnable confServers(String conffile) {
        ConfigNode conf = ConfigNode.create(conffile) ;
        return confServers(conf) ;
    }
        
    public static Runnable confServers(ConfigNode conf) {
        Platform platform = new Platform();
        ConfigNode.buildServers(conf, platform, location());
        return ()->platform.start() ;
    }

    private static int count = 0 ;

    private static Location location() {
        return Location.mem() ;
//        Location loc = new Location("tmp/"+(count)) ;
//        FileOps.ensureDir(loc.getDirectoryPath()) ;
//        count ++ ;
//        return loc ;
    }
    
    // ---- Deployment
    
    /** Build locally */
    public static void buildNodeServer(NodeServer ns, Platform platform, Location location) {
        Location loc = location.getSubLocation(ns.name) ;
        TServerNode srv = buildNodeServer(ns, loc) ;
        platform.add(srv) ;
    }
    
    /** Build directly, locally */
    private static TServerNode buildNodeServer(NodeServer ds, Location loc) {
        FmtLog.info(logConf, "buildNodeServer: %s %s", ds.port, loc) ;
        NodeTable nt = LzBuild.createNodeTable(loc) ;
        TServerNode serverNode = TServerNode.create(ds.port, nt) ;
        return serverNode ; 
    }
}
