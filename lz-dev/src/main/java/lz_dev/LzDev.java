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

import lizard.conf.Configuration ;
import lizard.index.TServerIndex ;
import lizard.index.TupleIndexRemote ;
import lizard.node.ClusterNodeTable ;
import lizard.node.TServerNode ;
import lizard.query.LzDataset ;
import lizard.sys.Deploy ;
import lizard.sys.Deployment ;
import lizard.system.Pingable ;
import migrate.Q ;
import org.apache.jena.atlas.lib.Lib ;
import org.apache.jena.atlas.lib.StrUtils ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.apache.jena.riot.RDFDataMgr ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import com.hp.hpl.jena.query.* ;
import com.hp.hpl.jena.rdf.model.Model ;
import com.hp.hpl.jena.sparql.core.DatasetGraph ;
import com.hp.hpl.jena.sparql.util.QueryExecUtils ;


public class LzDev {
    static { LogCtl.setLog4j(); } 
    public static Logger log = LoggerFactory.getLogger("Main") ;

    static String confNode          = Q.filename(Setup.confDir, "conf-node.ttl") ;
    static String confIndex         = Q.filename(Setup.confDir, "conf-index.ttl") ;
    static String confDataset       = Q.filename(Setup.confDir, "conf-dataset.ttl") ;
    static Model configurationModel = Q.readAll(confNode, confIndex, confDataset) ;
    static Configuration config     = Configuration.fromModel(configurationModel) ;
    
    static String deploymentFile        = Q.filename(Setup.confDir, "deploy-jvm.ttl") ;
    
    public static void main(String[] args) {
        //Setup, take apart - try N way transactions.
    }
    
    public static void main1(String[] args) {
        log.info("SERVERS") ;
        Deployment deployment = Deploy.deployServers(config, deploymentFile);

        log.info("DATASET") ;
        LzDataset lz = buildDataset(config) ;
        Dataset ds = queryEngine(lz, "D.ttl") ;
        
        lz.getComponents().forEach(c->{
//            System.out.println(c) ;
//            System.out.println(c.getClass().getName()) ;
            
            //SOME TRANSACTION STUFF
            
            if ( c instanceof TupleIndexRemote ) {
                TupleIndexRemote rIdx = (TupleIndexRemote)c ;
                rIdx.begin(ReadWrite.READ) ;
            }
        });
        
        performQuery(ds); 
        
        log.info("** Done **") ;
        System.exit(0) ;
        if ( false ) {
            while(true) { Lib.sleep(10000) ; }
        }
    }

    // -------- Dataset
    private static LzDataset buildDataset(Configuration config) {
        LzDataset lz = Local.buildDataset(configurationModel) ;
        return lz ;
    }
    
    private static Dataset queryEngine(LzDataset lz, String data) {
        //  Component: lizard.node.NodeTableRemote
        //  Component: lizard.index.TupleIndexRemote
        //  Component: lizard.index.TupleIndexRemote
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
        if ( data != null ) {
            // Making loading quieter.
            LogCtl.set(ClusterNodeTable.class, "WARN") ;
            LogCtl.set(TServerNode.class, "WARN") ;
            LogCtl.set(TServerIndex.class, "WARN") ;

            RDFDataMgr.read(ds, data) ;

            LogCtl.set(ClusterNodeTable.class, "INFO") ;
            LogCtl.set(TServerNode.class, "INFO") ;
            LogCtl.set(TServerIndex.class, "INFO") ;
        }
        return ds ;
    }

    // -------- Query
    private static void performQuery(Dataset ds) {
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

}
