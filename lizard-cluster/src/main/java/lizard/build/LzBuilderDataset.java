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

package lizard.build;

import java.util.* ;
import java.util.function.Supplier ;

import lizard.api.TxnClient ;
import lizard.cluster.Cluster ;
import lizard.conf.* ;
import lizard.dataset.TransactionalComponentRemote ;
import lizard.index.ClusterTupleIndex ;
import lizard.index.DistributorTupleIndex ;
import lizard.index.DistributorTuplesReplicate ;
import lizard.index.TupleIndexRemote ;
import lizard.node.ClusterNodeTable ;
import lizard.node.DistributorNodes ;
import lizard.node.DistributorNodesReplicate ;
import lizard.node.NodeTableRemote ;
import lizard.query.LzDataset ;
import lizard.query.QuackLizard ;
import lizard.system.Component ;
import lizard.system.LizardException ;
import migrate.TupleIndexEmpty ;

import org.apache.jena.atlas.lib.ColumnMap ;
import org.apache.jena.atlas.lib.StrUtils ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.jena.query.ARQ ;
import org.apache.jena.query.Dataset ;
import org.apache.jena.query.DatasetFactory ;
import org.apache.jena.query.ReadWrite ;
import org.apache.jena.sparql.engine.main.QC ;
import org.apache.jena.sparql.engine.optimizer.reorder.ReorderLib ;
import org.seaborne.dboe.base.file.Location ;
import org.seaborne.dboe.sys.Names ;
import org.seaborne.dboe.transaction.txn.* ;
import org.seaborne.tdb2.setup.StoreParams ;
import org.seaborne.tdb2.setup.TDBBuilder ;
import org.seaborne.tdb2.store.DatasetGraphTDB ;
import org.seaborne.tdb2.store.DatasetPrefixesTDB ;
import org.seaborne.tdb2.store.QuadTable ;
import org.seaborne.tdb2.store.TripleTable ;
import org.seaborne.tdb2.store.nodetable.NodeTable ;
import org.seaborne.tdb2.store.nodetable.NodeTableCache ;
import org.seaborne.tdb2.store.nodetable.NodeTableInline ;
import org.seaborne.tdb2.store.tupletable.TupleIndex ;
import org.seaborne.tdb2.sys.DatasetControl ;
import org.seaborne.tdb2.sys.DatasetControlNone ;
import org.slf4j.Logger ;

public class LzBuilderDataset {
    private static Logger logConf = Config.logConf ;
    
    public static LzDataset build(ConfCluster confCluster, Location location) {
        ConfDataset confDataset = confCluster.dataset ;
        if ( confDataset == null )
            return null ;

        List<Component> startables = new ArrayList<>() ;
        
        TransactionCoordinator txnCoord = new TransactionCoordinator(location) ;
        List<QuorumChooser> quorumChoosers = new ArrayList<>() ;

        // Indexes
        int N = confDataset.indexes.size() ;
        ClusterTupleIndex[] indexes = new ClusterTupleIndex[N] ;
        int i = 0 ;
        for ( ConfIndex ci : confDataset.indexes ) {
            indexes[i] = buildClientIndex(confCluster, ci, startables) ;
            i++ ;
        }
        
        ConfNodeTable cn = confDataset.nodeTable ;
        
        // ==== Wire up.
        
        // ---- NodeTable
        ClusterNodeTable cnt = buildClientNodeTable(confCluster, cn, startables) ;
        NodeTable nt = stackNodeTable(cnt) ;
        {
            DistributorNodes distNodes = cnt.getDistributor() ;
            // Move into buildClientNodeTable?
            //   Map: wite <-> TransactionalComponentRemote
            //   or replace wire with  TransactionalComponentRemote 
            Map<NodeTableRemote, TransactionalComponent> map = new HashMap<>() ;
            cnt.getDistributor().allRemotes().forEach( ntr -> {
                TxnClient<?> wire = ntr.getWireClient() ;
                ComponentId cid = ComponentId.allocLocal() ;
                TransactionalComponentRemote<?> x = new TransactionalComponentRemote<>(cid, wire) ;
                txnCoord.add(x) ;
                map.put(ntr, x) ;
            }) ;

            Supplier<ComponentGroup> readQuorum = () -> quorum(map, distNodes.allFind()) ;
            Supplier<ComponentGroup> writeQuorum = () -> quorum(map, distNodes.allStore()) ;

            
            QuorumChooser qChooseNode = new QuorumChooser(readQuorum, writeQuorum) ;
            quorumChoosers.add(qChooseNode) ;
        }
        // ---- NodeTable

        // ---- Indexes
        for ( ClusterTupleIndex ci : indexes ) {
            DistributorTupleIndex dti = ci.getDistributor() ;
            Map<TupleIndexRemote, TransactionalComponent> map = new HashMap<>() ;
            dti.allRemotes().forEach( ntr -> {
                TxnClient<?> wire = ntr.getWireClient() ;
                ComponentId cid = ComponentId.allocLocal() ;
                TransactionalComponentRemote<?> x = new TransactionalComponentRemote<>(cid, wire) ;
                txnCoord.add(x) ;
                map.put(ntr, x) ;
            }) ;
            Supplier<ComponentGroup> readQuorum = () -> quorum(map, dti.allFind()) ;
            Supplier<ComponentGroup> writeQuorum = () -> quorum(map, dti.allStore()) ;
            
            QuorumChooser qChooseIdx = new QuorumChooser(readQuorum, writeQuorum) ;
            quorumChoosers.add(qChooseIdx) ;
        }
        // ---- Indexes
        
        QuorumGenerator qGenerator = new QuorumChooserN(quorumChoosers) ;
        txnCoord.setQuorumGenerator(qGenerator) ;
        
        DatasetGraphTDB dsg = createDataset(txnCoord, Location.mem(), indexes, nt) ;
        // Delayed start object.
        LzDataset lz = new LzDataset(txnCoord, dsg, startables) ;
        
        // Use Zookeeper for transaction ids.
        txnCoord.setTxnIdGenerator(Cluster.getTxnIdGenerator());
        //Transactional transactional = new TransactionalBase(txnCoord) ;
        lz.start();
        return lz ;
    }
    
