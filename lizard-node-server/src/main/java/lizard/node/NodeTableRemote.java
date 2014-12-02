/*
 *  Copyright 2013, 2014 Andy Seaborne
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

import java.util.Iterator ;

import lizard.comms.ConnState ;
import lizard.system.Component ;
import org.apache.jena.atlas.lib.NotImplemented ;
import org.apache.jena.atlas.lib.Pair ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import com.hp.hpl.jena.graph.Node ;
import com.hp.hpl.jena.tdb.store.NodeId ;
import com.hp.hpl.jena.tdb.store.nodetable.NodeTable ;

/** NodeTbale interface to a remote node table server */
public class NodeTableRemote implements Component, NodeTable {

    public static NodeTableRemote create(String hostname, int port) {
        TClientNode remote = TClientNode.create(hostname, port) ;
        // ConnectionMgr.
        NodeTableRemote nt = new NodeTableRemote(remote) ;
        return nt ;
    }
    
    private static Logger log = LoggerFactory.getLogger(NodeTableRemote.class) ;
    
    private final TClientNode conn ;
    private String label ;
    
    private NodeTableRemote(TClientNode conn) { 
        this.conn = conn ;
        this.label = conn.getLabel() ;
    }
    
    public ConnState getStatus() { return conn.getConnectionStatus() ; }
    
    @Override
    public NodeId getAllocateNodeId(Node node) {
        return conn.getAllocateNodeId(node) ;
    }

    @Override
    public NodeId getNodeIdForNode(Node node) {
        return conn.getNodeIdForNode(node) ;
    }

    @Override
    public Node getNodeForNodeId(NodeId id) {
        return conn.getNodeForNodeId(id) ;
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
    public Iterator<Pair<NodeId, Node>> all() {
        throw new NotImplemented() ;
    }

    @Override
    public NodeId allocOffset() {
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
    public void start()         { conn.start(); }

    @Override
    public void stop()          { close() ; }

    @Override
    public boolean isRunning()  { return conn.isRunning() ; }

    @Override
    public boolean hasFailed()  { return conn.hasFailed() ; }

    @Override
    public void setStatus(Component.Status status)    { conn.setStatus(status) ; }

    @Override
    public void close() { conn.stop() ; }

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
}
