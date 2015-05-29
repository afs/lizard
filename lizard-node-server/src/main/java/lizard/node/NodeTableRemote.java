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

import java.util.Iterator ;
import java.util.List ;

import lizard.api.TxnClient ;
import lizard.comms.ConnState ;
import lizard.system.Component ;
import lizard.system.ComponentTxn ;
import lizard.system.RemoteControl ;

import org.apache.jena.atlas.lib.NotImplemented ;
import org.apache.jena.atlas.lib.Pair ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import org.apache.jena.graph.Node ;
import org.apache.jena.query.ReadWrite ;
import org.seaborne.tdb2.store.NodeId ;
import org.seaborne.tdb2.store.nodetable.NodeTable ;

/** NodeTable interface to a remote node table server */
public class NodeTableRemote implements ComponentTxn, Component, NodeTable, RemoteControl, TxnClient.Accessor {

    public static NodeTableRemote create(String hostname, int port) {
        TClientNode remote = TClientNode.create(hostname, port) ;
        NodeTableRemote nt = new NodeTableRemote(remote) ;
        return nt ;
    }
    
    private static Logger log = LoggerFactory.getLogger(NodeTableRemote.class) ;
    
    private final TClientNode client ;
    private String label ;
    
    private NodeTableRemote(TClientNode conn) { 
        this.client = conn ;
        this.label = conn.getLabel() ;
    }
    
    @Override
    public TxnClient<?> getWireClient() { 
        return client ;
    }
    
    public ConnState getStatus() { return client.getConnectionStatus() ; }
    
    @Override
    public NodeId getAllocateNodeId(Node node) {
        return client.getAllocateNodeId(node) ;
    }

    @Override
    public NodeId getNodeIdForNode(Node node) {
        return client.getNodeIdForNode(node) ;
    }

    @Override
    public Node getNodeForNodeId(NodeId id) {
        return client.getNodeForNodeId(id) ;
    }
    
    @Override
    public boolean containsNode(Node node) {
        NodeId x = getNodeIdForNode(node) ;
        return NodeId.isDoesNotExist(x) ;
    }

    @Override
    public boolean containsNodeId(NodeId nodeId) {
        Node x = getNodeForNodeId(nodeId) ;
        return x == null ;
    }

    @Override
    public List<NodeId> bulkNodeToNodeId(List<Node> nodes, boolean withAllocation) {
        return client.allocateNodeIds(nodes, withAllocation) ;
    }

    @Override
    public List<Node> bulkNodeIdToNode(List<NodeId> nodeIds) {
        return client.lookupNodeIds(nodeIds) ;
    }

    @Override
    public Iterator<Pair<NodeId, Node>> all() {
        throw new NotImplemented() ;
    }

    @Override
    public boolean isEmpty() {
        throw new NotImplemented() ;
    }

    @Override
    public void sync() {
        op0("SYNC") ;
    }

    @Override
    public void start()         { client.start(); }

    @Override
    public void stop()          { close() ; }

    @Override
    public boolean isRunning()  { return client.isRunning() ; }

    @Override
    public boolean hasFailed()  { return client.hasFailed() ; }

    @Override
    public void setStatus(Component.Status status)    { client.setStatus(status) ; }

    @Override
    public void close() { client.stop() ; }

    @Override
    public String toString() {
        return getLabel() ; 
    }
    
    // Share (ProxyTupleIndex)
    private boolean op0(String cmd) { return true ; }

    @Override
    public String getLabel() { 
        return label ;
    }

    @Override
    public void setLabel(String label) {}

    @Override
    public NodeTable wrapped() {
        return null ;
    }

    @Override
    public void ping() {
        client.ping() ;
    }

    @Override
    public void remoteStop() {
        client.remoteStop() ;
    }

    // Java does not have multiple implementation inheritance. 
    
    @Override public void begin(long txnId, ReadWrite mode)   { client.begin(txnId, mode) ; }
    @Override public void prepare()               { client.prepare() ; }
    @Override public void commit()                { client.commit() ; }
    @Override public void abort()                 { client.abort() ; }
    @Override public void end()                   { client.end() ; }
}
