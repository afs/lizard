/*
 *  Copyright 2014 Andy Seaborne
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

package lizard.conf.index;

import lizard.conf.DataServer ;

import com.hp.hpl.jena.rdf.model.Resource ;

public class IndexServer extends DataServer {
    public final IndexService indexService ;

    public IndexServer(Resource r, String name, IndexService indexService, String hostname, int port, String data) {
        super(r, name, hostname, port, data) ;
        this.indexService = indexService ;
    }
}

