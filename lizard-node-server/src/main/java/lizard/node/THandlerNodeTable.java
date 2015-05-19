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
import lizard.api.TxnHandler ;
import lizard.api.TLZ.TLZ_NodeId ;
import lizard.api.TLZ.TLZ_NodeTable ;
import lizard.api.TLZ.TLZ_RDF_Term ;
import lizard.comms.thrift.ThriftLib ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.jena.graph.Node ;
import org.apache.jena.riot.out.NodeFmtLib ;
import org.apache.thrift.TException ;
import org.seaborne.dboe.base.file.Location ;
import org.seaborne.dboe.trans.bplustree.BPlusTree ;
import org.seaborne.dboe.trans.data.TransBinaryDataFile ;
import org.seaborne.dboe.transaction.txn.TransactionalBase ;
import org.seaborne.dboe.transaction.txn.TransactionalSystem ;
import org.seaborne.dboe.transaction.txn.journal.Journal ;
import org.seaborne.tdb2.store.NodeId ;
import org.seaborne.tdb2.store.nodetable.NodeTable ;
import org.seaborne.tdb2.store.nodetable.NodeTableCache ;
import org.seaborne.tdb2.store.nodetable.NodeTableTRDF ;
import org.seaborne.tdb2.store.nodetable.NodeTableWrapper ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

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
    
    private static TransactionalSystem init(NodeTable nodeTable) {
        while ( nodeTable instanceof NodeTableWrapper )
            nodeTable = ((NodeTableWrapper)nodeTable).wrapped() ;
        if ( nodeTable instanceof NodeTableCache )
            nodeTable = ((NodeTableCache)nodeTable).wrapped() ;
        if ( ! ( nodeTable instanceof NodeTableTRDF ) )
            log.warn("Not a TDB2 node table") ;
        NodeTableTRDF nt = (NodeTableTRDF)nodeTable ;
        
        BPlusTree bpt = (BPlusTree)nt.getIndex() ; 
        TransBinaryDataFile bdf = (TransBinaryDataFile)nt.getData() ;
        // XXX !!!!!
        log.warn("Ad-hoc memory journal");  
        Journal journal = Journal.create(Location.mem()) ; 
        return new TransactionalBase(journal, bdf, bpt) ;
    }
    
    @Override
    public void nodePing() throws TException {
        log.info("ping") ;
    }

    @Override
    public TLZ_NodeId allocNodeId(long id, long txnId, TLZ_RDF_Term nz) throws TException {
        if ( txnId <= 0 )
            FmtLog.info(log, "[%d] txnId = %d", id, txnId) ;
        Node n = ThriftLib.decodeFromTLZ(nz) ;
        return txnAlwaysReturn(txnId, WRITE, ()-> {
            NodeId nid = nodeTable.getAllocateNodeId(n) ;
            TLZ_NodeId nidz = new TLZ_NodeId() ;
            nidz.setNodeId(nid.getId()) ;
            FmtLog.info(log, "[%d:%d] Node alloc request : %s => %s", id, txnId, n, nid) ;
            return nidz ;
        }) ;
    }

    @Override
    public TLZ_NodeId findByNode(long id, long txnId, TLZ_RDF_Term nz) throws TException {
        Node n = ThriftLib.decodeFromTLZ(nz) ;
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
    public TLZ_RDF_Term findByNodeId(long id, long txnId, TLZ_NodeId nz) throws TException {
        NodeId nid = NodeId.create(nz.getNodeId()) ;
        return txnAlwaysReturn(txnId, READ, ()-> {
            Node n = nodeTable.getNodeForNodeId(nid) ;
            if ( n == null )
                FmtLog.error(log, "NodeId not found: "+nid) ;
            String str = NodeFmtLib.str(n) ;
            FmtLog.info(log, "[%d:%d] NodeId get request : %s => %s", id, txnId, nid, n) ;
            TLZ_RDF_Term nlz = ThriftLib.encodeToTLZ(n) ;
            return nlz ;
        }) ;
    }
}
