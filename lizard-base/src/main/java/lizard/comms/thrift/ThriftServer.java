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

package lizard.comms.thrift;

import lizard.system.Component ;
import lizard.system.ComponentBase ;
import lizard.system.LizardException ;
import org.apache.jena.atlas.logging.Log ;
import org.apache.jena.dboe.transaction.txn.TransactionalSystem ;
import org.apache.thrift.TException ;
import org.apache.thrift.transport.TServerSocket ;
import org.apache.thrift.transport.TServerTransport ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

/** Base of all servers */
public class ThriftServer extends ComponentBase implements Component {
    private static Logger log = LoggerFactory.getLogger(ThriftServer.class) ; 
    
    private final int port ;
    private final TransactionalSystem txnSystem ;
    protected final TServerTransport serverTransport ;

    public ThriftServer(TransactionalSystem txnSystem, int port) {
        this.txnSystem = txnSystem ; 
        this.port = port;
        try { serverTransport = new TServerSocket(port) ; }
        catch (TException e) { throw new LizardException(e) ; } 
    }
    
    public int getPort()                            { return port ; }
    public TransactionalSystem getTxnSystem()       { return txnSystem ; }
    
    @Override
    public void start() {
        if (super.isRunning() ) {
            Log.error(this, "Already started: "+getLabel()) ;
            return ; 
        }
        super.start() ;
    }
    
    @Override
    public void stop() {
        super.stop() ;
        serverTransport.close() ;
    }
}
