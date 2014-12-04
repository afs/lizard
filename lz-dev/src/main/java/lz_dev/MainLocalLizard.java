/*
 *  Copyright 2013, 2014 Andy Seaborne
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

package lz_dev ;

import java.io.File ;

import lizard.cluster.Platform ;
import lizard.index.TServerIndex ;
import lizard.node.ClusterNodeTable ;
import lizard.query.LizardQuery ;
import lizard.query.LzDataset ;
import lizard.system.Pingable ;
import org.apache.jena.atlas.lib.FileOps ;
import org.apache.jena.atlas.lib.Lib ;
import org.apache.jena.atlas.lib.StrUtils ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.apache.jena.engine.explain.ExplainCategory ;
import org.apache.jena.riot.RDFDataMgr ;
import org.apache.thrift.server.TServer ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import com.hp.hpl.jena.query.* ;
import com.hp.hpl.jena.sparql.core.DatasetGraph ;
import com.hp.hpl.jena.sparql.util.QueryExecUtils ;
import com.hp.hpl.jena.tdb.TDB ;
import com.hp.hpl.jena.tdb.base.file.Location ;

public class MainLocalLizard {
    
    static { LogCtl.setLog4j() ; }
    
    public static Logger log = LoggerFactory.getLogger("Lizard") ;
    
    static String prefixes = StrUtils.strjoinNL(
        "PREFIX :      <urn:lizard:>",
        "PREFIX list:  <http://jena.hpl.hp.com/ARQ/list#>" ,
        "PREFIX xsd:   <http://www.w3.org/2001/XMLSchema#>",
        "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>",
        "PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>",
        "") ;

    private static void dev_init() {
        LizardQuery.init() ;
    }
    
    /** Run with in-JVM data servers and query engine */
    public static void main(String ...argv) {
        dev_init() ;
        //LogCtl.setWarn(Configuration.logConf.getName());
        String confNode  = MainLizard.filename(Setup.confDir, "conf-node.ttl") ;
        String confIndex = MainLizard.filename(Setup.confDir, "conf-index.ttl") ;
        

        LogCtl.set(TServer.class, "WARN") ;
        LogCtl.set(TServerIndex.class, "WARN") ;
        setupDataservers(confNode, confIndex) ;
        
        LogCtl.set(TServer.class, "INFO") ;
        LogCtl.set(TServerIndex.class, "INFO") ;

        try { 
            String confDataset = MainLizard.filename(Setup.confDir, "conf-dataset.ttl") ;
            MainLizard.runQuery(confNode, confIndex, confDataset) ;
        }
        catch (Throwable ex) {
            ex.printStackTrace(System.err) ;
            System.exit(0) ;
        }
        System.out.println("DONE") ;
        System.exit(0) ;
    }

    /** Run dataservers in-JVM with a memory storage location */
    private static void setupDataservers(String ... confFiles) {
        try {
            Location location = Location.mem();

            if ( ! location.isMem() ) {
                String dir = location.getDirectoryPath() ;
                if ( location.exists() ) {
                    // FileOps.clearDirectory(dir) ;
                    File d = new File(dir) ;
                    clearAll(d) ;
                }

                else
                    FileOps.ensureDir(dir) ;
            }

            log.info("SERVERS") ;

            Platform platform = Local.buildServers(location, confFiles) ;
            platform.start() ;
        } catch (Exception ex ) {
            ex.printStackTrace(System.err) ;
            System.exit(0) ;
        }
    }

    /** Recursive "rm -r" */
    // XXX Move to lizard-base:migrate */
    private static void clearAll(File d) {
        for ( File f : d.listFiles())
        {
            if ( ".".equals(f.getName()) || "..".equals(f.getName()) )
                continue ;
            if ( f.isDirectory() )
                clearAll(f) ;
            f.delete() ;
        }
    }
}
