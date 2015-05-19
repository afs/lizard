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

package lizard.conf.dataset;

import lizard.query.QuackLizard ;
import org.apache.jena.atlas.lib.ColumnMap ;
import org.apache.jena.atlas.lib.StrUtils ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import org.apache.jena.query.ARQ ;
import org.apache.jena.sparql.engine.main.QC ;
import org.apache.jena.sparql.engine.optimizer.reorder.ReorderTransformation ;
import org.seaborne.tdb2.TDBException ;
import org.seaborne.tdb2.base.file.FileSet ;
import org.seaborne.tdb2.base.file.Location ;
import org.seaborne.tdb2.index.IndexBuilder ;
import org.seaborne.tdb2.index.RangeIndexBuilder ;
import org.seaborne.tdb2.setup.* ;
import org.seaborne.tdb2.store.* ;
import org.seaborne.tdb2.store.nodetable.NodeTable ;
import org.seaborne.tdb2.store.tupletable.TupleIndex ;
import org.seaborne.tdb2.sys.DatasetControl ;
import org.seaborne.tdb2.sys.DatasetControlMRSW ;

/** Make TDB storage dataset graphs : not for transactional datasets */ 

public class DatasetBuilderLizard implements DatasetBuilder
{
    // **** Stolen from TDB.
    // Can DatasetBuilderStd now be used?
    
    
    private static final Logger log = LoggerFactory.getLogger(DatasetBuilderLizard.class) ;
    
    NodeTableBuilder nodeTableBuilder ;
    TupleIndexBuilder tupleIndexBuilder ;
    
    public DatasetBuilderLizard(IndexBuilder indexBuilder, RangeIndexBuilder rangeIndexBuilder)
    {
        super() ;
        ObjectFileBuilder objectFileBuilder = new BuilderStdDB.ObjectFileBuilderStd()  ;
        nodeTableBuilder    = new BuilderStdDB.NodeTableBuilderStd(indexBuilder, objectFileBuilder) ;
        tupleIndexBuilder   = new BuilderStdDB.TupleIndexBuilderStd(rangeIndexBuilder) ;
    }

    @Override
    public DatasetGraphTDB build(Location location, StoreParams params)
    {
        DatasetControl policy = createConcurrencyPolicy() ;
        NodeTable nodeTable = makeNodeTable(location, params.getIndexNode2Id(), params.getIndexId2Node(),
                                            -1, -1, -1) ; // No caches
                                            // Small caches 
                                            //10, 1000, 10) ;
                                            //params.Node2NodeIdCacheSize, params.NodeId2NodeCacheSize, params.NodeMissCacheSize) ;
        
        //nodeTable = new NodeTableLogger(null, nodeTable) ;
        
        TripleTable tripleTable = makeTripleTable(location, params.getTripleIndexes(), nodeTable, policy, params) ; 
        QuadTable quadTable = makeQuadTable(location, nodeTable, policy, params) ;
        DatasetPrefixesTDB prefixes = makePrefixTable(location, policy) ;
        ReorderTransformation transform  = chooseReorderTransformation(location) ;
        
        DatasetGraphTDB dsg = new DatasetGraphTDB(tripleTable, quadTable, prefixes, transform, null) ;

        dsg.getContext().set(ARQ.optFilterPlacementBGP, false);
        QC.setFactory(dsg.getContext(), QuackLizard.factoryLizard) ;
        return dsg ;
    }
    
    protected DatasetControl createConcurrencyPolicy() { return new DatasetControlMRSW() ; }
    
    protected ReorderTransformation chooseReorderTransformation(Location location)
    {    
        return DatasetBuilderStd.chooseOptimizer(location) ;
    }
    
