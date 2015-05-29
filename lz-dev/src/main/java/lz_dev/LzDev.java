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
import java.util.ArrayList ;
import java.util.List ;

import lizard.build.LzBuilderNodeServer ;
import lizard.build.LzDeploy ;
import lizard.cluster.Cluster ;
import lizard.cluster.Platform ;
import lizard.conf.* ;
import lizard.conf.parsers.LzConfParserRDF ;
import lizard.index.TServerIndex ;
import lizard.node.ClusterNodeTable ;
import lizard.node.NodeTableRemote ;
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
import org.apache.jena.atlas.lib.Timer ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.apache.jena.atlas.logging.ProgressLogger ;
import org.apache.jena.fuseki.cmd.FusekiCmd ;
import org.apache.jena.fuseki.server.FusekiEnv ;
import org.apache.jena.graph.Node ;
import org.apache.jena.graph.NodeFactory ;
import org.apache.jena.query.* ;
import org.apache.jena.rdf.model.Model ;
import org.apache.jena.riot.RDFDataMgr ;
import org.apache.jena.riot.system.StreamRDF ;
import org.apache.jena.riot.system.StreamRDFLib ;
import org.apache.jena.sparql.sse.SSE ;
import org.apache.jena.sparql.util.QueryExecUtils ;
import org.seaborne.dboe.base.file.Location ;
import org.seaborne.tdb2.lib.TDBTxn ;
import org.seaborne.tdb2.setup.StoreParams ;
import org.seaborne.tdb2.store.DatasetGraphTDB ;
import org.seaborne.tdb2.store.NodeId ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

public class LzDev {
    static { LogCtl.setLog4j(); } 
    public static Logger log = LoggerFactory.getLogger("Main") ;

    //static String confDir           = "setup-disk" ;
    static String confDir           = "setup-simple" ;
    static String confNode          = Q.filename(confDir, "conf-node.ttl") ;
    static String confIndex         = Q.filename(confDir, "conf-index.ttl") ;
    static String confDataset       = Q.filename(confDir, "conf-dataset.ttl") ;
    static Model configurationModel = Q.readAll(confNode, confIndex, confDataset) ;
    static ConfCluster config       = LzConfParserRDF.parseConfFile(configurationModel) ; 
    
    static NodeTableRemote ntr = null ;
    //static TupleIndexRemote tir = null ;
    static int counter = 0 ;
    
    public static void main(String[] args) {
        mainBatching() ;
        
        try { main$(args) ; }
        catch (Exception ex) { 
            System.out.flush() ;
            System.err.println(ex.getMessage()) ;
            ex.printStackTrace(System.err);
            System.exit(0) ;
        }
    }

    public static void mainBatching() {
        int port = 9090 ;
        {
            Platform platform = new Platform() ;
            Location location = Location.create("DB-N") ;
            FileOps.ensureDir("N") ;
            FileOps.clearAll("N");

            StoreParams params = StoreParams.getDftStoreParams() ;
            NetAddr here = NetAddr.create("localhost", port) ;
            ConfNodeTable nTableDesc = new ConfNodeTable(1, 1) ;
            ConfNodeTableElement x = new ConfNodeTableElement("Nodes", "node", nTableDesc, here) ;
            TServerNode nodeServer = LzBuilderNodeServer.buildNodeServer(platform, location, params, x) ;
            nodeServer.start(); 
        }
        
        TClientNode client = TClientNode.create("localhost", port) ;
        client.start() ;

//        int BatchSize = 100 ;
//        int Repeats = 10000 ;
        
        // Gains exists but drop up to batch size of 1000.
        
        int BatchSize = 10 ;
        int Repeats = 10000 ;
        
//        for ( int i = 0 ; i < 5 ; i++ ) {
//                System.out.println("-----") ;
//            time("Batched ",  BatchSize, Repeats,    ()->batched(client, BatchSize, Repeats)) ;
//            time("Batched ",  1, Repeats*BatchSize,  ()->batched(client, 1, Repeats*BatchSize)) ;
//            time("Single  ",  BatchSize, Repeats ,   ()->single(client, BatchSize, Repeats)) ;
//        }
        
        // Heap attack.
        System.out.println("---- Warm up") ;
        doOneTimedRun(client, 10, 50000) ;
        System.out.println("---- Live") ;
        doOneTimedRun(client, 1000, 500) ;
        doOneTimedRun(client, 10, 50000) ;
        doOneTimedRun(client, 100, 5000) ;
        doOneTimedRun(client, 1000, 500) ;
        //doOneTimedRun(client, 1, 500000) ;
        doOneTimedRun(client, 100, 5000) ;
        //doOneTimedRun(client, 1, 500000) ;
        
        System.exit(0) ;
    }
    
