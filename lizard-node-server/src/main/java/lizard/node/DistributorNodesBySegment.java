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

import java.util.ArrayList ;
import java.util.Arrays ;
import java.util.Collection ;
import java.util.List ;

import lizard.comms.CommsException ;
import lizard.comms.ConnState ;
import org.apache.jena.atlas.lib.BitsLong ;
import org.apache.jena.atlas.lib.Bytes ;
import org.apache.jena.atlas.lib.InternalErrorException ;
import org.apache.jena.ext.com.google.common.collect.ArrayListMultimap ;
import org.apache.jena.ext.com.google.common.collect.ListMultimap ;
import org.apache.jena.graph.Node ;
import org.seaborne.tdb2.lib.NodeLib ;
import org.seaborne.tdb2.store.Hash ;
import org.seaborne.tdb2.store.NodeId ;
import org.seaborne.tdb2.sys.SystemTDB ;

/** Distribute by segment (in Nodeid)
 *  OLD CODE
 */
public class DistributorNodesBySegment implements DistributorNodes {
    
    //@@ Replication.
    
    ListMultimap<Long, NodeTableRemote> places = ArrayListMultimap.create() ;
    private final int size ;
    
    public DistributorNodesBySegment(int N) {
        this.size = N ;
    }
    
    // segment = hashKey+1  
    
    public void add(Long hashKey, NodeTableRemote... nodeTables) {
        add(hashKey, Arrays.asList(nodeTables)) ;
    }

    public void add(Long hashKey, List<NodeTableRemote> nodeTables) {
        for ( NodeTableRemote nt : nodeTables )
            places.put(hashKey+1, nt) ;
    }
    
    @Override
    public List<NodeTableRemote> storeAt(Node node) {
        return locateWrite(node) ;
    }

    @Override
    public List<NodeTableRemote> findAt(NodeId nodeid) {
        return locateRead(nodeid) ;
    }

    @Override
    public List<NodeTableRemote> findAt(Node node) {
        return locateRead(node) ;
    }

    @Override
    public List<NodeTableRemote> allFind() {
        List<NodeTableRemote> placesToGo = new ArrayList<>() ;
        for ( Long x : places.keys() ) {
            List<NodeTableRemote> z = places.get(x) ;
            NodeTableRemote ntr = chooseOne(z) ;
            if ( ntr == null )
                throw new CommsException("Can't allFind - a segement is completely unavailable") ;
            placesToGo.add(ntr) ;
        }
        return placesToGo ;
    }
    
    private NodeTableRemote chooseOne(List<NodeTableRemote> z) {
        for ( NodeTableRemote ntr : z ) {
            // For each key, find one place
            if ( ntr.getStatus() == ConnState.OK )
                return ntr ;
        }
        return null ;    
    }
    
    @Override
    public Collection<NodeTableRemote> allStore() {
        Collection<NodeTableRemote> placesToGo = places.values() ;
        for ( NodeTableRemote ntr : placesToGo ) {
            if ( ntr.getStatus() != ConnState.OK )
                throw new CommsException("Can't store - an index is unavailable") ;
        }
        return placesToGo ;
    }


    private List<NodeTableRemote> locateRead(Node node) {
        // Any place we coudl have written it to.
        return locateWrite(node) ; 
    }
    
    private List<NodeTableRemote> locateRead(NodeId nodeId) {
        long segment = segment(nodeId) ;
        if ( segment == 0 || segment > 15 )
            throw new InternalErrorException("Segment out of range: got="+segment) ;
        List<NodeTableRemote> possibilities =  places.get(segment) ;
        // Get first active
        for ( NodeTableRemote idx : possibilities ) {
            if ( idx.getStatus() == ConnState.OK )
                return Arrays.asList(idx) ;
        }
        throw new CommsException("No segements available") ;
    }

    private List<NodeTableRemote> locateWrite(Node node) {
        if ( ! node.isConcrete() )
            throw new CommsException("Can't store node: "+node ) ;
        long segment = segment(node) ;
        List<NodeTableRemote> possibilities =  places.get(segment) ;
        // Check all available
        for ( NodeTableRemote ntr : possibilities ) {
            if ( ntr.getStatus() != ConnState.OK )
                throw new CommsException("Can't store - a segment is unavailable: "+ntr.toString()+" is "+ntr.getStatus()) ;
        }
        return possibilities ;

        
    }
    
//    private List<TupleIndexRemote> locate(Tuple<NodeId> tuple) {
//        NodeId n = tuple.get(0) ;
//        if ( NodeId.isAny(n) )
//            return allStore() ;
//        long segment = segment(n) ;
//        if ( segment == 0 || segment > size )
//            Log.fatal(this, "locateWrite -- hash >= size :: "+hash+" > "+size) ;
//        List<TupleIndexRemote> possibilities =  places.get(hash) ;
//        if ( possibilities == null )
//            Log.fatal(this, "No places to go for tuple: "+tuple+"("+hash+"/"+size+")") ;
//        return possibilities ;
//    }
    
    
    private long segment(Node node) {
        // @@ Overly expensive
        Hash hash = new Hash(SystemTDB.LenNodeHash) ;
        NodeLib.setHash(hash, node) ;
        byte k[] = hash.getBytes() ;
        // Positive 
        int x = Bytes.getInt(k, 0) & 0x8FFFFFF ;
        // Range 1 to 15 - zero is kept as an error marker.
        x = (x%size)+1 ;
        return x ;
    }

    private long segment(NodeId nodeId) {
        long x = BitsLong.unpack(nodeId.getId(), 48, 56) ;
        if ( x <= 0 || x > size )
            throw new InternalErrorException("Node segement out of range (0X"+Long.toHexString(x)+")") ;
        return x ;
    }
    
    @Override
    public String toString() { return "Nodes:"+places.toString() ; }
    
//    private List<NodeTable> locate(NodeId nodeId) {
//        long x = BitsLong.unpack(nodeId.getId(), 52, 56) ;
//        if ( x <= 0 || x > size )
//            throw new InternalErrorException("Node segement out of range") ;
//        return places.get(x) ;
//    }
}
