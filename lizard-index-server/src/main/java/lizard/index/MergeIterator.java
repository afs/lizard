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

package lizard.index;

import java.util.ArrayList ;
import java.util.Collections ;
import java.util.Iterator ;
import java.util.List ;

import org.apache.jena.atlas.iterator.IteratorSlotted ;
import org.apache.jena.atlas.lib.ColumnMap ;
import org.apache.jena.atlas.lib.InternalErrorException ;
import org.apache.jena.atlas.lib.Tuple ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import com.hp.hpl.jena.tdb.store.NodeId ;

/** Perform an N-way merge of the incoming iterators assuming
 * that they arrive in an appropriately sorted order.
 * Access to the index is given by the column map.
 */

public class MergeIterator extends IteratorSlotted<Tuple<NodeId>> {
    private static Logger log = LoggerFactory.getLogger(MergeIterator.class) ;
    // If there were a lot of iterators,
    // keeping a heap of current head tuples would be worthwhile.
    // For a few (single digit) it's not worth the trouble.
    private final int N ;
    private final List<Iterator<Tuple<NodeId>>> iterators ;
    private final List<Tuple<NodeId>> headTuples ;
    private final ColumnMap cmap ;
    private final boolean distinct ;
    private Tuple<NodeId> lastYielded = null ;
    
    private int countActive = 0 ;

    // @@ Yuk - we flip to sorted (index order) and back again.
    // Maybe network should be index order.
    
    MergeIterator(List<Iterator<Tuple<NodeId>>> _iterators, ColumnMap cmap, boolean distinct) {
        this.N = _iterators.size() ;
        this.iterators = Collections.unmodifiableList(_iterators) ;
        
//        this.iterators = new ArrayList<>() ;
//        for ( Iterator<Tuple<NodeId>> it : _iterators )
//            this.iterators.add(PeekIterator.create(it)) ;
        
        this.headTuples = new ArrayList<>(N) ;
        this.cmap = cmap ;
        this.distinct = distinct ;

        // Initialize the front of each iterator, in *index/mapped order*, or null  
        for ( int i = 0 ; i < N ; i ++ ) {
            Iterator<Tuple<NodeId>> iter = iterators.get(i) ;
            Tuple<NodeId> t = null ;
            if ( iter.hasNext() ) {
                t = iter.next() ;
                t = sortedOrder(t, cmap) ;
                countActive ++ ;
            }
            headTuples.add(t) ;
        }
    }
    
    private static Tuple<NodeId> sortedOrder(Tuple<NodeId> t, ColumnMap cmap) {
        if ( cmap == null )
            return t ;
        return cmap.unmap(t) ;
    }
    
    private static Tuple<NodeId> map(Tuple<NodeId> t, ColumnMap cmap) {
        if ( cmap == null )
            return t ;
        return cmap.map(t) ;
    }

    // test debugging. Very verbose.
    static final boolean DEBUG = false ;
    static { if ( DEBUG ) LogCtl.enable(MergeIterator.class) ; }
    
    @Override
    protected Tuple<NodeId> moveToNext() {
        if ( DEBUG ) log.debug("moveToNext") ;
        Tuple<NodeId> current = null ;
        int idx = -1 ;
        int count = 0 ; 
        for ( int i = 0 ; i < N ; i ++ ) {
            Tuple<NodeId> t = headTuples.get(i) ;
            if ( DEBUG ) log.debug(i+" => "+t) ;
            if ( t == null ) continue ;
            count++ ;
            if ( distinct && current != null && compare(current, t) == 0 ) {
                // Regardless of finding the next current, this is a duplicate so advance now.
                Tuple<NodeId> t2 = move(i) ;
                if ( DEBUG ) log.debug("duplicate => "+t) ;
                if ( t2 == null )
                    count -- ;
                continue ;
            }
            // MIN
            if ( current == null || compare(current, t) > 0 ) {
                current = t ;
                idx = i ;
            }
        }
        
        if ( count != countActive ) {
            log.error("Inconsistent: "+count+" / "+countActive) ;
            throw new InternalErrorException() ;
        }
        
        if ( current == null ) {
            log.error("Inconsistent: current == null") ;
            throw new InternalErrorException() ;
        }
        
        if ( idx == -1 ) {
            log.error("Inconsistent: idx == -1") ;
            throw new InternalErrorException() ;
        }
        
        // We are going to yield 'current;.
        // Advance it's iterator.
        move(idx) ;

        if ( DEBUG ) log.debug("**** "+current) ;
        return map(current, cmap)  ;
    }

    /** Move on, return null for no more elements, and change internal state  */
    private Tuple<NodeId> move(int idx) {
        if ( DEBUG ) log.debug("move: "+idx) ;
        Tuple<NodeId> t0 = headTuples.get(idx) ;      // Current value.
        if ( t0 == null )
            return null ;
        Iterator<Tuple<NodeId>> iter = iterators.get(idx) ;
        while( iter.hasNext() ) {
            Tuple<NodeId> t = iter.next() ;
            t = sortedOrder(t, cmap) ;
            if ( distinct && t0.equals(t) )
                continue ;
            headTuples.set(idx, t) ;
            return t ;
        }
        // Iterator runs out.
        countActive -- ;
        headTuples.set(idx, null) ;
        return null ;
    }
    
    @Override
    protected boolean hasMore() {
        return countActive != 0 ;
    }
    
    
    // ---- compare 
    
    public static int compare(NodeId n1, NodeId n2) {
        // Unsigned compare.
        long v1 = n1.getId() ;
        long v2 = n2.getId() ;
        
        if ( v1 == v2 ) return 0 ;
        
        if ( v1 >= 0 && v2 >= 0 )
            return Long.compare(v1, v2) ; 
        if ( v1 < 0 && v2 >= 0 )
            return 1 ;
        if ( v1 >= 0 && v2 < 0 )
            return -1 ;
        // v1 <0 && v2 < 0
        // Negative numbers encode so MAX unsigned (0xFF = -1) > (0xFE = -2 ) etc
        // which is unsigned order.
        return Long.compare(v1, v2) ;
    }
    
    /** Compare two tuples assumed to be in natural order 
     * (slot 1 is considered more significat than slot 2 etc) */  
    private static int compare(Tuple<NodeId> t1, Tuple<NodeId> t2) {
        if ( t1.size() != t2.size())
            throw new IllegalArgumentException("Tuples not the same length ("+t1.size()+" and "+t2.size()+")") ;
        return compare$(t1, t2, null) ;
    }

    /** Compare two tuples, using the columna mapping as provided. */  
    private static int compare(Tuple<NodeId> t1, Tuple<NodeId> t2, ColumnMap cmap) {
        if ( t1.size() != t2.size())
            throw new IllegalArgumentException("Tuples not the same length ("+t1.size()+" and "+t2.size()+")") ;
        return compare$(t1, t2, cmap) ;
    }

    /** cmap may be null for "none" */
    private static int compare$(Tuple<NodeId> t1, Tuple<NodeId> t2, ColumnMap cmap) {
        int N = Math.min(t1.size(), t2.size()) ;
        for ( int i = 0 ; i < N ; i++ ) {
            int x = ( cmap == null ) 
                ? compare(t1.get(i), t2.get(i)) 
                : compare(cmap.mapSlot(i, t1), cmap.mapSlot(i, t2)) ;  
            if ( x != 0 )
                return x ;
        }
        return Integer.compare(t1.size(),  t2.size()) ;
    }

    // ---- compare 


}