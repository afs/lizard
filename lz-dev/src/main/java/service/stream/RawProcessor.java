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

package service.stream;

import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.thrift.TException ;
import org.apache.thrift.TProcessor ;
import org.apache.thrift.protocol.TCompactProtocol ;
import org.apache.thrift.protocol.TMessage ;
import org.apache.thrift.protocol.TProtocol ;
import org.apache.thrift.server.TServer ;
import org.apache.thrift.server.TThreadPoolServer ;
import org.apache.thrift.transport.TServerSocket ;
import org.apache.thrift.transport.TServerTransport ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

public class RawProcessor implements TProcessor {

    @Override
    public boolean process(TProtocol in, TProtocol out) throws TException {
        while(true) {
            in.readMessageBegin() ;
            long a1 = in.readI64() ;
            int a2 = in.readI32() ;
            in.readMessageEnd();
            System.out.printf("(%d, %d)", a1, a2) ;
            out.writeMessageBegin(new TMessage());
            out.writeI32((int)a1+a2); 
            out.writeMessageEnd();
            out.getTransport().flush(); // ******
            if ( false )
                break ;
        }
        
        
        return false ;
    }

    static Logger log = LoggerFactory.getLogger(RawProcessor.class) ;
    static void server(int port) {
        try {
            TServerTransport serverTransport = new TServerSocket(port) ;

            new Thread(()-> {
                try {
                    TProcessor processor = new RawProcessor() ;
                    TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverTransport) ;
                    args.processor(processor) ;
                    args.inputProtocolFactory(new TCompactProtocol.Factory()) ;
                    args.outputProtocolFactory(new TCompactProtocol.Factory()) ;
                    TServer server = new TThreadPoolServer(args);
                    FmtLog.info(log, "Started index server: port = %d", port) ;
                    //sema.release(1);
                    server.serve();
                    FmtLog.info(log, "Finished index server: port = %d", port) ;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }) .start() ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}

