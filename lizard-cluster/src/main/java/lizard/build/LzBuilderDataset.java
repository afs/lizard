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

package lizard.build;

import java.util.* ;
import java.util.function.Supplier ;

import org.apache.jena.atlas.lib.ColumnMap ;
import org.apache.jena.atlas.lib.StrUtils ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.jena.query.ARQ ;
import org.apache.jena.query.Dataset ;
import org.apache.jena.query.DatasetFactory ;
import org.apache.jena.query.ReadWrite ;
import org.apache.jena.shared.PrefixMapping ;
import org.apache.jena.shared.impl.PrefixMappingImpl ;
import org.apache.jena.sparql.core.DatasetPrefixStorage ;
import org.apache.jena.sparql.engine.main.QC ;
import org.apache.jena.sparql.engine.optimizer.reorder.ReorderLib ;
import org.seaborne.dboe.base.file.Location ;
import org.seaborne.dboe.sys.Names ;
import org.seaborne.dboe.transaction.txn.* ;
import org.seaborne.tdb2.setup.StoreParams ;
import org.seaborne.tdb2.store.DatasetGraphTDB ;
import org.seaborne.tdb2.store.QuadTable ;
import org.seaborne.tdb2.store.TripleTable ;
import org.seaborne.tdb2.store.nodetable.NodeTable ;
import org.seaborne.tdb2.store.nodetable.NodeTableCache ;
import org.seaborne.tdb2.store.nodetable.NodeTableInline ;
import org.seaborne.tdb2.store.tupletable.TupleIndex ;
import org.seaborne.tdb2.sys.DatasetControl ;
import org.seaborne.tdb2.sys.DatasetControlNone ;
import org.slf4j.Logger ;

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

public class LzBuilderDataset {
    private static Logger logConf = Config.logConf ;
    
    public static LzDataset build(ConfCluster confCluster, Location location, String datasetVNode) {
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
            indexes[i] = buildClientIndex(confCluster, ci, startables, datasetVNode) ;
            i++ ;
        }
        ConfNodeTable cn = confDataset.nodeTable ;
        ClusterNodeTable cnt = buildClientNodeTable(confCluster, cn, startables, datasetVNode) ;
        NodeTable nt = stackNodeTable(cnt) ;
        
        // ==== Wire up.
        
        // ---- NodeTable
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
        // ---- Prefixes
        
        //// Add quorum chooser if not null prefix table.
        DatasetPrefixStorage prefixes = buildPrefixTable(Location.mem(), txnCoord, confCluster, startables, datasetVNode) ;
        // ---- Prefixes
        
        QuorumGenerator qGenerator = new QuorumChooserN(quorumChoosers) ;
        txnCoord.setQuorumGenerator(qGenerator) ;
        
        DatasetGraphTDB dsg = createDataset(txnCoord, Location.mem(), indexes, nt, prefixes) ;
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
    private static DatasetGraphTDB createDataset(TransactionCoordinator txnCoord, Location location, 
                                                 TupleIndex[] tripleIndexes, NodeTable nodeTable, DatasetPrefixStorage prefixes) {
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
        
        TransactionalSystem txnSys = new TransactionalBase(txnCoord) ;
        DatasetGraphTDB dsg = new DatasetGraphTDB(txnSys, tableTriples, tableQuads, prefixes, ReorderLib.fixed(),location, params) ;
        // Development.
        dsg.getContext().set(ARQ.optFilterPlacementBGP, false);
        // Query engine.
        QC.setFactory(dsg.getContext(), QuackLizard.factoryLizard) ;
        return dsg ;
    }
        
