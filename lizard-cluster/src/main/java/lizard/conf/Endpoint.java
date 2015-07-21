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

import java.util.Objects ;

/** An endpoint a name and a port.
 * 
 * The name might be a virtual vNode name or a DNS address.
 * 
 * @see NetAddr
 * @see VNodeAddr
 * 
 */
public class Endpoint {

    public static Endpoint create(String dnsName, int port) {
        return new Endpoint(dnsName, port) ;
    }

    public static Endpoint createHere(int port) {
        return new Endpoint("localhost", port) ;
    }

    private final String name ;
    private final int port ;
    public Endpoint(String name, int port) {
        Objects.requireNonNull(name, "Hostname is null") ;
        this.name = name ;
        this.port = port ;
    }
    @Override
    public int hashCode() {
        final int prime = 31 ;
        int result = 1 ;
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode()) ;
        result = prime * result + getPort() ;
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
        Endpoint other = (Endpoint)obj ;
        if ( getName() == null ) {
            if ( other.getName() != null )
                return false ;
        } else if ( !getName().equals(other.getName()) )
            return false ;
        if ( getPort() != other.getPort() )
            return false ;
        return true ;
    }
    
    public boolean sameHost(NetHost here) {
        return sameHost(here.hostname) ;
    }

    public boolean sameHost(String here) {
        return Objects.equals(getName(), here) ;
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }

    @Override public String toString() {
        String x = getName() ;
        if ( getPort() > 0 )
            x = x+":"+getPort() ;
        return x ;
    }
}
