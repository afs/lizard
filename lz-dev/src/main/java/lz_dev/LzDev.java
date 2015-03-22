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

import java.util.ArrayList ;
import java.util.List ;

import com.hp.hpl.jena.graph.Node ;
import com.hp.hpl.jena.query.* ;
import com.hp.hpl.jena.rdf.model.Model ;
import com.hp.hpl.jena.sparql.core.DatasetGraph ;
import com.hp.hpl.jena.sparql.sse.SSE ;
import com.hp.hpl.jena.sparql.util.QueryExecUtils ;
import com.hp.hpl.jena.tdb.store.NodeId ;

import lizard.api.TxnClient ;
import lizard.conf.Configuration ;
import lizard.index.TServerIndex ;
import lizard.index.TupleIndexRemote ;
import lizard.node.ClusterNodeTable ;
import lizard.node.NodeTableRemote ;
import lizard.node.TServerNode ;
import lizard.query.LzDataset ;
import lizard.sys.Deploy ;
import lizard.sys.Deployment ;
import lizard.system.LizardException ;
import lizard.system.Pingable ;
import migrate.Q ;

import org.apache.jena.atlas.lib.Lib ;
import org.apache.jena.atlas.lib.StrUtils ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.apache.jena.riot.RDFDataMgr ;
import org.seaborne.dboe.base.file.Location ;
import org.seaborne.dboe.transaction.ThreadTxn ;
import org.seaborne.dboe.transaction.Transactional ;
import org.seaborne.dboe.transaction.Txn ;
import org.seaborne.dboe.transaction.txn.* ;
import org.seaborne.dboe.transaction.txn.journal.Journal ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;


public class LzDev {
    static { LogCtl.setLog4j(); } 
    public static Logger log = LoggerFactory.getLogger("Main") ;

    static String confNode          = Q.filename(Setup.confDir, "conf-node.ttl") ;
    static String confIndex         = Q.filename(Setup.confDir, "conf-index.ttl") ;
    static String confDataset       = Q.filename(Setup.confDir, "conf-dataset.ttl") ;
    static Model configurationModel = Q.readAll(confNode, confIndex, confDataset) ;
    static Configuration config     = Configuration.fromModel(configurationModel) ;
    
    static String deploymentFile        = Q.filename(Setup.confDir, "deploy-jvm.ttl") ;
    
    public static void main1(String[] args) {
        //Setup, take apart - try N way transactions.
        log.info("SERVERS") ;
        Deployment deployment = Deploy.deployServers(config, deploymentFile);

        log.info("DATASET") ;
        LzDataset lz = buildDataset(config) ;
        Dataset ds = dataset(lz) ;
        load(ds, "D.ttl") ;
        
        performQuery(ds); 
        
        log.info("** Done **") ;
        System.exit(0) ;
        if ( false ) {
            while(true) { Lib.sleep(10000) ; }
        }

    }
    
    static NodeTableRemote ntr = null ;
    //static TupleIndexRemote tir = null ;
    static int counter = 0 ;

