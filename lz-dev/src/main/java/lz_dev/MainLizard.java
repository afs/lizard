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

public class MainLizard {
    
    static { 
        LogCtl.setLog4j() ;
    }
    public static Logger log = LoggerFactory.getLogger("Lizard") ;
    
    static String prefixes = StrUtils.strjoinNL(
        "PREFIX :      <urn:lizard:>",
        "PREFIX list:  <http://jena.hpl.hp.com/ARQ/list#>" ,
        "PREFIX xsd:   <http://www.w3.org/2001/XMLSchema#>",
        "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>",
        "PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>",
        "") ;

    static String confNode          = "conf-node.ttl" ;
    static String confIndex         = "conf-index.ttl" ;
    static String confDataset       = "conf-dataset.ttl" ;
    
    static boolean inProcess = true ;
    
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
    
    public static void main(String ...argv) {
        
        //LogCtl.setWarn(Configuration.logConf.getName());
        // Better debugging.

        ExplainCategory lizardClient    = ExplainCategory.create("lizard-client") ;
        ExplainCategory lizardComms     = ExplainCategory.create("lizard-comms") ;
        ExplainCategory lizardCluster   = ExplainCategory.create("lizard-cluster") ; 
        
        try {
            LizardQuery.init() ;
            if ( inProcess ) {
                LogCtl.set(TServer.class, "WARN") ;
                LogCtl.set(TServerIndex.class, "WARN") ;
            }

            Location location = Location.mem();
            if ( ! inProcess )
                location = Location.create("LZ") ;
            
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

            Platform platform = Local.buildServers(location, confNode, confIndex) ;
            platform.start() ;
            
            log.info("DATASET") ;
            DatasetGraph dsg = Local.buildDataset(confDataset, confNode, confIndex) ;
            Dataset ds = DatasetFactory.create(dsg) ;

            log.info("LOAD") ;
            //ds = (Dataset) AssemblerUtils.build(conffile, VocabLizard.lzDataset) ;
            if ( inProcess ) {
                LogCtl.set(ClusterNodeTable.class, "WARN") ;
                RDFDataMgr.read(ds, "D.ttl") ;
                LogCtl.set(ClusterNodeTable.class, "INFO") ;
                TDB.sync(ds) ;
            }
            
            //System.out.println(ds.getContext()) ;
            
            log.info("QUERY") ;
//            Quack.setVerbose(true) ;
//            ARQ.setExecutionLogging(InfoLevel.NONE);

            String qs = StrUtils.strjoinNL("PREFIX : <http://example/> SELECT * "
                                           , "{ :s1 ?p ?o }" 
                                           //, "{ ?x :k ?k . ?x :p ?v . }"
                                           //, "{ :x1 :p ?x . ?x :p ?v . }"
                                           // Filter placement occurs?
                                           //, "{ ?x :k ?k . ?x :p ?v . FILTER(?k = 2) }"
                                           //,"ORDER BY ?x"
                ) ;
            //"{ ?x :k ?k }" ;

            if ( true ) {
                LogCtl.set("lizard", "info") ;
//                LogCtl.disable("lizard.comms.common.tio") ;
//                LogCtl.disable("lizard.comms.server") ;
//                LogCtl.disable("lizard.comms.client") ;
            }
            
            Query q = QueryFactory.create(qs) ;
            System.out.println() ;
            System.out.print(q);
            System.out.println() ;
            int N = inProcess ? 1 : 20 ;
            for ( int i = 0 ; i < N ; i++ ) {
                doOne("Lizard", ds, q) ;
                if ( i != N-1 ) Lib.sleep(3000) ;
            }

            if ( true ) {
                Dataset dsStd = RDFDataMgr.loadDataset("D.ttl") ;
                doOne("ARQ", dsStd, q) ;
            }

        } catch (Exception ex ) {
            ex.printStackTrace(System.err) ;
            System.exit(0) ;
        }
        System.out.println("DONE") ;
        System.exit(0) ;
    }

    private static void doOne(String label, Dataset ds, Query query) {
        QueryExecution qExec = QueryExecutionFactory.create(query, ds) ;
        log.info("---- {}", label) ;
        QueryExecUtils.executeQuery(query, qExec);
    }
}
