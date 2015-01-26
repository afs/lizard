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
import java.util.Iterator ;
import java.util.List ;

import org.apache.jena.atlas.lib.Tuple ;
import org.seaborne.dboe.engine.access.AccessorBase ;

import com.hp.hpl.jena.tdb.store.NodeId ;

/** Needed? Replace ClusterTupleIndex? */
public class ClusterAccessor extends AccessorBase<NodeId> // implements Accessor<NodeId>
{
    // Or ClusterTupleIndex. 
    
    private final DistributorTupleIndex distributor ;

    public ClusterAccessor(DistributorTupleIndex distributor) {
        this.distributor = distributor ;
    }
    
    @Override
    public Iterator<Tuple<NodeId>> accessTuples(Tuple<NodeId> pattern) {

        List<TupleIndexRemote> places = distributor.findAt(pattern) ;
        List<Iterator<Tuple<NodeId>>> segments = new ArrayList<>() ;
        
        for ( TupleIndexRemote idx : places ) {
            Iterator<Tuple<NodeId>> iter = idx.find(pattern) ;
            segments.add(iter) ;
        }
        return new MergeIterator(segments, null, false) ;
    }
}
