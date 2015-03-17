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

import lizard.api.TLZ.TxnCtl ;
import lizard.api.TLZ.TxnCtl.Client ;
import lizard.system.ComponentTxn ;
import lizard.system.LizardException ;
import org.apache.thrift.TException ;

import com.hp.hpl.jena.query.ReadWrite ;

/** Client side transaction support for a single component
 *  that forwards to its remote pair.  
 *  Inherit or use as a hidden member. 
 */

public class TxnClient implements ComponentTxn {
    private final Client client ;
    private final ThreadLocal<Long> txn = new ThreadLocal<>() ;

    public TxnClient(TxnCtl.Client client) {
        this.client = client ;
    }
    
    @Override
    public void begin(ReadWrite mode) {
        exec(() -> {
            long z = 0 ;
            switch(Objects.requireNonNull(mode)) {
                case READ :
                    z = client.txnBeginRead() ;
                    break ;
                case WRITE :
                    z = client.txnBeginWrite() ;
                    break ;
            }
            txn.set(z);
        }) ;
    }

    @Override
    public void prepare() {
        exec(() -> client.txnPrepare(txn.get().longValue())) ;
    }

    @Override
    public void commit() {
        exec(() -> client.txnPrepare(txn.get().longValue())) ;

    }

    @Override
    public void abort() {
        exec(() -> client.txnPrepare(txn.get().longValue())) ;
    }

    @Override
    public void end() {
        Long z = txn.get() ;
        if ( z != null )
            exec(() -> client.txnPrepare(txn.get().longValue())) ;
        txn.set(null) ;
        txn.remove() ;
    }
    
    @FunctionalInterface
    interface ThriftRunnable { void run() throws TException ; }
    
    private static void exec(ThriftRunnable runnable) {
        try { runnable.run() ; } 
        catch (TException ex)   { throw new LizardException(ex) ; }
        catch (Exception ex)    { throw new LizardException("Unexpected exception: "+ex.getMessage(), ex) ; }
    }
}

