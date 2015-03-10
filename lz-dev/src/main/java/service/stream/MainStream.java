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

import lizard.comms.thrift.ThriftLib ;

import org.apache.jena.atlas.lib.Lib ;
import org.apache.thrift.protocol.TMessage ;
import org.apache.thrift.protocol.TProtocol ;
import org.apache.thrift.transport.TSocket ;
import org.apache.thrift.transport.TTransport ;

public class MainStream {
    public static void main(String[] args) throws Exception {
        int port = 1122 ;
        RawProcessor.server(port);
        try {
            TTransport trans1 = new TSocket("localhost", port) ;
            TProtocol proto = ThriftLib.protocol(trans1) ;
            trans1.open();
            TMessage msg = new TMessage() ;
            proto.writeMessageBegin(msg) ;
            proto.writeI64(1L);
            proto.writeI32(2);
            proto.writeMessageEnd();
            trans1.flush(); // ******
            proto.readMessageBegin() ;
            int r = proto.readI32() ;
            System.out.printf(" -> %d\n", r) ;
            proto.readMessageEnd() ;
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err) ;
        }
        finally {
            Lib.sleep(1000);
            System.exit(0) ;
        }
  
        
    }
}

