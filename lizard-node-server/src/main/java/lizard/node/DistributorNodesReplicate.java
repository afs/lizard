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
import java.util.List ;

import lizard.comms.CommsException ;
import lizard.comms.ConnState ;
import org.apache.jena.atlas.lib.DS ;

import org.apache.jena.graph.Node ;
import org.apache.jena.tdb.store.NodeId ;

/** An implementation of DistributorNodes that randomly places Node */
public class DistributorNodesReplicate implements DistributorNodes {
    // Cover set policy is W=N and R=1
    
    List<NodeTableRemote> places = DS.list() ;
    
    public DistributorNodesReplicate() { }
    
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
        return places ;
    }
    
    private List<NodeTableRemote> chooseActive(List<NodeTableRemote> z) {
        List<NodeTableRemote> x = new ArrayList<>() ;
        for ( NodeTableRemote ntr : z ) {
            // For each key, find one place
            if ( ntr.getStatus() == ConnState.OK )
                x.add(ntr) ; 
        }
        if ( x.isEmpty() )
            throw new CommsException("No node replicas available") ;
        return x ;    
    }
    
    @Override
    public List<NodeTableRemote> allStore() {
        for ( NodeTableRemote ntr : places ) {
            if ( ntr.getStatus() != ConnState.OK )
                throw new CommsException("Can't store - an index is unavailable") ;
        }
        return places ;
    }

    private List<NodeTableRemote> locateRead(Node node) {
        return chooseActive(places) ;
    }
    
    private List<NodeTableRemote> locateRead(NodeId nodeId) {
        return chooseActive(places) ;
    }

    private List<NodeTableRemote> locateWrite(Node node) {
        if ( ! node.isConcrete() )
            throw new CommsException("Can't store node: "+node ) ;
        List<NodeTableRemote> possibilities =  places ;
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
