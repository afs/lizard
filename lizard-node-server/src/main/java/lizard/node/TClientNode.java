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

package lizard.node;

import java.util.concurrent.atomic.AtomicLong ;

import lizard.api.TLZ.* ;
import lizard.comms.ConnState ;
import lizard.comms.Connection ;
import lizard.comms.thrift.ThriftClient ;
import lizard.system.ComponentBase ;
import lizard.system.LizardException ;
import lizard.system.Pingable ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.jena.riot.out.NodeFmtLib ;
import org.apache.thrift.TException ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import com.hp.hpl.jena.graph.Node ;
import com.hp.hpl.jena.sparql.sse.SSE ;
import com.hp.hpl.jena.tdb.store.NodeId ;
 
public class TClientNode extends ComponentBase implements Connection, Pingable
{
    private static Logger log = LoggerFactory.getLogger(TClientNode.class) ;
    private final ThriftClient client ;
    private ConnState connState ;
    
    public static TClientNode create(String host, int port) {
        return new TClientNode(host, port) ;
    }
    
    private TClientNode(String host, int port) {
        client = new ThriftClient(host, port) ;
        setLabel("NodeClient["+host+":"+port+"]") ;
        connState = ConnState.NOT_ACTIVATED ; 
    }
    
    @Override
    public void start() {
        if ( client.isRunning() ) {
            FmtLog.debug(log, "Already started (%s:%d)", client.getRemoteHost(), client.getRemotePort()) ;
            return ;
        }
        FmtLog.debug(log, "Start: %s", getLabel()) ;
        client.start() ;
        super.start() ;
        connState = ConnState.OK ;
    }
    
    private static AtomicLong counter = new AtomicLong(0) ;
    
    public NodeId getAllocateNodeId(Node node) {
        TLZ_NodeRequest request = new TLZ_NodeRequest() ;
        TLZ_NodeReply reply = new TLZ_NodeReply() ;
        String x =  NodeFmtLib.str(node) ;
        TLZ_Node lzn = new TLZ_Node().setNodeStr(x) ; 
        request.setAllocNodeId(lzn) ;
        sendReceive("getAllocateNodeId", request, reply) ;
        TLZ_NodeId tlzNodeId = reply.getAllocId() ;
        
        long idval = reply.getAllocId().getNodeId() ;
        NodeId nid = NodeId.create(idval) ;
        return nid ; 
    }

    public NodeId getNodeIdForNode(Node node) {
        TLZ_NodeRequest request = new TLZ_NodeRequest() ;
        TLZ_NodeReply reply = new TLZ_NodeReply() ;
        String x =  NodeFmtLib.str(node) ;
        TLZ_Node lzn = new TLZ_Node().setNodeStr(x) ; 
        request.setFindByNode(lzn) ;
        sendReceive("getNodeIdForNode", request, reply) ;
        long idval = reply.getAllocId().getNodeId() ;
        NodeId nid = NodeId.create(idval) ;
        return nid ; 
    }
    
    public Node getNodeForNodeId(NodeId id) {
        TLZ_NodeRequest request = new TLZ_NodeRequest() ;
        TLZ_NodeReply reply = new TLZ_NodeReply() ;
        TLZ_NodeId lznid = new TLZ_NodeId().setNodeId(id.getId()) ; 
        request.setFindByNodeId(lznid) ;
        sendReceive("getNodeForNodeId", request, reply) ;
        String x = reply.getFoundNode().getNodeStr() ;
        Node n = SSE.parseNode(x) ;
        return n ; 
    }
    
    private void sendReceive(String caller, TLZ_NodeRequest request, TLZ_NodeReply reply) {
        try { 
            request.setRequestId(counter.incrementAndGet()) ;
            //request.setGeneration(0) ;
            request.write(client.protocol()) ;
            client.protocol().getTransport().flush() ;
            reply.read(client.protocol()) ;
        }
        catch (TException ex) {
            FmtLog.error(log, ex, caller) ;
            throw new LizardException(ex) ;
        }
    }
    
    @Override
    public ConnState getConnectionStatus() {
        return connState ;
    }

    @Override
    public String label() {
        return getLabel() ;
    }

    @Override
    public void setConnectionStatus(ConnState status) { connState = status ; }

    private static TLZ_Ping tlzPing = new TLZ_Ping(8888) ;
    @Override
    public void ping() {
        TLZ_NodeRequest request = new TLZ_NodeRequest() ;
        TLZ_NodeReply reply = new TLZ_NodeReply() ;
        long requestId = counter.incrementAndGet() ;
        request.setRequestId(requestId) ;
        request.setPing(tlzPing) ;
        try {
            request.write(client.protocol()) ;
            client.protocol().getTransport().flush() ;
            reply.read(client.protocol()) ;
            if ( ! reply.isSetRequestId() )
                FmtLog.error(log, "ping: requestId not set in reply") ;
            else if ( reply.getRequestId() != requestId )
                FmtLog.error(log, "ping: requestId does not match that sent (%d,%d)", reply.getRequestId(), requestId) ;
        } catch (TException ex) {
            FmtLog.error(log, ex, "ping") ;
        }
    }

    @Override
    public void close() {
        client.close() ;
        connState = ConnState.CLOSED ;
    }
}
