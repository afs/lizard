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

import java.util.ArrayList ;
import java.util.Iterator ;
import java.util.List ;

import lizard.api.TLZlib ;
import lizard.api.TLZ.TLZ_Index ;
import lizard.api.TLZ.TLZ_ShardIndex ;
import lizard.api.TLZ.TLZ_TupleNodeId ;
import lizard.comms.thrift.ThriftServer ;

import com.hp.hpl.jena.tdb.store.NodeId ;
import com.hp.hpl.jena.tdb.store.tupletable.TupleIndex ;

import org.apache.jena.atlas.lib.Tuple ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.thrift.TException ;
import org.apache.thrift.protocol.TCompactProtocol ;
import org.apache.thrift.server.TServer ;
import org.apache.thrift.server.TThreadPoolServer ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;


public class TServerIndex extends ThriftServer
{
    private static Logger log = LoggerFactory.getLogger(TServerIndex.class) ;
    private TupleIndex index ;
    
    public static TServerIndex create(int port, TupleIndex index) {
        return new TServerIndex(port, index) ;
    }

    private TServerIndex(int port, TupleIndex index) {
        super(port) ;
        setLabel("IndexServer["+port+"]") ;
        this.index = index ;
    }

    @Override
    public void start() {
        FmtLog.info(log, "Start index server: port = %d", getPort()) ;
        // Semapahores to sync.
        //Semaphore sema = new Semaphore(0) ;
        
        new Thread(()-> {
            try {
                TLZ_Index.Iface handler = new TServerIndex.Handler(getLabel(), index) ;
                TLZ_Index.Processor<TLZ_Index.Iface> processor = new TLZ_Index.Processor<TLZ_Index.Iface>(handler);
                TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverTransport) ;
                args.processor(processor) ;
                args.inputProtocolFactory(new TCompactProtocol.Factory()) ;
                args.outputProtocolFactory(new TCompactProtocol.Factory()) ;
                TServer server = new TThreadPoolServer(args);
                FmtLog.info(log, "Started index server: port = %d", getPort()) ;
                //sema.release(1);
                server.serve();
                FmtLog.info(log, "Finished index server: port = %d", getPort()) ;
              } catch (Exception e) {
                e.printStackTrace();
              }
        }) .start() ;
        //sema.acquireUninterruptibly(); 
        super.start();
    }
    
    static class Handler implements TLZ_Index.Iface {

        private final TupleIndex index ;
        private final String label ;

        public Handler(String label, TupleIndex index) {
            this.label = label ;
            this.index = index ;
        }
        
        @Override
        public void idxPing() throws TException {
            log.info("ping") ;
        }

        @Override
        public boolean idxAdd(long id, TLZ_ShardIndex shard, TLZ_TupleNodeId tuple) throws TException {
            Tuple<NodeId> tuple2 = TLZlib.build(tuple) ;
            // Verbose.
            FmtLog.info(log, "[%d] add %s [%s]", id, index.getName(), index.getName()) ;
            boolean b = index.add(tuple2) ;
            return b ;
        }

        @Override
        public boolean idxDelete(long id, TLZ_ShardIndex shard, TLZ_TupleNodeId tuple) throws TException {
            Tuple<NodeId> tuple2 = TLZlib.build(tuple) ;
            // Verbose.
            FmtLog.info(log, "[%d] delete %s [%s]", id, index.getName(), index.getName()) ;
            boolean b = index.delete(tuple2) ;
            return b ;
        }

        @Override
        public List<TLZ_TupleNodeId> idxFind(long id, TLZ_ShardIndex shard, TLZ_TupleNodeId tuple) throws TException {
            Tuple<NodeId> pattern = TLZlib.build(tuple) ;
            Iterator<Tuple<NodeId>> iter = index.find(pattern) ;
            FmtLog.info(log, "[%d] find %s [%s]", id, index.getName(), index.getName()) ;
            // TODO XXX Revisit and stream this.
            List<TLZ_TupleNodeId> result = new ArrayList<>() ;
            iter.forEachRemaining(t->result.add(TLZlib.build(t))) ;
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
    
}
