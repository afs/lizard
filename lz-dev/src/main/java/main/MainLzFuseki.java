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

import java.nio.file.Paths ;

import com.hp.hpl.jena.rdf.model.Model ;

import lizard.cluster.Cluster ;
import lizard.conf.Configuration ;
import lizard.sys.Deploy ;
import lizard.sys.Deployment ;
import lizard.system.LizardException ;
import migrate.Q ;
import org.apache.curator.test.TestingServer ;
import org.apache.jena.atlas.lib.FileOps ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.apache.jena.fuseki.cmd.FusekiCmd ;
import org.apache.jena.fuseki.server.FusekiEnv ;

public class MainLzFuseki {
    static { LogCtl.setLog4j() ; }
    
    public static void main(String...argv) {
        String confFile = "setup-simple/fuseki.ttl" ;
        if ( argv.length == 1 )
            confFile = argv[0] ;
        if ( argv.length > 1 ) {
            System.err.println("Too many argument") ;
            System.exit(1) ;
        }
        
        Model configurationModel = Q.readAll(confFile) ;
        Configuration config     = Configuration.fromModel(configurationModel) ;
        
        try { 
            Deployment deployment = Deploy.deployServers(config, confFile);
        } catch ( LizardException ex) {
            System.err.println(ex.getMessage());
            System.exit(0) ;
        }

        // Init a simple ZK
        int zkPort = 2281 ;
        TestingServer zkTestServer;
        try { zkTestServer = new TestingServer(zkPort) ; }
        catch (Exception e) { e.printStackTrace(); }
        
        String zkConnect = "localhost:"+zkPort ;
        Cluster.createSystem(zkConnect);
        
        // ----------------------
        
        System.setProperty("FUSEKI_HOME", "/home/afs/Jena/jena-fuseki2/jena-fuseki-core/") ;
        FusekiEnv.FUSEKI_BASE = Paths.get("setup-simple/run").toAbsolutePath() ;
        FileOps.ensureDir(FusekiEnv.FUSEKI_BASE.toString()) ;
        FusekiCmd.main("--conf="+confFile) ;
    }
}