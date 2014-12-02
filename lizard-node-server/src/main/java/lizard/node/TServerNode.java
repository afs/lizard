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

package lizard.node;


import lizard.api.TLZ.TLZ_Node ;
import lizard.api.TLZ.TLZ_NodeId ;
import lizard.api.TLZ.TLZ_NodeReply ;
import lizard.api.TLZ.TLZ_NodeRequest ;
import lizard.comms.thrift.ThriftLib ;
import lizard.comms.thrift.ThriftServer ;
import lizard.system.ComponentBase ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.jena.riot.out.NodeFmtLib ;
import org.apache.thrift.TException ;
import org.apache.thrift.protocol.TProtocol ;
import org.apache.thrift.transport.TTransport ;
import org.apache.thrift.transport.TTransportException ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import com.hp.hpl.jena.graph.Node ;
import com.hp.hpl.jena.sparql.sse.SSE ;
import com.hp.hpl.jena.tdb.store.NodeId ;
import com.hp.hpl.jena.tdb.store.nodetable.NodeTable ;

// XXX Needs efficiency attention.s
public class TServerNode extends ComponentBase
{
    private static Logger log = LoggerFactory.getLogger(TServerNode.class) ;
    
    private final ThriftServer server ;
    
    public static TServerNode create(int port, NodeTable nodeTable) {
        return new TServerNode(port, nodeTable) ;
    }
    
    private TServerNode(int port, NodeTable nodeTable) {
        setLabel("NodeServer["+port+"]") ;
        server = new ThriftServer(port, new Handler(getLabel(), nodeTable)) ;
    }
    
    @Override
    public void start() {
        if ( server.isRunning() ) {
            FmtLog.debug(log, "Already started (port=%d)", server.getPort()) ;
            return ;
        }
        FmtLog.info(log, "Start node server, port = %d", server.getPort()) ;
        server.start() ;
        super.start() ; 
    }
    
    static class Handler implements ThriftServer.Handler {
        private final NodeTable nodeTable ;
        private final String label ;
        
        public Handler(String label, NodeTable nodeTable) {
            this.label = label ;
            this.nodeTable = nodeTable ;
        }
        
        /** Handler for one conenction */ 
        @Override
        public void handle(final TTransport transport) {
            try { 
                TProtocol protocol = ThriftLib.protocol(transport) ;
                TLZ_NodeRequest request = new TLZ_NodeRequest() ;
                TLZ_NodeReply reply = new TLZ_NodeReply() ;

                for(;;) {
                    try {
                        request.read(protocol) ;
                    } catch (TTransportException ex) {
                        // failed to read a request. Includes the other end going away.
                        FmtLog.info(log, label+": End TServerNode connection") ;
                        break ;
                    }
                    long id = request.getRequestId() ;
                    //FmtLog.info(log, "[%d] Node request : %s", id, request) ;
                    execute(id, request, reply) ;
                    reply.write(protocol) ;
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
        
        private void execute(long id, TLZ_NodeRequest request, TLZ_NodeReply reply) {
            if ( nodeTable == null ) {
                FmtLog.error(log, "No node table here!") ;
                return ; 
            }
            
            if ( request.isSetAllocNodeId() ) {
                TLZ_Node nz = request.getAllocNodeId() ;
                Node n = SSE.parseNode(nz.getNodeStr()) ;
                NodeId nid = nodeTable.getAllocateNodeId(n) ;
                TLZ_NodeId nidz = new TLZ_NodeId() ;
                nidz.setNodeId(nid.getId()) ;
                reply.setAllocId(nidz) ;
                FmtLog.info(log, "[%d] Node alloc request : %s => %s", id, n, nid) ;
                return ;
            }
            if ( request.isSetFindByNode() ) {
                TLZ_Node nz = request.getFindByNode() ;
                Node n = SSE.parseNode(nz.getNodeStr()) ;
                NodeId nid = nodeTable.getNodeIdForNode(n) ;
                TLZ_NodeId nidz = new TLZ_NodeId() ;
                nidz.setNodeId(nid.getId()) ;
                reply.setAllocId(nidz) ;
                FmtLog.info(log, "[%d] Node get request : %s => %s", id, n, nid) ;
                return ;
            }
            if ( request.isSetFindByNodeId() ) {
                TLZ_NodeId nz = request.getFindByNodeId() ;
                NodeId nid = NodeId.create(nz.getNodeId()) ; 
                
                Node n = nodeTable.getNodeForNodeId(nid) ;
                if ( n == null )
                    FmtLog.error(log, "NodeId not found: "+nid) ;
                String str = NodeFmtLib.str(n) ;
                TLZ_Node nlz = new TLZ_Node() ;
                nlz.setNodeStr(str) ;
                reply.setFoundNode(nlz) ;
                FmtLog.info(log, "[%d] NodeId get request : %s => %s", id, nid, n) ;
                return ;
            }
            FmtLog.error(log, "execute: Unrecognized request: %s", request) ;
        }
    }
}