    public static void doOneTimedRun(TClientNode client, int BatchSize, int Repeats) {
        time("",  BatchSize, Repeats,    ()->batched(client, BatchSize, Repeats)) ;
    }
    
    private static void batched(TClientNode client, int batchSize, int repeats) {
        // Multiple/batched.
        unique++ ;
        client.begin(999, ReadWrite.WRITE) ;
        List<Node> nodes = new ArrayList<>(batchSize) ;
        for ( int i = 0 ; i < repeats ; i++ ) {
            nodes.clear() ;
            for ( int j = 0 ; j < batchSize ; j++ ) {
                Node n = NodeFactory.createURI("http://example/n-"+unique+"-"+i+"-"+j) ;
                nodes.add(n) ;
            }
            List<NodeId> nodeIds = client.allocateNodeIds(nodes, true) ;
            //SList<Node> nodes2 = client.lookupNodeIds(nodeIds) ;
        }
        client.commit();
        client.end();
    }

    private static void single(TClientNode client, int batchSize, int repeats) {
        client.begin(998, ReadWrite.WRITE) ;
        for ( int i = 0 ; i < repeats*batchSize ; i++ ) {
            Node n = SSE.parseNode("<http://example/n-"+i+">") ;
            NodeId nodeId = client.getAllocateNodeId(n) ;
        }
        client.commit();
        client.end();
    }

    public static void main$(String[] args) {
        FileOps.clearAll("DB");

        log.info("SERVERS") ;
        // The deployment "here".
        NetHost here = NetHost.create("localhost") ;
        
        try { 
            LzDeploy.deployServers(config, here);
        } catch ( LizardException ex) {
            System.err.println(ex.getMessage());
            System.exit(0) ;
        }

        // Multiple query servers?
        log.info("DATASET") ;
        Dataset ds = LzDeploy.deployDataset(config, here) ;
        DatasetGraphTDB dsg = (DatasetGraphTDB)(ds.asDatasetGraph()) ;
        load(ds,"/home/afs/Datasets/BSBM/bsbm-250k.nt.gz");
        //System.exit(0) ;
        
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
    private static void time(String label, int batchSize, int repeats ,Runnable r) {
        label = label+"["+batchSize+","+repeats+"]" ;
        time(label, r) ;
    }

    private static void time(String label, Runnable r) {
        System.out.printf("%s\n",label) ;
        Timer timer = new Timer() ;
        timer.startTimer();
        r.run() ;
        long x = timer.endTimer() ;
        System.out.printf("%s Time: %.3f s\n", label, x / 1000.0) ;
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
    
    private static void load(Dataset ds, String datafile) {        
        log.info("LOAD : start") ;
        if ( datafile != null ) {
            // Making loading quieter.
            LogCtl.set(ClusterNodeTable.class, "WARN") ;
            LogCtl.set(TServerNode.class, "WARN") ;
            LogCtl.set(TServerIndex.class, "WARN") ;

            StreamRDF s1 = StreamRDFLib.dataset(ds.asDatasetGraph()) ;
            ProgressLogger plog = new ProgressLogger(LoggerFactory.getLogger("LOAD"), 
                                                     "Triples", 50000, 10) ;
            StreamRDFMonitor s2 = new StreamRDFMonitor(s1, plog) ;
            // Ensure transaction overheads acccounted for
            StreamRDFMerge s3 = new StreamRDFMerge(s2) ;
            s3.start();
            
            // Unwrap a layer of start/finish.
            TDBTxn.executeWrite(ds, () -> {
                RDFDataMgr.parse(s3, datafile) ;
            }) ;
            s3.finish();
            
            LogCtl.set(ClusterNodeTable.class, "INFO") ;
            LogCtl.set(TServerNode.class, "INFO") ;
            LogCtl.set(TServerIndex.class, "INFO") ;
        }
        log.info("LOAD : finish") ;
    }

    // -------- Query
    private static void performQuery(Dataset ds) {
        //            Quack.setVerbose(true) ;
        //            ARQ.setExecutionLogging(InfoLevel.NONE);

        String qs = StrUtils.strjoinNL("PREFIX : <http://example/>"
                                      ,"SELECT (count(*) AS ?C)  "
                                      ,"{ ?s ?p ?o }" ) ;
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
