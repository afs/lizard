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

package lizard.comms;

import lizard.api.TLZlib ;
import lizard.api.TLZ.TLZ_IndexName ;
import lizard.api.TLZ.TLZ_TupleNodeId ;
import lizard.comms.thrift.ThriftLib ;
import lizard.test.LzBaseTest ;
import org.apache.jena.atlas.lib.Tuple ;
import org.apache.thrift.protocol.TProtocol ;
import org.apache.thrift.transport.TMemoryBuffer ;
import org.apache.thrift.transport.TTransport ;
import org.junit.Test ;

import com.hp.hpl.jena.tdb.store.NodeId ;

/**
 * This class is just a few test to make sure things look OK.
 * It is not a comprehesive test of Thrift. 
 */
public class TestTLZ extends LzBaseTest {
    
    static private NodeId nid1 = NodeId.create(10) ;
    static private NodeId nid2 = NodeId.create(11) ;
    static private NodeId nid3 = NodeId.create(12) ;
    static private NodeId nid4 = NodeId.create(13) ;
    
    // TLZLib
    @Test public void data_01() {
        Tuple<NodeId> tuple = Tuple.createTuple(nid1, nid2, nid3) ;
        TLZ_TupleNodeId tlz = TLZlib.build(tuple) ;
        Tuple<NodeId> tuple2 = TLZlib.build(tlz) ;
        assertEquals(tuple, tuple2) ;
    }
    
    private static void testEnum(String name) {
        TLZ_IndexName e = TLZlib.indexToEnum(name) ;
        String x = TLZlib.indexEnumToName(e) ;
        assertEquals(name, x) ;
    }
    
    @Test public void data_11() { testEnum("SPO") ; } 
    @Test public void data_12() { testEnum("PSO") ; }
    @Test public void data_13() { testEnum("POS") ; }
    @Test public void data_14() { testEnum("OSP") ; }
    
    // Encoding
    @Test public void encode_01() throws Exception { 
        TProtocol proto = protocol() ;
        
        Tuple<NodeId> tuple = Tuple.createTuple(nid1, nid2, nid3) ;
        
        TLZ_TupleNodeId tlz = TLZlib.build(tuple) ;
        tlz.write(proto);
        
        TLZ_TupleNodeId tlz2 = new TLZ_TupleNodeId() ;
        tlz2.read(proto); 
        
        Tuple<NodeId> tuple2 = TLZlib.build(tlz2) ;
        
        assertEquals(tuple, tuple2) ;
    }

    private static TProtocol protocol() {
        TTransport transport = new TMemoryBuffer(1024) ;
        TProtocol proto = ThriftLib.protocol(transport) ;
        return proto ;
    }
}
