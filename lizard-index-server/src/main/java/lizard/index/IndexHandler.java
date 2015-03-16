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

package lizard.index;

import java.util.ArrayList ;
import java.util.Iterator ;
import java.util.List ;

import lizard.adapters.AdapterRangeIndex ;
import lizard.api.TLZlib ;
import lizard.api.TxnHandler ;
import lizard.api.TLZ.TLZ_Index ;
import lizard.api.TLZ.TLZ_ShardIndex ;
import lizard.api.TLZ.TLZ_TupleNodeId ;

import com.hp.hpl.jena.tdb.store.NodeId ;
import com.hp.hpl.jena.tdb.store.tupletable.TupleIndex ;
import com.hp.hpl.jena.tdb.store.tupletable.TupleIndexRecord ;

import org.apache.jena.atlas.lib.Tuple ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.thrift.TException ;
import org.seaborne.dboe.base.file.Location ;
import org.seaborne.dboe.index.RangeIndex ;
import org.seaborne.dboe.trans.bplustree.BPlusTree ;
import org.seaborne.dboe.transaction.Txn ;
import org.seaborne.dboe.transaction.txn.TransactionalBase ;
import org.seaborne.dboe.transaction.txn.journal.Journal ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

/* package */ class IndexHandler extends TxnHandler implements TLZ_Index.Iface {
    
    private static Logger log = LoggerFactory.getLogger(IndexHandler.class) ;
    @Override
    protected Logger getLog() { return log ; }
    
    private final String label ;
    @Override
    protected String getLabel() { return label ; }

    private final TupleIndex index ;

    public IndexHandler(String label, TupleIndex index) {
        super(init(index)) ;
        
        this.label = label ;
        this.index = index ;
    }
    
    private static TransactionalBase init(TupleIndex index) {
        BPlusTree x = unwrap(index) ;
        // XXX !!!!!
        log.warn("Ad-hoc memory journal");  
        Journal journal = Journal.create(Location.mem()) ; 
        return new TransactionalBase(journal, x) ;
    }

    static BPlusTree unwrap(TupleIndex index) {
        TupleIndexRecord tir = (TupleIndexRecord)index ;
        AdapterRangeIndex ari = (AdapterRangeIndex)(tir.getRangeIndex()) ;
        RangeIndex ri = ari.getUnderlyingRangeIndex() ;
        return (BPlusTree)ri ;
    }
    
    @Override
    public void idxPing() throws TException {
        log.info("ping") ;
    }

    @Override
    public boolean idxAdd(long id, TLZ_ShardIndex shard, TLZ_TupleNodeId tuple) throws TException {
        Tuple<NodeId> tuple2 = TLZlib.build(tuple) ;
        FmtLog.info(log, "[%d] add %s %s", id, index.getName(), tuple2) ;
        return writeTxnAlwaysReturn(() -> index.add(tuple2)) ;
    }

    // Single tuple add/delete. Not efficient.  Sort of autocommit.
    // Batches are better; this code hels in small scale changes
    // and setting up tests.
    
    @Override
    public boolean idxDelete(long id, TLZ_ShardIndex shard, TLZ_TupleNodeId tuple) throws TException {
        Tuple<NodeId> tuple2 = TLZlib.build(tuple) ;
        FmtLog.info(log, "[%d] delete %s %s", id, index.getName(), tuple2) ;
        return writeTxnAlwaysReturn(() -> index.delete(tuple2) ) ;
    }

    @Override
    public List<TLZ_TupleNodeId> idxFind(long id, TLZ_ShardIndex shard, TLZ_TupleNodeId tuple) throws TException {
        Tuple<NodeId> pattern = TLZlib.build(tuple) ;
        
        FmtLog.info(log, "[%d] find %s %s", id, index.getName(), pattern) ;
        // TODO XXX Revisit and stream this.
        // TODO Respect trasnaction id.
        List<TLZ_TupleNodeId> result = new ArrayList<>() ;
        Txn.executeRead(transactional, ()->{
            Iterator<Tuple<NodeId>> iter = index.find(pattern) ;
            iter.forEachRemaining(t->result.add(TLZlib.build(t))) ;
        }) ;
        return result ;
    }
}
