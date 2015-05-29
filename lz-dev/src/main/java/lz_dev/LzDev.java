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
import org.seaborne.tdb2.setup.TDBBuilder ;
import org.seaborne.tdb2.store.DatasetGraphTDB ;
import org.seaborne.tdb2.store.NodeId ;
import org.seaborne.tdb2.store.nodetable.NodeTable ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

public class LzDev {
    static { LogCtl.setLog4j(); } 
    public static Logger log = LoggerFactory.getLogger("Main") ;

    static String confDir           = "setup-disk" ;
    //static String confDir           = "setup-simple" ;
    static String confNode          = Q.filename(confDir, "conf-node.ttl") ;
    static String confIndex         = Q.filename(confDir, "conf-index.ttl") ;
    static String confDataset       = Q.filename(confDir, "conf-dataset.ttl") ;
    static Model configurationModel = Q.readAll(confNode, confIndex, confDataset) ;
    static ConfCluster config       = LzConfParserRDF.parseConfFile(configurationModel) ; 
    
    public static void main(String[] args) {
        //pureMemNT() ; System.exit(0) ;
        mainBatching2() ;
        
        try { main$(args) ; }
        catch (Exception ex) { 
            System.out.flush() ;
            System.err.println(ex.getMessage()) ;
            ex.printStackTrace(System.err);
            System.exit(0) ;
        }
    }

    public static void pureMemNT() {
        TDBBuilder builder = TDBBuilder.create(Location.mem()) ;
        NodeTable nt = builder.buildBaseNodeTable("nodes") ;
        builder.getTxnCoord().start() ;
        builder.getTxnCoord().begin(ReadWrite.WRITE) ;
        
        ProgressLogger plog = new ProgressLogger(LoggerFactory.getLogger("Dev"), "Nodes", 100000, 10) ;
        plog.start(); 
        int N = 2000000 ;
        for ( int i = 0 ; i < N ; i++ ) {
            Node n = NodeFactory.createURI("http://example/node-"+i) ;
            nt.getAllocateNodeId(n) ;
            plog.tick();
        }
        plog.finish() ;
        plog.finishMessage();
    }
    
    public static void mainBatching2() {
        int port1 = 9090 ;
        int port2 = 9292 ;
        {
            Platform platform = new Platform() ;
            String DIR1 = "--mem--/DB-N-1" ;
            String DIR2 = "--mem--/DB-N-2" ;
            FileOps.ensureDir(DIR1) ;
            FileOps.clearAll(DIR1);
            FileOps.ensureDir(DIR2) ;
            FileOps.clearAll(DIR2);
            Location location1 = Location.create(DIR1) ;
            Location location2 = Location.create(DIR2) ;
            //Location location1 = Location.mem("DB-N-1") ;
            //Location location2 = Location.mem("DB-N-2") ;

            StoreParams params = StoreParams.getDftStoreParams() ;
            
            NetAddr here1 = NetAddr.create("localhost", port1) ;
            NetAddr here2 = NetAddr.create("localhost", port2) ;
            ConfNodeTable nTableDesc1 = new ConfNodeTable(1, 1) ;
            ConfNodeTable nTableDesc2 = new ConfNodeTable(1, 1) ;
            ConfNodeTableElement x1 = new ConfNodeTableElement("Nodes", "node", nTableDesc1, here1) ;
            ConfNodeTableElement x2 = new ConfNodeTableElement("Nodes", "node", nTableDesc2, here2) ;
            TServerNode nodeServer1 = LzBuilderNodeServer.buildNodeServer(platform, location1, params, x1) ;
            TServerNode nodeServer2 = LzBuilderNodeServer.buildNodeServer(platform, location2, params, x2) ;
            nodeServer1.start(); 
            nodeServer2.start();
        }
        
        {
            TClientNode client = TClientNode.create("localhost", port1) ;
            client.start() ;
            NodeTableRemote ntr = NodeTableRemote.create("localhost", port2) ;
            ntr.start() ;

            for ( int i = 0 ; i < 10 ; i++ ) {
                System.out.println("---- NodeTableRemote") ;
                //doOneTimedRun(ntr, 0, 500000) ;
                //doOneTimedRun(ntr, 10, 50000) ;
                //doOneTimedRun(ntr, 100, 5000) ;
                doOneTimedRun(ntr, 1, 500000) ;
                System.out.println("---- TClientNode") ;
                //doOneTimedRun(client, 0, 500000) ;
                //doOneTimedRun(client, 10, 50000) ;
                //doOneTimedRun(client, 100, 5000) ;
                doOneTimedRun(client, 1, 500000) ;
            }
            System.exit(0) ;
        }
    }
    
