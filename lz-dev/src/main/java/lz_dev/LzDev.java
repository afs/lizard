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

package lz_dev;

import lizard.conf.ConfCluster ;
import lizard.conf.parsers.LzConfParserRDF ;
import lizard.deploy.Deploy ;
import migrate.Q ;

import org.apache.jena.atlas.lib.FileOps ;
import org.apache.jena.atlas.lib.Lib ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.apache.jena.query.Dataset ;
import org.apache.jena.query.ReadWrite ;
import org.apache.jena.rdf.model.Model ;
import org.seaborne.dboe.sys.Names ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

public class LzDev {
    static { LogCtl.setLog4j(); } 
    public static Logger log = LoggerFactory.getLogger("Main") ;

    //static String confDir           = "setup1" ;
    static String confDir           = "setup2" ;
    static String confNode          = Q.filename(confDir, "conf-node.ttl") ;
    static String confIndex         = Q.filename(confDir, "conf-index.ttl") ;
    static String confDataset       = Q.filename(confDir, "conf-dataset.ttl") ;
    static String confLayout        = Q.filename(confDir, "conf-layout.ttl") ;
    static Model configurationModel = Q.readAll(confNode, confIndex, confDataset, confLayout) ;
    static ConfCluster config  ;
    // then "config.fileroot = Names.memName" for in-memory testing.
    
    public static void main(String[] args) {
        try { 
            config = LzConfParserRDF.parseConfFile(configurationModel) ;
            
            String FILE = "/home/afs/Datasets/BSBM/bsbm-250k.nt.gz" ;
            config.fileroot = Names.memName ;
            
//            FILE = "/home/afs/Datasets/BSBM/bsbm-5m.nt.gz" ;
//            FileOps.clearAll("DB");
//            config.fileroot = "DB" ;
            
            if ( ! config.fileroot.startsWith(Names.memName) ) {
                FileOps.ensureDir(config.fileroot); 
                FileOps.clearAll(config.fileroot) ;
            }
            
            String here = "vnode1" ; 
            
            Deploy.deployZookeer(2186);
            Lib.sleep(100) ;
            Deploy.deployServers(config, "vnode1") ;
            Deploy.deployServers(config, "vnode2") ;
            
            Dataset ds = Deploy.deployDataset(config, here) ;
            //LzDataset lzds = LzDeploy.deployLzDataset(config, here);
            //Dataset ds = LzBuilderDataset.dataset(lzds) ;
            
            if ( false ) {
                Deploy.runFuseki(ds.asDatasetGraph(), 3030);
                System.exit(0) ;
            }
            
            // Alternatively ....
            Deploy.load(ds,FILE);
            ds.begin(ReadWrite.READ);
            Deploy.performQuery(ds);
            ds.end() ;
            System.exit(0) ;
        
        }
        catch (Exception ex) { 
            System.out.flush() ;
            System.err.println(ex.getMessage()) ;
            ex.printStackTrace(System.err);
            System.exit(0) ;
        }
    }
}
