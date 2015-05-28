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

import lizard.comms.ConnState ;
import lizard.comms.Connection ;
import lizard.system.ComponentTxn ;
import lizard.system.RemoteControl ;
import org.apache.jena.atlas.lib.Tuple ;
import org.seaborne.tdb2.store.NodeId ;

interface TClientIndexOps extends Connection, ComponentTxn, RemoteControl 
{
    @Override
    public void start() ;
    
    @Override
    public void stop() ;
    
    /** Insert a tuple */
    public void add(Tuple<NodeId> tuple) ;

    /** Delete a tuple */
    public void delete(Tuple<NodeId> tuple) ;
    
    @Override
    public void ping() ;

    public Iterator<Tuple<NodeId>> find(Tuple<NodeId> pattern) ;

    static Tuple<NodeId> tupleAny4 = Tuple.createTuple(NodeId.NodeIdAny, NodeId.NodeIdAny, NodeId.NodeIdAny, NodeId.NodeIdAny) ; 
    static Tuple<NodeId> tupleAny3 = Tuple.createTuple(NodeId.NodeIdAny, NodeId.NodeIdAny, NodeId.NodeIdAny) ; 
    
    /** return an iterator of everything */
    default public Iterator<Tuple<NodeId>> all() { return find(tupleAny3) ; }  
    
    @Override
    public ConnState getConnectionStatus() ;

    @Override
    public String label() ;

    @Override
    public void setConnectionStatus(ConnState status) ;
    
    @Override
    public void close() ;
}

    