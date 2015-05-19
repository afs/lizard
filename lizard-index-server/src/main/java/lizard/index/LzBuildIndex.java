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

package lizard.index;

import org.seaborne.dboe.base.block.BlockMgrBuilder ;
import org.seaborne.dboe.base.block.BlockMgrBuilderStd ;
import org.seaborne.dboe.index.IndexBuilder ;
import org.seaborne.dboe.index.RangeIndexBuilder ;
import org.seaborne.dboe.trans.bplustree.IndexBuilderBPTree ;
import org.seaborne.dboe.trans.bplustree.RangeIndexBuilderBPTree ;
import org.seaborne.dboe.transaction.txn.ComponentId ;

// To replace lzBuild code
public class LzBuildIndex {
    // Temporary
    static ComponentId cid = ComponentId.allocLocal() ;
    
    private static BlockMgrBuilder blockMgrBuilder                  = new BlockMgrBuilderStd() ;
    /*package*/ static IndexBuilder indexBuilder                    = new IndexBuilderBPTree(cid, blockMgrBuilder, blockMgrBuilder) ;
    /*package*/ static RangeIndexBuilder rangeIndexBuilderDBoe      = new RangeIndexBuilderBPTree(cid, blockMgrBuilder, blockMgrBuilder) ;
    
//    // Build TDB style but using Mantis.
//    static org.apache.jena.tdb.index.RangeIndexBuilder rangeIndexBuilderTDB     = new org.apache.jena.tdb.index.RangeIndexBuilder() {
//        @Override
//        public org.apache.jena.tdb.index.RangeIndex buildRangeIndex(org.apache.jena.tdb.base.file.FileSet fileSet, 
//                                          org.apache.jena.tdb.base.record.RecordFactory recordfactory, 
//                                          org.apache.jena.tdb.index.IndexParams indexParams) {
//            
//            FileSet fs = new FileSet(fileSet.getBasename()) ;
//            RecordFactory rf = new RecordFactory(recordfactory.keyLength(), recordfactory.valueLength()) ;
//            // Override with defaults.
//            RangeIndex ridx = rangeIndexBuilderDBoe.buildRangeIndex(fs, rf, IndexConst.getDftParams()) ;
//            return new AdapterRangeIndex(ridx) ;
//        }
//    } ;

//    private static TupleIndexBuilder tupleIndexBuilder     = new BuilderStdDB.TupleIndexBuilderStd(rangeIndexBuilderTDB) ;
//    
//    public static TupleIndex createTupleIndex(Location loc, String order, String name) {
//        FileSet fs = new FileSet(loc, order) ;
//        ColumnMap cMap = new ColumnMap(Names.primaryIndexTriples, order) ;   // Primary order.
//        TupleIndex tupleIndex = tupleIndexBuilder.buildTupleIndex(fs, cMap, name, null) ;
//        return tupleIndex ;
//    }
}