    public static ClusterTupleIndex buildClientIndex(ConfCluster confCluster, ConfIndex ci, List<Component> startables, String hereVNode) {
            String idxOrder = ci.indexOrder ;
            int N = idxOrder.length() ;
            if ( N != 3 && N != 4 )
                FmtLog.warn(logConf, "Strange index size: %d (from '%s')", N, idxOrder) ;
            ColumnMap cmap = new ColumnMap("SPO", idxOrder) ;
            DistributorTuplesReplicate dist = new DistributorTuplesReplicate(hereVNode, cmap) ;  
            List<TupleIndexRemote> indexes = new ArrayList<>() ;
            // Scan shards for index.
            confCluster.eltsIndex.stream().sequential()
                .filter(x -> ci.indexOrder.equals(x.conf.indexOrder))
                .forEach(x -> {
                    // Is it "here"?
                    if ( x.addr.sameHost(hereVNode) )
                        logConf.info("HERE: Index: "+x.addr.getPort()) ;
                    NetAddr netAddr = x.addr.placement(confCluster.placements, x.addr.getPort()) ; 
                    TupleIndexRemote idx = TupleIndexRemote.create(hereVNode, netAddr.getName(), netAddr.getPort(), idxOrder, cmap) ;
                    indexes.add(idx) ;
                    startables.add(idx) ;
                }) ;
            dist.add(indexes);
            // All shards, all replicas.
            ClusterTupleIndex tupleIndex = new ClusterTupleIndex(dist, N, cmap, ci.indexOrder);
            return tupleIndex ;
        }
        
    /** Build a Cluster node table, from the configuration.
     * No caches, no inlining. See {@link #stackNodeTable}
     */
    public static ClusterNodeTable buildClientNodeTable(ConfCluster confCluster, ConfNodeTable cn, List<Component> startables, String hereVNode) {
        DistributorNodesReplicate dist = new DistributorNodesReplicate(hereVNode) ;
        List<NodeTableRemote> nodeTableParts = new ArrayList<>() ;
        confCluster.eltsNodeTable.stream().sequential()
            .forEach(x -> {
                if ( x.addr.sameHost(hereVNode) )
                    logConf.info("HERE: Node: "+x.addr.getPort()) ;
                NetAddr netAddr = x.addr.placement(confCluster.placements, x.addr.getPort()) ;
                NodeTableRemote ntr = NodeTableRemote.create(hereVNode, netAddr.getName(), netAddr.getPort()) ;
                nodeTableParts.add(ntr) ;
                startables.add(ntr) ;
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
    
    public static Dataset dataset(LzDataset lz) {
        return DatasetFactory.wrap(lz.getDataset()) ;
    }
    
    /** Build a Cluster node table, from the configuration.
     * No caches, no inlining. See {@link #stackNodeTable}
     */
    public static DatasetPrefixStorage buildPrefixTable(Location location, TransactionCoordinator txnCoord, ConfCluster confCluster, List<Component> startables, String hereVNode) {
//        StoreParams params = StoreParams.getDftStoreParams() ;
//        TDBBuilder builder = TDBBuilder.create(txnCoord, location, params) ;
//        NodeTable nodeTablePrefixes = builder.buildNodeTable(params.getPrefixTableBaseName()) ;
//        DatasetPrefixesTDB prefixes = builder.buildPrefixTable(nodeTablePrefixes) ;
//        return prefixes ;
        DatasetPrefixStorage nullPrefixes =  new DatasetPrefixStorage() {
            @Override
            public void close() {}

            @Override
            public void sync() {}

            @Override
            public Set<String> graphNames() {
                return Collections.emptySet() ;
            }

            @Override
            public String readPrefix(String graphName, String prefix) {
                return null;
            }

            @Override
            public String readByURI(String graphName, String uriStr) {
                return null;
            }

            @Override
            public Map<String, String> readPrefixMap(String graphName) {
                return null;
            }

            @Override
            public void insertPrefix(String graphName, String prefix, String uri) {}

            @Override
            public void loadPrefixMapping(String graphName, PrefixMapping pmap) {}

            @Override
            public void removeFromPrefixMap(String graphName, String prefix) {}

            @Override
            public PrefixMapping getPrefixMapping() {
                return new PrefixMappingImpl() ;
            }

            @Override
            public PrefixMapping getPrefixMapping(String graphName) {
                return new PrefixMappingImpl() ;
            }} ;
            return nullPrefixes ;
    }
}
