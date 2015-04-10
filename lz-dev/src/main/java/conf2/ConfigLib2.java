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

package conf2;

import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;

import lizard.conf.dataset.LzDatasetDesc ;
import lizard.system.LizardException ;
import migrate.Q ;

import com.hp.hpl.jena.query.QuerySolution ;
import com.hp.hpl.jena.rdf.model.Model ;
import com.hp.hpl.jena.rdf.model.Resource ;

import org.apache.jena.atlas.lib.StrUtils ;
import org.apache.jena.riot.system.PrefixMap ;
import org.apache.jena.riot.system.PrefixMapFactory ;

/** Configuration helper functions */
public class ConfigLib2 {
    
    public static final String prefixes = StrUtils.strjoinNL
        ("PREFIX :          <urn:lizard:>",
         "PREFIX lizard:    <urn:lizard:ns#>",
         "PREFIX list:      <http://jena.hpl.hp.com/ARQ/list#>" ,
         "PREFIX xsd:       <http://www.w3.org/2001/XMLSchema#>",
         "PREFIX rdf:       <http://www.w3.org/1999/02/22-rdf-syntax-ns#>",
         "PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#>",
         "") ;
    
    public static PrefixMap confPrefixes = PrefixMapFactory.create() ;
    static {
        confPrefixes.add("",        "urn:lizard:") ;
        confPrefixes.add("lizard",  "urn:lizard:ns#") ;
        confPrefixes.add("list",    "http://jena.hpl.hp.com/ARQ/list#") ;
        confPrefixes.add("xsd",     "http://www.w3.org/2001/XMLSchema#") ;
        confPrefixes.add("rdf",     "http://www.w3.org/1999/02/22-rdf-syntax-ns#") ;
        confPrefixes.add("rdfs",    "http://www.w3.org/2000/01/rdf-schema#") ;
    }

//    /** Extract all data servers */
//    public static <X> void dataServers(Model model, String resourceType, Consumer<X> sink) {
//        String qs = StrUtils.strjoinNL(ConfigLib2.prefixes,
//                                       "SELECT * {",
//                                       " ?nServer a "+resourceType ,
//                                       "    OPTIONAL { ?nServer :name     ?name }",
//                                       "    OPTIONAL { ?nServer :hostname ?host }",
//                                       "    OPTIONAL { ?nServer :port     ?port }",
//                                       "    OPTIONAL { ?nServer :data     ?data }",
//                                       "}") ;
//        for ( QuerySolution row : Q.queryToList(model, qs) ) {
//            Resource svr = row.getResource("nServer") ;
//            String name = Q.getStringOrNull(row, "name") ;
//            if ( name == null )
//                throw new LizardException("No name for "+svr) ;
//            String host = Q.getStringOrNull(row, "host") ;
//            if ( host == null )
//                throw new LizardException(name+" : No host") ;
//            String p = Q.getStringOrNull(row, "port") ;
//            if ( p == null )
//                throw new LizardException(name+" : No port") ;
//            int port = Integer.parseInt(p) ;
//            
//            String data = Q.getStringOrNull(row, "data") ;
//            if ( data == null )
//                throw new LizardException(name+" : No data location") ;
//            DataServer ds = new DataServer(svr, name, host, port, data) ;
//            servers.put(svr, ds) ;
//        }
//    }

    /** Extract all dataset declarations (often and normally, one) */
    public static Map<Resource, LzDatasetDesc> datasets(Model model) {
        String qsDatasets = StrUtils.strjoinNL(ConfigLib2.prefixes,
                                               "SELECT * {",
                                               // lizard: <urn:lizard:ns#>
                                               " ?lz a lizard:Dataset ;",
                                               "    OPTIONAL { ?lz :name      ?name }",
                                               "    OPTIONAL { ?lz :indexes   ?indexes }",
                                               "    OPTIONAL { ?lz :nodetable ?nodes }",
                                               "}") ;
        
        Map<Resource, LzDatasetDesc> datasets = new HashMap<>() ;
        for ( QuerySolution row : Q.queryToList(model, qsDatasets) ) {
            Resource lz = row.getResource("lz") ;
            if ( datasets.containsKey(lz) )
                throw new LizardException("Multiple rows about "+lz) ;
            String name = Q.getStringOrNull(row, "name") ;
            if ( name == null ) {
                if ( lz.isAnon() )
                    name = "ds-"+(datasets.size()+1) ;
                else 
                    name = lz.getLocalName() ;
            }
            List<Resource> indexes = Q.getListResourceOrNull(row, "indexes") ;
            if ( indexes == null )
                throw new LizardException("No :indexes in lizard:Dataset description") ;
            List<Resource> nodes = Q.getListResourceOrNull(row, "nodes") ;
            if ( nodes == null )
                throw new LizardException("No :nodes in lizard:Dataset description") ;
            
            LzDatasetDesc desc = new LzDatasetDesc(lz, name, indexes, nodes) ;
            datasets.put(lz, desc) ;
        }
        
        return datasets ;
    }
}
