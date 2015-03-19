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
import lizard.comms.thrift.ThriftLib ;
import lizard.system.ComponentBase ;
import lizard.system.ComponentTxn ;

import com.hp.hpl.jena.query.ReadWrite ;

import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.jena.atlas.logging.Log ;
import org.slf4j.Logger ;

public abstract class TxnClient<X extends TxnCtl.Client> extends ComponentBase implements ComponentTxn {

    // request counter.
    private static AtomicLong requestCounter = new AtomicLong(0) ;
    // Thread's transaction.
    // XXX separate out beging per Client and system wide txnid.
    
    private ThreadLocal<Long> currentTxnId = new ThreadLocal<>() ;
    // Transaction interface.
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
    private final static boolean LOG_TXN = false ; 
    
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
            FmtLog.info(getLog(), "[Txn:%s:%d] aborts", getLabel(), getTxnId());
        ThriftLib.exec(()-> rpc.txnAbort(getTxnId())) ;
    }

    @Override
    public void end() {
        Long z = getTxnId() ;
        if ( z != null ) {
            if ( LOG_TXN )
                FmtLog.info(getLog(), "[Txn:%s:%d] end", getLabel(), getTxnId());
            ThriftLib.exec(() -> rpc.txnEnd(getTxnId())) ;
            currentTxnId.set(null) ;
        }

        // Because get may have created it. 
        currentTxnId.remove() ;
    }
}


