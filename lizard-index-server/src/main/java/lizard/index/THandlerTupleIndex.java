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

import static org.apache.jena.query.ReadWrite.READ ;
import static org.apache.jena.query.ReadWrite.WRITE ;

import java.util.ArrayList ;
import java.util.Iterator ;
import java.util.List ;

import lizard.api.TLZlib ;
import lizard.api.TxnHandler ;
import lizard.api.TLZ.TLZ_Index ;
import lizard.api.TLZ.TLZ_ShardIndex ;
import lizard.api.TLZ.TLZ_TupleNodeId ;

import org.apache.jena.atlas.lib.Tuple ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.thrift.TException ;
import org.seaborne.dboe.trans.bplustree.BPlusTree ;
import org.seaborne.dboe.transaction.txn.TransactionalSystem ;
import org.seaborne.tdb2.store.NodeId ;
import org.seaborne.tdb2.store.tupletable.TupleIndex ;
import org.seaborne.tdb2.store.tupletable.TupleIndexRecord ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

/* package */ class THandlerTupleIndex extends TxnHandler implements TLZ_Index.Iface {
    
    private static Logger log = LoggerFactory.getLogger(THandlerTupleIndex.class) ;
    @Override
    protected Logger log() { return log ; }
    
    private final String label ;
    @Override
    protected String getLabel() { return label ; }

    private final TupleIndex index ;

    public THandlerTupleIndex(TransactionalSystem txnSystem, String label, TupleIndex index) {
        super(txnSystem) ;
        this.label = label ;
        this.index = index ;
    }
    
    static BPlusTree unwrap(TupleIndex index) {
        TupleIndexRecord tir = (TupleIndexRecord)index ;
        return (BPlusTree)(tir.getRangeIndex()) ;
    }
    
    @Override
    public void idxPing() throws TException {
        log.info("ping") ;
    }

    // Single tuple add/delete. Not efficient.  Sort of autocommit.
    // Batches are better; this code helps in small scale changes
    // and setting up tests.  Autocommit per node is not safe/consistent
    // across the cluster but at least does not corrupt local storage.
    
    @Override
    public boolean idxAdd(long id, long txnId, TLZ_ShardIndex shard, TLZ_TupleNodeId tuple) throws TException {
        Tuple<NodeId> tuple2 = TLZlib.build(tuple) ;
        FmtLog.info(log, "[%d:%d] add %s %s", id, txnId, index.getName(), tuple2) ;
        return txnAlwaysReturn(txnId, WRITE, () -> index.add(tuple2)) ;
    }

    @Override
    public boolean idxDelete(long id, long txnId, TLZ_ShardIndex shard, TLZ_TupleNodeId tuple) throws TException {
        Tuple<NodeId> tuple2 = TLZlib.build(tuple) ;
        FmtLog.info(log, "[%d:%d] delete %s %s", id, txnId, index.getName(), tuple2) ;
        return txnAlwaysReturn(txnId, WRITE, () -> index.delete(tuple2) ) ;
    }

    @Override
    public List<TLZ_TupleNodeId> idxFind(long id, long txnId, TLZ_ShardIndex shard, TLZ_TupleNodeId tuple) throws TException {
        Tuple<NodeId> pattern = TLZlib.build(tuple) ;
        
        FmtLog.info(log, "[%d:%d] find %s %s", id, txnId, index.getName(), pattern) ;
        // TODO XXX Revisit and stream this.
        List<TLZ_TupleNodeId> result = new ArrayList<>() ;
        txnAlways(txnId, READ, ()->{
            Iterator<Tuple<NodeId>> iter = index.find(pattern) ;
            iter.forEachRemaining(t->result.add(TLZlib.build(t))) ;
        }) ;
        return result ;
    }
}
