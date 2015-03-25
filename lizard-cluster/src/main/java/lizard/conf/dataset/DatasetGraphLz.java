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

package lizard.conf.dataset;

import com.hp.hpl.jena.query.ReadWrite ;
import com.hp.hpl.jena.sparql.core.DatasetGraph ;
import com.hp.hpl.jena.sparql.core.DatasetGraphTrackActive ;
import com.hp.hpl.jena.sparql.util.Context ;
import com.hp.hpl.jena.tdb.store.DatasetGraphTDB ;

import org.seaborne.dboe.transaction.Transactional ;
import org.seaborne.dboe.transaction.txn.TransactionCoordinator ;

// Needs to be the local DatasetGraphTDB + a cluster-wise txn controller. 
public class DatasetGraphLz extends DatasetGraphTrackActive {
    private final DatasetGraphTDB dsg ;
    private final Transactional dboe ;
    private final TransactionCoordinator transCoord ;
    
    @Override
    protected DatasetGraph get() { return dsg ; } 
    
    public DatasetGraphLz(DatasetGraphTDB dsg, Transactional dboe, TransactionCoordinator transCoord) {
        this.dsg = dsg ;
        this.dboe = dboe ;
        this.transCoord = transCoord ;
    }
    
    @Override
    public Context getContext() {
        return dsg.getContext()  ;
    }

    @Override
    protected void checkActive() {}

    @Override
    protected void checkNotActive() {}

    @Override
    public boolean isInTransaction() {
        return false ;
    }

    @Override
    protected void _begin(ReadWrite readWrite) {
        dboe.begin(readWrite) ;
    }

    @Override
    protected void _commit() { dboe.commit(); }

    @Override
    protected void _abort() { dboe.abort(); }

    @Override
    protected void _end() { dboe.end(); }

    @Override
    protected void _close() {}

    public DatasetGraphTDB getDatasetTDB() {
        return dsg ;
    }

    public Transactional getTransactional() {
        return dboe ;
    }
    
    public TransactionCoordinator getCoordinator() {
        return transCoord ;
    }

}
