/*
 *  Copyright 2014 Andy Seaborne
 *
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
 */

package lizard.comms.thrift;

import lizard.system.Component ;
import lizard.system.ComponentBase ;
import lizard.system.LizardException ;
import org.apache.jena.atlas.logging.Log ;
import org.apache.thrift.TException ;
import org.apache.thrift.transport.TServerSocket ;
import org.apache.thrift.transport.TServerTransport ;
import org.apache.thrift.transport.TTransport ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

public class ThriftServer extends ComponentBase implements Component {
    
    /** handle a new conenction */
    public interface Handler { public void handle(TTransport transport) ; }
    
    private static Logger log = LoggerFactory.getLogger(ThriftServer.class) ; 
    
    private final int port ;
    private final Handler handler ;
    private final TServerTransport serverTransport ;

    public ThriftServer(int port, Handler handler) {
        this.port = port;
        this.handler = handler ;
        try { serverTransport = new TServerSocket(port) ; }
        catch (TException e) { throw new LizardException(e) ; } 
    }
    
    public int getPort() { return port ; }
    
    @Override
    public void start() {
        if (super.isRunning() ) {
            Log.fatal(this, "Already started (port="+getPort()+")") ;
            return ; 
        }
        log.debug("Start: port = "+port) ;
        new Thread(() -> server(serverTransport)).start() ;
    }
    
    public void server(TServerTransport serverTransport) {
        for ( ;; ) 
            try {
                TTransport transport = serverTransport.accept() ;
                log.debug("Connection: "+transport) ;
                new Thread(()->handler.handle(transport)).start() ;
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
