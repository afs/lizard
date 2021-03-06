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

package lizard.node;

import static lizard.comms.thrift.ThriftLib.decodeFromTLZ ;
import static lizard.comms.thrift.ThriftLib.encodeToTLZ ;
import static org.apache.jena.query.ReadWrite.READ ;
import static org.apache.jena.query.ReadWrite.WRITE ;

import java.util.ArrayList ;
import java.util.List ;

import lizard.api.TxnHandler ;
import lizard.api.TLZ.TLZ_NodeId ;
import lizard.api.TLZ.TLZ_NodeTable ;
import lizard.system.LzLog ;
import org.apache.jena.atlas.lib.Bytes ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.jena.dboe.transaction.txn.TransactionalSystem ;
import org.apache.jena.graph.Node ;
import org.apache.jena.riot.out.NodeFmtLib ;
import org.apache.jena.riot.thrift.wire.RDF_Term ;
import org.apache.jena.tdb2.store.NodeId ;
import org.apache.jena.tdb2.store.NodeIdFactory ;
import org.apache.jena.tdb2.store.nodetable.NodeTable ;
import org.apache.thrift.TException ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

//XXX Needs efficiency attention.
/* package */ class THandlerNodeTable extends TxnHandler implements TLZ_NodeTable.Iface {
    private static Logger log = LoggerFactory.getLogger(THandlerNodeTable.class) ;
    private static Logger logtxn = LoggerFactory.getLogger(LzLog.logTxnBase+".NodeTableTxn") ;
    
    @Override
    protected Logger log() { return log ; }
    @Override
    protected Logger logtxn() { return logtxn ; }
    
    private final String label ;
    private final NodeTable nodeTable ;
    
    @Override
    protected String getLabel() { return label ; }    

    public THandlerNodeTable(TransactionalSystem txnSystem, String label, NodeTable nodeTable) {
        super(txnSystem) ;
        this.label = label ;
        this.nodeTable = nodeTable ;
    }
    
    @Override
    public TLZ_NodeId allocNodeId(long id, long txnId, RDF_Term nz) throws TException {
        //FmtLog.debug(log, "[%d] allocNodeId : txnId = %d", id, txnId) ;
        checkActive() ;
        Node n = decodeFromTLZ(nz) ;
        return txnAlwaysReturn(txnId, WRITE, ()-> {
            NodeId nid = nodeTable.getAllocateNodeId(n) ;
            TLZ_NodeId nidz = new TLZ_NodeId() ;
            nidz.setNodeId(toLong(nid)) ;
            FmtLog.info(log, "[%d:%d] Node alloc request : %s => %s", id, txnId, n, nid) ;
            return nidz ;
        }) ;
    }

    @Override
    public TLZ_NodeId findByNode(long id, long txnId, RDF_Term nz) throws TException {
        //FmtLog.debug(log, "[%d] findByNode : txnId = %d", id, txnId) ;
        checkActive() ;
        Node n = decodeFromTLZ(nz) ;
        return txnAlwaysReturn(txnId, READ, ()-> {
            NodeId nid = nodeTable.getNodeIdForNode(n) ;
            // XXX Remove little structs
            TLZ_NodeId nidz = new TLZ_NodeId() ;
            nidz.setNodeId(toLong(nid)) ;
            FmtLog.info(log, "[%d:%d] Node get request : %s => %s", id, txnId, n, nid) ;
            return nidz ;
        }) ;
    }

    @Override
    public RDF_Term findByNodeId(long id, long txnId, TLZ_NodeId nz) throws TException {
        //FmtLog.debug(log, "[%d] findByNodeId : txnId = %d", id, txnId) ;
        checkActive() ;
        //NodeId nid = NodeId.create(nz.getNodeId()) ;
        NodeId nid = fromLong(nz.getNodeId());
        
        return txnAlwaysReturn(txnId, READ, ()-> {
            Node n = nodeTable.getNodeForNodeId(nid) ;
            if ( n == null )
                FmtLog.error(log, "NodeId not found: "+nid) ;
            String str = NodeFmtLib.str(n) ;
            FmtLog.info(log, "[%d:%d] NodeId get request : %s => %s", id, txnId, nid, n) ;
            RDF_Term nlz = encodeToTLZ(n) ;
            return nlz ;
        }) ;
    }

    @Override
    public List<TLZ_NodeId> allocNodeIds(long id, long txnId, List<RDF_Term> nodes) throws TException {
        FmtLog.info(log, "[%d] allocNodeIds(%d) : txnId = %d", id, nodes.size(), txnId) ;
        checkActive() ;
        return txnAlwaysReturn(txnId, WRITE, ()-> {
            // Local bulk operations?
            List<TLZ_NodeId> nodeids = new ArrayList<>(nodes.size()) ;
            for ( RDF_Term nz : nodes ) {
                Node n = decodeFromTLZ(nz) ;
                NodeId nid = nodeTable.getAllocateNodeId(n) ;
                TLZ_NodeId nidz = new TLZ_NodeId() ;
                nidz.setNodeId(toLong(nid)) ;
                nodeids.add(nidz) ;
                //FmtLog.info(log, "[%d:%d] Batched node alloc : %s => %s", id, txnId, n, nid) ;
            }
            return nodeids ;
        }) ;
    }

    @Override
    public List<RDF_Term> lookupNodeIds(long id, long txnId, List<TLZ_NodeId> nodeIds) throws TException {
        FmtLog.debug(log, "[%d] lookupNodeIds : txnId = %d", id, txnId) ;
        checkActive() ;
        return txnAlwaysReturn(txnId, WRITE, ()-> {
            List<RDF_Term> nodes = new ArrayList<>(nodeIds.size()) ;
            for ( TLZ_NodeId nz : nodeIds ) {
                // Local bulk operations?
                NodeId nid = decodeFromTLZ(nz) ;
                Node n = nodeTable.getNodeForNodeId(nid) ;
                nodes.add(encodeToTLZ(n)) ;
                //FmtLog.info(log, "[%d:%d] Batched node alloc : %s => %s", id, txnId, n, nid) ;
            }
            return nodes ;
        }) ;
    }
    
    // [UPDATE] 
    // Hack to make compilable.
    static private long toLong(NodeId nid) {
        byte[] b = new byte[8];
        NodeIdFactory.set(nid, b);
        long v = Bytes.getLong(b);
        return v ;
    }
    
    private NodeId fromLong(long nodeId) {
        byte[] b = Bytes.packLong(nodeId);
        return NodeIdFactory.get(b);
    }
}
