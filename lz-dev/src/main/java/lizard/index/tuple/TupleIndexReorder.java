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

package lizard.index.tuple;


/** Machinary to map between any order+colmap and with col map applied. */
public class TupleIndexReorder {}

//public class TupleIndexReorder extends TupleIndexBase {
//
//    protected TupleIndexReorder(int N, ColumnMap colMapping, String name) {
//        super(N, colMapping, name) ;
//    }
//
//    @Override
//    public Iterator<Tuple<NodeId>> all() {
//        return null ;
//    }
//
//    @Override
//    public long size() {
//        return 0 ;
//    }
//
//    @Override
//    public boolean isEmpty() {
//        return false ;
//    }
//
//    @Override
//    public void clear() {}
//
//    @Override
//    public void sync() {}
//
//    @Override
//    public void close() {}
//
//    @Override
//    protected boolean performAdd(Tuple<NodeId> tuple) {
//        tuple = colMap.map(tuple) ;
//        return performAddRaw(tuple) ;
//    }
//
//    @Override
//    protected boolean performDelete(Tuple<NodeId> tuple) {
//        tuple = colMap.map(tuple) ;
//        return performDeleteRaw(tuple) ;
//    }
//
//    //@Override
//    protected boolean performDeleteRaw(Tuple<NodeId> tuple) {
//        return false ;
//    }
//
//    //@Override
//    protected boolean performAddRaw(Tuple<NodeId> tuple) {
//        return false ;
//    }
//
//    //@Override
//    protected Iterator<Tuple<NodeId>> performFindRaw(Tuple<NodeId> tuple) {
//        return null ;
//    }
//
//    @Override
//    protected Iterator<Tuple<NodeId>> performFind(Tuple<NodeId> tuple) {
//        tuple = colMap.map(tuple) ;
//        return findOrScan(tuple) ;
//    }
//
//    /** Find all matching tuples - a slot of NodeId.NodeIdAny (or null) means match any.
//     *  Input pattern in natural order, not index order.
//     */
//    
//    // Package visibility for testing.
//    final Iterator<Tuple<NodeId>> findOrScan(Tuple<NodeId> pattern)
//    {
//        return findWorker(pattern, true, true) ;
//    }
//    
//    final Iterator<Tuple<NodeId>> findOrPartialScan(Tuple<NodeId> pattern)
//    {
//        return findWorker(pattern, true, false) ;
//    }
//
//    final Iterator<Tuple<NodeId>> findByIndex(Tuple<NodeId> pattern)
//    {
//        return findWorker(pattern, false, false) ;
//    }
//    
//    private Iterator<Tuple<NodeId>> findWorker(Tuple<NodeId> patternMappedOrder, boolean partialScanAllowed, boolean fullScanAllowed)
//    {
//        Tuple<NodeId> pattern = patternMappedOrder ;
////        // Convert to index order.
////        Tuple<NodeId> pattern = colMap.map(patternNaturalOrder) ;
//        
//        // Canonical form.
//        int numSlots = 0 ;
//        int leadingIdx = -2;    // Index of last leading pattern NodeId.  Start less than numSlots-1
//        boolean leading = true ;
//        
//        // Records.
//        Record minRec = factory.createKeyOnly() ;
//        Record maxRec = factory.createKeyOnly() ;
//        
//        // Set the prefixes.
//        for ( int i = 0 ; i < pattern.size() ; i++ )
//        {
//            NodeId X = pattern.get(i) ;
//            if ( NodeId.isAny(X) )
//            {
//                X = null ;
//                // No longer seting leading key slots.
//                leading = false ;
//                continue ;
//            }
//
//            numSlots++ ;
//            if ( leading )
//            {
//                leadingIdx = i ;
//                Bytes.setLong(X.getId(), minRec.getKey(), i*SizeOfNodeId) ;
//                Bytes.setLong(X.getId(), maxRec.getKey(), i*SizeOfNodeId) ;
//            }
//        }
//        
//        // Is it a simple existence test?
//        if ( numSlots == pattern.size() )
//        {
//            if ( index.contains(minRec) )
//                return new SingletonIterator<>(pattern) ;
//            else
//                return new NullIterator<>() ;
//        }
//        
//        Iterator<Record> iter = null ;
//        
//        if ( leadingIdx < 0 )
//        {
//            if ( ! fullScanAllowed )
//                return null ;
//            //System.out.println("Full scan") ;
//            // Full scan necessary
//            iter = index.iterator() ;
//        }
//        else 
//        {
//            // Adjust the maxRec.
//            NodeId X = pattern.get(leadingIdx) ;
//            // Set the max Record to the leading NodeIds, +1.
//            // Example, SP? inclusive to S(P+1)? exclusive where ? is zero. 
//            Bytes.setLong(X.getId()+1, maxRec.getKey(), leadingIdx*SizeOfNodeId) ;
//            iter = index.iterator(minRec, maxRec) ;
//        }
//        
//        //***** return tuples in raw order *****
//        
//        if ( leadingIdx < numSlots-1 ) {
//            if ( ! partialScanAllowed )
//                return null ;
//            // Didn't match all defined slots in request.  
//            // Partial or full scan needed.
//            //pattern.unmap(colMap) ;
//            tuples = scan(tuples, patternNaturalOrder) ;
//            
//        }
//        
//        
//        Iterator<Tuple<NodeId>> tuples = Iter.map(iter, transformToTuple) ;
//        
//        if ( leadingIdx < numSlots-1 )
//        {
//            if ( ! partialScanAllowed )
//                return null ;
//            // Didn't match all defined slots in request.  
//            // Partial or full scan needed.
//            //pattern.unmap(colMap) ;
//            tuples = scan(tuples, patternNaturalOrder) ;
//        }
//        
//        return tuples ;
//    }
//    
////    @Override
////    public Iterator<Tuple<NodeId>> all()
////    {
////        Iterator<Record> iter = index.iterator() ;
////        return Iter.map(iter, transformToTuple) ;
////    }
//    
//    private final Transform<Record, Tuple<NodeId>> transformToTuple = new Transform<Record, Tuple<NodeId>>()
//    {
//        @Override
//        public final Tuple<NodeId> apply(Record item)
//        {
//            return TupleLib.tuple(item, colMap) ;
//        }
//    } ; 
//    
//    private Iterator<Tuple<NodeId>> scan(Iterator<Tuple<NodeId>> iter,
//                                         final Tuple<NodeId> pattern)
//    {
//        Filter<Tuple<NodeId>> filter = new Filter<Tuple<NodeId>>()
//        {
//            @Override
//            public boolean accept(Tuple<NodeId> item)
//            {
//                // Check on pattern and item (both in natural order)
//                for ( int i = 0 ; i < tupleLength ; i++ )
//                {
//                    NodeId n = pattern.get(i) ;
//                    // The pattern must be null/Any or match the tuple being tested.
//                    if ( ! NodeId.isAny(n) )
//                        if ( ! item.get(i).equals(n) ) 
//                            return false ;
//                }
//                return true ;
//            }
//        } ;
//        
//        return Iter.filter(iter, filter) ;
//    }
//    
//}

