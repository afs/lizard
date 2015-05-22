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

import org.apache.jena.atlas.lib.StrUtils ;
import org.apache.jena.riot.system.PrefixMap ;
import org.apache.jena.riot.system.PrefixMapFactory ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

public class Config {
    public static Logger logConf = LoggerFactory.getLogger("lizard.Config") ;
    
    public static final String prefixes = StrUtils.strjoinNL
        ("PREFIX :          <urn:lizard:>",
         "PREFIX lizard:    <urn:lizard:ns#>",
         "PREFIX list:      <http://jena.apache.org/ARQ/list#>" ,
         "PREFIX xsd:       <http://www.w3.org/2001/XMLSchema#>",
         "PREFIX rdf:       <http://www.w3.org/1999/02/22-rdf-syntax-ns#>",
         "PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#>",
         "") ;
    
    public static PrefixMap confPrefixes = PrefixMapFactory.create() ;
    static {
        confPrefixes.add("",        "urn:lizard:") ;
        confPrefixes.add("lizard",  "urn:lizard:ns#") ;
        confPrefixes.add("list",    "http://jena.apache.org/ARQ/list#") ;
        confPrefixes.add("xsd",     "http://www.w3.org/2001/XMLSchema#") ;
        confPrefixes.add("rdf",     "http://www.w3.org/1999/02/22-rdf-syntax-ns#") ;
        confPrefixes.add("rdfs",    "http://www.w3.org/2000/01/rdf-schema#") ;
    }
}

