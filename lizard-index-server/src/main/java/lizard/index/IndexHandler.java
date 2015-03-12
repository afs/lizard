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

import lizard.api.TLZlib ;
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
import org.seaborne.dboe.transaction.Transactional ;
import org.seaborne.dboe.transaction.Txn ;
import org.seaborne.dboe.transaction.txn.TransactionalBase ;
import org.seaborne.dboe.transaction.txn.journal.Journal ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

/* package */ class IndexHandler implements TLZ_Index.Iface {
    
    private static Logger log = LoggerFactory.getLogger(IndexHandler.class) ;
    private final TupleIndex index ;
    private final String label ;
    private final TransactionalBase transactional ;

    public IndexHandler(String label, TupleIndex index) {
        this.label = label ;
        this.index = index ;
        BPlusTree x = unwrap(index) ;
        Journal journal = Journal.create(Location.mem()) ; 
        this.transactional = new TransactionalBase(journal, x) ;
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
        FmtLog.info(log, "[%d] add %s [%s]", id, index.getName(), index.getName()) ;
        return Txn.executeWriteReturn(transactional, ()->{
            // Verbose.
            boolean b = index.add(tuple2) ;
            return b ;
        });
    }

    @Override
    public boolean idxDelete(long id, TLZ_ShardIndex shard, TLZ_TupleNodeId tuple) throws TException {
        Tuple<NodeId> tuple2 = TLZlib.build(tuple) ;
        // Verbose.
        FmtLog.info(log, "[%d] delete %s [%s]", id, index.getName(), index.getName()) ;
        return Txn.executeWriteReturn(transactional, ()->{
            boolean b = index.delete(tuple2) ;
            return b ;
        });
    }

    @Override
    public List<TLZ_TupleNodeId> idxFind(long id, TLZ_ShardIndex shard, TLZ_TupleNodeId tuple) throws TException {
        Tuple<NodeId> pattern = TLZlib.build(tuple) ;
        FmtLog.info(log, "[%d] find %s [%s]", id, index.getName(), index.getName()) ;
        // TODO XXX Revisit and stream this.
        List<TLZ_TupleNodeId> result = new ArrayList<>() ;
        Txn.executeRead(transactional, ()->{
            Iterator<Tuple<NodeId>> iter = index.find(pattern) ;
            iter.forEachRemaining(t->result.add(TLZlib.build(t))) ;
        }) ;
        return result ;
    }

    @Override
    public long txnBeginRead() throws TException {
        log.warn("TServerIndex:txnBeginRead - not implemented"); 
        return 0 ;
    }

    @Override
    public long txnBeginWrite() throws TException {
        log.warn("TServerIndex:txnBeginWrite - not implemented"); 
        return 0 ;
    }

    @Override
    public void txnPrepare(long txnId) throws TException {}

    @Override
    public void txnCommit(long txnId) throws TException {}

    @Override
    public void txnAbort(long txnId) throws TException {}

    @Override
    public void txnEnd(long txnId) throws TException {}
}
