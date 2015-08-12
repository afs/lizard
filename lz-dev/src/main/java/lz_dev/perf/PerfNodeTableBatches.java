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

package lz_dev.perf;

import java.util.ArrayList ;
import java.util.List ;

import lizard.build.LzBuilderNodeServer ;
import lizard.cluster.Platform ;
import lizard.conf.* ;
import lizard.node.NodeTableRemote ;
import lizard.node.TClientNode ;
import lizard.node.TServerNode ;

import org.apache.jena.atlas.lib.FileOps ;
import org.apache.jena.atlas.lib.Timer ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.apache.jena.atlas.logging.ProgressLogger ;
import org.apache.jena.graph.Node ;
import org.apache.jena.graph.NodeFactory ;
import org.apache.jena.query.ReadWrite ;
import org.apache.jena.sparql.sse.SSE ;
import org.seaborne.dboe.base.file.Location ;
import org.seaborne.tdb2.setup.StoreParams ;
import org.seaborne.tdb2.setup.TDBBuilder ;
import org.seaborne.tdb2.store.NodeId ;
import org.seaborne.tdb2.store.nodetable.NodeTable ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

public class PerfNodeTableBatches {
    static { LogCtl.setLog4j(); } 
    public static Logger log = LoggerFactory.getLogger("Main") ;

    public static void main(String[] args) {
        //-XX:-PrintGC
        //-XX:-PrintGCDetails
        //pureMemNT() ; System.exit(0) ;
        batchingToNodeTables() ; System.exit(0) ;
    }
    
    // BaseNodeTable insertion rate.
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
    
    // Create two separate nodetables
    public static void batchingToNodeTables() {
        int port1 = 9090 ;
        int port2 = 9292 ;
        String DIR1 = "--mem--/DB-N-1" ;
        String DIR2 = "--mem--/DB-N-2" ;

        createNodeServer(DIR1, port1) ;
        createNodeServer(DIR2, port2) ;

        TClientNode client = TClientNode.create("localhost", port1) ;
        client.start() ;
        NodeTableRemote ntr = NodeTableRemote.create(null, "localhost", port2) ;
        ntr.start() ;

        // 5-15s a run (SSD, 2014 desktop machine).
        int N = 5 ;
        for ( int i = 1 ; i <= N ; i++ ) {
            System.out.printf("++++ Loop %d of %d\n", i, N) ;
            timedRun(ntr, 0, 500000) ;
            timedRun(ntr, 1, 500000) ;
            timedRun(ntr, 10, 50000) ;
            timedRun(ntr, 100, 5000) ;
            
            timedRun(client, 0, 500000) ;
            timedRun(client, 1, 500000) ;
            timedRun(client, 10, 50000) ;
            timedRun(client, 100, 5000) ;
        }
        System.out.println("++++ Finished") ;
    }
 
    /** Create and start a node server */
    private static void createNodeServer(String directory, int port) {
        Platform platform = new Platform() ;
        Location location = Location.create(directory) ;
        if ( !location.isMem() ) {
            FileOps.ensureDir(directory) ;
            FileOps.clearAll(directory) ;
        }
        StoreParams params = StoreParams.getDftStoreParams() ;

        NetAddr here = NetAddr.create("localhost", port) ;
        ConfNodeTable nTableDesc = new ConfNodeTable(1, 1) ;
        VNode vNode = new VNode("local", here) ;
        ConfNodeTableElement x = new ConfNodeTableElement("Nodes", "node", nTableDesc, VNodeAddr.create("local", port)) ;
        TServerNode nodeServer = LzBuilderNodeServer.buildNodeServer(platform, location, params, x) ;
        nodeServer.start() ;
    }

    private static void timedRun(TClientNode client, int batchSize, int repeats) {
        Runnable r = ()->batched(client, batchSize, repeats) ;
        if ( batchSize <= 0 )
            r = ()->single(client, repeats) ;
        time("TClientNode:      ",  batchSize, repeats, r) ;
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

    private static void timedRun(NodeTableRemote client, int batchSize, int repeats) {
        Runnable r = ()->batched(client, batchSize, repeats) ;
        if ( batchSize <= 0 )
            r = ()->single(client, repeats) ;
        time("NodeTableRemote:  ",  batchSize, repeats, r) ;
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

    static int unique = 0 ;
    private static void time(String label, int batchSize, int repeats, Runnable r) {
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
}
