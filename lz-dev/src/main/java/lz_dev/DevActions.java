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

import lizard.build.LzBuildZk ;
import lizard.build.LzDeploy ;
import lizard.index.ClusterTupleIndex ;
import lizard.index.TClientIndex ;
import lizard.index.TServerIndex ;
import lizard.node.ClusterNodeTable ;
import lizard.node.TClientNode ;
import lizard.node.TServerNode ;
import lizard.query.LzDataset ;
import lizard.system.LizardException ;
import lizard.system.RemoteControl ;

import org.apache.jena.atlas.lib.FileOps ;
import org.apache.jena.atlas.lib.Lib ;
import org.apache.jena.atlas.lib.StrUtils ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.apache.jena.atlas.logging.ProgressLogger ;
import org.apache.jena.fuseki.Fuseki ;
import org.apache.jena.fuseki.cmd.FusekiCmd ;
import org.apache.jena.fuseki.jetty.JettyFuseki ;
import org.apache.jena.fuseki.jetty.JettyServerConfig ;
import org.apache.jena.fuseki.server.FusekiEnv ;
import org.apache.jena.fuseki.server.FusekiServerListener ;
import org.apache.jena.fuseki.server.ServerInitialConfig ;
import org.apache.jena.query.* ;
import org.apache.jena.riot.RDFDataMgr ;
import org.apache.jena.riot.system.StreamRDF ;
import org.apache.jena.sparql.core.DatasetGraph ;
import org.apache.jena.sparql.util.QueryExecUtils ;
import org.seaborne.tdb2.lib.TDBTxn ;
import org.seaborne.tdb2.loader.StreamRDFBatchSplit ;
import org.seaborne.tdb2.loader.StreamRDFMonitor ;
import org.seaborne.tdb2.store.DatasetGraphTDB ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

public class DevActions {

    static Logger LOG = LoggerFactory.getLogger("LzLoader") ;

    static Dataset deployHere(String string) {
        // --- The deployment "here".
        LzDev.log.info("SERVERS") ;
        LzBuildZk.zookeeperSimple(2186);
    
        String here = "vnode1" ;
        try { 
            LzDeploy.deployServers(LzDev.config, here);
        } catch ( LizardException ex) {
            System.err.println(ex.getMessage());
            System.exit(0) ;
        }
    
        // Multiple query servers?
        LzDev.log.info("DATASET") ;
        
        //config.print(IndentedWriter.stdout);
        
        Dataset ds = LzDeploy.deployDataset(LzDev.config, here) ;
        return ds ;
    }

    static void runFuseki(DatasetGraph dsg, int port) {
        // -- Run fuseki
        //FusekiServer.
        ServerInitialConfig fuConf = new ServerInitialConfig() ;
        fuConf.dsg = dsg ;
        fuConf.datasetPath = "/ds" ;
        fuConf.allowUpdate = true ;
        
        FusekiServerListener.initialSetup = fuConf ;
        System.setProperty("FUSEKI_HOME", "/home/afs/Jena/jena-fuseki2/jena-fuseki-core/") ;
        FusekiEnv.FUSEKI_BASE = Paths.get("run").toAbsolutePath() ;
        FileOps.ensureDir(FusekiEnv.FUSEKI_BASE.toString()) ;
    
        JettyServerConfig   jettyServerConfig = new JettyServerConfig() ;
        jettyServerConfig.port = 3030 ;
        jettyServerConfig.contextPath = "/" ;
        jettyServerConfig.jettyConfigFile = null ;
        jettyServerConfig.pages = Fuseki.PagesStatic ;
        jettyServerConfig.enableCompression = true ;
        jettyServerConfig.verboseLogging = false ;
    
        JettyFuseki.initializeServer(jettyServerConfig) ;
        JettyFuseki.instance.start() ;
        JettyFuseki.instance.join() ;
        System.exit(0) ;
    }

    private static void ping(LzDataset lz) {
        lz.getComponents().stream().sequential().forEach(c -> {
            //System.out.println("Component: "+c.getClass().getTypeName()) ;
            if ( c instanceof RemoteControl ) {
                RemoteControl p = (RemoteControl)c ;
                p.ping();
            }
        }) ;
    }

    static void load(Dataset ds, String datafile) {        
            LzDev.log.info("LOAD : "+datafile) ;
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
            LzDev.log.info("LOAD : finish") ;
        }

    public static void bulkLoad(Dataset ds, String ... files) {
        // c.f. TDB2 Loader.bulkLoad (which does not currently batch split).
        DatasetGraphTDB dsg = (DatasetGraphTDB)ds.asDatasetGraph() ;
        StreamRDF s1 = new StreamRDFBatchSplit(dsg, 100) ;
        
        ProgressLogger plog = new ProgressLogger(LzDev.log, "Triples", 100000, 10) ;
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
    static void performQuery(Dataset ds) {
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

    static void doOne(String label, Dataset ds, Query query) {
        ds.begin(ReadWrite.READ);
        try (QueryExecution qExec = QueryExecutionFactory.create(query, ds) ) {
            LzDev.log.info("---- {}", label) ;
            QueryExecUtils.executeQuery(query, qExec);
        }
        ds.end() ;
    }

    public static void mainFuseki(String[] args) {
        System.setProperty("FUSEKI_HOME", "/home/afs/Jena/jena-fuseki2/jena-fuseki-core/") ;
        FusekiEnv.FUSEKI_BASE = Paths.get("setup-simple/run").toAbsolutePath() ;
        FileOps.ensureDir(FusekiEnv.FUSEKI_BASE.toString()) ;
        FusekiCmd.main("--conf=setup-simple/fuseki.ttl") ;
        System.exit(0) ;
    }

}

