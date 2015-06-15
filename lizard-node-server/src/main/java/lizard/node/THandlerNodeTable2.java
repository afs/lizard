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

package lizard.node;

import static org.apache.jena.query.ReadWrite.READ ;
import static org.apache.jena.query.ReadWrite.WRITE ;

import java.util.HashMap ;
import java.util.Map ;

import lizard.api.TxnHandler ;
import lizard.api.TLZ.TLZ_ThriftObjectTable ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.jena.riot.thrift.wire.RDF_Term ;
import org.apache.thrift.TException ;
import org.seaborne.dboe.index.Index ;
import org.seaborne.dboe.transaction.txn.TransactionalSystem ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

public
/* package */ class THandlerNodeTable2 extends TxnHandler implements TLZ_ThriftObjectTable.Iface {
    private static Logger log = LoggerFactory.getLogger(THandlerNodeTable2.class) ;
    @Override
    protected Logger log() { return log ; }
    
    private final String label ;
    private final ThriftObjectFile objectFile ;
    private final Idx<RDF_Term, Long> index ;
    
    @Override
    protected String getLabel() { return label ; }    

    interface Idx<K,V> { 
        V get(K key) ;
        boolean contains(K key) ;
        void put(K key, V value) ;
    }
    
    // XXX TEMP
    static class IdxTermLong implements Idx<RDF_Term, Long> {
        //private final Index index ;
        private final Map<RDF_Term, Long> map ;
        
        public IdxTermLong(Index index) {
            //this.index = index ;
            map = new HashMap<>() ;
        }
        
        @Override
        public Long get(RDF_Term key) {
            return map.get(key) ;
        }

        @Override
        public boolean contains(RDF_Term key) {
            return map.containsKey(key) ;
        }

        @Override
        public void put(RDF_Term key, Long value) {
            map.put(key, value) ;
        }
        
    }
    
    public THandlerNodeTable2(TransactionalSystem txnSystem, String label,
                              Idx<RDF_Term, Long> index,
                              ThriftObjectFile objectFile) {
        super(txnSystem) ;
        this.label = label ;
        this.index = index ; 
        this.objectFile = objectFile ;
    }
    
    @Override
    public long allocNodeId(long requestId, long txnId, RDF_Term nz) throws TException {
        //FmtLog.debug(log, "[%d] allocNodeId : txnId = %d", id, txnId) ;
        // XXX May already exist.
        checkActive() ;
        return txnAlwaysReturn(txnId, WRITE, ()-> {
            long x = objectFile.writeToTable(nz) ;
            index.put(nz, x) ;
            FmtLog.info(log, "[%d:%d] Node alloc request : %s => %s", requestId, txnId, nz, x) ;
            return x ;
        }) ;
    }

    @Override
    public long findByTerm(long requestId, long txnId, RDF_Term nz) throws TException {
        //FmtLog.debug(log, "[%d] findByNode : txnId = %d", id, txnId) ;
        checkActive() ;
        return txnAlwaysReturn(txnId, READ, ()-> {
            Long x = index.get(nz) ;
            if ( x == null )
                return -8L ; // NodeDoesNotExist
            FmtLog.info(log, "[%d:%d] Node get request : %s => %s", requestId, txnId, nz, x) ;
            return x ;
        }) ;
    }

    @Override
    public RDF_Term findById(long requestId, long txnId, long item) throws TException {
        //FmtLog.debug(log, "[%d] findByNodeId : txnId = %d", id, txnId) ;
        checkActive() ;
        return txnAlwaysReturn(txnId, READ, ()-> {
            RDF_Term nlz =  objectFile.readFromTable(item) ;
            FmtLog.info(log, "[%d:%d] NodeId get request : %s => %s", requestId, txnId, item, nlz) ;
            return nlz ;
        }) ;
    }

//    @Override
//    public List<TLZ_NodeId> allocNodeIds(long requestId, long txnId, List<RDF_Term> nodes) throws TException {
//        FmtLog.info(log, "[%d] allocNodeIds(%d) : txnId = %d", requestId, nodes.size(), txnId) ;
//        checkActive() ;
//        return txnAlwaysReturn(txnId, WRITE, ()-> {
//            // Local bulk operations?
//            List<TLZ_NodeId> nodeids = new ArrayList<>(nodes.size()) ;
//            for ( RDF_Term nz : nodes ) {
//                Node n = decodeFromTLZ(nz) ;
//                NodeId nid = nodeTable.getAllocateNodeId(n) ;
//                TLZ_NodeId nidz = new TLZ_NodeId() ;
//                nidz.setNodeId(nid.getId()) ;
//                nodeids.add(nidz) ;
//                //FmtLog.info(log, "[%d:%d] Batched node alloc : %s => %s", id, txnId, n, nid) ;
//            }
//            return nodeids ;
//        }) ;
//    }
//
//    @Override
//    public List<RDF_Term> lookupNodeIds(long requestId, long txnId, List<TLZ_NodeId> nodeIds) throws TException {
//        FmtLog.debug(log, "[%d] lookupNodeIds : txnId = %d", requestId, txnId) ;
//        checkActive() ;
//        return txnAlwaysReturn(txnId, WRITE, ()-> {
//            List<RDF_Term> nodes = new ArrayList<>(nodeIds.size()) ;
//            for ( TLZ_NodeId nz : nodeIds ) {
//                // Local bulk operations?
//                NodeId nid = decodeFromTLZ(nz) ;
//                Node n = nodeTable.getNodeForNodeId(nid) ;
//                nodes.add(encodeToTLZ(n)) ;
//                //FmtLog.info(log, "[%d:%d] Batched node alloc : %s => %s", id, txnId, n, nid) ;
//            }
//            return nodes ;
//        }) ;
//    }
}
