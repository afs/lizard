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

package lizard.conf;

import java.util.Objects ;

public class NetAddr {

    public static NetAddr create(String hostname, int port) {
        return new NetAddr(hostname, port) ;
    }
    public final String hostname ;
    public final int port ;
    public NetAddr(String hostname, int port) {
        Objects.requireNonNull(hostname, "Hostname is null") ;
        this.hostname = hostname ;
        this.port = port ;
    }
    @Override
    public int hashCode() {
        final int prime = 31 ;
        int result = 1 ;
        result = prime * result + ((hostname == null) ? 0 : hostname.hashCode()) ;
        result = prime * result + port ;
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
        NetAddr other = (NetAddr)obj ;
        if ( hostname == null ) {
            if ( other.hostname != null )
                return false ;
        } else if ( !hostname.equals(other.hostname) )
            return false ;
        if ( port != other.port )
            return false ;
        return true ;
    }
    public boolean sameHost(NetHost here) {
        return sameHost(here.hostname) ;
    }

    public boolean sameHost(String here) {
        return Objects.equals(hostname, here) ;
    }    
}
