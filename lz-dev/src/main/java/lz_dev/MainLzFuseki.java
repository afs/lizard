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

import org.apache.jena.atlas.logging.LogCtl ;
import org.apache.jena.fuseki.cmd.FusekiCmd ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

public class MainLzFuseki {
    static { LogCtl.setLog4j() ; }
    
    public static Logger log        = LoggerFactory.getLogger("Lizard") ;  
    public static Logger logConf    = LoggerFactory.getLogger("Conf") ;
    static String confNode          = "conf-node.ttl" ;
    static String confIndex         = "conf-index.ttl" ;
    static String confDataset       = "conf-dataset.ttl" ;
    
    public static void main(String...argv) {
        //String pwd = System.getProperty("user.dir") ;
        System.setProperty("FUSEKI_HOME", "/home/afs/Jena/jena-fuseki2") ;
        System.setProperty("FUSEKI_BASE", "run") ;
//        FusekiServer.FUSEKI_HOME = Paths.get("/home/afs/Jena/jena-fuseki2") ;
//        FusekiServer.FUSEKI_BASE = Paths.get("run").toAbsolutePath() ;
//        lizard.sys.Lizard.init() ;
        FusekiCmd.main("-v","--conf=fuseki-config.ttl") ;
    }
}