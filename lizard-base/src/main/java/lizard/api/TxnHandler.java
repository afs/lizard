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

package lizard.api;

import java.util.Map ;
import java.util.concurrent.ConcurrentHashMap ;
import java.util.function.Supplier ;

import lizard.api.TLZ.TxnCtl ;

import com.hp.hpl.jena.query.ReadWrite ;

import org.apache.jena.atlas.logging.FmtLog ;
import org.seaborne.dboe.transaction.Txn ;
import org.seaborne.dboe.transaction.txn.TransactionCoordinatorState ;
import org.seaborne.dboe.transaction.txn.TransactionalBase ;
import org.slf4j.Logger ;

// Support for the server side. 
public abstract class TxnHandler implements TxnCtl.Iface {
    //private static Logger log = LoggerFactory.getLogger(IndexHandler.class) ;
    protected final TransactionalBase transactional ;

    protected TxnHandler(TransactionalBase transactional) {
        this.transactional = transactional ;
    }
    
    private Map<Long, TransactionCoordinatorState> transactions = new ConcurrentHashMap<>() ; 
    private long currentWriter = -1 ;

    protected abstract Logger getLog() ;
    protected abstract String getLabel() ;
    protected long getCurrentWriter() { return currentWriter ; }
    
    protected boolean activeWriteTransaction() {
        return currentWriter > 0 ; 
    }

    @Override
    public void txnBeginRead(long txnId) {
        txnBegin(txnId, ReadWrite.READ) ;
    }

    @Override
    public void txnBeginWrite(long txnId) {
        if ( currentWriter >= 0 )
            getLog().warn("TServerIndex:txnBeginWrite - already in a W transaction");
        txnBegin(txnId, ReadWrite.WRITE) ;
        currentWriter = txnId ;
    }

    // synchronized?  This is RPC and per connection so client end is responsible. 
    private void txnBegin(long txnId, ReadWrite mode) {
        FmtLog.info(getLog(), "[Txn:%s:%d] begin[%s]", getLabel(), txnId, mode) ;
        transactional.begin(mode) ;
        TransactionCoordinatorState txnState = transactional.detach() ;
        if ( transactions.containsKey(txnId) )
            getLog().warn("TxnHandler - Transaction already exists") ; 
        transactions.put(txnId, txnState) ;
    }
    
    /** Perform an action inside the transaction {@code txnId} */ 
    protected void txnAction(long txnId, Runnable action) {
        TransactionCoordinatorState s = transactions.get(txnId) ;
        // May be null -> auto-ended 
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
    
    /** Perform an action inside the transaction {@code txnId}; return an object */ 
    protected <X> X txnActionReturn(long txnId, Supplier<X> action) {
        TransactionCoordinatorState s = transactions.get(txnId) ;
        transactional.attach(s); 
        X x = action.get() ;
        s = transactional.detach() ;
        transactions.put(txnId, s) ;
        return x ;
    }

    /** Perform a write action and return a value; either inside the current writer or inside a new writer */ 
    protected <X> X writeTxnAlwaysReturn(Supplier<X> action) {
        if ( activeWriteTransaction() )
            return txnActionReturn(getCurrentWriter(), action) ;
        else
            return Txn.executeWriteReturn(transactional, action) ;
    }

    /** Perform a write action and return a value */ 
    protected void writeTxnAlways(Runnable action) {
        if ( activeWriteTransaction() )
            txnAction(getCurrentWriter(), action) ;
        else
            Txn.executeWrite(transactional, action) ;
    }

    @Override
    public void txnPrepare(long txnId) {
        FmtLog.info(getLog(), "[Txn:%s:%d] prepare", getLabel(), txnId) ;
        txnAction(txnId, () -> getLog().info("Prepare!")) ;
    }

    @Override
    public void txnCommit(long txnId) {
        FmtLog.info(getLog(), "[Txn:%s:%d] commit", getLabel(), txnId) ;
        txnAction(txnId, () -> transactional.commit()) ;
    }

    @Override
    public void txnAbort(long txnId) {
        FmtLog.info(getLog(), "[Txn:%s:%d] abort", getLabel(), txnId) ; 
        txnAction(txnId, () -> transactional.abort()) ;
    }

    @Override
    public void txnEnd(long txnId) {
        FmtLog.info(getLog(), "[Txn:%s:%d] end", getLabel(), txnId) ; 
        txnAction(txnId, () -> transactional.end()) ;
    }
}

