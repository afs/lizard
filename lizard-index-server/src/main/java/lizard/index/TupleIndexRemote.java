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

import lizard.api.TLZlib ;
import lizard.api.TxnClient ;
import lizard.api.TLZ.TLZ_IndexName ;
import lizard.comms.ConnState ;
import lizard.system.Component ;
import lizard.system.ComponentBase ;
import lizard.system.ComponentTxn ;
import lizard.system.RemoteControl ;

import org.apache.jena.query.ReadWrite ;
import org.seaborne.tdb2.store.NodeId ;
import org.seaborne.tdb2.store.tupletable.TupleIndexBase ;
import org.apache.jena.atlas.lib.ColumnMap ;
import org.apache.jena.atlas.lib.NotImplemented ;
import org.apache.jena.atlas.lib.Tuple ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

/** Client side of a remote index */
public class TupleIndexRemote extends TupleIndexBase implements Component, ComponentTxn, RemoteControl, TxnClient.Accessor
{
    // Relationship of TupleIndexRemote and TClientIndex
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
    
    @Override
    public TxnClient<?> getWireClient() {
        return client ;
    }
    
    public ConnState getStatus() { return client.getConnectionStatus() ; }
    
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
        throw new NotImplemented("TupleIndexRemote.clear") ;
    }

    @Override
    public void sync() {
        //throw new NotImplemented("TupleIndexRemote.sync") ;
    }

    @Override
    public void close() {
        throw new NotImplemented() ;
    }

    @Override
    public void ping() {
        client.ping() ;
    }

    @Override
    public void remoteStop() {
        client.remoteStop() ;
    }

    @Override
    protected void performAdd(Tuple<NodeId> tuple) {
        client.add(tuple) ;
    }
    
    @Override
    protected void performDelete(Tuple<NodeId> tuple) {
        client.delete(tuple) ;
    }

    @Override
    protected Iterator<Tuple<NodeId>> performFind(Tuple<NodeId> tuple) {
        return access.accessTuples(tuple) ;
    }

    @Override
    public void start() { 
        client.start() ;
        component.start() ;
    }

    @Override
    public void stop() { component.stop() ; }

    @Override public void begin(long tnxId, ReadWrite mode)   { client.begin(tnxId, mode) ; }
    @Override public void prepare()               { client.prepare() ; }
    @Override public void commit()                { client.commit() ; }
    @Override public void abort()                 { client.abort() ; }
    @Override public void end()                   { client.end() ; }

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
}
