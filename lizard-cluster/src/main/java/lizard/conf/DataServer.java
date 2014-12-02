/*
 *  Copyright 2013, 2014 Andy Seaborne
 *
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
 */

package lizard.conf;

import com.hp.hpl.jena.rdf.model.Resource ;

/** DataServers */ 
public class DataServer {
    public final Resource resource ;
    public final String name ;
    public final String hostname ;
    public final int port;
    public final String data ;

    public DataServer(Resource r, String name, String hostname, int port, String data) {
        this.resource = r ;
        this.name = name ;
        this.hostname = hostname ;
        this.port = port ;
        this.data = data ;
    }
    
    @Override
    public String toString() { return name ; } 
}