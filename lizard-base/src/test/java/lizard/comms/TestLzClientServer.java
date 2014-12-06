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

package lizard.comms;

import java.util.concurrent.atomic.AtomicInteger ;
import java.util.concurrent.atomic.AtomicReference ;

import lizard.api.TLZ.TLZ_Ping ;
import lizard.comms.thrift.ThriftClient ;
import lizard.comms.thrift.ThriftLib ;
import lizard.comms.thrift.ThriftServer ;
import lizard.comms.thrift.ThriftServer.Handler ;
import lizard.test.LzBaseTest ;
import org.apache.thrift.TException ;
import org.apache.thrift.protocol.TProtocol ;
import org.apache.thrift.transport.TMemoryBuffer ;
import org.apache.thrift.transport.TTransport ;
import org.junit.Test ;

/**
 * Test Client/Server
 */
public class TestLzClientServer extends LzBaseTest {
    
    public static final int test_port = 12345 ;
    public static AtomicInteger handlerErrors = new AtomicInteger(0) ;
    public static AtomicInteger handlerWarnings = new AtomicInteger(0) ;
    public static AtomicReference<TException> handlerException = new AtomicReference<>(null)  ;
    
    @Test public void wire_01() throws Exception {
        Handler handler = new Handler() {
            @Override
            public void handle(TTransport transport) {
                try {
                    TProtocol proto = ThriftLib.protocol(transport) ;
                    TLZ_Ping p = new TLZ_Ping() ;
                    p.read(proto);
                    p.write(proto);
                    transport.flush();
                } catch (TException ex) {
                    handlerException.set(ex);
                }
            }
        };
        ThriftServer server = new ThriftServer(test_port, handler) ;
        
        server.start() ;
        
        ThriftClient c = new ThriftClient("localhost", test_port) ;
        c.start() ;
        TProtocol proto = c.protocol() ;
        TLZ_Ping p = new TLZ_Ping(1324) ;
        p.write(proto);
        proto.getTransport().flush() ;
        TLZ_Ping p2 = new TLZ_Ping() ;
        p2.read(proto);
        assertEquals(p.getMarker(), p2.getMarker()) ;
        assertNull(handlerException.get()) ;
        c.stop(); 
        server.stop() ;
        
    }

//    ThriftServer
//    ThriftClient

    private static TProtocol protocol() {
        TTransport transport = new TMemoryBuffer(1024) ;
        TProtocol proto = ThriftLib.protocol(transport) ;
        return proto ;
    }
}
