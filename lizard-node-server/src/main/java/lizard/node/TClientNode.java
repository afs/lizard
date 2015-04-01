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

import lizard.api.TxnClient ;
import lizard.api.TLZ.TLZ_Node ;
import lizard.api.TLZ.TLZ_NodeId ;
import lizard.api.TLZ.TLZ_NodeTable ;
import lizard.comms.ConnState ;
import lizard.comms.Connection ;
import lizard.comms.thrift.ThriftClient ;
import lizard.comms.thrift.ThriftLib ;
import lizard.comms.thrift.ThriftLib.ThriftCallable ;
import lizard.system.ComponentTxn ;
import lizard.system.LizardException ;
import lizard.system.Pingable ;

import com.hp.hpl.jena.graph.Node ;
import com.hp.hpl.jena.sparql.sse.SSE ;
import com.hp.hpl.jena.tdb.store.NodeId ;

import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.jena.riot.out.NodeFmtLib ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
 
public class TClientNode extends TxnClient<TLZ_NodeTable.Client> implements ComponentTxn, Connection, Pingable
{
    private static Logger log = LoggerFactory.getLogger(TClientNode.class) ;
    @Override protected Logger getLog() { return log ; }

    private final ThriftClient client ;
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
        client.start() ;
        FmtLog.debug(log, "Start: %s", getLabel()) ;
        // Delay until starting (client.protocol not valid until then).
        super.setRPC(new TLZ_NodeTable.Client(client.protocol())) ;
        super.start() ;
        connState = ConnState.OK ;
    }
    
    public NodeId getAllocateNodeId(Node node) {
        // XXX Can do away with little structs
        // XXX Reforator for common code
        long id = allocRequestId() ;
        long txnId = getTxnId() ;
        String x =  NodeFmtLib.str(node) ;
        TLZ_Node lzn = new TLZ_Node().setNodeStr(x) ; 
        TLZ_NodeId tlzNodeId = call("allocNodeId", ()-> rpc.allocNodeId(id, txnId, lzn)) ;
        long idval = tlzNodeId.getNodeId() ;
        NodeId nid = NodeId.create(idval) ;
        return nid ; 
    }

    public NodeId getNodeIdForNode(Node node) {
        // XXX Can do away with little structs
        long id = allocRequestId() ; 
        long txnId = getTxnId() ;
        String x =  NodeFmtLib.str(node) ;
        TLZ_Node lzn = new TLZ_Node().setNodeStr(x) ; 
        TLZ_NodeId tlzNodeId = call("allocNodeId", ()-> rpc.findByNode(id, txnId, lzn)) ;
        long idval = tlzNodeId.getNodeId() ;
        NodeId nid = NodeId.create(idval) ;
        return nid ; 
    }
    
    public Node getNodeForNodeId(NodeId nid) {
        // XXX Can do away with little structs
        long id = allocRequestId() ; 
        long txnId = getTxnId() ;
        TLZ_NodeId lznid = new TLZ_NodeId().setNodeId(nid.getId()) ; 
        TLZ_Node lzn = call("allocNodeId", ()-> rpc.findByNodeId(id, txnId, lznid)) ;
        String x = lzn.getNodeStr() ;
        Node n = SSE.parseNode(x) ;
        return n ; 
    }
    
    @Override
    public void ping() {
        call("ping", ()-> { rpc.nodePing(); return null;}) ;
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
