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

import org.apache.jena.atlas.lib.ColumnMap ;
import org.apache.jena.atlas.lib.Tuple ;

import com.google.common.hash.HashCode ;
import com.google.common.hash.HashFunction ;
import com.google.common.hash.Hashing ;
import org.apache.jena.tdb.store.NodeId ;

public class Shard {
    // Copied from proto-lizard:DistributorTuplesBySubject
    // BAD Use Fnv.
    
    public static final long NO_SHARD = -1 ;  

    /** Calculate the chard for this tuple or tuple pattern.
     * Return {@link #NO_SHARD} for "any"
     * @param tuple
     * @return int
     */
    public static long shardBySubject(ColumnMap mapper, int numShard, Tuple<NodeId> tuple) {
        NodeId n = mapper.fetchSlot(0, tuple) ;
        if ( NodeId.isAny(n) )
            return NO_SHARD ;
        long shard = shard(n, numShard) ;
        return shard ;
    }

    private static HashFunction hasher = Hashing.goodFastHash(32) ;
    
    public static long shard(NodeId n, int numShard) {
        if ( n == null )
            throw new IllegalArgumentException("Asked to hash null") ;
        if ( numShard == 0 )
            // Zero bits - fixed.
            return 0L ;
        // Must use all bits (lower few are probably all zero)
        //byte[] b = new byte[NodeId.SIZE] ;

        long v = n.getId() ;
        int x = hash(v) & 0xFFFFFF ;
        return x % numShard ;
    }

    static final public int hash(long v) {
        HashCode hc = hasher.hashLong(v) ;
        return hasher.hashLong(v).asInt() ;
    }
}
