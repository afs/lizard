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

import org.apache.thrift.TException ;
import org.apache.thrift.transport.TServerTransport ;
import org.apache.thrift.transport.TTransport ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

/** This is a very raw ThriftServer that accepts connections and passes to a handler.
 *  The handler is expecting to engage in a long term interation with client,
 *  possibly stream based.
 */
public class ThriftServerAcceptFork extends ThriftServer {
    
    /** Handle a new Connection */
    public interface Handler { public void handle(TTransport transport) ; }
    
    private static Logger log = LoggerFactory.getLogger(ThriftServerAcceptFork.class) ; 
    
    private final Handler handler ;

    public ThriftServerAcceptFork(int port, Handler handler) {
        super(null, port) ;
        this.handler = handler ;
    }
    
    @Override
    public void start() {
        super.start() ;
        new Thread(() -> server(serverTransport)).start() ;
    }
    
    @Override
    public void stop() {
        serverTransport.close() ;
        super.stop() ;
    }

    // OLD
    // Better - use one the TServer implementations with custom TProcessor
    public void server(TServerTransport serverTransport) {
        for ( ;; ) 
            try {
                TTransport transport = serverTransport.accept() ;
                log.debug("Connection: "+transport) ;
                new Thread(()->handler.handle(transport)).start() ;
            } 
        catch (TException e) {}
        catch (Exception e) { e.printStackTrace(); }
    }
}
