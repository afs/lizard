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
import java.util.stream.Collectors ;

import lizard.api.TLZlib ;
import lizard.api.TLZ.* ;
import lizard.comms.ConnState ;
import lizard.comms.Connection ;
import lizard.comms.thrift.ThriftClient ;
import lizard.system.ComponentBase ;
import lizard.system.Pingable ;
import org.apache.jena.atlas.iterator.Iter ;
import org.apache.jena.atlas.lib.ColumnMap ;
import org.apache.jena.atlas.lib.Tuple ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.thrift.TException ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import com.hp.hpl.jena.tdb.store.NodeId ;

class TClientIndex extends ComponentBase implements Connection, Pingable 
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
    
    private final TLZ_IdxRequest request = new TLZ_IdxRequest() ;
    private final TLZ_IdxReply reply = new TLZ_IdxReply() ;
    private static int counter = 0;
    
    /** Insert a tuple - return true if it was really added, false if it was a duplicate */
    public boolean add(Tuple<NodeId> tuple) {
        TLZ_TupleNodeId x = TLZlib.build(tuple) ;
        request.setAddTuple(x) ;
        try {
            sendReceive() ;
        } catch (TException ex) {
            FmtLog.error(log, ex, "add(Tuple)") ;
        }
        boolean b = reply.isYesOrNo() ;
        request.clear() ;
        reply.clear() ;
        return b ;
    }

    /** Delete a tuple - return true if it was deleted, false if it didn't exist */
    public boolean delete(Tuple<NodeId> tuple)  { 
        TLZ_TupleNodeId x = TLZlib.build(tuple) ;
        request.setDeleteTuple(x) ;
        try {
            sendReceive() ;
        } catch (TException ex) {
            FmtLog.error(log, ex, "add(Tuple)") ;
        }
        boolean b = reply.isYesOrNo() ;
        request.clear() ;
        reply.clear() ;
        return b ;
    }
    
    public Iterator<Tuple<NodeId>> find(Tuple<NodeId> pattern) { 
        TLZ_TupleNodeId x = TLZlib.build(pattern) ;
        request.setPattern(x) ;
        try {
            sendReceive() ;
        } catch (TException ex) {
            FmtLog.error(log, ex, "add(Tuple)") ;
        }
        
        if ( reply.getTuples() == null ) {
            request.clear() ;
            reply.clear() ;
            return Iter.nullIterator() ;
        }
        
        List<Tuple<NodeId>> rows = reply.getTuples().stream().map(z -> TLZlib.build(z)).collect(Collectors.toList()) ;
        request.clear() ;
        reply.clear() ;
        return rows.iterator() ;
    }
        
    private static Tuple<NodeId> tupleAny4 = Tuple.createTuple(NodeId.NodeIdAny, NodeId.NodeIdAny, NodeId.NodeIdAny, NodeId.NodeIdAny) ; 
    private static Tuple<NodeId> tupleAny3 = Tuple.createTuple(NodeId.NodeIdAny, NodeId.NodeIdAny, NodeId.NodeIdAny) ; 
    
    /** return an iterator of everything */
    public Iterator<Tuple<NodeId>> all()                        { return find(tupleAny3) ; }  
    
    private void sendReceive() throws TException {
        request.setRequestId(++counter) ;
        request.setIndex(shard) ;
        request.write(client.protocol()) ;
        client.protocol().getTransport().flush() ;
        reply.read(client.protocol()) ;
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
        TLZ_IdxRequest request = new TLZ_IdxRequest() ;
        TLZ_IdxReply reply = new TLZ_IdxReply() ;
        long requestId = ++counter ;
        request.setRequestId(requestId) ;
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

    