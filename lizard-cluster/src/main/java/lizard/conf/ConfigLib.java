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

package lizard.conf;

import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;
import java.util.Set ;

import lizard.system.LizardException ;
import migrate.Q ;
import org.apache.jena.atlas.lib.StrUtils ;

import com.hp.hpl.jena.query.QuerySolution ;
import com.hp.hpl.jena.rdf.model.Model ;
import com.hp.hpl.jena.rdf.model.RDFNode ;
import com.hp.hpl.jena.rdf.model.Resource ;

/** Configuration helper functions */
public class ConfigLib {

    /** Data servers */
    public static Map<Resource, DataServer> dataServers(Model model, String resourceType) {
        String qs = StrUtils.strjoinNL(Config.prefixes,
                                       "SELECT * {",
                                       " ?svr a "+resourceType ,
                                       "    OPTIONAL { ?svr :name     ?name }",
                                       "    OPTIONAL { ?svr :hostname ?host }",
                                       "    OPTIONAL { ?svr :port     ?port }",
                                       "    OPTIONAL { ?svr :data     ?data }",
                                       "}") ;
        Map<Resource, DataServer> servers = new HashMap<>() ;
        for ( QuerySolution row : Q.queryToList(model, qs) ) {
            Resource svr = row.getResource("svr") ;
            String name = Q.getStringOrNull(row, "name") ;
            if ( name == null )
                throw new LizardException("No name for "+svr) ;
            String host = Q.getStringOrNull(row, "host") ;
            if ( host == null )
                throw new LizardException(name+" : No host") ;
            String p = Q.getStringOrNull(row, "port") ;
            if ( p == null )
                throw new LizardException(name+" : No port") ;
            int port = Integer.parseInt(p) ;
            
            String data = Q.getStringOrNull(row, "data") ;
            if ( data == null )
                throw new LizardException(name+" : No data location") ;
            DataServer ds = new DataServer(svr, name, host, port, data) ;
            servers.put(svr, ds) ;
        }
        return servers ;
    }
        

    /** All resources */
    public static Set<RDFNode> resources(Model m, String queryString, String varName) {
        List<QuerySolution> x = Q.queryToList(m, queryString) ;
        return Q.project(x, varName) ;
    }
    
    public static void printMap(Map<Resource, ?> map) {
        map.entrySet().stream().forEach(
           e -> System.out.printf("  %s:\"%s\"\n", "<"+e.getKey()+">", e.getValue())
        ) ;
    }

}
