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


import lizard.api.TLZ.TLZ_NodeTable ;
import lizard.comms.thrift.ThriftServer ;

import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.thrift.protocol.TCompactProtocol ;
import org.apache.thrift.server.TServer ;
import org.apache.thrift.server.TThreadPoolServer ;
import org.seaborne.dboe.transaction.txn.TransactionalSystem ;
import org.seaborne.tdb2.store.nodetable.NodeTable ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

public class TServerNode extends ThriftServer
{
    private static Logger log = LoggerFactory.getLogger(TServerNode.class) ;
    private final NodeTable nodeTable ;
    
    public static TServerNode create(TransactionalSystem txnSystem, int port, NodeTable nodeTable) {
        return new TServerNode(txnSystem, port, nodeTable) ;
    }
    
    private TServerNode(TransactionalSystem txnSystem, int port, NodeTable nodeTable) {
        super(txnSystem, port) ;
        setLabel("NodeServer["+port+"]") ;
        this.nodeTable = nodeTable ; 
    }
    
    @Override
    public void start() {
        //FmtLog.debug(log, "Start node server, port = %d", getPort()) ;
        TLZ_NodeTable.Iface handler = new THandlerNodeTable(getTxnSystem(), getLabel(), nodeTable) ;
        TLZ_NodeTable.Processor<TLZ_NodeTable.Iface> processor = new TLZ_NodeTable.Processor<TLZ_NodeTable.Iface>(handler);

        // Semapahores to sync??
        new Thread(()-> {
            try {
                getTxnSystem().getTxnMgr().start();
                TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverTransport) ;
                args.processor(processor) ;
                args.inputProtocolFactory(new TCompactProtocol.Factory()) ;
                args.outputProtocolFactory(new TCompactProtocol.Factory()) ;
                TServer server = new TThreadPoolServer(args);
                FmtLog.info(log, "Started index server: port = %d", getPort()) ;
                server.serve();
                FmtLog.info(log, "Finished index server: port = %d", getPort()) ;
                getTxnSystem().getTxnMgr().shutdown();
              } catch (Exception e) {
                e.printStackTrace();
              }
        }) .start() ;
        super.start() ;
    }
}
