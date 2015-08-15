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

import java.util.Objects ;
import java.util.concurrent.atomic.AtomicLong ;

import lizard.api.TLZ.TxnCtl ;
import lizard.comms.CommsException ;
import lizard.comms.thrift.ThriftLib ;
import lizard.comms.thrift.ThriftLib.ThriftCallable ;
import lizard.comms.thrift.ThriftLib.ThriftRunnable ;
import lizard.system.ComponentBase ;
import lizard.system.ComponentTxn ;
import lizard.system.LizardException ;

import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.jena.atlas.logging.Log ;
import org.apache.jena.query.ReadWrite ;
import org.slf4j.Logger ;

public abstract class TxnClient<X extends TxnCtl.Client> extends ComponentBase implements ComponentTxn {
    private final static boolean LOG_TXN = true ; 

    public interface WireClient { TxnClient<?> getWireClient() ; }
    
    private static AtomicLong requestCounter = new AtomicLong(0) ;
    private ThreadLocal<Long> currentTxnId = new ThreadLocal<>() ;
    protected X rpc = null ;
    // Synchronize thrift operations per client.
    // XXX Do this at the server!
    private final Object lock = new Object() ;
    
    protected TxnClient() { }

    protected void setRPC(X rpcx) {
        if ( this.rpc != null )
            Log.warn(this, "RPC handler for transactions already set") ;
        this.rpc = rpcx ;
    }
    
    protected long allocRequestId() {
        return requestCounter.incrementAndGet() ;
    }

    protected long getTxnId() {
        Long z = currentTxnId.get() ;
        if ( z == null )
            return -99 ;
        return z ;
    }

    protected abstract Logger getLog() ;
    protected abstract void resetConnection() ;
    
    @Override
    public void begin(long txnId, ReadWrite mode) {
        if ( LOG_TXN )
            FmtLog.info(getLog(), "[Txn:%s:%d] begin/%s", getLabel(), txnId, mode.toString().toLowerCase());
        
        try { rpcBegin(txnId, mode) ; }
        catch (LizardException ex) {
            // Attempt to restart.
            rpc = null ;
            resetConnection() ;
            rpcBegin(txnId, mode) ;
        }

        currentTxnId.set(txnId) ;
    }

    private void rpcBegin(long txnId, ReadWrite mode) {
        ThriftLib.exec(lock, ()-> {
            switch(Objects.requireNonNull(mode)) {
                case READ :
                    rpc.txnBeginRead(txnId) ; break ;        
                case WRITE :
                    rpc.txnBeginWrite(txnId) ; break ;
            }
        }) ;
    }
        
    @Override
    public void prepare() {
       completeAsyncOperations() ;
        if ( LOG_TXN )
            FmtLog.info(getLog(), "[Txn:%s:%d] prepare", getLabel(), getTxnId());
        ThriftLib.exec(lock, ()-> rpc.txnPrepare(getTxnId())) ;
    }

    @Override
    public void commit() {
        if ( LOG_TXN )
            FmtLog.info(getLog(), "[Txn:%s:%d] commit", getLabel(), getTxnId());
        ThriftLib.exec(lock, ()-> rpc.txnCommit(getTxnId())) ;
    }

    @Override
    public void abort() {
        if ( LOG_TXN )
            FmtLog.info(getLog(), "[Txn:%s:%d] abort", getLabel(), getTxnId());
        ThriftLib.exec(lock, ()-> rpc.txnAbort(getTxnId())) ;
    }

    @Override
    public void end() {
        Long z = getTxnId() ;
        /*if ( z != null )*/ {
            if ( z < 0 ) {
                if ( LOG_TXN )
                    FmtLog.info(getLog(), "[Txn:%s:%d] end (no call)", getLabel(), getTxnId());
            } else { 
                if ( LOG_TXN )
                    FmtLog.info(getLog(), "[Txn:%s:%d] end", getLabel(), getTxnId());
                ThriftLib.exec(lock, () -> rpc.txnEnd(getTxnId())) ;
            }
            currentTxnId.set(null) ;
        }

        // Because get may have created it. 
        currentTxnId.remove() ;
    }
    
    protected <T> T call(String label, ThriftCallable<T> action) {
        checkRunning() ;
        completeAsyncOperations();
        try { return ThriftLib.call(lock, action) ; } 
        catch (Exception ex) {
            FmtLog.error(getLog(), ex, label) ;
            throw new LizardException(ex) ;
        }
    }
    
    protected void exec(String label, ThriftRunnable action) {
        checkRunning() ;
        completeAsyncOperations();
        try { ThriftLib.exec(lock, action) ; } 
        catch (Exception ex) {
            FmtLog.error(getLog(), ex, label) ;
            throw new LizardException(ex) ;
        }
    }
    
    private final Async async = new Async(1, 2) ;
    
    protected void execAsync(String label, ThriftRunnable action) {
        async.execAsync(lock, action);
    }
    
    private void completeAsyncOperations() {
        async.completeAsyncOperations();
    }

    private void reduceAsyncQueue(int reduceSize) {
        async.reduceAsyncQueue(reduceSize);
    }

    private void checkRunning() {
        if ( ! isRunning() )
            throw new CommsException("not running") ; 
    }

    public void remoteStop() {
        exec("remoteStop", ()->rpc.stop()) ;
    }

    public void ping() {
        exec("ping", ()->rpc.ping()) ;
    }
}


