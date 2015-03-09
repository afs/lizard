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
import java.util.Objects ;

import lizard.api.TLZlib ;
import lizard.api.TLZ.TLZ_IndexName ;
import lizard.api.TLZ.TLZ_TxnBeginRead ;
import lizard.comms.ConnState ;
import lizard.system.* ;
import org.apache.jena.atlas.lib.ColumnMap ;
import org.apache.jena.atlas.lib.NotImplemented ;
import org.apache.jena.atlas.lib.Tuple ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import com.hp.hpl.jena.query.ReadWrite ;
import com.hp.hpl.jena.tdb.store.NodeId ;
import com.hp.hpl.jena.tdb.store.tupletable.TupleIndexBase ;

/** Client side of a remote index */
public class TupleIndexRemote extends TupleIndexBase implements Component, ComponentTxn, Pingable
{
    // Relationship of  TupleIndexRemote and TClientIndex
    public static TupleIndexRemote create(String hostname, int port, String indexStr, ColumnMap cmap) {
        TClientIndex client = TClientIndex.create(hostname, port, indexStr, cmap) ;
        String name = "Idx"+indexStr+"["+port+"]" ;
        TupleIndexRemote index = new TupleIndexRemote(client, indexStr, cmap, name) ;
        return index ;
    }
    
    private static Logger log = LoggerFactory.getLogger(TupleIndexRemote.class) ; 
    private final Tuple<NodeId> anyTuple ;
    private final RemoteAccessData access ;
    private final TLZ_IndexName indexName ;
    private Component component = new ComponentBase() ;
    private final TClientIndex client ;

    public TupleIndexRemote(TClientIndex client, String indexStr, ColumnMap colMapping, String name) {
        super(indexStr.length(), colMapping, name) ;
        this.client = client ;
        RemoteAccessData access = new RemoteAccessData(client) ;
        this.indexName = TLZlib.indexToEnum(indexStr) ;
        this.access = access ;
        NodeId[] x = new NodeId[indexStr.length()] ;
        for ( int i = 0 ; i < x.length; i++ )
            x[i] = NodeId.NodeIdAny ;
        anyTuple = Tuple.create(x) ;
        component.setLabel(super.getName()) ;
    }
    
    public ConnState getStatus() { return client.getConnectionStatus() ; }
    
//    /** Testing / debugging */
//    public Connection getConnection() { return access.getConnection() ; }
    
    @Override
    public Iterator<Tuple<NodeId>> all() {
        return performFind(anyTuple) ;
    }

    @Override
    public long size() {
        return 0 ;
    }

    @Override
    public boolean isEmpty() {
        return false ;
    }

    @Override
    public void clear() { 
        //op0("CLEAR") ;
        throw new NotImplemented("TupleIndexRemote.clear") ;
    }

    @Override
    public void sync() {
        //op0("SYNC") ;
        //throw new NotImplemented("TupleIndexRemote.sync") ;
    }

    @Override
    public void close() {
        throw new NotImplemented() ;
    }

    @Override
    protected boolean performAdd(Tuple<NodeId> tuple) {
        return client.add(tuple) ;
    }
    
    @Override
    protected boolean performDelete(Tuple<NodeId> tuple) {
        return client.delete(tuple) ;
    }

    @Override
    protected Iterator<Tuple<NodeId>> performFind(Tuple<NodeId> tuple) {
        //log.info("find["+shard+"] "+tuple) ;
        return access.accessTuples(tuple) ;
    }

    @Override
    public void start() { 
        client.start() ;
        component.start() ;
    }

    @Override
    public void stop() { component.stop() ; }

    // ---- Transactions
    
    @Override
    public LzTxnId begin(ReadWrite mode) {
        return client.begin(mode) ;
    }

    @Override
    public void prepare(LzTxnId txnId) {
        client.prepare(txnId);
    }

    @Override
    public void commit(LzTxnId txnId) {
        client.commit(txnId);
    }

    @Override
    public void abort(LzTxnId txnId) {
        client.abort(txnId);
    }

    @Override
    public void end(LzTxnId txnId) {
        client.end(txnId) ;
    }

    // ---- Transactions

    @Override
    public boolean isRunning() { return component.isRunning() ; }

    @Override
    public boolean hasFailed() { return component.hasFailed() ; }

    @Override
    public void setStatus(Status status) { component.setStatus(status); }

    @Override
    public String getLabel() {
        return component.getLabel() ;
    }

    @Override
    public void setLabel(String label) { component.setLabel(label) ; }

    @Override
    public void ping() { client.ping(); }
}
