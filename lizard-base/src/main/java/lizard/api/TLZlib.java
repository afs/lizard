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

package lizard.api;

import lizard.api.TLZ.TLZ_IndexName ;
import lizard.api.TLZ.TLZ_TupleNodeId ;
import org.apache.jena.atlas.lib.Bytes ;
import org.apache.jena.atlas.lib.InternalErrorException ;
import org.apache.jena.atlas.lib.tuple.Tuple ;
import org.apache.jena.atlas.lib.tuple.TupleFactory ;
import org.apache.jena.tdb2.store.NodeId ;
import org.apache.jena.tdb2.store.NodeIdFactory ;

public class TLZlib {
    public static TLZ_TupleNodeId build(Tuple<NodeId> tuple) {
        TLZ_TupleNodeId tlzTuple = new TLZ_TupleNodeId() ;
        if ( tuple.len() == 4 ) {
            tlzTuple
            .setG(get(tuple, 0))
            .setS(get(tuple, 1))
            .setP(get(tuple, 2))
            .setO(get(tuple, 3)) ;
        } else {
            tlzTuple
            .setS(get(tuple, 0))
            .setP(get(tuple, 1))
            .setO(get(tuple, 2)) ;
        }
        return tlzTuple ;
    }
    
    private static long get(Tuple<NodeId> tuple, int idx) {
        NodeId n = tuple.get(idx) ; 
        if ( n == null )
            n = NodeId.NodeIdAny ;
        System.err.println("WARNING: Update thrift for byte[] NodeIds");
        return 0L;
    }
    
    public static Tuple<NodeId> build(TLZ_TupleNodeId tnid) {
        NodeId s = createNodeId(tnid.getS()) ;
        NodeId p = createNodeId(tnid.getP()) ;
        NodeId o = createNodeId(tnid.getO()) ;
        if (tnid.isSetG()) {
            NodeId g = createNodeId(tnid.getG()) ;
            return TupleFactory.tuple(g, s, p, o) ;
        }
        else
            return TupleFactory.tuple(s, p, o) ; 
    }

    
    
    private static NodeId createNodeId(long x) {
        System.err.println("WARNING: Update thrift for byte[] NodeIds");
        return NodeIdFactory.get(Bytes.packLong(x));
    }

    public static String indexEnumToName(TLZ_IndexName idxEnum) {
        switch(idxEnum) {
            case SPO : return "SPO" ;
            case POS : return "POS" ;
            case PSO : return "PSO" ;
            case OSP : return "OSP" ;
            default: throw new InternalErrorException() ;
        }
    }
    
    public static TLZ_IndexName indexToEnum(String indexName) {
        switch(indexName) {
            case "SPO" : return TLZ_IndexName.SPO ;
            case "POS" : return TLZ_IndexName.POS ;
            case "PSO" : return TLZ_IndexName.PSO ;
            case "OSP" : return TLZ_IndexName.OSP ;
            default: throw new InternalErrorException("Index name: "+indexName) ;
        }
    }

}
