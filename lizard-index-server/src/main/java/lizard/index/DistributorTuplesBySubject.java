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
import org.apache.jena.atlas.logging.Log ;
import org.apache.jena.ext.com.google.common.collect.ArrayListMultimap ;
import org.apache.jena.ext.com.google.common.collect.ListMultimap ;
import org.seaborne.tdb2.store.NodeId ;

/** Policy for the placement of triples (and finding them) partition by subject. */

public class DistributorTuplesBySubject implements DistributorTupleIndex {
    private ListMultimap<Long, TupleIndexRemote> places = ArrayListMultimap.create() ;
    private final ColumnMap mapper ;
    private final int size ;
    
    /** Create a DistributorTuplesBySubject of N replicas */
    public DistributorTuplesBySubject(ColumnMap mapper, int N) {
        this.mapper = mapper ;
        this.size = N ;
    }

    public void add(Long hashKey, TupleIndexRemote... indexes) {
        add(hashKey, Arrays.asList(indexes)) ;
    }

    public void add(Long hashKey, List<TupleIndexRemote> indexes) {
        for ( TupleIndexRemote idx : indexes ) {
            places.put(hashKey, idx) ;
        }
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
        List<TupleIndexRemote> placesToGo = new ArrayList<>() ;
        for ( Long x : places.keys() ) {
            List<TupleIndexRemote> z = places.get(x) ;
            TupleIndexRemote idx = chooseOne(z) ;
            if ( idx == null )
                throw new CommsException("Can't 'allFind' - a segment is completely unavailable") ;
            placesToGo.add(idx) ;
        }
        return placesToGo ;
    }

    private TupleIndexRemote chooseOne(List<TupleIndexRemote> z) {
        for ( TupleIndexRemote idx : z ) {
            // For each key, find one place
            if ( idx.getStatus() == ConnState.OK )
                return idx ;
        }
        return null ;    
    }
    
    @Override
    public Collection<TupleIndexRemote> allStore() {
        Collection<TupleIndexRemote> placesToGo = places.values() ;
        for ( TupleIndexRemote idx : placesToGo ) {
            if ( idx.getStatus() != ConnState.OK )
                throw new CommsException("Can't store - an index is unavailable") ;
        }
        return placesToGo ;
    }
    
    private List<TupleIndexRemote> locateRead(Tuple<NodeId> tuple) {
        long shard = Shard.shardBySubject(mapper, size, tuple) ;
        if ( shard == Shard.NO_SHARD )
            return allFind() ;
        // Concrete subject - go to one place. 
        if ( shard >= size )
            Log.fatal(this, "locateRead -- shard >= size :: "+shard+" > "+size) ;
        List<TupleIndexRemote> possibilities =  places.get(shard) ;
        // Get first active
        for ( TupleIndexRemote idx : possibilities ) {
            if ( idx.getStatus() == ConnState.OK )
                return Arrays.asList(idx) ;
        }
        throw new CommsException("No indexes available") ;
    }

    private List<TupleIndexRemote> locateWrite(Tuple<NodeId> tuple) {
        for ( NodeId n : tuple ) {
            if ( ! NodeId.isConcrete(n) )
                throw new CommsException("Can't store a tuple containing "+n ) ;
        }
        long shard = Shard.shardBySubject(mapper, size, tuple) ;
        if ( shard == Shard.NO_SHARD )
            throw new CommsException("Can't write tuple with unknown shard placement"+tuple) ;
        List<TupleIndexRemote> possibilities =  places.get(shard) ;
        // Check all available
        for ( TupleIndexRemote idx : possibilities ) {
            if ( idx.getStatus() != ConnState.OK )
                throw new CommsException("Can't store - an index is unavailable: "+idx.getStatus()) ;
        }
        return possibilities ;
    }
    
    @Override
    public String toString() { return "Tuples:"+mapper.getLabel()+" :: "+places ; }
}

