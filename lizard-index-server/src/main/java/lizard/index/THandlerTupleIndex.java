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
import lizard.system.LzLog ;
import org.apache.jena.atlas.lib.tuple.Tuple ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.jena.dboe.trans.bplustree.BPlusTree ;
import org.apache.jena.dboe.transaction.txn.TransactionalSystem ;
import org.apache.jena.tdb2.store.NodeId ;
import org.apache.jena.tdb2.store.tupletable.TupleIndex ;
import org.apache.jena.tdb2.store.tupletable.TupleIndexRecord ;
import org.apache.thrift.TException ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

/* package */ class THandlerTupleIndex extends TxnHandler implements TLZ_Index.Iface {
    
    private static Logger log = LoggerFactory.getLogger(THandlerTupleIndex.class) ;
    private static Logger logtxn = LoggerFactory.getLogger(LzLog.logTxnBase+".IndexTxn") ;
    
    @Override
    protected Logger log() { return log ; }
    @Override
    protected Logger logtxn() { return logtxn ; }
    
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
    
    // Single tuple add/delete. Not efficient.  Sort of autocommit.
    // Batches are better; this code helps in small scale changes
    // and setting up tests.  Autocommit per node is not safe/consistent
    // across the cluster but at least does not corrupt local storage.
    
    @Override
    public void idxAdd(long id, long txnId, TLZ_ShardIndex shard, TLZ_TupleNodeId tuple) throws TException {
        Tuple<NodeId> tuple2 = TLZlib.build(tuple) ;
        FmtLog.info(log, "[%d:%d] add %s %s", id, txnId, index.getName(), tuple2) ;
        txnAlways(txnId, WRITE, () -> index.add(tuple2)) ;
    }

    @Override
    public void idxAddAll(long id, long txnId, TLZ_ShardIndex shard, List<TLZ_TupleNodeId> tuples) throws TException {
        FmtLog.info(log, "[%d] idxAddAll(%d) : txnId = %d", id, tuples.size(), txnId) ;
        txnAlways(txnId, WRITE, () -> {
            for ( TLZ_TupleNodeId tuple : tuples ) {
                Tuple<NodeId> tuple2 = TLZlib.build(tuple) ;
                //FmtLog.info(log, "[%d:%d] add %s %s", id, txnId, index.getName(), tuple2) ;
                index.add(tuple2) ;
            }
        }) ;
    }

    @Override
    public void idxDelete(long id, long txnId, TLZ_ShardIndex shard, TLZ_TupleNodeId tuple) throws TException {
        Tuple<NodeId> tuple2 = TLZlib.build(tuple) ;
        FmtLog.info(log, "[%d:%d] delete %s %s", id, txnId, index.getName(), tuple2) ;
        txnAlways(txnId, WRITE, () -> index.delete(tuple2) ) ;
    }

    @Override
    public void idxDeleteAll(long id, long txnId, TLZ_ShardIndex shard, List<TLZ_TupleNodeId> tuples) throws TException {
        FmtLog.debug(log, "[%d] idxDeleteBulk : txnId = %d", id, txnId) ;
        txnAlways(txnId, WRITE, () -> {
            for ( TLZ_TupleNodeId tuple : tuples ) {
                Tuple<NodeId> tuple2 = TLZlib.build(tuple) ;
                FmtLog.info(log, "[%d:%d] delete %s %s", id, txnId, index.getName(), tuple2) ;
                index.delete(tuple2) ;
            }
        }) ;
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
