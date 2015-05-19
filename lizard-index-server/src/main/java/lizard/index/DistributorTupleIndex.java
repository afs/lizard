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

import java.util.Collection ;
import java.util.List ;

import org.apache.jena.atlas.lib.Tuple ;
import org.seaborne.tdb2.store.NodeId ;

/** Policy for the placement of triples (and for finding them). */
public interface DistributorTupleIndex {
    /** Places where a triple is stored.
     * Each place must be updated.
     * @param tuple
     * @return List<TupleIndex>
     */
    
    public List<TupleIndexRemote> storeAt(Tuple<NodeId> tuple) ;
    
    /** Places to try to find a triple pattern.
     *  Each place in the list must be tried.
     * @param tuple
     * @return List<TupleIndex>
     */
    public List<TupleIndexRemote> findAt(Tuple<NodeId> tuple) ;
    
    /** A set of places that covers the entire space, with one replicator per possible place */  
    public Collection<TupleIndexRemote> allFind() ;
    
    /** A set of places that covers the entire space, all places, for storage */  
    public Collection<TupleIndexRemote> allStore() ;
}