    //XXX Refactor
    public NodeTable makeNodeTable(Location location, String indexNode2Id, String indexId2Node, 
                                      int sizeNode2NodeIdCache, int sizeNodeId2NodeCache, int sizeNodeMissCache)
    {
        FileSet fsNodeToId = new FileSet(location, indexNode2Id) ;
        FileSet fsId2Node = new FileSet(location, indexId2Node) ;
        StoreParams params = StoreParams.builder()
            .indexNode2Id(indexNode2Id).node2NodeIdCacheSize(sizeNode2NodeIdCache)
            .indexId2Node(indexId2Node).nodeId2NodeCacheSize(sizeNodeId2NodeCache)
            .nodeMissCacheSize(sizeNodeMissCache)
            .build() ;
        NodeTable nt = nodeTableBuilder.buildNodeTable(fsNodeToId, fsId2Node, params) ;
        return nt ;
    }
    
    // ======== Dataset level
    public TripleTable makeTripleTable(Location location, String[] indexes, NodeTable nodeTable, DatasetControl policy, StoreParams params)
    {    
        String primary = params.getPrimaryIndexTriples() ;
        //String[] indexes = params.tripleIndexes ;
        
        if ( indexes.length != 3 )
            error(log, "Wrong number of triple table indexes: "+StrUtils.strjoin(",", indexes)) ;
        log.debug("Triple table: "+primary+" :: "+StrUtils.strjoin(",", indexes)) ;
        
        TupleIndex tripleIndexes[] = makeTupleIndexes(location, primary, indexes, params) ;
        
        if ( tripleIndexes.length != indexes.length )
            error(log, "Wrong number of triple table tuples indexes: "+tripleIndexes.length) ;
        TripleTable tripleTable = new TripleTable(tripleIndexes, nodeTable, policy) ;
        return tripleTable ;
    }
    
    public QuadTable makeQuadTable(Location location, NodeTable nodeTable, DatasetControl policy, StoreParams params)
    {    
        String primary = params.getPrimaryIndexQuads() ;
        String[] indexes = params.getQuadIndexes() ;
        
        if ( indexes.length != 6 )
            error(log, "Wrong number of quad table indexes: "+StrUtils.strjoin(",", indexes)) ;
        
        FmtLog.debug(log, "Quad table: %s  :: %s", primary, StrUtils.strjoin(",", indexes)) ;
        
        TupleIndex quadIndexes[] = makeTupleIndexes(location, primary, indexes, params) ;
        if ( quadIndexes.length != indexes.length )
            error(log, "Wrong number of quad table tuples indexes: "+quadIndexes.length) ;
        QuadTable quadTable = new QuadTable(quadIndexes, nodeTable, policy) ;
        return quadTable ;
    }

    public DatasetPrefixesTDB makePrefixTable(Location location, DatasetControl policy)
    {    
        return org.apache.jena.tdb.setup.Build.makePrefixes(location, policy) ;
    }
    
    private TupleIndex[] makeTupleIndexes(Location location, String primary, String[] indexNames, StoreParams params)
    {
        return makeTupleIndexes(location, primary, indexNames, indexNames, params) ;
    }
    
    private TupleIndex[] makeTupleIndexes(Location location, String primary, String[] indexNames, String[] filenames, StoreParams params)
    {
        if ( primary.length() != 3 && primary.length() != 4 )
            error(log, "Bad primary key length: "+primary.length()) ;
    
        int indexRecordLen = primary.length()*NodeId.SIZE ;
        TupleIndex indexes[] = new TupleIndex[indexNames.length] ;
        for (int i = 0 ; i < indexes.length ; i++)
            indexes[i] = makeTupleIndex(location, filenames[i], primary, indexNames[i], params) ;
        return indexes ;
    }

    // ----
    public TupleIndex makeTupleIndex(Location location, String name, String primary, String indexOrder, StoreParams params)
    {
        // Commonly,  name == indexOrder.
        // FileSet
        FileSet fs = new FileSet(location, name) ;
        ColumnMap colMap = new ColumnMap(primary, indexOrder) ;
        return tupleIndexBuilder.buildTupleIndex(fs, colMap, indexOrder, params) ;
    }

    private static void error(Logger log, String msg)
    {
        if ( log != null )
            log.error(msg) ;
        throw new TDBException(msg) ;
    }
}
