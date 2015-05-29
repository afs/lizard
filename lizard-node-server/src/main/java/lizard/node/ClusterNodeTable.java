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

import java.util.Collection ;
import java.util.Iterator ;
import java.util.List ;
import java.util.Objects ;

import org.apache.jena.atlas.iterator.Iter ;
import org.apache.jena.atlas.lib.InternalErrorException ;
import org.apache.jena.atlas.lib.Pair ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.jena.graph.Node ;
import org.seaborne.tdb2.store.NodeId ;
import org.seaborne.tdb2.store.nodetable.NodeTable ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

/** NodeTable of a cluster of client side of remote NodeTables */
public class ClusterNodeTable implements NodeTable {

    private static Logger log = LoggerFactory.getLogger(ClusterNodeTable.class) ;
    private final DistributorNodes distributor ;
    private final NodeTableRemote remoteNodeTable ; // If only one.
    
    public ClusterNodeTable(DistributorNodes distributor) {
        this.distributor = distributor ;
        log.info(distributor.toString()) ;
        // XXX Development.
        remoteNodeTable = remoteNodeTable() ;
    }
    
    public DistributorNodes getDistributor() { return distributor ; }
    
    @Override
    public List<NodeId> bulkNodeToNodeId(List<Node> nodes, boolean withAllocation) {
        // Parallel
        if ( remoteNodeTable != null )
            return remoteNodeTable.bulkNodeToNodeId(nodes, withAllocation) ;
        return NodeTable.super.bulkNodeToNodeId(nodes, withAllocation) ;
    }

    @Override
    public List<Node> bulkNodeIdToNode(List<NodeId> nodeIds) {
        // Parallel
        if ( remoteNodeTable != null )
            return remoteNodeTable.bulkNodeIdToNode(nodeIds) ;
        return NodeTable.super.bulkNodeIdToNode(nodeIds) ;
    }

    private NodeTableRemote remoteNodeTable() {
        if ( distributor.allRemotes().size() == 1 )
            return distributor.allRemotes().stream().findFirst().get() ;
        else
            return null ;
    }
    
    // Bulk operations.
    
//    public List<NodeId> allocateNodeIds(List<Node> nodes) {
//        // Should break into shard units. 
//        List<NodeTableRemote> tables = (List<NodeTableRemote>)distributor.allStore() ; // distributor.storeAt(node) ;
//
//        for ( NodeTableRemote nt : tables ) {
//            NodeId nid1 = nt.
//            if ( nid == null )
//                nid = nid1 ;
//            else {
//                if ( ! Objects.equals(nid, nid1) )
//                    FmtLog.warn(log, "Different NodeIds allocated for %s : %s != %s", node, nid, nid1) ;
//            }
//        }
//        
//        FmtLog.info(log, "getAllocateNodeId(%s) -> %s", node, nid) ;
//        if ( log.isDebugEnabled() )
//            tables.forEach(nt -> FmtLog.debug(log, "  store(%s) @ %s", node, nt)) ;
//        return nid ;
//        
//    }
    
    
    @Override
    public NodeId getAllocateNodeId(Node node) {
        // XXX Could useful know whether it was find or store
        // find - stop early, store, do all 
        List<NodeTableRemote> tables = distributor.storeAt(node) ;
        NodeId nid = null ; 
        
        for ( NodeTableRemote nt : tables ) {
            NodeId nid1 = nt.getAllocateNodeId(node) ;
            if ( nid == null )
                nid = nid1 ;
            else {
                if ( ! Objects.equals(nid, nid1) )
                    FmtLog.warn(log, "Different NodeIds allocated for %s : %s != %s", node, nid, nid1) ;
            }
        }
        
        FmtLog.info(log, "getAllocateNodeId(%s) -> %s", node, nid) ;
        if ( log.isDebugEnabled() )
            tables.forEach(nt -> FmtLog.debug(log, "  store(%s) @ %s", node, nt)) ;
        return nid ;
    }

    @Override
    public NodeId getNodeIdForNode(Node node) {
        List<NodeTableRemote> tables = distributor.findAt(node) ;
        for ( NodeTableRemote nt : tables ) {
            NodeId nid = nt.getNodeIdForNode(node) ;
            if ( nid != null ) {
                FmtLog.info(log, "getNodeIdForNode(%s) -> %s", node, nid) ;
                return nid ;
            }
        }
        FmtLog.info(log, "getAllocateNodeId(%s) -> no found", node) ;
        return NodeId.NodeDoesNotExist ;
    }

    @Override
    public Node getNodeForNodeId(NodeId id) {
        List<NodeTableRemote> tables = distributor.findAt(id) ;
        
        for ( NodeTableRemote nt : tables ) {
            Node n = nt.getNodeForNodeId(id) ;
            if ( n != null )
                return n ;
        }
        throw new InternalErrorException("Shouldn't be here in getNodeForNodeId") ;
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
        Collection<NodeTableRemote> tables = distributor.allFind() ;
        Iter<Pair<NodeId, Node>> iter = null ;
        for ( NodeTableRemote nt : tables ) {
            Iter<Pair<NodeId, Node>> iter1 = Iter.iter(nt.all()) ;
            if ( iter != null )
                iter = iter.append(iter1) ;
            else
                iter = iter1 ;
        }
        return iter ; 
    }

    @Override
    public boolean isEmpty() {
        Collection<NodeTableRemote> tables = distributor.allStore() ;
        for ( NodeTableRemote nt : tables ) {
            if ( ! nt.isEmpty() )
                return false ;
        }
        return true ; 

    }

    @Override
    public void close() {
        Collection<NodeTableRemote> tables = distributor.allStore() ;
        for ( NodeTableRemote nt : tables )
            nt.close() ;
    }

    @Override
    public void sync() {
        Collection<NodeTableRemote> tables = distributor.allStore() ;
        for ( NodeTableRemote nt : tables )
            nt.sync() ;
    }

    @Override
    public String toString() {
        return "ClusterNT" ; 
    }

    @Override
    public NodeTable wrapped() {
        return null ;
    }
}
