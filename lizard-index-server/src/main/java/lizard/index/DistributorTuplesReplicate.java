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
import java.util.List ;

import lizard.comms.CommsException ;
import lizard.comms.ConnState ;
import org.apache.jena.atlas.lib.ColumnMap ;
import org.apache.jena.atlas.lib.Tuple ;

import com.hp.hpl.jena.tdb.store.NodeId ;

/** Policy for the placement of triples based on eventual consistent replication.
 *  For simplicity, for N replicas, R=1 W=N
 *  This policy does not shard.
 */ 

public class DistributorTuplesReplicate implements DistributorTupleIndex {
    private final List<TupleIndexRemote> remotes = new ArrayList<>() ;
    private final ColumnMap mapper ;
    
    /** Create a DistributorTuplesBySubject is N replicas */
    public DistributorTuplesReplicate(ColumnMap mapper) {
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
        return chooseOne(remotes) ;
    }

    private static List<TupleIndexRemote> chooseOne(List<TupleIndexRemote> z) {
        List<TupleIndexRemote> x = new ArrayList<>() ;
        for ( TupleIndexRemote tir : z ) {
            if ( tir.getStatus() == ConnState.OK )
                return Arrays.asList(tir) ;
        }
        if ( x.isEmpty() )
            throw new CommsException("No index replicas available") ;
        return x ;  
    }
    
    private static List<TupleIndexRemote> chooseActive(List<TupleIndexRemote> z) {
        List<TupleIndexRemote> x = new ArrayList<>() ;
        for ( TupleIndexRemote tir : z ) {
            // For each key, find one place
            if ( tir.getStatus() == ConnState.OK )
                x.add(tir) ; 
        }
        if ( x.isEmpty() )
            throw new CommsException("No index replicas available") ;
        return x ;    
    }
    
    @Override
    public List<TupleIndexRemote> allStore() {
        return remotes ;
    }
    
    private List<TupleIndexRemote> locateRead(Tuple<NodeId> tuple) {
        NodeId n = mapper.fetchSlot(0, tuple) ;
        if ( NodeId.isAny(n) )
            return allFind() ;
        // Concrete subject 
        return chooseOne(remotes) ;
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