    /** Convert a collection of didtributor results to a ComponentGroup */ 
    static <X> ComponentGroup quorum(Map<X, TransactionalComponent> xmap, Collection<X> q) {
        ComponentGroup cg = new ComponentGroup() ;
        q.stream().map(x -> {
            TransactionalComponent tc = xmap.get(x) ;
            return tc ;
        })
                                      
                  .forEach(tc -> {
                      if ( tc == null )
                          logConf.warn("NULL");
                      cg.add(tc) ;
                  }) ;
            //tc.forEach(tc -> cg.add(tc)) ;
        return cg ;
    }
    
    /** Framework for an implementation of QuorumGenerator */
    static class QuorumChooser implements QuorumGenerator {
        private Supplier<ComponentGroup> readPolicy ;
        private Supplier<ComponentGroup> writePolicy ;
        QuorumChooser(Supplier<ComponentGroup> readPolicy, Supplier<ComponentGroup> writePolicy) {
            this.readPolicy = readPolicy ;
            this.writePolicy = writePolicy ;
        }
        
        @Override
        public ComponentGroup genQuorum(ReadWrite mode) {
            switch(mode) {
                case READ: return readPolicy.get() ;
                case WRITE: return writePolicy.get() ;
                default: throw new LizardException("Null ReadWrite mode") ;
            }
        }
    }
    
    /** QuorumGenerator of QuorumGenerators.  Combines the results of each sub-QuorumGenerator */
    static class QuorumChooserN implements QuorumGenerator {
        List<QuorumChooser> elements = new ArrayList<>() ;
        QuorumChooserN(List<QuorumChooser> elements) {
            this.elements = elements ;
        }
        
        @Override
        public ComponentGroup genQuorum(ReadWrite mode) { 
            ComponentGroup cg = new ComponentGroup() ;
            elements.forEach(qc -> {
                ComponentGroup cgx = qc.genQuorum(mode) ;
                cg.addAll(cgx); 
            });
            return cg ;
        }
    }
    
    // 1: Restructure TDBBuilder to make index, make node tables then build upwards. 
    // 2: Subclass TDBBuilder here 
    private static DatasetGraphTDB createDataset(TransactionCoordinator txnCoord, Location location, TupleIndex[] tripleIndexes, NodeTable nodeTable) {
        DatasetControl policy = new DatasetControlNone() ;
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
        TDBBuilder builder = TDBBuilder.create(txnCoord, location, params) ;
        TransactionalSystem txnSys = new TransactionalBase(txnCoord) ;
        NodeTable nodeTablePrefixes = builder.buildNodeTable(params.getPrefixTableBaseName()) ;
        DatasetPrefixesTDB prefixes = builder.buildPrefixTable(nodeTablePrefixes) ;
        DatasetGraphTDB dsg = new DatasetGraphTDB(txnSys, tableTriples, tableQuads, prefixes, ReorderLib.fixed(), builder.getLocation(), builder.getParams()) ;
        // Development.
        dsg.getContext().set(ARQ.optFilterPlacementBGP, false);
        // Query engine.
        QC.setFactory(dsg.getContext(), QuackLizard.factoryLizard) ;
        return dsg ;
    }
        
