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
import java.util.concurrent.Callable ;
import java.util.stream.Collectors ;

import lizard.api.TxnClient ;
import lizard.api.TLZlib ;
import lizard.api.TLZ.TLZ_Index ;
import lizard.api.TLZ.TLZ_IndexName ;
import lizard.api.TLZ.TLZ_ShardIndex ;
import lizard.api.TLZ.TLZ_TupleNodeId ;
import lizard.comms.ConnState ;
import lizard.comms.Connection ;
import lizard.comms.thrift.ThriftClient ;
import lizard.system.ComponentTxn ;
import lizard.system.LizardException ;
import lizard.system.Pingable ;

import com.hp.hpl.jena.tdb.store.NodeId ;

import org.apache.jena.atlas.lib.ColumnMap ;
import org.apache.jena.atlas.lib.Tuple ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

class TClientIndex extends TxnClient<TLZ_Index.Client> implements Connection, ComponentTxn, Pingable 
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
        // Delay until starting (client.protocol not valid until then).
        super.setRPC(new TLZ_Index.Client(client.protocol())) ;
        super.start() ; 
        connState = ConnState.OK ;
    }
    
    @Override
    public void stop() {
        close() ;
        super.stop() ;
    }
    
    /** Insert a tuple - return true if it was really added, false if it was a duplicate */
    public boolean add(Tuple<NodeId> tuple) {
        long id = allocRequestId() ;
        long txnId = getTxnId() ;
        TLZ_TupleNodeId x = TLZlib.build(tuple) ;
        return exec("add", ()->rpc.idxAdd(id, txnId, shard, x)) ;
    }

    /** Delete a tuple - return true if it was deleted, false if it didn't exist */
    public boolean delete(Tuple<NodeId> tuple)  {
        long id = allocRequestId() ;
        long txnId = getTxnId() ;
        TLZ_TupleNodeId x = TLZlib.build(tuple) ;
        return exec("delete", ()->rpc.idxDelete(id, txnId, shard, x)) ;
    }
    
    @Override
    public void ping() {
        exec("ping", ()-> { rpc.idxPing(); return null;}) ;
    }

    public Iterator<Tuple<NodeId>> find(Tuple<NodeId> pattern) {
        long id = allocRequestId() ;
        long txnId = getTxnId() ;
        TLZ_TupleNodeId x = TLZlib.build(pattern) ;
        List<TLZ_TupleNodeId> find = exec("find", ()->rpc.idxFind(id, txnId, shard, x)) ;
        // TODO Avoid copy (harder to debug?)
        List<Tuple<NodeId>> rows = find.stream().map(z -> TLZlib.build(z)).collect(Collectors.toList()) ;
        return rows.iterator() ;
    }
        
    private <T> T exec(String label, Callable<T> action) {
        try { return action.call() ; } 
        catch (Exception ex) {
          FmtLog.error(log, ex, label) ;
          throw new LizardException(ex) ;
        }
    }

    private static Tuple<NodeId> tupleAny4 = Tuple.createTuple(NodeId.NodeIdAny, NodeId.NodeIdAny, NodeId.NodeIdAny, NodeId.NodeIdAny) ; 
    private static Tuple<NodeId> tupleAny3 = Tuple.createTuple(NodeId.NodeIdAny, NodeId.NodeIdAny, NodeId.NodeIdAny) ; 
    
    /** return an iterator of everything */
    public Iterator<Tuple<NodeId>> all()                        { return find(tupleAny3) ; }  
    
    @Override
    public ConnState getConnectionStatus() { return connState ; }

    @Override
    public String label() {
        return getLabel() ;
    }

    @Override
    public void setConnectionStatus(ConnState status) { connState = status ; }
    
    @Override
    public void close() {
        client.close() ;
        connState = ConnState.CLOSED ;
    }
    
}

    