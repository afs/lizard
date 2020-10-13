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

import static lizard.comms.thrift.ThriftLib.decodeFromTLZ ;
import static lizard.comms.thrift.ThriftLib.encodeToTLZ ;

import java.util.ArrayList ;
import java.util.List ;

import lizard.api.TxnClient ;
import lizard.api.TLZ.TLZ_NodeId ;
import lizard.api.TLZ.TLZ_NodeTable ;
import lizard.comms.ConnState ;
import lizard.comms.Connection ;
import lizard.comms.thrift.ThriftClient ;
import lizard.system.ComponentTxn ;
import lizard.system.RemoteControl ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.jena.graph.Node ;
import org.apache.jena.riot.thrift.wire.RDF_Term ;
import org.apache.jena.tdb2.store.NodeId ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
 
public class TClientNode extends TxnClient<TLZ_NodeTable.Client> implements ComponentTxn, Connection, RemoteControl
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
    
    
    @Override
    protected void resetConnection() {
        try { client.close() ; } catch (Exception ex) { }  
        client.startProtocol(); 
        super.setRPC(new TLZ_NodeTable.Client(client.protocol())) ;
    }
    
    public List<NodeId> allocateNodeIds(List<Node> nodes, boolean withAllocation) {
        if ( ! withAllocation )
            log.warn("allocateNodeIds : withAllocation=false (ignored)") ;
        long id = allocRequestId() ;
        long txnId = getTxnId() ;
        
        List<RDF_Term> lzn = new ArrayList<>(nodes.size()) ;
        for ( Node node : nodes ) { 
            lzn.add(encodeToTLZ(node)) ;
        }
        List<TLZ_NodeId> tlzNodeIds = call("allocNodeIds", ()-> rpc.allocNodeIds(id, txnId, lzn)) ;
        List<NodeId> lznids = new ArrayList<>(nodes.size()) ;
        for ( TLZ_NodeId tlzNodeId : tlzNodeIds )
            lznids.add(decodeFromTLZ(tlzNodeId)) ;
        return lznids ; 
    }
    
    public List<Node> lookupNodeIds(List<NodeId> nodeIds) {
        long id = allocRequestId() ;
        long txnId = getTxnId() ;
        
        List<TLZ_NodeId> lzn = new ArrayList<>(nodeIds.size()) ;
        for ( NodeId nid : nodeIds ) { 
            lzn.add(encodeToTLZ(nid)) ;
        }
        List<RDF_Term> tlzTerms = call("lookupNodeIds", ()-> rpc.lookupNodeIds(id, txnId, lzn)) ;
        List<Node> lzns = new ArrayList<>(tlzTerms.size()) ;
        for ( RDF_Term tlzTerm : tlzTerms )
            lzns.add(decodeFromTLZ(tlzTerm)) ;
        return lzns ; 
    }

    public NodeId getAllocateNodeId(Node node) {
        long id = allocRequestId() ;
        long txnId = getTxnId() ;
        RDF_Term lzn = encodeToTLZ(node) ;
        TLZ_NodeId tlzNodeId = call("allocNodeId", ()-> rpc.allocNodeId(id, txnId, lzn)) ;
        return decodeFromTLZ(tlzNodeId) ;
    }

    public NodeId getNodeIdForNode(Node node) {
        // XXX Can do away with little structs
        long id = allocRequestId() ; 
        long txnId = getTxnId() ;
        RDF_Term lzn = encodeToTLZ(node) ;
        TLZ_NodeId tlzNodeId = call("allocNodeId", ()-> rpc.findByNode(id, txnId, lzn)) ;
        return decodeFromTLZ(tlzNodeId) ;
    }
    
    public Node getNodeForNodeId(NodeId nid) {
        // XXX Can do away with little structs
        long id = allocRequestId() ; 
        long txnId = getTxnId() ;
        TLZ_NodeId lznid = encodeToTLZ(nid) ; 
        RDF_Term lzn = call("allocNodeId", ()-> rpc.findByNodeId(id, txnId, lznid)) ;
        Node n = decodeFromTLZ(lzn) ;
        return n ; 
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
