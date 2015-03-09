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

import java.util.Iterator ;
import java.util.List ;
import java.util.Objects ;
import java.util.concurrent.atomic.AtomicLong ;
import java.util.stream.Collectors ;

import lizard.api.TLZlib ;
import lizard.api.TLZ.* ;
import lizard.comms.ConnState ;
import lizard.comms.Connection ;
import lizard.comms.thrift.ThriftClient ;
import lizard.system.ComponentBase ;
import lizard.system.ComponentTxn ;
import lizard.system.LzTxnId ;
import lizard.system.Pingable ;
import org.apache.jena.atlas.iterator.Iter ;
import org.apache.jena.atlas.lib.ColumnMap ;
import org.apache.jena.atlas.lib.Tuple ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.thrift.TException ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import com.hp.hpl.jena.query.ReadWrite ;
import com.hp.hpl.jena.tdb.store.NodeId ;

class TClientIndex extends ComponentBase implements Connection, ComponentTxn, Pingable 
{
    private static Logger log = LoggerFactory.getLogger(TClientIndex.class) ;
    private final ThriftClient client ;
    private ConnState connState ; 
    private final TLZ_IndexName indexName ;
    private final TLZ_ShardIndex shard ;    // remove.
    

    public static TClientIndex create(String host, int port, String indexName, ColumnMap colMap) {
        return new TClientIndex(host, port, indexName, colMap) ;
    }
    
    private TClientIndex(String host, int port, String indexName, ColumnMap colMap) {
        this.client = new ThriftClient(host, port) ;
        this.indexName = TLZlib.indexToEnum(indexName) ;
        this.shard = new TLZ_ShardIndex(this.indexName, 0) ;
        setLabel("ClientIndex["+host+":"+port+"]") ;
        connState = ConnState.NOT_ACTIVATED ;
    }
    
    @Override
    public void start() {
        if ( client.isRunning() ) {
            FmtLog.debug(log, "Already started (%s:%d)", client.getRemoteHost(), client.getRemotePort()) ;
            return ;
        }
        FmtLog.debug(log, "Start: %s", getLabel()) ;
        client.start() ;
        super.start() ; 
        connState = ConnState.OK ;
    }
    
    
    @Override
    public void stop() {
        close() ;
        super.stop() ;
    }
    
    private static AtomicLong counter = new AtomicLong(0) ;
    
    private TLZ_IdxRequest generateRequest()    { return new TLZ_IdxRequest() ; }
    private TLZ_IdxReply   generateReply()      { return new TLZ_IdxReply() ; }
    
    /** Insert a tuple - return true if it was really added, false if it was a duplicate */
    public boolean add(Tuple<NodeId> tuple) {
        TLZ_IdxRequest request = generateRequest() ;
        TLZ_IdxReply reply = generateReply() ;
        TLZ_TupleNodeId x = TLZlib.build(tuple) ;
        request.setAddTuple(x) ;
        sendReceive("add(Tuple)", request, reply) ;
        return reply.isYesOrNo() ;
    }

    /** Delete a tuple - return true if it was deleted, false if it didn't exist */
    public boolean delete(Tuple<NodeId> tuple)  { 
        TLZ_IdxRequest request = generateRequest() ;
        TLZ_IdxReply reply = generateReply() ;
        TLZ_TupleNodeId x = TLZlib.build(tuple) ;
        request.setDeleteTuple(x) ;
        sendReceive("delete(Tuple)", request, reply) ;
        return reply.isYesOrNo() ;
    }
    
    public Iterator<Tuple<NodeId>> find(Tuple<NodeId> pattern) { 
        TLZ_IdxRequest request = generateRequest() ;
        TLZ_IdxReply reply = generateReply() ;
        TLZ_TupleNodeId x = TLZlib.build(pattern) ;
        request.setPattern(x) ;
        sendReceive("find(Tuple)", request, reply) ;

        if ( reply.getTuples() == null )
            return Iter.nullIterator() ;
        
        List<Tuple<NodeId>> rows = reply.getTuples().stream().map(z -> TLZlib.build(z)).collect(Collectors.toList()) ;
        return rows.iterator() ;
    }
        
