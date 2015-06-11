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
import java.util.concurrent.* ;
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

    public interface Accessor { TxnClient<?> getWireClient() ; }
    
    private static AtomicLong requestCounter = new AtomicLong(0) ;
    private ThreadLocal<Long> currentTxnId = new ThreadLocal<>() ;
    protected X rpc = null ;
    
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
    
    @Override
    public void begin(long txnId, ReadWrite mode) {
        if ( LOG_TXN )
            FmtLog.info(getLog(), "[Txn:%s:%d] begin/%s", getLabel(), txnId, mode.toString().toLowerCase());
        ThriftLib.exec(()-> {
            switch(Objects.requireNonNull(mode)) {
                case READ :
                    rpc.txnBeginRead(txnId) ; break ;        
                case WRITE :
                    rpc.txnBeginWrite(txnId) ; break ;
            }
        }) ;
        currentTxnId.set(txnId) ;
    }

    @Override
    public void prepare() {
       completeAsyncOperations() ;
        if ( LOG_TXN )
            FmtLog.info(getLog(), "[Txn:%s:%d] prepare", getLabel(), getTxnId());
        ThriftLib.exec(()-> rpc.txnPrepare(getTxnId())) ;
    }

    @Override
    public void commit() {
        if ( LOG_TXN )
            FmtLog.info(getLog(), "[Txn:%s:%d] commit", getLabel(), getTxnId());
        ThriftLib.exec(()-> rpc.txnCommit(getTxnId())) ;
    }

    @Override
    public void abort() {
        if ( LOG_TXN )
            FmtLog.info(getLog(), "[Txn:%s:%d] abort", getLabel(), getTxnId());
        ThriftLib.exec(()-> rpc.txnAbort(getTxnId())) ;
    }

    @Override
    public void end() {
        Long z = getTxnId() ;
        if ( z != null ) {
            if ( z < 0 ) {
                if ( LOG_TXN )
                    FmtLog.info(getLog(), "[Txn:%s:%d] end (no call)", getLabel(), getTxnId());
            } else { 
                if ( LOG_TXN )
                    FmtLog.info(getLog(), "[Txn:%s:%d] end", getLabel(), getTxnId());
                ThriftLib.exec(() -> rpc.txnEnd(getTxnId())) ;
            }
            currentTxnId.set(null) ;
        }

        // Because get may have created it. 
        currentTxnId.remove() ;
    }
    
    protected <T> T call(String label, ThriftCallable<T> action) {
        checkRunning() ;
        try { return ThriftLib.call(action) ; } 
        catch (Exception ex) {
          FmtLog.error(getLog(), ex, label) ;
          throw new LizardException(ex) ;
        }
    }
    
    protected void exec(String label, ThriftRunnable action) {
        completeAsyncOperations();
        try { ThriftLib.exec(action) ; } 
        catch (Exception ex) {
          FmtLog.error(getLog(), ex, label) ;
          throw new LizardException(ex) ;
        }
    }

    // ---- Async control
    
    private void completeAsyncOperations() {
        reduceAsyncQueue(0) ;
    }
    
    private void reduceAsyncQueue(int reduceSize) {
        while ( outstanding.size() > reduceSize ) {
            try { outstanding.take().get() ; }
            catch (Exception ex) {
                throw new LizardException("Exception taking from async queue", ex) ;
            } 
        }
    }
    
    private static int OutstandingQueueMax = 2 ;
    private static int BlockingQueueSize = OutstandingQueueMax+2 ;
    
    // This causes a single action to be outstanding at any one time.
    // i.e. a write transaction is still single-writer at destination
    // if sync and async are not mixed.
    // XXX Consider server side lockign, reconsider "1 thread" policy    
    private ExecutorService executorService = Executors.newFixedThreadPool(1) ;
    private BlockingQueue<Future<Void>> outstanding = new ArrayBlockingQueue<>(BlockingQueueSize) ;
    
    protected void execAsync(String label, ThriftRunnable action) {
        if ( false ) {
            // Do now, synchronously.
            // Development and debugging sue.
            exec(label, action);
            return ; 
        }
        
        reduceAsyncQueue(OutstandingQueueMax) ;
        
        // Block if queue too long.
        while ( outstanding.size() > 2 ) {
            try {
                Future<Void> task = outstanding.take() ;
                task.get() ;
            }
            catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } 
        }
        
        Future<Void> task = executorService.submit(()-> {
            try {
                action.run() ;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null ;
        }) ;
        outstanding.add(task) ;
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


