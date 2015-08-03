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

import java.nio.file.Paths ;

import lizard.build.LzDeploy ;
import lizard.cluster.Cluster ;
import lizard.conf.ConfCluster ;
import lizard.conf.NetHost ;
import lizard.conf.parsers.LzConfParserRDF ;
import lizard.index.ClusterTupleIndex ;
import lizard.index.TClientIndex ;
import lizard.index.TServerIndex ;
import lizard.node.ClusterNodeTable ;
import lizard.node.TClientNode ;
import lizard.node.TServerNode ;
import lizard.query.LzDataset ;
import lizard.system.LizardException ;
import lizard.system.RemoteControl ;
import migrate.Q ;

import org.apache.curator.test.TestingServer ;
import org.apache.jena.atlas.lib.FileOps ;
import org.apache.jena.atlas.lib.Lib ;
import org.apache.jena.atlas.lib.StrUtils ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.apache.jena.atlas.logging.ProgressLogger ;
import org.apache.jena.fuseki.cmd.FusekiCmd ;
import org.apache.jena.fuseki.server.FusekiEnv ;
import org.apache.jena.query.* ;
import org.apache.jena.rdf.model.Model ;
import org.apache.jena.riot.RDFDataMgr ;
import org.apache.jena.riot.system.StreamRDF ;
import org.apache.jena.sparql.util.QueryExecUtils ;
import org.seaborne.dboe.sys.Names ;
import org.seaborne.tdb2.lib.TDBTxn ;
import org.seaborne.tdb2.loader.StreamRDFBatchSplit ;
import org.seaborne.tdb2.loader.StreamRDFMonitor ;
import org.seaborne.tdb2.store.DatasetGraphTDB ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

public class LzDev {
    static { LogCtl.setLog4j(); } 
    public static Logger log = LoggerFactory.getLogger("Main") ;

    static String confDir           = "setup1" ;
    static String confNode          = Q.filename(confDir, "conf-node.ttl") ;
    static String confIndex         = Q.filename(confDir, "conf-index.ttl") ;
    static String confDataset       = Q.filename(confDir, "conf-dataset.ttl") ;
    static String confLayout        = Q.filename(confDir, "conf-layout.ttl") ;
    static Model configurationModel = Q.readAll(confNode, confIndex, confDataset, confLayout) ;
    static ConfCluster config  ;
    // then "config.fileroot = Names.memName" for in-memory testing.
    
    public static void main(String[] args) {
        try { main$(args) ; }
        catch (Exception ex) { 
            System.out.flush() ;
            System.err.println(ex.getMessage()) ;
            ex.printStackTrace(System.err);
            System.exit(0) ;
        }
    }

    public static void main$(String[] args) {
        
        config = LzConfParserRDF.parseConfFile(configurationModel) ;
        
        FileOps.clearAll("DB");

        log.info("SERVERS") ;

        String FILE = "/home/afs/Datasets/BSBM/bsbm-250k.nt.gz" ;
        config.fileroot = Names.memName ;
        
//        FILE = "/home/afs/Datasets/BSBM/bsbm-5m.nt.gz" ;
//        config.fileroot = "DB" ;
        
        if ( ! config.fileroot.startsWith(Names.memName) ) {
            FileOps.ensureDir(config.fileroot); 
            FileOps.clearAll(config.fileroot) ;
        }

        // The deployment "here".
        NetHost here = NetHost.create("vnode1") ;
        
        try { 
            LzDeploy.deployServers(config, here);
        } catch ( LizardException ex) {
            System.err.println(ex.getMessage());
            System.exit(0) ;
        }

        // Multiple query servers?
        log.info("DATASET") ;
        
        //config.print(IndentedWriter.stdout);
        
        Dataset ds = LzDeploy.deployDataset(config, here) ;
        DatasetGraphTDB dsg = (DatasetGraphTDB)(ds.asDatasetGraph()) ;
        load(ds,FILE);
        
//        ds.begin(ReadWrite.WRITE);
//        RDFDataMgr.read(ds, "D.ttl") ;
//        ds.commit() ;
//        ds.end() ;
        
        //L.async(()->load(ds,"/home/afs/Datasets/BSBM/bsbm-5m.nt.gz")) ;
        //Lib.sleep(1000);

        log.info("QUERY") ;
        ds.begin(ReadWrite.READ) ;
        performQuery(ds);
        //txn.prepare(); 
        ds.end() ;
        
        LogCtl.set("lizard", "info");
        LogCtl.set("org.seaborne", "info");
        
        Cluster.close();
        System.exit(0) ;
    }
    
    static int unique = 0 ;
    private static void ping(LzDataset lz) {
        lz.getComponents().stream().sequential().forEach(c -> {
            //System.out.println("Component: "+c.getClass().getTypeName()) ;
            if ( c instanceof RemoteControl ) {
                RemoteControl p = (RemoteControl)c ;
                p.ping();
            }
        }) ;
    }
    
