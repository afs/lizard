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

import java.util.ArrayList ;
import java.util.Arrays ;
import java.util.Collection ;
import java.util.List ;

import lizard.comms.CommsException ;
import lizard.comms.ConnState ;

import org.apache.jena.graph.Node ;
import org.seaborne.tdb2.store.NodeId ;

/** An implementation of DistributorNodes that provides a replicated, unsharded policy. */
public class DistributorNodesReplicate implements DistributorNodes {
    // Cover set policy is W=N and R=1
    private List<NodeTableRemote> places = new ArrayList<>() ;
    private final String localVNode ;   // The preferred vnode
    
    public DistributorNodesReplicate(String localVNode) {
        this.localVNode = localVNode ;
    }
    
    public void add(NodeTableRemote... nodeTables) {
        
        add(Arrays.asList(nodeTables)) ;
    }

    public void add(List<NodeTableRemote> nodeTables) {
        for ( NodeTableRemote nt : nodeTables )
            places.add(nt) ;
    }
    
    @Override
    public List<NodeTableRemote> storeAt(Node node) {
        return locateWrite(node) ;
    }

    @Override
    public List<NodeTableRemote> findAt(NodeId nodeid) {
        return locateRead(nodeid) ;
    }

    @Override
    public List<NodeTableRemote> findAt(Node node) {
        return locateRead(node) ;
    }

    @Override
    public List<NodeTableRemote> allFind() {
        return choose(places) ;
    }

    private List<NodeTableRemote> choose(List<NodeTableRemote> z) {
        return Arrays.asList(chooseOne(z)) ;
    }
    
    /** Choose one remote, preferring a local vnode */ 
    private NodeTableRemote chooseOne(List<NodeTableRemote> z) {
        NodeTableRemote maybe = null ;
        for ( NodeTableRemote ntr : z ) {
            // For each key, find one place
            if ( ntr.getStatus() == ConnState.OK ) {
                if ( localVNode == null )
                    return ntr ;
                if ( ntr.getRemoteVNode().equals(localVNode) )
                    return ntr ;
                maybe = ntr ;
            }
        }
        if ( maybe == null )
            throw new CommsException("No node replicas available") ;
        return maybe ;    
    }
    
    @Override
    public List<NodeTableRemote> allStore() {
        for ( NodeTableRemote ntr : places ) {
            if ( ntr.getStatus() != ConnState.OK )
                throw new CommsException("Can't store - an index is unavailable") ;
        }
        return places ;
    }
    
    @Override
    public Collection<NodeTableRemote> allRemotes() {
        return places ;
    }

    private List<NodeTableRemote> locateRead(Node node) {
        return choose(places) ;
    }
    
    private List<NodeTableRemote> locateRead(NodeId nodeId) {
        return choose(places) ;
    }

    private List<NodeTableRemote> locateWrite(Node node) {
        if ( ! node.isConcrete() )
            throw new CommsException("Can't store node: "+node ) ;
        List<NodeTableRemote> possibilities = places ;
        // Check all available
        for ( NodeTableRemote ntr : possibilities ) {
            if ( ntr.getStatus() != ConnState.OK )
                throw new CommsException("Can't store - a server is unavailable: "+ntr.toString()+" is "+ntr.getStatus()) ;
        }
        return possibilities ;
    }
    
    @Override
    public String toString() { return "Nodes:"+places.toString() ; }
}
