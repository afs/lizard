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

import lizard.index.TServerIndex ;
import lizard.node.ClusterNodeTable ;
import lizard.node.TServerNode ;
import lizard.query.LizardQuery ;
import lizard.query.LzDataset ;
import lizard.system.Pingable ;
import org.apache.jena.atlas.lib.Lib ;
import org.apache.jena.atlas.lib.StrUtils ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.apache.jena.engine.explain.ExplainCategory ;
import org.apache.jena.riot.RDFDataMgr ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import com.hp.hpl.jena.query.* ;
import com.hp.hpl.jena.sparql.core.DatasetGraph ;
import com.hp.hpl.jena.sparql.util.QueryExecUtils ;
import com.hp.hpl.jena.tdb.TDB ;

/** Run lizartd dataset, external data servers */
public class MainLizard {
    
    static { LogCtl.setLog4j() ; }
    
    public static Logger log = LoggerFactory.getLogger("Lizard") ;
    
    static String prefixes = StrUtils.strjoinNL(
        "PREFIX :      <urn:lizard:>",
        "PREFIX list:  <http://jena.hpl.hp.com/ARQ/list#>" ,
        "PREFIX xsd:   <http://www.w3.org/2001/XMLSchema#>",
        "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>",
        "PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>",
        "") ;

    private static ExplainCategory lizardClient    = ExplainCategory.create("lizard-client") ;
    private static ExplainCategory lizardComms     = ExplainCategory.create("lizard-comms") ;
    private static ExplainCategory lizardCluster   = ExplainCategory.create("lizard-cluster") ; 

    private static void dev_init() {
        LizardQuery.init() ;
    }
    
    /** Run with in-JVM query engine */
    public static void main(String ...argv) {
        dev_init() ;
        //LogCtl.setWarn(Configuration.logConf.getName());

        String confNode          = filename(Setup.confDir, "conf-node.ttl") ;
        String confIndex         = filename(Setup.confDir, "conf-index.ttl") ;
        String confDataset       = filename(Setup.confDir, "conf-dataset.ttl") ;

        try { 
            runQuery(confDataset, confNode, confIndex) ;
        }
        catch (Throwable ex) {
            ex.printStackTrace(System.err) ;
            System.exit(0) ;
        }
        System.out.println("DONE") ;
        System.exit(0) ;
    }

    /** Run a query engine with data servers already running. */
    static void runQuery(String ... configFiles) {
        log.info("DATASET") ;
        LzDataset lz = Local.buildDataset(configFiles) ;
//        Component: lizard.node.NodeTableRemote
//        Component: lizard.index.TupleIndexRemote
//        Component: lizard.index.TupleIndexRemote
        lz.getComponents().stream().forEach(c -> {
            //System.out.println("Component: "+c.getClass().getTypeName()) ;
            if ( c instanceof Pingable ) {
                Pingable p = (Pingable)c ;
                p.ping();
            }
        }) ;
        
        DatasetGraph dsg = lz.getDataset() ; 
        Dataset ds = DatasetFactory.create(dsg) ;

        log.info("LOAD") ;
        //ds = (Dataset) AssemblerUtils.build(conffile, VocabLizard.lzDataset) ;
        if ( Setup.loadData ) {
            
            LogCtl.set(ClusterNodeTable.class, "WARN") ;
            LogCtl.set(TServerNode.class, "WARN") ;
            LogCtl.set(TServerIndex.class, "WARN") ;
            
            RDFDataMgr.read(ds, "D.ttl") ;
            
            LogCtl.set(ClusterNodeTable.class, "INFO") ;
            LogCtl.set(TServerNode.class, "INFO") ;
            LogCtl.set(TServerIndex.class, "INFO") ;
            
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
        int N = 1 ;// inProcess ? 1 : 20 ;
        for ( int i = 0 ; i < N ; i++ ) {
            doOne("Lizard", ds, q) ;
            if ( i != N-1 ) Lib.sleep(3000) ;
        }

        if ( true ) {
            Dataset dsStd = RDFDataMgr.loadDataset("D.ttl") ;
            doOne("ARQ", dsStd, q) ;
        }

    }

    private static void doOne(String label, Dataset ds, Query query) {
        QueryExecution qExec = QueryExecutionFactory.create(query, ds) ;
        log.info("---- {}", label) ;
        QueryExecUtils.executeQuery(query, qExec);
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
    
    static String filename(String dir, String fn) {
        if ( dir == null ) 
            return fn ;
        if ( ! dir.endsWith("/") )
            dir = dir + "/" ;
        return dir+fn ;
    }
}
