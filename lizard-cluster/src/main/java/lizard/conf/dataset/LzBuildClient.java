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

import java.util.ArrayList ;
import java.util.List ;

import lizard.adapters.A ;
import lizard.api.TxnClient ;
import lizard.cluster.Cluster ;
import lizard.conf.Config ;
import lizard.query.LzDataset ;
import lizard.query.QuackLizard ;
import migrate.TupleIndexEmpty ;

import org.apache.jena.query.ARQ ;
import org.apache.jena.query.Dataset ;
import org.apache.jena.query.DatasetFactory ;
import org.apache.jena.sparql.core.DatasetGraph ;
import org.apache.jena.sparql.engine.main.QC ;
import org.apache.jena.sparql.engine.optimizer.reorder.ReorderLib ;
import org.apache.jena.tdb.base.file.FileSet ;
import org.apache.jena.tdb.base.record.RecordFactory ;
import org.apache.jena.tdb.index.* ;
import org.apache.jena.tdb.setup.StoreParams ;
import org.apache.jena.tdb.store.* ;
import org.apache.jena.tdb.store.nodetable.NodeTable ;
import org.apache.jena.tdb.store.tupletable.TupleIndex ;
import org.apache.jena.tdb.sys.DatasetControl ;
import org.apache.jena.tdb.sys.DatasetControlMRSW ;
import org.apache.jena.tdb.sys.Names ;

import org.apache.jena.atlas.lib.ColumnMap ;
import org.apache.jena.atlas.lib.StrUtils ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.seaborne.dboe.base.file.Location ;
import org.seaborne.dboe.transaction.Transactional ;
import org.seaborne.dboe.transaction.txn.* ;
import org.seaborne.dboe.transaction.txn.journal.Journal ;
import org.slf4j.Logger ;

/** Build dataset related structutes - client side, that is, query server */
public class LzBuildClient
{
    static int counter = 0 ;
    public static DatasetGraph datasetGraph(LzDataset lz, Location location) {
        List<TransactionalComponent> tComp = new ArrayList<>() ;
        // Add the global lock manager.
        tComp.add(new TransactionalComponentZkLock()) ;
        ComponentId base = ComponentId.allocLocal() ;
        lz.getComponents().forEach(c->{
            if ( c instanceof TxnClient.Accessor ) {
                TxnClient<?> wire = ((TxnClient.Accessor)c).getWireClient() ;
                int i = ++counter ;
                ComponentId cid = ComponentId.alloc(base, c.getLabel() , i) ;
                TransactionalComponent x = new TransactionalComponentRemote<>(cid, wire) ;
                tComp.add(x) ;
            }
        });
        
        Journal journal = Journal.create(location) ;
        TransactionCoordinator transCoord = new TransactionCoordinator(journal, tComp) ;
        // Use Zookeeper for transaction ids.
        transCoord.setTxnIdGenerator(Cluster.getTxnIdGenerator());
        Transactional transactional = new TransactionalBase(transCoord) ;

        DatasetGraphTDB dsgtdb = lz.getDataset() ;
        DatasetGraph dsg = new DatasetGraphLz(dsgtdb, transactional, transCoord) ;
        return dsg ;
    }

    public static Dataset dataset(LzDataset lz, Location location) {
        DatasetGraph dsg = datasetGraph(lz, location) ;
        return DatasetFactory.create(dsg) ;
    }
    
    static Logger logConf = Config.logConf ;

    // KEEP.
    public static DatasetGraphTDB createDataset(org.seaborne.dboe.base.file.Location _location, TupleIndex[] tripleIndexes, NodeTable nodeTable) {
        org.apache.jena.tdb.base.file.Location location = A.convert(_location) ;
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
        
        DatasetGraphTDB dsg = new DatasetGraphTDB(tableTriples, tableQuads, prefixes, ReorderLib.fixed(), null) ;
        
        dsg.getContext().set(ARQ.optFilterPlacementBGP, false);
        QC.setFactory(dsg.getContext(), QuackLizard.factoryLizard) ;
        return dsg ;
    }
}
