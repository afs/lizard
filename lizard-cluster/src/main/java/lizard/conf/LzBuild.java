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

import lizard.cluster.Platform ;
import lizard.conf.dataset.DatasetBuilderLizard ;
import lizard.conf.index.IndexServer ;
import lizard.conf.node.NodeServer ;
import lizard.index.TServerIndex ;
import lizard.node.TServerNode ;
import lizard.query.QuackLizard ;
import lizard.system.LzLog ;
import org.apache.jena.atlas.lib.ColumnMap ;
import org.apache.jena.atlas.lib.StrUtils ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.slf4j.Logger ;

import com.hp.hpl.jena.query.ARQ ;
import com.hp.hpl.jena.sparql.engine.main.QC ;
import com.hp.hpl.jena.sparql.engine.optimizer.reorder.ReorderLib ;
import com.hp.hpl.jena.tdb.base.file.FileSet ;
import com.hp.hpl.jena.tdb.base.file.Location ;
import com.hp.hpl.jena.tdb.index.BuilderStdIndex ;
import com.hp.hpl.jena.tdb.index.IndexBuilder ;
import com.hp.hpl.jena.tdb.index.RangeIndexBuilder ;
import com.hp.hpl.jena.tdb.setup.* ;
import com.hp.hpl.jena.tdb.store.DatasetGraphTDB ;
import com.hp.hpl.jena.tdb.store.DatasetPrefixesTDB ;
import com.hp.hpl.jena.tdb.store.QuadTable ;
import com.hp.hpl.jena.tdb.store.TripleTable ;
import com.hp.hpl.jena.tdb.store.nodetable.NodeTable ;
import com.hp.hpl.jena.tdb.store.tupletable.TupleIndex ;
import com.hp.hpl.jena.tdb.sys.DatasetControl ;
import com.hp.hpl.jena.tdb.sys.DatasetControlMRSW ;
import com.hp.hpl.jena.tdb.sys.Names ;

/** Build a dataset */
public class LzBuild
{
    static Logger logConf = Config.logConf ;
    // XXX 
    // *** To be replaced 
    private static ObjectFileBuilder objectFileBuilder     = new BuilderStdDB.ObjectFileBuilderStd() ;
    private static BlockMgrBuilder blockMgrBuilder         = new BuilderStdIndex.BlockMgrBuilderStd() ;
    /*package*/ static IndexBuilder indexBuilder               = new BuilderStdIndex.IndexBuilderStd(blockMgrBuilder, blockMgrBuilder) ;
    /*package*/ static RangeIndexBuilder rangeIndexBuilder     = new BuilderStdIndex.RangeIndexBuilderStd(blockMgrBuilder, blockMgrBuilder) ;
    
    private static NodeTableBuilder nodeTableBuilder       = new BuilderStdDB.NodeTableBuilderStd(indexBuilder, objectFileBuilder) ;
    private static TupleIndexBuilder tupleIndexBuilder     = new BuilderStdDB.TupleIndexBuilderStd(rangeIndexBuilder) ;
    private static StoreParams params = StoreParams.builder().build() ;
    
    public static TupleIndex createTupleIndex(Location loc, String order, String name) {
        FileSet fs = new FileSet(loc, order) ;
        ColumnMap cMap = new ColumnMap(Names.primaryIndexTriples, order) ;   // Primary order.
        TupleIndex tupleIndex = tupleIndexBuilder.buildTupleIndex(fs, cMap, name, params) ;
        return tupleIndex ;
    }
    
    public static NodeTable createNodeTable(Location location) {
        FileSet fsNodeToId = new FileSet(location, Names.indexNode2Id) ;
        FileSet fsId2Node = new FileSet(location, Names.indexId2Node) ;
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
    
    public static DatasetGraphTDB createDataset(Location location, TupleIndex[] tripleIndexes, NodeTable nodeTable) {
        DatasetControl policy = new DatasetControlMRSW() ;
        StoreParams params = StoreParams.getDftStoreParams() ;
        DatasetBuilderLizard dbb = new DatasetBuilderLizard(LzBuild.indexBuilder, LzBuild.rangeIndexBuilder) ;
        // Hack node table.
        DatasetPrefixesTDB prefixes = dbb.makePrefixTable(location, policy) ; 

        // Special.
        String indexes[] = new String[tripleIndexes.length] ;
        for ( int i = 0 ; i < indexes.length ; i++ ) {
            indexes[i] = tripleIndexes[i].getName() ;
        }

        TripleTable tableTriples = new TripleTable(tripleIndexes, nodeTable, policy) ;
        FmtLog.debug(LzLog.logConf, "Triple table: %s :: %s", indexes[0], StrUtils.strjoin(",", indexes)) ;


        //        String[] tripleIndexes = new String[] { params.primaryIndexTriples, "POS", "PSO", "OSP" } ;
        //        TripleTable tableTriples = dbb.makeTripleTable(location, tripleIndexes, nt, policy) ;
        //        TupleIndex[] quadIndexes ;
        //        QuadTable tableQuads = new QuadTable(quadIndexes, nodeTable, policy) ;

        QuadTable tableQuads = dbb.makeQuadTable(location, nodeTable, policy, params) ;
        DatasetGraphTDB dsg = new DatasetGraphTDB(tableTriples, tableQuads, prefixes, ReorderLib.fixed(), null) ;
        
        dsg.getContext().set(ARQ.optFilterPlacementBGP, false);
        QC.setFactory(dsg.getContext(), QuackLizard.factoryLizard) ;
        return dsg ;
    }
    
    /** Build index server, locally */ 
    public static void buildIndexServer(IndexServer idxSvc, Platform platform) {
        Location location = Location.create(idxSvc.data) ;
        FmtLog.info(logConf, "BuildIndexServer: %s %s", idxSvc.port, location) ;
        String indexOrder = idxSvc.indexService.indexOrder ;
        TupleIndex index = LzBuild.createTupleIndex(location, indexOrder, "Idx"+indexOrder) ;
        TServerIndex serverIdx = TServerIndex.create(idxSvc.port, index) ;
        platform.add(serverIdx) ;
    }
    
    /** Build noder server, locally */
    public static void buildNodeServer(NodeServer ns, Platform platform) {
        Location location = Location.create(ns.data) ;
        FmtLog.info(logConf, "buildNodeServer: %s %s", ns.port, location) ;
        NodeTable nt = LzBuild.createNodeTable(location) ;
        TServerNode serverNode = TServerNode.create(ns.port, nt) ;
        platform.add(serverNode) ;
    }
}
