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

package conf2.build;

import org.seaborne.dboe.base.file.Location ;
import org.seaborne.dboe.base.record.RecordFactory ;
import org.seaborne.dboe.index.Index ;
import org.seaborne.dboe.index.RangeIndex ;
import org.seaborne.dboe.transaction.txn.TransactionCoordinator ;
import org.seaborne.tdb2.setup.StoreParams ;
import org.seaborne.tdb2.store.nodetable.NodeTable ;
import org.seaborne.tdb2.store.nodetable.NodeTableCache ;
import org.seaborne.tdb2.store.tupletable.TupleIndex ;

public class Lz2BuildDBOE {

    public static TupleIndex createTupleIndex(TransactionCoordinator coord, Location loc, StoreParams params, String indexOrder, String name) {
        System.err.println("createTupleIndex") ;
        return Builder2.buildTupleIndex(coord, /*compoentid*/null, loc, /*StoreParams*/null, name, indexOrder) ;
    }

    public static NodeTable createNodeTable(TransactionCoordinator coord, Location loc, StoreParams params, String name) {
        System.err.println("createNodeTable") ;
        NodeTable nt = Builder2.buildBaseNodeTable(coord, /*componentid*/null, loc, params, name) ;
        // No inline.
        nt = NodeTableCache.create(nt, params) ;
        return nt ;
    }

    public static Index createIndex(Location loc, String name) {
        System.err.println("createIndex") ;
        return null ;
    }

    
    public static RangeIndex createRangeIndex(TransactionCoordinator coord, Location loc, RecordFactory recordFactory, String indexOrder) {
        System.err.println("createRangeIndex") ;
        return null ;
    }

    
}

