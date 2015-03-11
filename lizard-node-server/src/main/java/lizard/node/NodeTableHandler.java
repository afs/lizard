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

import lizard.api.TLZ.TLZ_Node ;
import lizard.api.TLZ.TLZ_NodeId ;
import lizard.api.TLZ.TLZ_NodeTable ;

import com.hp.hpl.jena.graph.Node ;
import com.hp.hpl.jena.sparql.sse.SSE ;
import com.hp.hpl.jena.tdb.store.NodeId ;
import com.hp.hpl.jena.tdb.store.nodetable.NodeTable ;

import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.jena.riot.out.NodeFmtLib ;
import org.apache.thrift.TException ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

//XXX Needs efficiency attention.
/* package */ class NodeTableHandler implements TLZ_NodeTable.Iface {
    private static Logger log = LoggerFactory.getLogger(NodeTableHandler.class) ;
    private final NodeTable nodeTable ;
    private final String label ;

    public NodeTableHandler(String label, NodeTable nodeTable) {
      this.label = label ;
      this.nodeTable = nodeTable ;
    }
    
    @Override
    public void nodePing() throws TException {
        log.info("ping") ;
    }

    @Override
    public TLZ_NodeId allocNodeId(long id, TLZ_Node nz) throws TException {
      Node n = SSE.parseNode(nz.getNodeStr()) ;
      NodeId nid = nodeTable.getAllocateNodeId(n) ;
      TLZ_NodeId nidz = new TLZ_NodeId() ;
      nidz.setNodeId(nid.getId()) ;
      FmtLog.info(log, "[%d] Node alloc request : %s => %s", id, n, nid) ;
      return nidz ; 
    }

    @Override
    public TLZ_NodeId findByNode(long id, TLZ_Node nz) throws TException {
      Node n = SSE.parseNode(nz.getNodeStr()) ;
      NodeId nid = nodeTable.getNodeIdForNode(n) ;
      // XXX Remove little structs
      TLZ_NodeId nidz = new TLZ_NodeId() ;
      nidz.setNodeId(nid.getId()) ;
      FmtLog.info(log, "[%d] Node get request : %s => %s", id, n, nid) ;
      return nidz ;
    }

    @Override
    public TLZ_Node findByNodeId(long id, TLZ_NodeId nz) throws TException {
        NodeId nid = NodeId.create(nz.getNodeId()) ; 
        Node n = nodeTable.getNodeForNodeId(nid) ;
        if ( n == null )
            FmtLog.error(log, "NodeId not found: "+nid) ;
        String str = NodeFmtLib.str(n) ;
        FmtLog.info(log, "[%d] NodeId get request : %s => %s", id, nid, n) ;
        TLZ_Node nlz = new TLZ_Node().setNodeStr(str) ;
        return nlz ;
    }
    
    @Override
    public long txnBeginRead() throws TException {
        log.warn("TServerNode:txnBeginRead - not implemented"); 
        return 0 ;
    }

    @Override
    public long txnBeginWrite() throws TException {
        log.warn("TServerNode:txnBeginWrite - not implemented"); 
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
