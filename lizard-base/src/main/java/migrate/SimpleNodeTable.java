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

package migrate;

import static java.util.stream.Collectors.toList ;

import java.util.HashMap ;
import java.util.Iterator ;
import java.util.Map ;

import org.apache.jena.atlas.lib.Pair ;
import org.apache.jena.graph.Node ;
import org.apache.jena.tdb.store.NodeId ;
import org.apache.jena.tdb.store.nodetable.NodeTable ;

/** Simple implemention of a NodeTable to support testing and development */   
public class SimpleNodeTable implements NodeTable {
    private long counter = 0 ;
    private Map<NodeId, Node> idToNode = new HashMap<>() ;
    private Map<Node, NodeId> nodeToId = new HashMap<>() ;
    
    public SimpleNodeTable() {
    }

    @Override
    public NodeId getAllocateNodeId(Node node) {
        NodeId nid = nodeToId.get(node) ;
        if ( nid == null ) {
            nid = NodeId.create(counter++) ;
            nodeToId.put(node, nid) ;
            idToNode.put(nid, node) ;
        }
        return nid ;
    }

    @Override
    public NodeId getNodeIdForNode(Node node) {
        NodeId nid = nodeToId.get(node) ;
        if ( nid == null )
            return NodeId.NodeDoesNotExist ;
        return nid ;
    }

    @Override
    public Node getNodeForNodeId(NodeId id) {
        return idToNode.get(id) ;
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
        // Materialize for simplicity.
        return idToNode.entrySet().stream()
            .map(item->Pair.create(item.getKey(), item.getValue()))
            .collect(toList())
            .iterator() ;
    }

    @Override
    public NodeId allocOffset() {
        return NodeId.create(counter) ;
    }

    @Override
    public boolean isEmpty() {
        return idToNode.isEmpty() ;
    }

    @Override
    public void sync() {}

    @Override
    public void close() {}

    @Override
    public NodeTable wrapped() {
        return null ;
    }

}
