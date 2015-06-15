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
import lizard.api.TLZ.TLZ_ThriftObjectTable ;
import lizard.comms.ConnState ;
import lizard.comms.Connection ;
import lizard.comms.thrift.ThriftClient ;
import lizard.system.ComponentTxn ;
import lizard.system.RemoteControl ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.jena.graph.Node ;
import org.apache.jena.riot.thrift.wire.RDF_Term ;
import org.seaborne.tdb2.store.NodeId ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
 
public class TClientNode2 extends TxnClient<TLZ_ThriftObjectTable.Client> implements ComponentTxn, Connection, RemoteControl
{
    private static Logger log = LoggerFactory.getLogger(TClientNode2.class) ;
    @Override protected Logger getLog() { return log ; }

    private final ThriftClient client ;
    private ConnState connState ;
    
    public static TClientNode2 create(String host, int port) {
        return new TClientNode2(host, port) ;
    }
    
    private TClientNode2(String host, int port) {
        this.client = new ThriftClient(host, port) ;
        setLabel("NodeClient["+host+":"+port+"]") ;
        connState = ConnState.NOT_ACTIVATED ; 
    }
    
    @Override
    public void start() {
        client.start() ;
        FmtLog.debug(log, "Start: %s", getLabel()) ;
        // Delay until starting (client.protocol not valid until then).
        super.setRPC(new TLZ_ThriftObjectTable.Client(client.protocol())) ;
        super.start() ;
        connState = ConnState.OK ;
    }
    
    // Bulk hacks.
    
    public List<NodeId> allocateNodeIds(List<Node> nodes, boolean withAllocation) {
        List<NodeId> x = new ArrayList<>(nodes.size()) ;
        for ( Node n : nodes )
            x.add(getAllocateNodeId(n)) ;
        return x ;
        
//        if ( ! withAllocation )
//            log.warn("allocateNodeIds : withAllocation=false (ignored)") ;
//        long id = allocRequestId() ;
//        long txnId = getTxnId() ;
//        
//        List<RDF_Term> lzn = new ArrayList<>(nodes.size()) ;
//        for ( Node node : nodes ) { 
//            lzn.add(encodeToTLZ(node)) ;
//        }
//        List<TLZ_NodeId> tlzNodeIds = call("allocNodeIds", ()-> rpc.allocNodeIds(id, txnId, lzn)) ;
//        List<NodeId> lznids = new ArrayList<>(nodes.size()) ;
//        for ( TLZ_NodeId tlzNodeId : tlzNodeIds )
//            lznids.add(decodeFromTLZ(tlzNodeId)) ;
//        return lznids ; 
    }
    
    public List<Node> lookupNodeIds(List<NodeId> nodeIds) {
        List<Node> x = new ArrayList<>(nodeIds.size()) ;
        
        for ( NodeId nid : nodeIds )
            x.add(getNodeForNodeId(nid)) ;
        return x ;
//        long id = allocRequestId() ;
//        long txnId = getTxnId() ;
//        
//        List<TLZ_NodeId> lzn = new ArrayList<>(nodeIds.size()) ;
//        for ( NodeId nid : nodeIds ) { 
//            lzn.add(encodeToTLZ(nid)) ;
//        }
//        List<RDF_Term> tlzTerms = call("lookupNodeIds", ()-> rpc.lookupNodeIds(id, txnId, lzn)) ;
//        List<Node> lzns = new ArrayList<>(tlzTerms.size()) ;
//        for ( RDF_Term tlzTerm : tlzTerms )
//            lzns.add(decodeFromTLZ(tlzTerm)) ;
//        return lzns ; 
    }

    public NodeId getAllocateNodeId(Node node) {
        long id = allocRequestId() ;
        long txnId = getTxnId() ;
        RDF_Term lzn = encodeToTLZ(node) ;
        long x = call("allocNodeId", ()-> rpc.allocNodeId(id, txnId, lzn)) ;
        return NodeId.create(x) ;
    }

    public NodeId getNodeIdForNode(Node node) {
        // XXX Can do away with little structs
        long id = allocRequestId() ; 
        long txnId = getTxnId() ;
        RDF_Term lzn = encodeToTLZ(node) ;
        long z = call("allocNodeId", ()-> rpc.findByTerm(id, txnId, lzn)) ;
        return NodeId.create(z) ;
    }
    
    public Node getNodeForNodeId(NodeId nid) {
        long id = allocRequestId() ; 
        long txnId = getTxnId() ;
        RDF_Term lzn = call("allocNodeId", ()-> rpc.findById(id, txnId, nid.getId())) ;
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