    public static void mainBatching() {
        int port = 9090 ;
        {
            Platform platform = new Platform() ;
            //Location location = Location.create("DB-N") ;
            Location location = Location.mem() ;
            FileOps.ensureDir("N") ;
            FileOps.clearAll("N");

            StoreParams params = StoreParams.getDftStoreParams() ;
            
            NetAddr here = NetAddr.create("localhost", port) ;
            ConfNodeTable nTableDesc = new ConfNodeTable(1, 1) ;
            ConfNodeTableElement x = new ConfNodeTableElement("Nodes", "node", nTableDesc, here) ;
            TServerNode nodeServer = LzBuilderNodeServer.buildNodeServer(platform, location, params, x) ;
            nodeServer.start(); 
        }
        
        if ( true ) {
            // DIRECT

            TClientNode client = TClientNode.create("localhost", port) ;
            client.start() ;

            // int BatchSize = 10 ;
            // int Repeats = 10000 ;

            //        for ( int i = 0 ; i < 5 ; i++ ) {
            //                System.out.println("-----") ;
            //            time("Batched ",  BatchSize, Repeats,    ()->batched(client, BatchSize, Repeats)) ;
            //            time("Batched ",  1, Repeats*BatchSize,  ()->batched(client, 1, Repeats*BatchSize)) ;
            //            time("Single  ",  BatchSize, Repeats ,   ()->single(client, BatchSize, Repeats)) ;
            //        }

            // Heap attack.
            System.out.println("---- TClientNode") ;
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
        }
        if ( true ) {
            // Via a 
            NodeTableRemote ntr = NodeTableRemote.create("localhost", port) ;
            ntr.start(); 
            System.out.println("---- NodeTableRemote") ;
            
            System.out.println("+--- Warm up") ;
            doOneTimedRun(ntr, 10, 50000) ;
            System.out.println("+--- Live") ;
            doOneTimedRun(ntr, 1000, 500) ;
            doOneTimedRun(ntr, 10, 50000) ;
            doOneTimedRun(ntr, 100, 5000) ;
            doOneTimedRun(ntr, 1000, 500) ;
            //doOneTimedRun(client, 1, 500000) ;
            doOneTimedRun(ntr, 100, 5000) ;
            //doOneTimedRun(client, 1, 500000) ;
        }
        
        System.exit(0) ;
    }
    
    public static void doOneTimedRun(TClientNode client, int batchSize, int repeats) {
        Runnable r = ()->batched(client, batchSize, repeats) ;
        if ( batchSize <= 0 )
            r = ()->single(client, repeats) ;
        time("",  batchSize, repeats, r) ;
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
            //List<Node> nodes2 = client.lookupNodeIds(nodeIds) ;
        }
        client.commit();
        client.end();
    }

    public static void doOneTimedRun(NodeTableRemote client, int batchSize, int repeats) {
        Runnable r = ()->batched(client, batchSize, repeats) ;
        if ( batchSize <= 0 )
            r = ()->single(client, repeats) ;
        time("",  batchSize, repeats, r) ;
    }

    private static void batched(NodeTableRemote ntr, int batchSize, int repeats) {
        // Multiple/batched.
        unique++ ;
        ntr.begin(999, ReadWrite.WRITE) ;
        List<Node> nodes = new ArrayList<>(batchSize) ;
        for ( int i = 0 ; i < repeats ; i++ ) {
            nodes.clear() ;
            for ( int j = 0 ; j < batchSize ; j++ ) {
                Node n = NodeFactory.createURI("http://example/n-"+unique+"-"+i+"-"+j) ;
                nodes.add(n) ;
            }
            List<NodeId> nodeIds = ntr.bulkNodeToNodeId(nodes, true) ;
            //List<Node> nodes2 = ntr.bulkNodeIdToNode(nodeIds) ;
        }
        ntr.commit();
        ntr.end();
    }
    
    private static void single(TClientNode client, int repeats) {
        client.begin(998, ReadWrite.WRITE) ;
        for ( int i = 0 ; i < repeats ; i++ ) {
            Node n = SSE.parseNode("<http://example/n-"+i+">") ;
            NodeId nodeId = client.getAllocateNodeId(n) ;
        }
        client.commit();
        client.end();
    }

    private static void single(NodeTableRemote ntr, int repeats) {
        ntr.begin(998, ReadWrite.WRITE) ;
        for ( int i = 0 ; i < repeats ; i++ ) {
            Node n = NodeFactory.createURI("http://example/n-"+i) ;
            NodeId nodeId = ntr.getAllocateNodeId(n) ;
        }
        ntr.commit();
        ntr.end();
    }

    public static void main$(String[] args) {
        FileOps.clearAll("DB");

        log.info("SERVERS") ;

        config.fileroot = "--mem--" ;
        //config.fileroot = "DB" ;
        
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
        
        //config.print(IndentedWriter.stdout);
        
        Dataset ds = LzDeploy.deployDataset(config, here) ;
        DatasetGraphTDB dsg = (DatasetGraphTDB)(ds.asDatasetGraph()) ;
        load(ds,"/home/afs/Datasets/BSBM/bsbm-1m.nt.gz");
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
        System.out.printf("%s",label) ;
        Timer timer = new Timer() ;
        timer.startTimer();
        r.run() ;
        long x = timer.endTimer() ;
        System.out.printf(" Time: %.3f s\n", x / 1000.0) ;
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

            DatasetGraphTDB dsg = (DatasetGraphTDB)(ds.asDatasetGraph()) ;
            ProgressLogger plog = new ProgressLogger(LoggerFactory.getLogger("LOAD"), "Triples", 50000, 10) ;
            
            StreamRDF s0 = StreamRDFLib.dataset(ds.asDatasetGraph()) ;
            StreamRDF s1 = new StreamRDFBatchSplit(dsg, 10) ;
            StreamRDFMonitor s2 = new StreamRDFMonitor(s0, plog) ;
            
            StreamRDF s3 = s2 ;
            
            s2.startMonitor();
            
            TDBTxn.executeWrite(ds, () -> {
                RDFDataMgr.parse(s3, datafile) ;
            }) ;
            s2.finishMonitor();
            
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
