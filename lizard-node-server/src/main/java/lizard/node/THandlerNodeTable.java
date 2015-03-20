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

import lizard.adapters.AdapterObjectFile ;
import lizard.adapters.AdapterRangeIndex ;
import lizard.api.TxnHandler ;
import lizard.api.TLZ.TLZ_Node ;
import lizard.api.TLZ.TLZ_NodeId ;
import lizard.api.TLZ.TLZ_NodeTable ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.jena.riot.out.NodeFmtLib ;
import org.apache.thrift.TException ;
import org.seaborne.dboe.base.file.Location ;
import org.seaborne.dboe.index.RangeIndex ;
import org.seaborne.dboe.trans.bplustree.BPlusTree ;
import org.seaborne.dboe.trans.data.TransObjectFile ;
import org.seaborne.dboe.transaction.txn.TransactionalBase ;
import org.seaborne.dboe.transaction.txn.journal.Journal ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import com.hp.hpl.jena.graph.Node ;
import com.hp.hpl.jena.sparql.sse.SSE ;
import com.hp.hpl.jena.tdb.store.NodeId ;
import com.hp.hpl.jena.tdb.store.nodetable.NodeTable ;
import com.hp.hpl.jena.tdb.store.nodetable.NodeTableCache ;
import com.hp.hpl.jena.tdb.store.nodetable.NodeTableWrapper ;

import static com.hp.hpl.jena.query.ReadWrite.* ;

//XXX Needs efficiency attention.
/* package */ class THandlerNodeTable extends TxnHandler implements TLZ_NodeTable.Iface {
    private static Logger log = LoggerFactory.getLogger(THandlerNodeTable.class) ;
    @Override
    protected Logger log() { return log ; }
    
    private final String label ;
    private final NodeTable nodeTable ;
    
    @Override
    protected String getLabel() { return label ; }    

    public THandlerNodeTable(String label, NodeTable nodeTable) {
        super(init(nodeTable)) ;
        this.label = label ;
        this.nodeTable = nodeTable ;
    }
    
    private static TransactionalBase init(NodeTable nodeTable) {
        while ( nodeTable instanceof NodeTableWrapper )
            nodeTable = ((NodeTableWrapper)nodeTable).wrapped() ;
        if ( nodeTable instanceof NodeTableCache )
            nodeTable = ((NodeTableCache)nodeTable).wrapped() ;
        if ( ! ( nodeTable instanceof NodeTableDBOE ) )
            log.warn("Not a DBOE node table") ;
        NodeTableDBOE nt = (NodeTableDBOE)nodeTable ;
        
        // Unpick and transactionalize.
        // ObjectFile.
        AdapterObjectFile aof = ((AdapterObjectFile)nt.getObjectFile()) ;
        TransObjectFile of = (TransObjectFile)aof.getUnderlyingObjectFile() ;
        
        AdapterRangeIndex ari = (AdapterRangeIndex)(nt.getIndex()) ;
        RangeIndex ri = ari.getUnderlyingRangeIndex() ;
        BPlusTree bpt = (BPlusTree)ri ; 
        
        // XXX !!!!!
        log.warn("Ad-hoc memory journal");  
        Journal journal = Journal.create(Location.mem()) ; 
        return new TransactionalBase(journal, of, bpt) ;
    }
    
    @Override
    public void nodePing() throws TException {
        log.info("ping") ;
    }

    @Override
    public TLZ_NodeId allocNodeId(long id, long txnId, TLZ_Node nz) throws TException {
        if ( txnId <= 0 )
            FmtLog.info(log, "[%d] txnId = %d", id, txnId) ;
        Node n = SSE.parseNode(nz.getNodeStr()) ;
        return txnAlwaysReturn(txnId, WRITE, ()-> {
            NodeId nid = nodeTable.getAllocateNodeId(n) ;
            TLZ_NodeId nidz = new TLZ_NodeId() ;
            nidz.setNodeId(nid.getId()) ;
            FmtLog.info(log, "[%d:%d] Node alloc request : %s => %s", id, txnId, n, nid) ;
            return nidz ;
        }) ;
    }

    @Override
    public TLZ_NodeId findByNode(long id, long txnId, TLZ_Node nz) throws TException {
        Node n = SSE.parseNode(nz.getNodeStr()) ;
        return txnAlwaysReturn(txnId, READ, ()-> {
            NodeId nid = nodeTable.getNodeIdForNode(n) ;
            // XXX Remove little structs
            TLZ_NodeId nidz = new TLZ_NodeId() ;
            nidz.setNodeId(nid.getId()) ;
            FmtLog.info(log, "[%d:%d] Node get request : %s => %s", id, txnId, n, nid) ;
            return nidz ;
        }) ;
    }

    @Override
    public TLZ_Node findByNodeId(long id, long txnId, TLZ_NodeId nz) throws TException {
        NodeId nid = NodeId.create(nz.getNodeId()) ;
        return txnAlwaysReturn(txnId, READ, ()-> {
            Node n = nodeTable.getNodeForNodeId(nid) ;
            if ( n == null )
                FmtLog.error(log, "NodeId not found: "+nid) ;
            String str = NodeFmtLib.str(n) ;
            FmtLog.info(log, "[%d:%d] NodeId get request : %s => %s", id, txnId, nid, n) ;
            TLZ_Node nlz = new TLZ_Node().setNodeStr(str) ;
            return nlz ;
        }) ;
    }
}
