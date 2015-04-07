/**
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

package migrate;

import java.util.Iterator ;

import org.apache.jena.atlas.iterator.Iter ;
import org.apache.jena.atlas.lib.ColumnMap ;
import org.apache.jena.atlas.lib.Tuple ;

import com.hp.hpl.jena.tdb.store.NodeId ;
import com.hp.hpl.jena.tdb.store.tupletable.TupleIndexBase ;

/** An immutably empty tuple idnex - it is a sink (add provided but does nothing) */
public class TupleIndexEmpty extends TupleIndexBase {

    public TupleIndexEmpty(ColumnMap colMapping, String name) {
        super(colMapping.length(), colMapping, name) ;
    }

    @Override
    public Iterator<Tuple<NodeId>> all() {
        return Iter.nullIterator() ;
    }

    @Override
    public long size() {
        return 0 ;
    }

    @Override
    public boolean isEmpty() {
        return true ;
    }

    @Override
    public void clear() {}

    @Override
    public void sync() {}

    @Override
    public void close() {}

    @Override
    protected boolean performAdd(Tuple<NodeId> tuple) {
        return false ;
    }

    @Override
    protected boolean performDelete(Tuple<NodeId> tuple) {
        return false ;
    }

    @Override
    protected Iterator<Tuple<NodeId>> performFind(Tuple<NodeId> tuple) {
        return Iter.nullIterator() ;
    }
}
