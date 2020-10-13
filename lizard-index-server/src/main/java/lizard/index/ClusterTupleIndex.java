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
import java.util.Collection ;
import java.util.Iterator ;
import java.util.List ;

import lizard.comms.CommsException ;
import org.apache.jena.atlas.iterator.Iter ;
import org.apache.jena.atlas.lib.tuple.Tuple ;
import org.apache.jena.atlas.lib.tuple.TupleFactory ;
import org.apache.jena.atlas.lib.tuple.TupleMap ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.jena.tdb2.store.NodeId ;
import org.apache.jena.tdb2.store.tupletable.TupleIndex ;
import org.apache.jena.tdb2.store.tupletable.TupleIndexBase ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

/**
 * A {@link TupleIndex} that deals with the cluster
 * e.g. going to multiple places to perform a <tt>find</tt>,
 * coping with link failure, and storing in multiple locations.    
 */
public class ClusterTupleIndex extends TupleIndexBase
{
    // XXX No parallel access (easy of debugging)
    private static Logger log = LoggerFactory.getLogger(ClusterTupleIndex.class) ; 
    private Tuple<NodeId> anyTuple ;
    private DistributorTupleIndex distributor ;

    public ClusterTupleIndex(DistributorTupleIndex distributor, int N, TupleMap mapping, String name) {
        super(N, mapping, name) ;
        
        //log.info(name+":"+colMapping.getLabel()+" "+distributor.toString()) ;
        log.info(distributor.toString()) ;
        
        NodeId[] x = new NodeId[N] ;
        for ( int i = 0 ; i < N ; i++ )
            x[i] = NodeId.NodeIdAny ;
        anyTuple = TupleFactory.create(x) ;
        this.distributor = distributor ;
    }
    
    public DistributorTupleIndex getDistributor() { return distributor ; }
    
    @Override
    public Iterator<Tuple<NodeId>> all() {
        return performFind(anyTuple) ;
    }

    @Override
    public long size() {
        long x = 0 ;
        for ( TupleIndex idx : distributor.allFind() )
            x += idx.size() ;
        return 0 ;
    }

    @Override
    public boolean isEmpty() {
        return false ;
    }

    @Override
    public void clear() {
        for ( TupleIndex idx : distributor.allStore() )
            idx.clear() ;
    }

    @Override
    public void sync() {
        for ( TupleIndex idx : distributor.allStore() )
            idx.sync() ;
    }

    @Override
    public void close() {
        for ( TupleIndex idx : distributor.allStore() )
            idx.close() ;
    }

    @Override
    protected void performAdd(Tuple<NodeId> tuple) {
        FmtLog.debug(log, "Add - %s", tuple);
        List<TupleIndexRemote> places = distributor.storeAt(tuple) ;
        // ----
        for ( TupleIndexRemote idx : places ) {
            FmtLog.debug(log, "  Add @%s", idx.getLabel()) ;
            idx.add(tuple) ;
        }
    }

    @Override
    public void addAll(Collection<Tuple<NodeId>> tuples) {
        // XXX Sharding.
        Collection<TupleIndexRemote> places = distributor.allStore() ;
        // ----
        for ( TupleIndexRemote idx : places ) {
            FmtLog.debug(log, "  allAll @%s", idx.getLabel()) ;
            idx.addAll(tuples) ;
        }
    }

    @Override
    protected void performDelete(Tuple<NodeId> tuple) {
        FmtLog.debug(log, "Del - %s", tuple) ;
        List<TupleIndexRemote> places = distributor.storeAt(tuple) ;
        // ----
        for ( TupleIndexRemote idx : places ) {
            FmtLog.debug(log, "  Del @%s", idx.getLabel()) ;
            idx.delete(tuple) ;
        }
    }
    
    @Override
    public void deleteAll(Collection<Tuple<NodeId>> tuples) {
        // XXX Sharding.
        Collection<TupleIndexRemote> places = distributor.allStore() ;
        // ----
        for ( TupleIndexRemote idx : places ) {
            FmtLog.debug(log, "  deleteAll @%s", idx.getLabel()) ;
            idx.deleteAll(tuples) ;
        }
    }

    @Override
    protected Iterator<Tuple<NodeId>> performFind(Tuple<NodeId> tuple) {
        try { 
            return performFindOnce(tuple) ;
        } catch (CommsException ex) {
            // Single failure tolerated 
            return performFindOnce(tuple) ;
        }
    }
        
    private Iterator<Tuple<NodeId>> performFindOnce(Tuple<NodeId> tuple) {
        List<TupleIndexRemote> places = distributor.findAt(tuple) ;
        // ----
        FmtLog.info(log, "Going to: %s",places) ;
        
        for ( TupleIndexRemote idx : places ) {
            FmtLog.info(log, "  Place: %s %s", idx, tuple) ;
            idx.find(tuple).forEachRemaining(z -> FmtLog.info(log, "   %s",z)) ;
        }
        
        // In parallel ...
        List<Iterator<Tuple<NodeId>>> incoming = new ArrayList<>() ;
        for ( TupleIndexRemote idx : places ) {
            incoming.add(idx.find(tuple)) ;
        }

        // Merge 
        Iterator<Tuple<NodeId>> iter = merge(incoming, getMapping()) ;
        if ( log.isInfoEnabled() ) {
            if ( iter.hasNext() ) {
              List<Tuple<NodeId>> x = Iter.toList(iter) ;
              //FmtLog.info(log, "... results(%d)",x.size()) ;
              for ( Tuple<NodeId> t : x )
                  FmtLog.info(log, "...   %s",t) ;
              iter = x.iterator() ;
            } else
                FmtLog.info(log, "... zero results") ;
        }
        return iter ;
    }
    
    // Better way to get in index order?
    
    /** Perform an N-way merge of the incoming iterators assuming
     * that they arrive in an appropriately sorted order given by
     * the column map.
     * @param iterators
     * @param cmap
     * @return
     */
    private static Iterator<Tuple<NodeId>> merge(List<Iterator<Tuple<NodeId>>> iterators, TupleMap tmap) {
        if ( iterators.size() == 1 )
            return iterators.get(0) ;
        return new MergeIterator(iterators, tmap, false) ;
    }
}
