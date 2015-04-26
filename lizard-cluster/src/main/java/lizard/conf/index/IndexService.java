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

package lizard.conf.index;

import java.util.List ;

import org.apache.jena.rdf.model.Resource ;

public class IndexService {
    public final Resource resource ;
    public final String name ;
    public final String indexOrder ;
    public final List<Resource> servers ;
    
    public IndexService(Resource resource, String name, String indexOrder, List<Resource> servers) {
        this.resource = resource ;
        this.name = name ;
        this.indexOrder = indexOrder ;
        this.servers = servers ;
    }
    
    @Override
    public String toString() { return name ; } 
}