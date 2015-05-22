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

import java.io.File ;
import java.io.FileFilter ;
import java.util.UUID ;

import org.apache.jena.atlas.lib.ColumnMap ;
import org.apache.jena.atlas.lib.StrUtils ;
import org.apache.jena.sparql.core.DatasetGraph ;
import org.apache.jena.sparql.engine.main.QC ;
import org.apache.jena.sparql.engine.optimizer.reorder.ReorderLib ;
import org.seaborne.dboe.DBOpEnvException ;
import org.seaborne.dboe.base.file.* ;
import org.seaborne.dboe.base.record.RecordFactory ;
import org.seaborne.dboe.index.Index ;
import org.seaborne.dboe.index.RangeIndex ;
import org.seaborne.dboe.sys.Names ;
import org.seaborne.dboe.trans.bplustree.BPlusTree ;
import org.seaborne.dboe.trans.bplustree.BPlusTreeFactory ;
import org.seaborne.dboe.trans.data.TransBinaryDataFile ;
import org.seaborne.dboe.transaction.Transactional ;
import org.seaborne.dboe.transaction.txn.ComponentId ;
import org.seaborne.dboe.transaction.txn.TransactionCoordinator ;
import org.seaborne.dboe.transaction.txn.TransactionalBase ;
import org.seaborne.dboe.transaction.txn.journal.Journal ;
import org.seaborne.tdb2.TDBException ;
import org.seaborne.tdb2.setup.StoreParams ;
import org.seaborne.tdb2.setup.StoreParamsCodec ;
import org.seaborne.tdb2.setup.StoreParamsConst ;
import org.seaborne.tdb2.setup.StoreParamsFactory ;
import org.seaborne.tdb2.solver.OpExecutorTDB1 ;
import org.seaborne.tdb2.store.* ;
import org.seaborne.tdb2.store.nodetable.NodeTable ;
import org.seaborne.tdb2.store.nodetable.NodeTableCache ;
import org.seaborne.tdb2.store.nodetable.NodeTableInline ;
import org.seaborne.tdb2.store.nodetable.NodeTableTRDF ;
import org.seaborne.tdb2.store.nodetupletable.NodeTupleTable ;
import org.seaborne.tdb2.store.nodetupletable.NodeTupleTableConcrete ;
import org.seaborne.tdb2.store.tupletable.TupleIndex ;
import org.seaborne.tdb2.store.tupletable.TupleIndexRecord ;
import org.seaborne.tdb2.sys.DatasetControl ;
import org.seaborne.tdb2.sys.DatasetControlMRSW ;
import org.seaborne.tdb2.sys.SystemTDB ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

// Takes from TDB2Builder
// Converted to statics.

/** Build TDB2 databases.
 * <p>
 * <b>Do not call these operations directly - use StoreConnection.</b>
 */
public class Builder2 {
    private static Logger log = LoggerFactory.getLogger(Builder2.class) ;
    
    public static DatasetGraph build(Location location) {
        return build(location, StoreParams.getDftStoreParams()) ;
    }

//    // Recover from existing.
//    // Align component ids from existing.
//    
//    private final ComponentId tdbComponentId ;
    private static int componentCounter = 1 ; // TEMPORARY HACK
    
//    private final Location location ;
//    private final StoreParams params ;
    private static DatasetControl createPolicy() { return new DatasetControlMRSW() ; }
    
    // Or state?
    private static ComponentIdMgr componentIdMgr = new ComponentIdMgr(UUID.randomUUID()) ;
    // BuildContext
    //   Location,
    //   Coordinator
    //   StoreParams
    //   UUID
    
    
    public static DatasetGraphTxn build(Location location, StoreParams appParams) {
        StoreParams locParams = StoreParamsCodec.read(location) ;
        StoreParams dftParams = StoreParams.getDftStoreParams() ;
        // This can write the chosen parameters if necessary (new database, appParams != null, locParams == null)
        boolean newArea = isNewDatabaseArea(location) ;
        if ( newArea ) {
        }
        StoreParams params = StoreParamsFactory.decideStoreParams(location, newArea, appParams, locParams, dftParams) ;
        //return new TDB2Builder(location, params).build() ;
        return build$(location, params) ;
    }
    
    /** Look at a directory and see if it is a new area */
    private static boolean isNewDatabaseArea(Location location) {
        if ( location.isMem() )
            return true ;
        File d = new File(location.getDirectoryPath()) ;
        if ( !d.exists() )
            return true ;
        FileFilter ff = fileFilterNewDB ;
        File[] entries = d.listFiles(ff) ;
        return entries.length == 0 ;
    }
    
