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

package lizard.api;

import java.util.Map ;
import java.util.concurrent.ConcurrentHashMap ;
import java.util.function.Supplier ;

import lizard.api.TLZ.TxnCtl ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.jena.query.ReadWrite ;
import org.apache.jena.system.Txn ;
import org.seaborne.dboe.transaction.txn.TransactionCoordinatorState ;
import org.seaborne.dboe.transaction.txn.TransactionalSystem ;

// Support for the server side. 
public abstract class TxnHandler extends NodeHandler implements TxnCtl.Iface {
    private final static boolean LOG_TXN = true ;

    protected final TransactionalSystem transactional ;
    
    protected TxnHandler(TransactionalSystem transactional) {
        this.transactional = transactional ;
    }
    
    private Map<Long, TransactionCoordinatorState> transactions = new ConcurrentHashMap<>() ; 
    private static long NO_WRITER = -1 ;
    // Volatile because a differnt thread server side
    // may be used to service the next request.  While this is
    // per-thread-transaction at the client, it isn't at the server.
    private volatile long currentWriter = NO_WRITER ;
    
    // readers
    //private 

    private void setCurrentWriter(long currentWriter) {
        this.currentWriter = currentWriter ;
    }

    protected long getCurrentWriter() {
        return currentWriter ;
    }
    
    protected boolean activeWriteTransaction() {
        return getCurrentWriter() > 0 ; 
    }

    @Override
    public void txnBeginRead(long txnId) {
        txnBegin(txnId, ReadWrite.READ) ;
    }

    @Override
    public void txnBeginWrite(long txnId) {
        if ( getCurrentWriter() >= 0 )
            log().error("TxnHandler:txnBeginWrite - already in a W transaction");
        txnBegin(txnId, ReadWrite.WRITE) ;
        setCurrentWriter(txnId) ;
    }

    // synchronized?  This is RPC and per connection so client end is responsible. 
    private void txnBegin(long txnId, ReadWrite mode) {
        if ( LOG_TXN )
            FmtLog.info(logtxn(), "[Txn:%s:%d] begin[%s]", getLabel(), txnId, mode) ;
        transactional.begin(mode) ;
        TransactionCoordinatorState txnState = transactional.detach() ;
        if ( transactions.containsKey(txnId) )
            log().warn("TxnHandler - Transaction already exists") ; 
        transactions.put(txnId, txnState) ;
    }
    
    @Override
    public void txnPrepare(long txnId) {
        if ( LOG_TXN )
            FmtLog.info(logtxn(), "[Txn:%s:%d] prepare", getLabel(), txnId) ;
        internal_txnAction(txnId, () -> transactional.commitPrepare() ) ;
    }

    @Override
    public void txnCommit(long txnId) {
        if ( LOG_TXN )
            FmtLog.info(logtxn(), "[Txn:%s:%d] commit", getLabel(), txnId) ;
        internal_txnAction(txnId, () -> { transactional.commitExec() ; }) ;
        
    }

    @Override
    public void txnAbort(long txnId) {
        if ( LOG_TXN )
            FmtLog.info(logtxn(), "[Txn:%s:%d] abort", getLabel(), txnId) ; 
        internal_txnAction(txnId, () -> transactional.abort()) ;
    }

    @Override
    public void txnEnd(long txnId) {
        if ( LOG_TXN )
            FmtLog.info(logtxn(), "[Txn:%s:%d] end", getLabel(), txnId) ;
        if ( txnId > 0 )
            internal_txnAction(txnId, () -> transactional.end()) ;
        setCurrentWriter(NO_WRITER) ;
    }

    /** Perform an action inside the transaction {@code txnId}
     * This is for transaction lifecycl operations, not client
     * actions.  
     * @see #txnRead
     * @see #txnWrite
     */
    private void internal_txnAction(long txnId, Runnable action) {
        TransactionCoordinatorState s = transactions.get(txnId) ;
        //( s == null ) if (1) no begin (2) auto-ended
        // Do action without a transaction. 

        if ( s != null )
            transactional.attach(s); 
        action.run() ;
        if ( s == null )
            return ;
        s = transactional.detach() ;
        if ( s == null )
            // End.
            transactions.remove(txnId) ;
        else
            transactions.put(txnId, s) ;
    }
    

    // Used?
    /** Perform an action inside the transaction {@code txnId}; return an object */ 
    private <X> X internal_txnActionReturn(long txnId, Supplier<X> action) {
        TransactionCoordinatorState s = transactions.get(txnId) ;
        transactional.attach(s); 
        X x = action.get() ;
        s = transactional.detach() ;
        transactions.put(txnId, s) ;
        return x ;
    }

    private void autoCommit(long txnId, Runnable action, ReadWrite mode) {
        FmtLog.warn(logtxn(), "[Txn:%s:%d] autocommit(%s)", getLabel(), txnId, mode) ;
        switch(mode) {
            case READ : Txn.execRead(transactional, action); break ;
            case WRITE : Txn.execWrite(transactional, action); break ;
        }
    }

    private <X> X autoCommitReturn(long txnId, Supplier<X> action, ReadWrite mode) {
        FmtLog.warn(logtxn(), "[Txn:%s:%d] autocommit(%s)", getLabel(), txnId, mode) ;
        switch(mode) {
            case READ : return Txn.execReadRtn(transactional, action) ;
            case WRITE : return Txn.execWriteRtn(transactional, action) ;
        }
        return null ;   // Dummy
    }

    /** Perform a transaction,use autocommitif necessary */ 
    protected void txnAlways(long txnId, ReadWrite mode, Runnable action) {
        if ( ! transactions.containsKey(txnId) ) {
            autoCommit(txnId, action, mode); 
            return ;
        }
        internal_txnAction(txnId, action); 
    }

    /** Perform a write action and return a value; either inside the current writer or inside a new writer */ 
    protected <X> X txnAlwaysReturn(long txnId, ReadWrite mode, Supplier<X> action) {
        if ( ! transactions.containsKey(txnId) )
            return autoCommitReturn(txnId, action, mode) ; 
        return internal_txnActionReturn(txnId, action); 
    }
}

