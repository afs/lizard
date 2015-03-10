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

package rpc;

import lizard.comms.thrift.ThriftLib ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.apache.thrift.protocol.TCompactProtocol ;
import org.apache.thrift.protocol.TProtocol ;
import org.apache.thrift.server.TServer ;
import org.apache.thrift.server.TThreadPoolServer ;
import org.apache.thrift.transport.* ;
import rpc.thrift.S1 ;

public class MainRPC {

    static { LogCtl.setLog4j(); }
    public static void main(String[] args) throws Exception {
        server() ;
        
        try {
            TTransport trans1 = new TSocket("localhost", 9091) ;
            TProtocol proto = ThriftLib.protocol(trans1) ;
            
            trans1.open();

            S1.Client client = new S1.Client(proto) ;
            client.ping(); 
            long x = client.inc(55) ;
            System.out.println("x="+x) ;
            System.out.println("DONE") ;
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err) ;
        }
        finally {
            System.exit(0) ;
        }
    }

    private static void server() {
        S1.Iface handler = new S1_Server() ;
        S1.Processor<S1.Iface> processor = new S1.Processor<S1.Iface>(handler);
        
        new Thread(()-> {
            try {
                TServerTransport serverTransport = new TServerSocket(9091);
                TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverTransport) ;
                args.processor(processor) ;
                args.inputProtocolFactory(new TCompactProtocol.Factory()) ;
                args.outputProtocolFactory(new TCompactProtocol.Factory()) ;
                TServer server = new TThreadPoolServer(args);
                System.out.println("Starting the server...");
                server.serve();
              } catch (Exception e) {
                e.printStackTrace();
              }
        }) .start() ;
    }
}
