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

import java.util.concurrent.Callable ;
import java.util.concurrent.atomic.AtomicLong ;

import lizard.api.TLZ.TLZ_Node ;
import lizard.api.TLZ.TLZ_NodeId ;
import lizard.api.TLZ.TLZ_NodeRequest ;
import lizard.comms.ConnState ;
import lizard.comms.Connection ;
import lizard.comms.thrift.ThriftClient ;
import lizard.system.ComponentBase ;
import lizard.system.LizardException ;
import lizard.system.Pingable ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.jena.riot.out.NodeFmtLib ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import com.hp.hpl.jena.graph.Node ;
import com.hp.hpl.jena.sparql.sse.SSE ;
import com.hp.hpl.jena.tdb.store.NodeId ;
 
public class TClientNode extends ComponentBase implements Connection, Pingable
{
    private static Logger log = LoggerFactory.getLogger(TClientNode.class) ;
    private final ThriftClient client ;
    private TLZ_NodeRequest.Client rpc ;
    private ConnState connState ;
    
    public static TClientNode create(String host, int port) {
        return new TClientNode(host, port) ;
    }
    
    private TClientNode(String host, int port) {
        this.client = new ThriftClient(host, port) ;
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
        // Delay until starting (client.protocol not valid until then).
        rpc = new TLZ_NodeRequest.Client(client.protocol()) ;
        super.start() ;
        connState = ConnState.OK ;
    }
    
    private static AtomicLong counter = new AtomicLong(0) ;
    
    public NodeId getAllocateNodeId(Node node) {
        // XXX Can do away with little structs
        String x =  NodeFmtLib.str(node) ;
        TLZ_Node lzn = new TLZ_Node().setNodeStr(x) ; 
        TLZ_NodeId tlzNodeId = exec("allocNodeId", ()-> rpc.allocNodeId(lzn)) ;
        long idval = tlzNodeId.getNodeId() ;
        NodeId nid = NodeId.create(idval) ;
        return nid ; 
    }

    public NodeId getNodeIdForNode(Node node) {
        // XXX Can do away with little structs
        String x =  NodeFmtLib.str(node) ;
        TLZ_Node lzn = new TLZ_Node().setNodeStr(x) ; 
        TLZ_NodeId tlzNodeId = exec("allocNodeId", ()-> rpc.findByNode(lzn)) ;
        long idval = tlzNodeId.getNodeId() ;
        NodeId nid = NodeId.create(idval) ;
        return nid ; 
    }
    
    public Node getNodeForNodeId(NodeId id) {
        // XXX Can do away with little structs
        TLZ_NodeId lznid = new TLZ_NodeId().setNodeId(id.getId()) ; 
        TLZ_Node lzn = exec("allocNodeId", ()-> rpc.findByNodeId(lznid)) ;
        String x = lzn.getNodeStr() ;
        Node n = SSE.parseNode(x) ;
        return n ; 
    }
    
    @Override
    public void ping() {
        exec("ping", ()-> { rpc.nodePing(); return null;}) ;
    }
    
    private <T> T exec(String label, Callable<T> action) {
        try { return action.call() ; } 
        catch (Exception ex) {
          FmtLog.error(log, ex, label) ;
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

    @Override
    public void close() {
        client.close() ;
        connState = ConnState.CLOSED ;
    }
}
