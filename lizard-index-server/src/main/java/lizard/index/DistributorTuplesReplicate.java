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

package lizard.index;

import java.util.ArrayList ;
import java.util.Arrays ;
import java.util.Collection ;
import java.util.List ;

import lizard.comms.CommsException ;
import lizard.comms.ConnState ;

import org.apache.jena.atlas.lib.ColumnMap ;
import org.apache.jena.atlas.lib.Tuple ;
import org.seaborne.tdb2.store.NodeId ;

/** Policy for the placement of triples with multiple copies.
 *  For simplicity, for N replicas, R=1 W=N
 *  This policy does not shard.
 */ 

public class DistributorTuplesReplicate implements DistributorTupleIndex {
    private final List<TupleIndexRemote> remotes = new ArrayList<>() ;
    private final ColumnMap mapper ;
    private final String localVNode ;
    
    /** Create a DistributorTuplesBySubject is N replicas */
    public DistributorTuplesReplicate(String localVNode, ColumnMap mapper) {
        this.localVNode = localVNode ;
        this.mapper = mapper ;
    }

    public void add(TupleIndexRemote... indexes) {
        add(Arrays.asList(indexes)) ;
    }

    public void add(List<TupleIndexRemote> indexes) {
        remotes.addAll(indexes) ;
    }
    
    @Override
    public List<TupleIndexRemote> storeAt(Tuple<NodeId> tuple) {
        return locateWrite(tuple) ;
    }

    @Override
    public List<TupleIndexRemote> findAt(Tuple<NodeId> tuple) {
        return locateRead(tuple) ;
    }

    @Override
    public List<TupleIndexRemote> allFind() {
        return choose(remotes) ;
    }

    private List<TupleIndexRemote> choose(List<TupleIndexRemote> z) {
        return Arrays.asList(chooseOne(z)) ;
    }
    
    /** Choose one remote, preferring a local vnode */ 
    private TupleIndexRemote chooseOne(List<TupleIndexRemote> z) {
        TupleIndexRemote maybe = null ;
        for ( TupleIndexRemote tir : z ) {
            if ( tir.getStatus() == ConnState.OK ) {
                if ( localVNode == null )
                    return tir ;
                if ( tir.getRemoteVNode().equals(localVNode) )
                    return tir ;
                maybe = tir ;
            }
        }
        if ( maybe == null )
            throw new CommsException("No index replicas available") ;
        return maybe ;  
    }
    
    /** Choose all active remotes */ 
//    private static List<TupleIndexRemote> chooseActive(List<TupleIndexRemote> z) {
//        List<TupleIndexRemote> x = new ArrayList<>() ;
//        for ( TupleIndexRemote tir : z ) {
//            if ( tir.getStatus() == ConnState.OK )
//                x.add(tir) ; 
//        }
//        if ( x.isEmpty() )
//            throw new CommsException("No index replicas available") ;
//        return x ;    
//    }
    
    @Override
    public List<TupleIndexRemote> allStore() {
        return remotes ;
    }
    
    @Override
    public Collection<TupleIndexRemote> allRemotes() {
        return remotes ;
    }

    private List<TupleIndexRemote> locateRead(Tuple<NodeId> tuple) {
        NodeId n = mapper.fetchSlot(0, tuple) ;
        if ( NodeId.isAny(n) )
            return allFind() ;
        // Concrete subject 
        return choose(remotes) ;
    }

    private List<TupleIndexRemote> locateWrite(Tuple<NodeId> tuple) {
        if ( remotes.isEmpty() )
            throw new CommsException("Can't store - no remotes indexes") ;
        for ( NodeId n : tuple ) {
            if ( ! NodeId.isConcrete(n) )
                throw new CommsException("Can't store a tuple containing "+n ) ;
        }
        NodeId n = mapper.fetchSlot(0, tuple) ;
        // Check all available
        for ( TupleIndexRemote idx : remotes ) {
            if ( idx.getStatus() != ConnState.OK )
                throw new CommsException("Can't store - an index is unavailable: "+idx.getStatus()) ;
        }
        return remotes ;
    }
    
    @Override
    public String toString() { return "Tuple replicas:"+mapper.getLabel() ; }
}