    /** FileFilter
     * Skips "..", "." and "tdb.cfg"
     * 
     */
    static FileFilter fileFilterNewDB  = (pathname)->{
        String fn = pathname.getName() ;
        if ( fn.equals(".") || fn.equals("..") )
            return false ;
        if ( pathname.isDirectory() )
            return true ;
        if ( fn.equals(StoreParamsConst.TDB_CONFIG_FILE) )
            return false ;
        return true ;
    } ;
    
    public static TransactionCoordinator buildTransactionCoordinator(Location location) {
        Journal journal = Journal.create(location) ;
        TransactionCoordinator txnCoord = new TransactionCoordinator(journal) ;
        return txnCoord ;
    }

    private static DatasetGraphTxn build$(Location location, StoreParams params) {
        TransactionCoordinator txnCoord = buildTransactionCoordinator(location) ;
        
        NodeTable nodeTable = buildNodeTable(txnCoord, location, params, params.getNodeTableBaseName()) ;
        
        TripleTable tripleTable = buildTripleTable(txnCoord, location, params, nodeTable) ;
        QuadTable quadTable = buildQuadTable(txnCoord, location, params, nodeTable) ;
        
        NodeTable nodeTablePrefixes = buildNodeTable(txnCoord, 
                                                     location, params, params.getPrefixTableBaseName()) ;
        DatasetPrefixesTDB prefixes = buildPrefixTable(txnCoord, location, params, nodeTablePrefixes) ;
        
        DatasetGraphTDB dsg = new DatasetGraphTDB(tripleTable, quadTable, prefixes, ReorderLib.fixed(), location, params) ;
        Transactional trans = new TransactionalBase(txnCoord) ;
        DatasetGraphTxn dsgtxn = new DatasetGraphTxn(dsg, trans, txnCoord) ;
        QC.setFactory(dsgtxn.getContext(), OpExecutorTDB1.OpExecFactoryTDB) ;
        return dsgtxn ;
    }

    public static TripleTable buildTripleTable(TransactionCoordinator txnCoord, 
                                                Location location, StoreParams params,
                                                NodeTable nodeTable)
    {    
        String primary = params.getPrimaryIndexTriples() ;
        String[] indexes = params.getTripleIndexes() ;

        if ( indexes.length != 3 )
            error(log, "Wrong number of triple table indexes: "+StrUtils.strjoin(",", indexes)) ;
        log.debug("Triple table: "+primary+" :: "+StrUtils.strjoin(",", indexes)) ;

        TupleIndex tripleIndexes[] = makeTupleIndexes(txnCoord, location, params, primary, indexes) ;

        if ( tripleIndexes.length != indexes.length )
            error(log, "Wrong number of triple table tuples indexes: "+tripleIndexes.length) ;
        TripleTable tripleTable = new TripleTable(tripleIndexes, nodeTable, createPolicy()) ;
        return tripleTable ;
    }

    public static QuadTable buildQuadTable(TransactionCoordinator txnCoord, 
                                            Location location, StoreParams params,
                                            NodeTable nodeTable)
    {    
        String primary = params.getPrimaryIndexQuads() ;
        String[] indexes = params.getQuadIndexes() ;

        if ( indexes.length != 6 )
            error(log, "Wrong number of quad table indexes: "+StrUtils.strjoin(",", indexes)) ;
        log.debug("Quad table: "+primary+" :: "+StrUtils.strjoin(",", indexes)) ;

        TupleIndex tripleIndexes[] = makeTupleIndexes(txnCoord, location, params, primary, indexes) ;

        if ( tripleIndexes.length != indexes.length )
            error(log, "Wrong number of triple table tuples indexes: "+tripleIndexes.length) ;
        QuadTable tripleTable = new QuadTable(tripleIndexes, nodeTable, createPolicy()) ;
        return tripleTable ;
    }

    public static DatasetPrefixesTDB buildPrefixTable(TransactionCoordinator txnCoord,
                                                       Location location, StoreParams params,
                                                       NodeTable prefixNodes) {
        String primary = params.getPrimaryIndexPrefix() ;
        String[] indexes = params.getPrefixIndexes() ;

        TupleIndex prefixIndexes[] = makeTupleIndexes(txnCoord, 
                                                      location, params, 
                                                      primary, indexes) ;
        if ( prefixIndexes.length != 1 )
            error(log, "Wrong number of triple table tuples indexes: "+prefixIndexes.length) ;

        // No cache - the prefix mapping is a cache
        //NodeTable prefixNodes = makeNodeTable(location, pnNode2Id, pnId2Node, -1, -1, -1)  ;
        NodeTupleTable prefixTable = new NodeTupleTableConcrete(primary.length(),
                                                                prefixIndexes,
                                                                prefixNodes, createPolicy()) ;
        DatasetPrefixesTDB prefixes = new DatasetPrefixesTDB(prefixTable) ; 
        log.debug("Prefixes: "+primary+" :: "+StrUtils.strjoin(",", indexes)) ;
        return prefixes ;
    }

