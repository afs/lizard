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

package lizard.conf;

import lizard.node.NodeTableDBOE ;
import org.apache.jena.atlas.lib.ColumnMap ;
import org.seaborne.dboe.base.block.BlockMgrBuilder ;
import org.seaborne.dboe.base.block.BlockMgrBuilderStd ;
import org.seaborne.dboe.index.IndexBuilder ;
import org.seaborne.dboe.index.RangeIndexBuilder ;
import org.seaborne.dboe.trans.bplustree.IndexBuilderBPTree ;
import org.seaborne.dboe.trans.bplustree.RangeIndexBuilderBPTree ;
import org.seaborne.dboe.transaction.txn.ComponentId ;
import org.slf4j.Logger ;

import com.hp.hpl.jena.tdb.base.file.Location ;
import com.hp.hpl.jena.tdb.setup.BuilderStdDB ;
import com.hp.hpl.jena.tdb.setup.NodeTableBuilder ;
import com.hp.hpl.jena.tdb.setup.StoreParams ;
import com.hp.hpl.jena.tdb.setup.TupleIndexBuilder ;
import com.hp.hpl.jena.tdb.store.nodetable.NodeTable ;
import com.hp.hpl.jena.tdb.store.nodetable.NodeTableCache ;
import com.hp.hpl.jena.tdb.store.nodetable.NodeTableInline ;
import com.hp.hpl.jena.tdb.store.tupletable.TupleIndex ;
import com.hp.hpl.jena.tdb.sys.Names ;
import com.hp.hpl.jena.tdb.sys.SystemTDB ;

/** Build a dataset using DBOE components */
public class LzBuildDBOE
{
    static Logger logConf = Config.logConf ;
    // Temporary - for indexes
    static ComponentId cid1 = ComponentId.allocLocal() ;
    // Temporary - for object files.
    static ComponentId cid2 = ComponentId.allocLocal() ;

    private static BlockMgrBuilder blockMgrBuilder                 = new BlockMgrBuilderStd() ;
    /*package*/ static IndexBuilder indexBuilderDBoe               = new IndexBuilderBPTree(cid1, blockMgrBuilder, blockMgrBuilder) ;
    /*package*/ static RangeIndexBuilder rangeIndexBuilderDBoe     = new RangeIndexBuilderBPTree(cid1, blockMgrBuilder, blockMgrBuilder) ;

    // DBOE to TDB class hierarchies.
    // Build TDB style but using Mantis.
    static com.hp.hpl.jena.tdb.index.RangeIndexBuilder rangeIndexBuilderTDB     = new AdapterRangeIndexBuilder() ;
    static com.hp.hpl.jena.tdb.index.IndexBuilder      indexBuilderTDB          = new AdapterIndexBuilder() ;
    static com.hp.hpl.jena.tdb.setup.ObjectFileBuilder objectFileBuilderTDB     = new AdapterObjectFileBuilder() ;

    private static TupleIndexBuilder tupleIndexBuilder     = new BuilderStdDB.TupleIndexBuilderStd(rangeIndexBuilderTDB) ;
    private static NodeTableBuilder nodeTableBuilder       = new NodeTableBuilderDBOE(indexBuilderTDB, objectFileBuilderTDB) ;

    private static StoreParams params = StoreParams.builder().build() ;

    public static TupleIndex createTupleIndex(Location loc, String order, String name) {
        com.hp.hpl.jena.tdb.base.file.FileSet fs = new com.hp.hpl.jena.tdb.base.file.FileSet(loc, order) ;
        ColumnMap cMap = new ColumnMap(Names.primaryIndexTriples, order) ;   // Primary order.
        TupleIndex tupleIndex = tupleIndexBuilder.buildTupleIndex(fs, cMap, name, params) ;
        return tupleIndex ;
    }

    public static NodeTable createNodeTable(Location location) {
        com.hp.hpl.jena.tdb.base.file.FileSet fsNodeToId  = new com.hp.hpl.jena.tdb.base.file.FileSet(location, Names.indexNode2Id) ;
        com.hp.hpl.jena.tdb.base.file.FileSet fsId2Node   = new com.hp.hpl.jena.tdb.base.file.FileSet(location, Names.indexId2Node) ;
        StoreParams params2 = StoreParams.builder(params)
            .node2NodeIdCacheSize(-1)
            .nodeId2NodeCacheSize(-1)
            .nodeMissCacheSize(-1)
            .build() ;
        NodeTable nt = nodeTableBuilder.buildNodeTable(fsNodeToId, fsId2Node, params2) ;
        return nt ;
    }

    // Lizard related builds

    //    /** Helper to build an index */ 
    //    public static TupleIndex createIndex(Location loc, String indexStr, ShardRefIndex shard) {
    //        Location loc2 = loc.getSubLocation(shard.name) ;
    //        if ( ! loc2.isMem() )
    //            FileOps.ensureDir(loc2.getDirectoryPath());
    //        TupleIndex index = LzBuild.createTupleIndex(loc2, indexStr, shard.toString()) ;
    //        return index ;
    //    }
    
    static class NodeTableBuilderDBOE implements NodeTableBuilder
    {
        public NodeTableBuilderDBOE(com.hp.hpl.jena.tdb.index.IndexBuilder indexBuilder, com.hp.hpl.jena.tdb.setup.ObjectFileBuilder objectFileBuilder) {
            
        }
    
        @Override
        public NodeTable buildNodeTable(com.hp.hpl.jena.tdb.base.file.FileSet fsIndex,
                                        com.hp.hpl.jena.tdb.base.file.FileSet fsObjectFile, StoreParams params) {
            com.hp.hpl.jena.tdb.base.record.RecordFactory recordFactory = new com.hp.hpl.jena.tdb.base.record.RecordFactory(SystemTDB.LenNodeHash, SystemTDB.SizeOfNodeId) ;
            com.hp.hpl.jena.tdb.index.Index idx = indexBuilderTDB.buildIndex(fsIndex, recordFactory, params) ;
            com.hp.hpl.jena.tdb.base.objectfile.ObjectFile objectFile = objectFileBuilderTDB.buildObjectFile(fsObjectFile, Names.extNodeData) ;
            NodeTable nodeTable = new NodeTableDBOE(idx, objectFile) ;
            nodeTable = NodeTableCache.create(nodeTable, 
                                              params.getNode2NodeIdCacheSize(),
                                              params.getNodeId2NodeCacheSize(),
                                              params.getNodeMissCacheSize()) ;
            nodeTable = NodeTableInline.create(nodeTable) ;
            return nodeTable ;
        }
    }

    // Build TDB style but using Mantis.

}
