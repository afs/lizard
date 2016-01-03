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

import java.util.Collection ;
import java.util.Iterator ;

import org.apache.jena.atlas.lib.tuple.Tuple ;
import org.apache.jena.atlas.lib.tuple.TupleMap ;
import org.seaborne.dboe.base.record.Record ;
import org.seaborne.tdb2.store.NodeId ;
import org.seaborne.tdb2.store.tupletable.TupleIndexBase ;

/** Framework for general mapping of Tuples and base storage.
 *  No recording.
 */ 
public class TupleIndexConverter extends TupleIndexBase {

    interface Converter<N,R> {
         R tupleToRecord(Tuple<N> tuple) ;
         Tuple<N> recordToTuple(R record) ;
    }
    
    Converter<NodeId, Record> converter = new Converter<NodeId, Record>() {
        @Override
        public Record tupleToRecord(Tuple<NodeId> tuple) {
            return null ;
        }

        @Override
        public Tuple<NodeId> recordToTuple(Record record) {
            return null ;
        }} ;
    
    protected TupleIndexConverter(int N, TupleMap tupleMap, String name) {
        super(N, tupleMap, name) ;
    }

    @Override
    public Iterator<Tuple<NodeId>> all() {
        return null ;
    }

    @Override
    public long size() {
        return 0 ;
    }

    @Override
    public boolean isEmpty() {
        return false ;
    }

    @Override
    public void clear() {}

    @Override
    public void sync() {}

    @Override
    public void close() {}

    @Override
    public void addAll(Collection<Tuple<NodeId>> tuples) {}

    @Override
    public void deleteAll(Collection<Tuple<NodeId>> tuples) {}

    @Override
    protected void performAdd(Tuple<NodeId> tuple) {
    }

    @Override
    protected void performDelete(Tuple<NodeId> tuple) {
    }

    @Override
    protected Iterator<Tuple<NodeId>> performFind(Tuple<NodeId> tuple) {
        return null ;
    }
}