    public static void main(String[] args) {
        try { main$(args) ; }
        catch (Exception ex) 
        { 
            System.out.flush() ;
            System.err.println(ex.getMessage()) ;
            //ex.printStackTrace(System.err);
            System.exit(0) ;
        }
    }
    public static void main$(String[] args) {
        log.info("SERVERS") ;
        try { 
        Deployment deployment = Deploy.deployServers(config, deploymentFile);
        } catch ( LizardException ex) {
            System.err.println(ex.getMessage());
            System.exit(0) ;
        }

        log.info("DATASET") ;
        LzDataset lz = buildDataset(config) ;
        Dataset ds = dataset(lz) ;
        
        List<TransactionalComponent> tComp = new ArrayList<>() ;
        
        log.info("TRANSACTIONS") ;
        
        ComponentId base = ComponentId.allocLocal() ;
        lz.getComponents().forEach(c->{
//            System.out.println(c) ;
//            System.out.println(c.getClass().getName()) ;
            
            //SOME TRANSACTION STUFF
            
            if ( c instanceof TupleIndexRemote ) {
                TupleIndexRemote rIdx = (TupleIndexRemote)c ;
                //log.info("TupleIndexRemote: "+rIdx.getLabel()) ;
                // Remove this can catch at creation time.
                TxnClient<?> wire = rIdx.getWireClient() ;
                int i = ++counter ;
                ComponentId cid = ComponentId.alloc(base, "TupleIndex"+i, i) ;
                TransactionalComponentRemote<TxnClient<?>> x = new TransactionalComponentRemote<>(cid, wire) ;
                tComp.add(x) ;
            }
            if ( c instanceof NodeTableRemote ) {
                NodeTableRemote r = (NodeTableRemote)c ;
                ntr = r ;
                //log.info("NodeTableRemote: "+r.getLabel()) ;
                // Remove this can catch at creation time.
                TxnClient<?> wire = r.getWireClient() ;
                int i = ++counter ;
                ComponentId cid = ComponentId.alloc(base, "NodeTable:"+i, i) ;
                TransactionalComponentRemote<TxnClient<?>> x = new TransactionalComponentRemote<>(cid, wire) ;
                tComp.add(x) ;
            }
        });
        
        Journal journal = Journal.create(Location.mem()) ;
        TransactionCoordinator transCoord = new TransactionCoordinator(journal, tComp) ;
        Transactional transactional = new TransactionalBase(transCoord) ;
        
        if ( false )
        {
            Node n1 = SSE.parseNode("<http://example/s1>") ;
            Node n2 = SSE.parseNode("<http://example/s2>") ;
            
            Txn.executeWrite(transactional, ()->{
                NodeId nid = ntr.getAllocateNodeId(n1) ;
                System.out.printf("T1: Node = %s ; NodeId = %s\n", n1, nid) ;

                Node n1a = ntr.getNodeForNodeId(nid) ;
                System.out.printf("T1: Node = %s ; NodeId = %s\n", n1a, nid) ;
            });

            ThreadTxn tt = Txn.threadTxnRead(transactional, ()->{
                NodeId nid1 = ntr.getNodeIdForNode(n1) ;
                System.out.printf("TA11: Node = %s ; NodeId = %s\n", n1, nid1) ;
                NodeId nid2 = ntr.getNodeIdForNode(n2) ;
                System.out.printf("TA12: Node = %s ; NodeId = %s\n", n2, nid2) ;
            }) ;
            
            Txn.executeWrite(transactional, ()->{
                NodeId nid2 = ntr.getAllocateNodeId(n2) ;
                System.out.printf("T2: Node = %s ; NodeId = %s\n", n2, nid2) ;
            }) ;
            
            tt.run();
            Txn.threadTxnRead(transactional, ()->{
                NodeId nid1 = ntr.getNodeIdForNode(n1) ;
                System.out.printf("TA21: Node = %s ; NodeId = %s\n", n1, nid1) ;
                NodeId nid2 = ntr.getNodeIdForNode(n2) ;
                System.out.printf("TA22: Node = %s ; NodeId = %s\n", n2, nid2) ;
            }).run();
            
            log.info("** Done **") ;
            System.exit(0) ;
        }
        
        log.info("LOAD 1") ;
        Transaction txn = transCoord.begin(ReadWrite.WRITE) ;
        RDFDataMgr.read(ds, "D1.ttl") ;
        //txn.prepare(); 
        txn.commit();
        txn.end() ;
        log.info("LOAD 2") ;
        Txn.executeWrite(transactional, () -> RDFDataMgr.read(ds, "D2.ttl")) ;
        
        
        Transaction txnR = transCoord.begin(ReadWrite.READ) ;
        performQuery(ds); 
        txnR.end() ;
        
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
    
    private static Dataset dataset(LzDataset lz) {
        lz.getComponents().stream().forEach(c -> {
            //System.out.println("Component: "+c.getClass().getTypeName()) ;
            if ( c instanceof Pingable ) {
                Pingable p = (Pingable)c ;
                p.ping();
            }
        }) ;

        DatasetGraph dsg = lz.getDataset() ; 
        Dataset ds = DatasetFactory.create(dsg) ;
        return ds ;
    }
    
    private static void load(Dataset ds, String data) {        
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
    }

    // -------- Query
    private static void performQuery(Dataset ds) {
        log.info("QUERY") ;
        //            Quack.setVerbose(true) ;
        //            ARQ.setExecutionLogging(InfoLevel.NONE);

        String qs = StrUtils.strjoinNL("PREFIX : <http://example/> SELECT * "
                                       , "{ ?s :p ?o }" 
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
