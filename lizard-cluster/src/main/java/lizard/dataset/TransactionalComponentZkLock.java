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

package lizard.dataset;

import lizard.cluster.Cluster ;
import org.apache.jena.dboe.transaction.txn.ComponentId ;
import org.apache.jena.dboe.transaction.txn.TransactionalComponentBase ;
import org.apache.jena.dboe.transaction.txn.TxnId ;
import org.apache.jena.query.ReadWrite ;

final public class TransactionalComponentZkLock extends TransactionalComponentBase<Object> {
    
    public TransactionalComponentZkLock(ComponentId id) {
        super(id) ;
    }

    @Override
    protected Object _begin(ReadWrite readWrite, TxnId txnId) {
        if ( readWrite == ReadWrite.WRITE )
            Cluster.acquireWriteLock() ;
        return null ;
    }

    @Override
    protected void _complete(TxnId txnId, Object state) {
        if ( getReadWriteMode() == ReadWrite.WRITE )
            Cluster.releaseWriteLock() ;
    }
}
