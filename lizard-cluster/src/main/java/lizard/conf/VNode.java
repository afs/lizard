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

/**
 * Configuration representation of one vNode - that is, a JVM instance. VNodes
 * host index and node table servers. There is a symbolic name, address mapping
 * and an port for contact (e.g. admin).  The contact port is not the port
 * for service access.
 * <p>
 * Many vNodes can be on one machine - it is the responsibility of the
 * configuration to avoid clashes of ports.
 */
public class VNode {
    
    public final String vname ;
    private final NetAddr endpoint ;
    
    public VNode(String vname, NetAddr endpoint) {
        this.vname = vname ;
        this.endpoint = endpoint ;
    }
    
    public String getDNSname() {
        return endpoint.getName() ;
    }

    public NetAddr getAdminEndpoint() {
        return endpoint ;
    }
    
    @Override
    public String toString() {
        return vname+"->"+endpoint ;
    }
}

