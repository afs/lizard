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


import lizard.api.TLZ.TLZ_Node ;
import lizard.api.TLZ.TLZ_NodeId ;
import lizard.api.TLZ.TLZ_NodeTable ;
import lizard.comms.thrift.ThriftServer ;

import com.hp.hpl.jena.graph.Node ;
import com.hp.hpl.jena.sparql.sse.SSE ;
import com.hp.hpl.jena.tdb.store.NodeId ;
import com.hp.hpl.jena.tdb.store.nodetable.NodeTable ;

import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.jena.riot.out.NodeFmtLib ;
import org.apache.thrift.TException ;
import org.apache.thrift.protocol.TCompactProtocol ;
import org.apache.thrift.server.TServer ;
import org.apache.thrift.server.TThreadPoolServer ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

// XXX Needs efficiency attention.
public class TServerNode extends ThriftServer
{
    private static Logger log = LoggerFactory.getLogger(TServerNode.class) ;
    private final NodeTable nodeTable ;
    
    public static TServerNode create(int port, NodeTable nodeTable) {
        return new TServerNode(port, nodeTable) ;
    }
    
    private TServerNode(int port, NodeTable nodeTable) {
        super(port) ;
        setLabel("NodeServer["+port+"]") ;
        //server = new ThriftServer(port, new Handler(getLabel(), nodeTable)) ;
        this.nodeTable = nodeTable ; 
    }
    
    @Override
    public void start() {
        //FmtLog.debug(log, "Start node server, port = %d", getPort()) ;
        TLZ_NodeTable.Iface handler = new TServerNode.Handler(getLabel(), nodeTable) ;
        TLZ_NodeTable.Processor<TLZ_NodeTable.Iface> processor = new TLZ_NodeTable.Processor<TLZ_NodeTable.Iface>(handler);

        // Semapahores to sync??
        new Thread(()-> {
            try {
                TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverTransport) ;
                args.processor(processor) ;
                args.inputProtocolFactory(new TCompactProtocol.Factory()) ;
                args.outputProtocolFactory(new TCompactProtocol.Factory()) ;
                TServer server = new TThreadPoolServer(args);
                FmtLog.info(log, "Started index server: port = %d", getPort()) ;
                server.serve();
                FmtLog.info(log, "Finished index server: port = %d", getPort()) ;
              } catch (Exception e) {
                e.printStackTrace();
              }
        }) .start() ;
        super.start() ;
    }
    
    static class Handler implements TLZ_NodeTable.Iface {
        private final NodeTable nodeTable ;
        private final String label ;

        public Handler(String label, NodeTable nodeTable) {
          this.label = label ;
          this.nodeTable = nodeTable ;
        }
        
        @Override
        public void nodePing() throws TException {
            log.info("ping") ;
        }

        @Override
        public TLZ_NodeId allocNodeId(TLZ_Node nz) throws TException {
          Node n = SSE.parseNode(nz.getNodeStr()) ;
          NodeId nid = nodeTable.getAllocateNodeId(n) ;
          TLZ_NodeId nidz = new TLZ_NodeId() ;
          nidz.setNodeId(nid.getId()) ;
          FmtLog.info(log, "[%d] Node alloc request : %s => %s", /*id*/0, n, nid) ;
          return nidz ; 
        }

        @Override
        public TLZ_NodeId findByNode(TLZ_Node nz) throws TException {
          Node n = SSE.parseNode(nz.getNodeStr()) ;
          NodeId nid = nodeTable.getNodeIdForNode(n) ;
          // XXX Remove little structs
          TLZ_NodeId nidz = new TLZ_NodeId() ;
          nidz.setNodeId(nid.getId()) ;
          FmtLog.info(log, "[%d] Node get request : %s => %s", /*id*/0, n, nid) ;
          return nidz ;
        }

        @Override
        public TLZ_Node findByNodeId(TLZ_NodeId nz) throws TException {
            NodeId nid = NodeId.create(nz.getNodeId()) ; 
            Node n = nodeTable.getNodeForNodeId(nid) ;
            if ( n == null )
                FmtLog.error(log, "NodeId not found: "+nid) ;
            String str = NodeFmtLib.str(n) ;
            FmtLog.info(log, "[%d] NodeId get request : %s => %s", /*id*/0, nid, n) ;
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
}
