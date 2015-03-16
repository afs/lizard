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

import lizard.conf.dataset.DatasetBuilderLizard ;
import lizard.query.QuackLizard ;
import org.apache.jena.atlas.lib.StrUtils ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.slf4j.Logger ;

import com.hp.hpl.jena.query.ARQ ;
import com.hp.hpl.jena.sparql.engine.main.QC ;
import com.hp.hpl.jena.sparql.engine.optimizer.reorder.ReorderLib ;
import com.hp.hpl.jena.tdb.base.file.FileSet ;
import com.hp.hpl.jena.tdb.base.file.Location ;
import com.hp.hpl.jena.tdb.base.record.RecordFactory ;
import com.hp.hpl.jena.tdb.index.* ;
import com.hp.hpl.jena.tdb.setup.StoreParams ;
import com.hp.hpl.jena.tdb.store.DatasetGraphTDB ;
import com.hp.hpl.jena.tdb.store.DatasetPrefixesTDB ;
import com.hp.hpl.jena.tdb.store.QuadTable ;
import com.hp.hpl.jena.tdb.store.TripleTable ;
import com.hp.hpl.jena.tdb.store.nodetable.NodeTable ;
import com.hp.hpl.jena.tdb.store.tupletable.TupleIndex ;
import com.hp.hpl.jena.tdb.sys.DatasetControl ;
import com.hp.hpl.jena.tdb.sys.DatasetControlMRSW ;

/** Build dataset related structutes - client side, that is, query server */
public class LzBuildClient
{
    static Logger logConf = Config.logConf ;

    public static DatasetGraphTDB createDataset(Location location, TupleIndex[] tripleIndexes, NodeTable nodeTable) {
        DatasetControl policy = new DatasetControlMRSW() ;
        StoreParams params = StoreParams.getDftStoreParams() ;
        
        // Dummies because we rewrite.
        IndexBuilder indexBuilder                = new IndexBuilder() {
            @Override
            public Index buildIndex(FileSet fileSet, RecordFactory recordfactory, IndexParams indexParams) {
                return null ;
            }
        } ;
        
        RangeIndexBuilder rangeIndexBuilder      = new RangeIndexBuilder() {
            @Override
            public RangeIndex buildRangeIndex(FileSet fileSet, RecordFactory recordfactory, IndexParams indexParams) {
                return null ;
            }
        } ;
        
        //DatasetBuilderLizard dbb = new DatasetBuilderLizard(LzBuildClient.indexBuilder, LzBuildClient.rangeIndexBuilder) ;
        DatasetBuilderLizard dbb = new DatasetBuilderLizard(indexBuilder, rangeIndexBuilder) ;
        // Hack node table.
        DatasetPrefixesTDB prefixes = dbb.makePrefixTable(location, policy) ; 

        // Special.
        String indexes[] = new String[tripleIndexes.length] ;
        for ( int i = 0 ; i < indexes.length ; i++ ) {
            indexes[i] = tripleIndexes[i].getName() ;
        }

        TripleTable tableTriples = new TripleTable(tripleIndexes, nodeTable, policy) ;
        FmtLog.debug(logConf, "Triple table: %s :: %s", indexes[0], StrUtils.strjoin(",", indexes)) ;


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
}
