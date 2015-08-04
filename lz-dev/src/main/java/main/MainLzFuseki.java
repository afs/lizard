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

import lizard.build.LzDeploy ;
import lizard.conf.ConfCluster ;
import lizard.conf.parsers.LzConfParserRDF ;
import migrate.Q ;

import org.apache.jena.atlas.lib.FileOps ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.apache.jena.fuseki.cmd.FusekiCmd ;
import org.apache.jena.fuseki.server.FusekiEnv ;
import org.apache.jena.rdf.model.Model ;

public class MainLzFuseki {
    static { LogCtl.setLog4j() ; }
    
    // Run all inside a fuseki server.
    public static void main(String...argv) {
        String confFile = "setup-simple/fuseki.ttl" ;
        if ( argv.length == 1 )
            confFile = argv[0] ;
        if ( argv.length > 1 ) {
            System.err.println("Too many argument") ;
            System.exit(1) ;
        }
        
        Model configurationModel = Q.readAll(confFile) ;
        ConfCluster conf =  LzConfParserRDF.parseConfFile(configurationModel) ;
        LzDeploy.deployServers(conf, "vnode1") ;
        
        System.setProperty("FUSEKI_HOME", "/home/afs/Jena/jena-fuseki2/jena-fuseki-core/") ;
        FusekiEnv.FUSEKI_BASE = Paths.get("setup-simple/run").toAbsolutePath() ;
        FileOps.ensureDir(FusekiEnv.FUSEKI_BASE.toString()) ;
        FusekiCmd.main("--conf="+confFile) ;
    }
}