    public static ClusterTupleIndex buildClientIndex(ConfCluster confCluster, ConfIndex ci, List<Component> startables) {
            String idxOrder = ci.indexOrder ;
            int N = idxOrder.length() ;
            if ( N != 3 && N != 4 )
                FmtLog.warn(logConf, "Strange index size: %d (from '%s')", N, idxOrder) ;
            ColumnMap cmap = new ColumnMap("SPO", idxOrder) ;
            DistributorTuplesReplicate dist = new DistributorTuplesReplicate(cmap) ;  
            List<TupleIndexRemote> indexes = new ArrayList<>() ;
            // Scan shards for index.
            
            // XXX QUORUM
            
            confCluster.eltsIndex.stream().sequential()
                .filter(x -> ci.indexOrder.equals(x.conf.indexOrder))
                .forEach(x -> {
                    NetAddr netAddr = x.addr.placement(confCluster.placements, x.addr.getPort()) ; 
                    TupleIndexRemote idx = TupleIndexRemote.create(netAddr.getName(), netAddr.getPort(), idxOrder, cmap) ;
                    indexes.add(idx) ;
                    startables.add(idx) ;
                }) ;
            dist.add(indexes);
            // All shards, all replicas.
            ClusterTupleIndex tupleIndex = new ClusterTupleIndex(dist, N, cmap, ci.indexOrder);
            return tupleIndex ;
        }
        
    /** Build a Cluster node table, from the configuration.
     * No acaches, no inlining. See {@link #stackNodeTable}
     */
    public static ClusterNodeTable buildClientNodeTable(ConfCluster confCluster, ConfNodeTable cn, List<Component> startables) {
        DistributorNodesReplicate dist = new DistributorNodesReplicate() ;
        List<NodeTableRemote> nodeTableParts = new ArrayList<>() ;

        // XXX QUORUM via dist.
        
        confCluster.eltsNodeTable.stream().sequential()
            .forEach(x -> {
                NetAddr netAddr = x.addr.placement(confCluster.placements, x.addr.getPort()) ; 
                NodeTableRemote ntr = NodeTableRemote.create(netAddr.getName(), netAddr.getPort()) ;
                nodeTableParts.add(ntr) ;
                startables.add(ntr) ; // COMPONENTS
                new TransactionalComponentRemote<>(null, ntr.getWireClient()) ;
            });
        dist.add(nodeTableParts) ;
        ClusterNodeTable cnt = new ClusterNodeTable(dist) ;
        return cnt ;
    }
    
    public static NodeTable stackNodeTable(NodeTable nodeTable) {
        nodeTable = NodeTableCache.create(nodeTable, 100000, 100000, 1000) ; 
        nodeTable =  NodeTableInline.create(nodeTable) ;
        return nodeTable ;
    }
    
//    private static DatasetGraph xdatasetGraph(LzDataset lz) {
//        // Not persistent across restarts
//        ComponentId cidZk = ComponentId.allocLocal() ;
//        TransactionalComponentZkLock zkLock = new TransactionalComponentZkLock(cidZk) ;
//        TransactionCoordinator transCoord = lz.getTxnMgr() ;
//        transCoord.add(zkLock) ;
//
//        lz.getComponents().forEach(c->{
//            if ( c instanceof TxnClient.WireClient ) {
//                TxnClient<?> wire = ((TxnClient.WireClient)c).getWireClient() ;
//                ComponentId cid = ComponentId.allocLocal() ;
//                // XXX TransactionalComponentRemote
//                TransactionalComponent x = new TransactionalComponentRemote<>(cid, wire) ;
//                transCoord.add(x) ;
//            } else {
//                logConf.warn("Not a TxnClient.WireClient: "+c) ;
//            }
//                
//        });
//        
//        // Use Zookeeper for transaction ids.
//        transCoord.setTxnIdGenerator(Cluster.getTxnIdGenerator());
//        Transactional transactional = new TransactionalBase(transCoord) ;
//        DatasetGraphTDB dsgtdb = lz.getDataset() ;
//        lz.start();
//        return dsgtdb ;
//    }
//
//    public static Dataset dataset(LzDataset lz) {
//        DatasetGraph dsg = datasetGraph(lz) ;
//        return DatasetFactory.create(dsg) ;
//    }
    
    public static Dataset dataset(LzDataset lz) {
        return DatasetFactory.create(lz.getDataset()) ;
    }

}
