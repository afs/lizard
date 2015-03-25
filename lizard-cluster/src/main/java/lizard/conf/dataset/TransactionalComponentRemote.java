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

import java.nio.ByteBuffer ;
import java.util.concurrent.atomic.AtomicLong ;

import lizard.api.TxnClient ;
import lizard.conf.dataset.TransactionalComponentRemote.TxnRemoteState ;

import com.hp.hpl.jena.query.ReadWrite ;

import org.seaborne.dboe.transaction.txn.ComponentId ;
import org.seaborne.dboe.transaction.txn.TransactionalComponentLifecycle ;
import org.seaborne.dboe.transaction.txn.TxnId ;

/** Transactional management for a TxnClient - that is, 
 *  the client side of cluster point-to-point connection.
 */ 
public class TransactionalComponentRemote<X extends TxnClient<?>> extends TransactionalComponentLifecycle<TxnRemoteState> {
    // Local state is the Txn id.
    
    static class TxnRemoteState{
        TxnId txnId ;
        Long wireId ;
        TxnRemoteState(TxnId txnId, Long wireId) {
            this.txnId = txnId ; 
            this.wireId = wireId ; 
        }
        // The TxnId <-> i64 mapping.
    }
    
    private static AtomicLong protocolTxnId = new AtomicLong(1) ;
    private final X worker ;
    private ComponentId cid ;
    
    public TransactionalComponentRemote(ComponentId cid, X worker) {
        this.cid = cid ;
        this.worker = worker ;
    }
    
    @Override
    public ComponentId getComponentId() {
        return cid ;
    }

    @Override
    public void startRecovery() {}

    @Override
    public void recover(ByteBuffer ref) {}

    @Override
    public void finishRecovery() {}

    @Override
    public void cleanStart() {}

    @Override
    protected TxnRemoteState _begin(ReadWrite readWrite, TxnId txnId) {
        // The protocol uses a 64 bit number.
        long x = protocolTxnId.incrementAndGet() ;
        worker.begin(x, readWrite) ;
        return new TxnRemoteState(txnId, x) ;
    }

    @Override
    protected ByteBuffer _commitPrepare(TxnId txnId, TxnRemoteState state) {
        // Local details.
        worker.prepare(); 
        return null ;   // Long to bytes. 
    }

    @Override
    protected void _commit(TxnId txnId, TxnRemoteState state) {
        worker.commit(); 
    }

    @Override
    protected void _commitEnd(TxnId txnId, TxnRemoteState state) { 
        // Remove local details.
    }

    @Override
    protected void _abort(TxnId txnId, TxnRemoteState state) {
        worker.abort(); 
    }

    @Override
    protected void _complete(TxnId txnId, TxnRemoteState state) {
     // Remove local details.
        worker.end() ; 
    }

    @Override
    protected void _shutdown() {
        worker.end() ;
    }
}

