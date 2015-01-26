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

package lizard.query;

import java.util.Iterator ;

import org.apache.jena.atlas.lib.Tuple ;
import org.seaborne.jena.engine.PredicateObjectList ;
import org.seaborne.jena.engine.Row ;
import org.seaborne.jena.engine.Slot ;
import org.seaborne.jena.engine.access.Accessor ;
import org.seaborne.jena.engine.access.AccessorBase ;
import org.seaborne.jena.engine.tdb.AccessorTDB ;
import org.seaborne.jena.engine.tdb.AccessorTDBDebug ;
import org.seaborne.jena.engine.tdb.StorageTDB ;

import com.hp.hpl.jena.tdb.store.NodeId ;

public class AccessorRemote extends AccessorTDB implements Accessor<NodeId> {
    public static AccessorTDB create(StorageTDB db) {
        AccessorTDB a = new AccessorRemote(db) ;
        a = new AccessorTDBDebug("Lizard: ", a) ;
        return a ;
    }
    
    // Temp way
    private AccessorRemote(StorageTDB db) {
        super(db) ;
    }
    
    @Override
    public Iterator<Tuple<NodeId>> accessTuples(Tuple<NodeId> pattern) {
        // just while we wire the system
        return getDB().getTupleTable(pattern).find(pattern) ;
    }

    @Override
    public Iterator<Row<NodeId>> accessRows(Tuple<Slot<NodeId>> pattern) {
        return AccessorBase.accessRows(this,pattern) ; 
    }

    @Override
    public Iterator<Row<NodeId>> fetch(PredicateObjectList<NodeId> accessList) {
        return AccessorBase.fetch(this, accessList).iterator() ;
    }
}