    // ---- Build structures

    public static TupleIndex[] makeTupleIndexes(TransactionCoordinator txnCoord,
                                                 Location location, StoreParams params,
                                                 String primary, String[] indexNames) {
        int indexRecordLen = primary.length()*SystemTDB.SizeOfNodeId ;
        TupleIndex indexes[] = new TupleIndex[indexNames.length] ;
        for (int i = 0 ; i < indexes.length ; i++) {
            String indexName = indexNames[i] ;
            String indexLabel = indexNames[i] ;
            indexes[i] = buildTupleIndex(txnCoord, location, params, primary, indexName, indexLabel) ;
        }
        return indexes ;
    }

    public static String choosePrimaryForIndex(StoreParams params, String index) {
        String primary3 = params.getPrimaryIndexTriples() ;
        String primary4 = params.getPrimaryIndexQuads() ;
        
        if ( index.length() == primary3.length() )
            return primary3 ;
        if ( index.length() == primary4.length() )
            return primary4 ;
        throw new DBOpEnvException("Can't find primary for '"+index+"'") ;
    }

    public static TupleIndex buildTupleIndex(TransactionCoordinator txnMgr,
                                             Location location, StoreParams params,
                                             String primary, String index, String name) {
        ColumnMap cmap = new ColumnMap(primary, index) ;
        RecordFactory rf = new RecordFactory(SystemTDB.SizeOfNodeId * cmap.length(), 0) ;
        ComponentId cid1 = componentIdMgr.getComponentId(index) ;
        RangeIndex rIdx = buildRangeIndex(txnMgr, cid1, location, params, rf, index) ;
        TupleIndex tIdx = new TupleIndexRecord(primary.length(), cmap, index, rf, rIdx) ;
        return tIdx ;
    }
    
    public static RangeIndex buildRangeIndex(TransactionCoordinator coord, ComponentId cid,
                                             Location location, StoreParams params,
                                             RecordFactory recordFactory,
                                             String name) {
        FileSet fs = new FileSet(location, name) ;
        BPlusTree bpt = BPlusTreeFactory.createBPTree(cid, fs, recordFactory) ;
        coord.add(bpt) ;
        return bpt ;
    }
    
    public static NodeTable buildNodeTable(TransactionCoordinator coord, Location location, StoreParams params, String name) {
        NodeTable nodeTable = buildBaseNodeTable(coord, location, params, name) ;
        nodeTable = NodeTableCache.create(nodeTable, params) ;
        nodeTable = NodeTableInline.create(nodeTable) ;
        return nodeTable ; 
    }

    public static NodeTable buildBaseNodeTable(TransactionCoordinator coord, Location location, StoreParams params, String name) {
        RecordFactory recordFactory = new RecordFactory(SystemTDB.LenNodeHash, SystemTDB.SizeOfNodeId) ;
        
        ComponentId cid1 = componentIdMgr.getComponentId(name) ;
        Index index = buildRangeIndex(coord, cid1, location, params, recordFactory, name) ;
        
        String dataname = name+"-data" ; 
        ComponentId cid2 = componentIdMgr.getComponentId(dataname) ;
        TransBinaryDataFile transBinFile = buildBinaryDataFile(coord, cid2, location, params, dataname) ;
        coord.add(transBinFile) ;
        return new NodeTableTRDF(index, transBinFile) ;
    }
    
    public static TransBinaryDataFile buildBinaryDataFile(TransactionCoordinator coord, ComponentId cid,
                                                          Location location, StoreParams params, String name) {
        FileSet fs = new FileSet(location, name) ; 
        BinaryDataFile binFile = FileFactory.createBinaryDataFile(fs, Names.extObjNodeData) ;
        BufferChannel pState = FileFactory.createBufferChannel(fs, Names.extBdfState) ;
        // ComponentId mgt.
        TransBinaryDataFile transBinFile = new TransBinaryDataFile(binFile, cid, pState) ;
        return transBinFile ;
    }
    
    private static void error(Logger log, String msg)
    {
        if ( log != null )
            log.error(msg) ;
        throw new TDBException(msg) ;
    }
}
