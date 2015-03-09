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

package lizard.index;

import java.util.Iterator ;

import lizard.api.TLZlib ;
import lizard.api.TLZ.TLZ_IdxReply ;
import lizard.api.TLZ.TLZ_IdxRequest ;
import lizard.api.TLZ.TLZ_ShardIndex ;
import lizard.api.TLZ.TLZ_TupleNodeId ;
import lizard.comms.thrift.ThriftLib ;
import lizard.comms.thrift.ThriftServer ;
import lizard.system.ComponentBase ;
import org.apache.jena.atlas.lib.Tuple ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.thrift.TException ;
import org.apache.thrift.protocol.TProtocol ;
import org.apache.thrift.transport.TTransport ;
import org.apache.thrift.transport.TTransportException ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import com.hp.hpl.jena.tdb.store.NodeId ;
import com.hp.hpl.jena.tdb.store.tupletable.TupleIndex ;

// XXX Needs efficiency attention.
public class TServerIndex extends ComponentBase
{
    private static Logger log = LoggerFactory.getLogger(TServerIndex.class) ;
    
    private ThriftServer server ;
    
    public static TServerIndex create(int port, TupleIndex index) {
        return new TServerIndex(port, index) ;
    }
    
    private TServerIndex(int port, TupleIndex index) {
        setLabel("IndexServer["+port+"]") ;
        server = new ThriftServer(port, new Handler(getLabel(), index)) ;
    }
    
    @Override
    public void start() {
        if ( server.isRunning() ) {
            FmtLog.debug(log, "Already started (port=%d)", server.getPort()) ;
            return ;
        }
        FmtLog.info(log, "Start index server: port = %d", server.getPort()) ;
        server.start() ;
        super.start() ; 
    }
    
    static class Handler implements ThriftServer.Handler {
        private final TupleIndex index ;
        private final String label ;

        public Handler(String label, TupleIndex index) {
            this.label = label ;
            this.index = index ;
        }
        
        @Override
        public void handle(final TTransport transport) {
            try { 
                TProtocol protocol = ThriftLib.protocol(transport) ;
                TLZ_IdxRequest request = new TLZ_IdxRequest() ;
                TLZ_IdxReply reply = new TLZ_IdxReply() ;

                for(;;) {
                    try {
                        request.read(protocol) ;
                    } catch (TTransportException ex) {
                        // failed to read a request. Includes the other end going away.
                        FmtLog.info(log, label+": End TServerIndex connection") ;
                        break ;
                    }
                    long id = request.getRequestId() ;
                    FmtLog.info(log, "[%d] Index request : %s", id, request) ;
                    execute(id, request, reply) ;
                    reply.setRequestId(id) ;
                    reply.write(protocol) ;
                    // XXX !!!!
                    protocol.getTransport().flush();
                    request.clear() ; 
                    reply.clear() ;
                }
                
            } catch (TException ex) {
                FmtLog.error(log, ex, "TException: %s", ex.getMessage()) ;
            } finally {
                try { transport.close() ; } catch (Throwable th) {}
            }
        }
        
        // Use Thrift RPC?
        private void execute(long id, TLZ_IdxRequest request, TLZ_IdxReply reply) {
            TLZ_ShardIndex ref = request.getIndex() ;
            if ( index == null ) {
                FmtLog.error(log, "[%d] No index table here for %s", id, ref) ;
                return ; 
            }
            
            if ( request.isSetPattern() ) {
                Tuple<NodeId> pattern = TLZlib.build(request.getPattern()) ;
                
                FmtLog.info(log, "[%d] find %s [%s]", id, index.getName(), index.getName()) ;
                Iterator<Tuple<NodeId>> iter = index.find(pattern) ;

                int count = 0 ;
                while(iter.hasNext()) {
                    count++ ;
                    Tuple<NodeId> r = iter.next() ;
                    TLZ_TupleNodeId r2 = TLZlib.build(r) ;
                    reply.addToTuples(r2) ;
                }
                return ;
            }
            
            if ( request.isSetSubPreds() ) {
                return ;
            }

            if ( request.isSetAddTuple() ) {
                // Replace
                Tuple<NodeId> tuple = TLZlib.build(request.getAddTuple()) ;
                boolean b = index.add(tuple) ;
                reply.setYesOrNo(b) ;
                return ;
            }
            if ( request.isSetDeleteTuple() ) {
                // Replace
                Tuple<NodeId> tuple = TLZlib.build(request.getAddTuple()) ;
                boolean b = index.delete(tuple) ;
                reply.setYesOrNo(b) ;
                return ;
            }
            
            if ( request.isSetPing() ) {
                FmtLog.info(log, "[%d] ping", id) ;
                return ;
            }
            
            if ( request.isSetTxnBeginRead() ) {}
            if ( request.isSetTxnBeginWrite() ) {}
            
            FmtLog.error(log, "[%d] execute: Unrecognized request: %s", id, request) ;
        }
    }
}
