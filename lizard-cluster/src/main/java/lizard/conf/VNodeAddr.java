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

import org.apache.jena.atlas.logging.FmtLog ;

/**
 * Address on a vNode service.  This is the vNode abstract name and a port number.
 * <p>
 * Many vNodes can be on one machine - it is the responsibility of the
 * configuration to avoid clashes of ports.
 */
public class VNodeAddr extends Endpoint {
    public static VNodeAddr create(String dnsName, int port) {
        return new VNodeAddr(dnsName, port) ;
    }

    public VNodeAddr(String name, int port) {
        super(name, port) ;
    }

    public NetAddr placement(VNodeLayout placements, int port) {
        VNode vNode = placements.get(getName()) ;
        if ( vNode == null ) {
            FmtLog.warn(this.getClass(), "No placement for %s\n", getName()) ;
            return null ;
        }
        return NetAddr.create(vNode.getDNSname(), port) ;
    }
}
