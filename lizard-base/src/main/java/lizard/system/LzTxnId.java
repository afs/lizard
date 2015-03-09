/**
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

package lizard.system;

import java.util.UUID ;
import java.util.concurrent.atomic.AtomicLong ;

import com.hp.hpl.jena.shared.uuid.Bits ;

public class LzTxnId {
    public static final LzTxnId txnNil = new LzTxnId(LizardConst.uuidNil, 0) ;
    
    static final private UUID here = UUID.randomUUID() ;
    static final private AtomicLong counter = new AtomicLong(0) ;
    
    private final UUID uuid ;
    private final long count ;
    
    public static LzTxnId alloc() {
        long x = counter.incrementAndGet() ;
        return new LzTxnId(here, x) ;
    }
    
    private LzTxnId(UUID uuid, long x) {
        this.uuid = uuid ; 
        this.count = x ;
    }

    public long generation() {
        if ( this.equals(txnNil) )
            return 0L ;
        long x = uuid.getLeastSignificantBits() ;
        x = Bits.clear(x, 32, 64) ;
        int y = (int)count ;
        x = x | count ;
        return x ;
    }

    @Override
    public int hashCode() {
        final int prime = 31 ;
        int result = 1 ;
        result = prime * result + (int)(count ^ (count >>> 32)) ;
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode()) ;
        return result ;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true ;
        if ( obj == null )
            return false ;
        if ( getClass() != obj.getClass() )
            return false ;
        LzTxnId other = (LzTxnId)obj ;
        if ( count != other.count )
            return false ;
        if ( uuid == null ) {
            if ( other.uuid != null )
                return false ;
        } else if ( !uuid.equals(other.uuid) )
            return false ;
        return true ;
    }
    
}