    private static Tuple<NodeId> tupleAny4 = Tuple.createTuple(NodeId.NodeIdAny, NodeId.NodeIdAny, NodeId.NodeIdAny, NodeId.NodeIdAny) ; 
    private static Tuple<NodeId> tupleAny3 = Tuple.createTuple(NodeId.NodeIdAny, NodeId.NodeIdAny, NodeId.NodeIdAny) ; 
    
    /** return an iterator of everything */
    public Iterator<Tuple<NodeId>> all()                        { return find(tupleAny3) ; }  
    
    // ---- Transactions
    // RPC?
    
    interface ActionTxn { void setup(TLZ_IdxRequest request, TLZ_IdxReply reply) ; }
    
    private void exec(LzTxnId txn, ActionTxn c) {
        TLZ_IdxRequest request = generateRequest() ;
        request.setGeneration(0) ;  // Control message
        TLZ_IdxReply reply = generateReply() ;
        c.setup(request, reply) ;
        sendReceive("begin", request, reply);
    }
    
    @Override
    public LzTxnId begin(ReadWrite mode) {
        Objects.requireNonNull(mode) ;
        LzTxnId txn = LzTxnId.alloc() ;
        exec(txn, (request, reply)->{
            switch(mode) {
                case READ : {
                    TLZ_TxnBeginRead x = new TLZ_TxnBeginRead(txn.generation()) ;
                    request.setTxnBeginRead(x) ;
                    break ;
                }
                case WRITE : {
                    TLZ_TxnBeginWrite x = new TLZ_TxnBeginWrite(txn.generation()) ;
                    request.setTxnBeginWrite(x) ;
                    break ;
                }
            }
        }) ;
        //reply
        return txn ;
    }

    @Override
    public void prepare(LzTxnId txn) {
        exec(txn, (request, reply)->{
            request.setTxnPrepare(new TLZ_TxnPrepare(txn.generation())) ;
        }) ;
    }

    @Override
    public void commit(LzTxnId txn) {
        exec(txn, (request, reply)->{
            request.setTxnCommit(new TLZ_TxnCommit(txn.generation())) ;
        }) ;  
    }

    @Override
    public void abort(LzTxnId txn) {
        exec(txn, (request, reply)->{
            request.setTxnAbort(new TLZ_TxnAbort(txn.generation())) ;
        }) ;        
    }

    @Override
    public void end(LzTxnId txn) {
        exec(txn, (request, reply)->{
            request.setTxnEnd(new TLZ_TxnEnd(txn.generation())) ;
        }) ;        
    }
    

    // ---- Transactions

    private void sendReceive(String caller, TLZ_IdxRequest request, TLZ_IdxReply reply ) {
        try {
            request.setRequestId(counter.incrementAndGet()) ;
            request.setGeneration(9) ;
            request.setIndex(shard) ;
            request.write(client.protocol()) ;
            client.protocol().getTransport().flush() ;
            reply.read(client.protocol()) ;
        } catch (TException ex) {
            FmtLog.error(log, ex, caller);
        }
    }

    @Override
    public ConnState getConnectionStatus() { return connState ; }

    @Override
    public String label() {
        return getLabel() ;
    }

    @Override
    public void setConnectionStatus(ConnState status) { connState = status ; }

    private static TLZ_Ping tlzPing = new TLZ_Ping(9999) ;
    @Override
    public void ping() {
        TLZ_IdxRequest request = generateRequest() ;
        TLZ_IdxReply reply = generateReply() ;
        long requestId = counter.incrementAndGet() ;
        request.setRequestId(requestId) ;
        request.setGeneration(-1) ;
        request.setIndex(shard) ;
        request.setPing(tlzPing) ;

        try {
            request.write(client.protocol()) ;
            client.protocol().getTransport().flush() ;
            reply.read(client.protocol()) ;
            if ( ! reply.isSetRequestId() )
                FmtLog.error(log, "ping: requestId not set in reply") ;
            else if ( reply.getRequestId() != requestId )
                FmtLog.error(log, "ping: requestId does not match that sent") ;
        } catch (TException ex) {
            FmtLog.error(log, ex, "ping") ;
        }
    }

    @Override
    public void close() {
        client.close() ;
        connState = ConnState.CLOSED ;
    }
    
}

    