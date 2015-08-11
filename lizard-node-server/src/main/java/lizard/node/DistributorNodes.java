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
import java.util.List ;

import lizard.system.Distributor ;

import org.apache.jena.graph.Node ;
import org.seaborne.tdb2.store.NodeId ;

public interface DistributorNodes extends Distributor {
    /** W quorum of {@link NodeTableRemote} for storing a node */  
    public List<NodeTableRemote> storeAt(Node node) ;
    /** R quorum of {@link NodeTableRemote} for finding the node for a nodeid */  
    public List<NodeTableRemote> findAt(NodeId nodeid) ;
    /** R quorum of {@link NodeTableRemote} for finding the nodeid for a node */  
    public List<NodeTableRemote> findAt(Node node) ;
    /** 
     * Collection of all the places to go to find something - will contain R of N replicas.
     * Normally, R=1, W=N.
     */ 
    public Collection<NodeTableRemote> allFind() ;
    /**
     * Collection of all the places to go to store something - will contain W of N replica.
     * Normally, R=1, W=N.
     */
    public Collection<NodeTableRemote> allStore() ;
    
    /** All the elements managed by this distributor */ 
    public Collection<NodeTableRemote> allRemotes() ;
}
