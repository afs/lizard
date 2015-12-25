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

package lizard.conf;

// Typed string.
public class NetHost {
    public final String hostname ;

    public static NetHost create(String hostname) {
        return new NetHost(hostname) ;
    }

    private NetHost(String hostname) {
        this.hostname = hostname ;
    }

    @Override
    public String toString() {
        return "NetHost:"+hostname ;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31 ;
        int result = 1 ;
        result = prime * result + ((hostname == null) ? 0 : hostname.hashCode()) ;
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
        NetHost other = (NetHost)obj ;
        if ( hostname == null ) {
            if ( other.hostname != null )
                return false ;
        } else if ( !hostname.equals(other.hostname) )
            return false ;
        return true ;
    }
}
