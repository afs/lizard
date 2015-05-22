/**
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

package lizard.conf.build;

import java.util.ArrayList ;
import java.util.List ;

import lizard.api.TxnClient ;
import lizard.cluster.Cluster ;
import lizard.conf.* ;
import lizard.conf.dataset.TransactionalComponentRemote ;
import lizard.conf.dataset.TransactionalComponentZkLock ;
import lizard.index.ClusterTupleIndex ;
import lizard.index.DistributorTuplesReplicate ;
import lizard.index.TupleIndexRemote ;
import lizard.node.ClusterNodeTable ;
import lizard.node.DistributorNodesReplicate ;
import lizard.node.NodeTableRemote ;
import lizard.query.LzDataset ;
import lizard.query.QuackLizard ;
import lizard.system.Component ;
import migrate.TupleIndexEmpty ;

import org.apache.jena.atlas.lib.ColumnMap ;
import org.apache.jena.atlas.lib.StrUtils ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.jena.query.ARQ ;
import org.apache.jena.query.Dataset ;
import org.apache.jena.query.DatasetFactory ;
import org.apache.jena.sparql.core.DatasetGraph ;
import org.apache.jena.sparql.engine.main.QC ;
import org.apache.jena.sparql.engine.optimizer.reorder.ReorderLib ;
import org.seaborne.dboe.base.file.Location ;
import org.seaborne.dboe.sys.Names ;
import org.seaborne.dboe.transaction.Transactional ;
import org.seaborne.dboe.transaction.txn.ComponentId ;
import org.seaborne.dboe.transaction.txn.TransactionCoordinator ;
import org.seaborne.dboe.transaction.txn.TransactionalBase ;
import org.seaborne.dboe.transaction.txn.TransactionalComponent ;
import org.seaborne.tdb2.setup.StoreParams ;
import org.seaborne.tdb2.store.* ;
import org.seaborne.tdb2.store.nodetable.NodeTable ;
import org.seaborne.tdb2.store.nodetable.NodeTableCache ;
import org.seaborne.tdb2.store.nodetable.NodeTableInline ;
import org.seaborne.tdb2.store.tupletable.TupleIndex ;
import org.seaborne.tdb2.sys.DatasetControl ;
import org.seaborne.tdb2.sys.DatasetControlMRSW ;
import org.slf4j.Logger ;

public class Lz2BuilderDataset {
    private static Logger logConf = Config.logConf ;
    
    public static LzDataset build(ConfCluster confCluster, Location location) {
        ConfDataset confDataset = confCluster.dataset ;
        if ( confDataset == null )
            return null ;

        List<Component> startables = new ArrayList<>() ;
        
        TransactionCoordinator txnCoord = new TransactionCoordinator(location) ;
        
        // Indexes
        int N = confDataset.indexes.size() ;
        TupleIndex[] indexes = new TupleIndex[N] ;
        int i = 0 ;
        for ( ConfIndex ci : confDataset.indexes ) {
            indexes[i] = buildClientIndex(confCluster, ci, startables) ;
            i++ ;
        }
        
        //Node table.
        ConfNodeTable cn = confDataset.nodeTable ;
        NodeTable nt = buildClientNodeTable(confCluster, cn, startables) ;
        
        DatasetGraphTDB dsg = createDataset(txnCoord, Location.mem(), indexes, nt) ;
        // Delayed start object.
        LzDataset lizard = new LzDataset(txnCoord, dsg, startables) ;
        return lizard ;
    }
    
    // Move to Builder2?
    private static DatasetGraphTDB createDataset(TransactionCoordinator txnCoord, Location location, TupleIndex[] tripleIndexes, NodeTable nodeTable) {
        DatasetControl policy = new DatasetControlMRSW() ;
        StoreParams params = StoreParams.getDftStoreParams() ;
        // Special triple table
        TripleTable tableTriples ;
        {
            String indexes[] = new String[tripleIndexes.length] ;
            for ( int i = 0 ; i < indexes.length ; i++ ) {
                indexes[i] = tripleIndexes[i].getName() ;
            }

            tableTriples = new TripleTable(tripleIndexes, nodeTable, policy) ;
            FmtLog.debug(logConf, "Triple table: %s :: %s", indexes[0], StrUtils.strjoin(",", indexes)) ;
        }

        // Special quad table : two empty placeholder indexes.
        QuadTable tableQuads ;
        {
            String[] indexes = { Names.primaryIndexQuads, "SPOG" } ;
            TupleIndex[] quadIndexes = new TupleIndex[indexes.length] ;
            for ( int i = 0 ; i < indexes.length ; i++ ) {
                String n = indexes[i] ;
                quadIndexes[i] = new TupleIndexEmpty(new ColumnMap(Names.primaryIndexQuads, n), n) ;
            }
            tableQuads = new QuadTable(quadIndexes, nodeTable, policy) ;
            FmtLog.debug(logConf, "Quad table: %s :: %s", indexes[0], StrUtils.strjoin(",", indexes)) ; 
        }
        
        // Local only.
        Builder2 builder = Builder2.create(txnCoord, location, params) ;
        NodeTable nodeTablePrefixes = builder.buildNodeTable(params.getPrefixTableBaseName()) ;
        DatasetPrefixesTDB prefixes = builder.buildPrefixTable(nodeTablePrefixes) ;
        DatasetGraphTDB dsg = new DatasetGraphTDB(tableTriples, tableQuads, prefixes, ReorderLib.fixed(), builder.getLocation(), builder.getParams()) ;
        // Development.
        dsg.getContext().set(ARQ.optFilterPlacementBGP, false);
        // Query engine.
        QC.setFactory(dsg.getContext(), QuackLizard.factoryLizard) ;
        return dsg ;
    }
        
    public static TupleIndex buildClientIndex(ConfCluster confCluster, ConfIndex ci, List<Component> startables) {
            String idxOrder = ci.indexOrder ;
            int N = idxOrder.length() ;
            if ( N != 3 && N != 4 )
                FmtLog.warn(logConf, "Strange index size: %d (from '%s')", N, idxOrder) ;
            ColumnMap cmap = new ColumnMap("SPO", idxOrder) ;
            DistributorTuplesReplicate dist = new DistributorTuplesReplicate(cmap) ;  
            List<TupleIndexRemote> indexes = new ArrayList<>() ;
            // Scan shards for index.
            confCluster.eltsIndex.stream().sequential()
                .filter(x -> ci.indexOrder.equals(x.conf.indexOrder))
                .sequential()
                .forEach(x -> {
                    TupleIndexRemote idx = TupleIndexRemote.create(x.netAddr.hostname, x.netAddr.port, idxOrder, cmap) ;
                    indexes.add(idx) ;
                    startables.add(idx) ;
                }) ;
            dist.add(indexes);
            // All shards, all replicas.
            ClusterTupleIndex tupleIndex = new ClusterTupleIndex(dist, N, cmap, ci.indexOrder);
            return tupleIndex ;
        }
        
    /** Build a Cluster node table, from the configuration */
    public static NodeTable buildClientNodeTable(ConfCluster confCluster, ConfNodeTable cn, List<Component> startables) {
        DistributorNodesReplicate dist = new DistributorNodesReplicate() ;
        List<NodeTableRemote> nodeTableParts = new ArrayList<>() ;
        confCluster.eltsNodeTable.stream()
            .sequential()
            .forEach(x -> {
                NodeTableRemote ntr = NodeTableRemote.create(x.netAddr.hostname, x.netAddr.port) ;
                nodeTableParts.add(ntr) ;
                startables.add(ntr) ;
            });
        dist.add(nodeTableParts) ;
        ClusterNodeTable cnt = new ClusterNodeTable(dist) ;
        return stackNodeTable(cnt) ;
    }
    
    public static NodeTable stackNodeTable(NodeTable nodeTable) {
        nodeTable = NodeTableCache.create(nodeTable, 10000, 10000, 100) ; 
        nodeTable =  NodeTableInline.create(nodeTable) ;
        return nodeTable ;
    }
    
    public static DatasetGraph datasetGraph(LzDataset lz) {
        // Not persistent across restarts
        ComponentId cidZk = ComponentId.allocLocal() ;
        TransactionalComponentZkLock zkLock = new TransactionalComponentZkLock(cidZk) ;
        TransactionCoordinator transCoord = lz.getTxnMgr() ;
        transCoord.add(zkLock) ;

        ComponentId base = ComponentId.allocLocal() ;
        lz.getComponents().forEach(c->{
            if ( c instanceof TxnClient.Accessor ) {
                TxnClient<?> wire = ((TxnClient.Accessor)c).getWireClient() ;
                ComponentId cid = ComponentId.allocLocal() ;
                TransactionalComponent x = new TransactionalComponentRemote<>(cid, wire) ;
                transCoord.add(x) ;
            }
        });
        
        // Use Zookeeper for transaction ids.
        transCoord.setTxnIdGenerator(Cluster.getTxnIdGenerator());
        Transactional transactional = new TransactionalBase(transCoord) ;
        DatasetGraphTDB dsgtdb = lz.getDataset() ;
        DatasetGraph dsg = new DatasetGraphTxn(dsgtdb, transactional, transCoord) ;
        lz.start();
        return dsg ;
    }

    public static Dataset dataset(LzDataset lz) {
        DatasetGraph dsg = datasetGraph(lz) ;
        return DatasetFactory.create(dsg) ;
    }
}