    private static void load(Dataset ds, String datafile) {        
        log.info("LOAD : "+datafile) ;
        if ( datafile != null ) {
            // Making loading quieter.
            LogCtl.set(ClusterNodeTable.class, "WARN") ;
            LogCtl.set(TClientNode.class, "WARN") ;
            LogCtl.set("lizard.node.THandlerNodeTable", "WARN") ;
            
            LogCtl.set(ClusterTupleIndex.class, "WARN") ;
            LogCtl.set(TClientIndex.class, "WARN") ;
            LogCtl.set("lizard.index.THandlerTupleIndex", "WARN") ; 
            
            //Loader.bulkLoad(ds, datafile) ;
            bulkLoad(ds, datafile) ;

//            DatasetGraphTDB dsg = (DatasetGraphTDB)(ds.asDatasetGraph()) ;
//            ProgressLogger plog = new ProgressLogger(LoggerFactory.getLogger("LOAD"), "Triples", 100000, 10) ;
//            
//            StreamRDF s0 = StreamRDFLib.dataset(ds.asDatasetGraph()) ;
//            StreamRDF s1 = new StreamRDFBatchSplit(dsg, 100) ;
//            StreamRDFMonitor s2 = new StreamRDFMonitor(s1, plog) ;
//            
//            StreamRDF s3 = s2 ;
//            
//            s2.startMonitor();
//            
//            TDBTxn.executeWrite(ds, () -> {
//                RDFDataMgr.parse(s3, datafile) ;
//            }) ;
//            s2.finishMonitor();
            
            LogCtl.set(ClusterNodeTable.class, "INFO") ;
            LogCtl.set(TServerNode.class, "INFO") ;
            LogCtl.set(TServerIndex.class, "INFO") ;
        }
        log.info("LOAD : finish") ;
    }

    private static Logger LOG = LoggerFactory.getLogger("LzLoader") ;
    
    public static void bulkLoad(Dataset ds, String ... files) {
        // c.f. TDB2 Loader.bulkLoad (which does not currently batch split).
        DatasetGraphTDB dsg = (DatasetGraphTDB)ds.asDatasetGraph() ;
        StreamRDF s1 = new StreamRDFBatchSplit(dsg, 100) ;
        
        ProgressLogger plog = new ProgressLogger(log, "Triples", 100000, 10) ;
        StreamRDFMonitor sMonitor = new StreamRDFMonitor(s1, plog) ;
        StreamRDF s3 = sMonitor ;

        sMonitor.startMonitor(); 
        TDBTxn.executeWrite(ds, () -> {
            for ( String fn : files ) {
                if ( files.length > 1 )
                    FmtLog.info(LOG, "File: %s",fn);
                RDFDataMgr.parse(s3, fn) ;
            }
        }) ;
        sMonitor.finishMonitor();  
    }
    
    // -------- Query
    private static void performQuery(Dataset ds) {
        //            Quack.setVerbose(true) ;
        //            ARQ.setExecutionLogging(InfoLevel.NONE);

        String x = "<http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/ProductType15>" ;
        String qs1 = "SELECT * { VALUES ?s {"+x+"} ?s ?p ?o }" ;
        
        String qs2 = StrUtils.strjoinNL("PREFIX : <http://example/>"
                                      ,"SELECT (count(*) AS ?C)  "
                                      ,"{ ?s ?p ?o }" ) ;
        String qs = qs1 ;
        Query q = QueryFactory.create(qs) ;
        performQuery(ds, q);
    }
    
    private static void performQuery(Dataset ds, Query q) {
        System.out.println() ;
        System.out.print(q);
        System.out.println() ;
        int N = 1 ;// inProcess ? 1 : 20 ;
        for ( int i = 0 ; i < N ; i++ ) {
            doOne("Lizard", ds, q) ;
            if ( i != N-1 ) Lib.sleep(3000) ;
        }
    }

    private static void doOne(String label, Dataset ds, Query query) {
        QueryExecution qExec = QueryExecutionFactory.create(query, ds) ;
        log.info("---- {}", label) ;
        QueryExecUtils.executeQuery(query, qExec);
    }
    
    static TestingServer zkTestServer;
    public static void zookeeper(int port) {
        try { zkTestServer = new TestingServer(port) ; }
        catch (Exception e) { e.printStackTrace(); }
    }

    public static void mainFuseki(String[] args) {
        System.setProperty("FUSEKI_HOME", "/home/afs/Jena/jena-fuseki2/jena-fuseki-core/") ;
        FusekiEnv.FUSEKI_BASE = Paths.get("setup-simple/run").toAbsolutePath() ;
        FileOps.ensureDir(FusekiEnv.FUSEKI_BASE.toString()) ;
        FusekiCmd.main("--conf=setup-simple/fuseki.ttl") ;
        System.exit(0) ;
    }
}
