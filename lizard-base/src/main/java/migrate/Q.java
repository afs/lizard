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

package migrate;

import java.util.ArrayList ;
import java.util.LinkedHashSet ;
import java.util.List ;
import java.util.Set ;

import org.apache.jena.atlas.iterator.Iter ;

import com.hp.hpl.jena.query.* ;
import com.hp.hpl.jena.rdf.model.Literal ;
import com.hp.hpl.jena.rdf.model.Model ;
import com.hp.hpl.jena.rdf.model.RDFNode ;
import com.hp.hpl.jena.rdf.model.Resource ;

public class Q {

    static public String getStringOrNull(QuerySolution row, String var) {
        Literal lit = row.getLiteral(var) ;
        if ( lit == null ) return null ; 
        return lit.getLexicalForm() ;
    }

    static public Resource getResourceOrNull(QuerySolution row, String var) {
        return row.getResource(var) ;
    }

    static public Long getIntegerOrNull(QuerySolution row, String var) {
        Literal lit = row.getLiteral(var) ;
        if ( lit == null ) return null ; 
        return lit.getLong() ;
    }

    static public List<Resource> getListResourceOrNull(QuerySolution row, String var) {
        Resource r = row.getResource(var) ;
        if ( r == null ) return null ;
        return listResources(r) ;
    }

    static public Set<RDFNode> project(List<QuerySolution> rows, String varName) {
        Set<RDFNode> x = new LinkedHashSet<>() ;   // Predictable iteration.
        for ( QuerySolution qs : rows )
            x.add(qs.get(varName)) ;
        return x ;
    }

    public static List<Resource> listResources(Resource r) {
        String qs = "PREFIX list: <http://jena.hpl.hp.com/ARQ/list#> SELECT * { ?r list:index (?idx ?member) }" ;
        List<QuerySolution> rows = queryToList(r.getModel(), qs, "r", r) ;
        List<Resource> elts = new ArrayList<>() ;
        for ( QuerySolution row : rows ) {
            Resource member = row.getResource("member") ;
            elts.add(member) ;
        }
        return elts ;
    }

    public static List<String> listStrings(Resource r) {
        String qs = "PREFIX list: <http://jena.hpl.hp.com/ARQ/list#> SELECT * { ?r list:index (?idx ?member) }" ;
        List<QuerySolution> rows = queryToList(r.getModel(), qs, "r", r) ;
        List<String> elts = new ArrayList<>() ;
        for ( QuerySolution row : rows ) {
            Literal member = row.getLiteral("member") ;
            elts.add(member.getLexicalForm()) ;
        }
        return elts ;
    }


    public static List<RDFNode> listMembers(Resource r) {
        String qs = "PREFIX list: <http://jena.hpl.hp.com/ARQ/list#> SELECT * { ?r list:index (?idx ?member) }" ;
        List<QuerySolution> rows = queryToList(r.getModel(), qs, "r", r) ;
        List<RDFNode> elts = new ArrayList<>() ;
        for ( QuerySolution row : rows ) {
            RDFNode member = row.get("member") ;
            elts.add(member) ;
        }
        return elts ;
    }

    
    static public List<QuerySolution> queryToList(Model m, String queryString) {
        return queryToList(m, queryString, null) ;
    }

    static public List<QuerySolution> queryToList(Model m, String queryString, String var, RDFNode r) {
        QuerySolutionMap qsm = new QuerySolutionMap() ;
        qsm.add(var, r);
        return queryToList(m, queryString, qsm) ;
    }

    static public List<QuerySolution> queryToList(Model m, String queryString, QuerySolution init) {
        Query q = QueryFactory.create(queryString) ;
        try ( QueryExecution qExec = createQueryExecution(q, m, init) ) {
            ResultSet rs = qExec.execSelect() ;
            return Iter.toList(rs) ;
        }
    }
    
    static private QueryExecution createQueryExecution(Query q, Model m, QuerySolution init) {
        if (init == null)
            return QueryExecutionFactory.create(q, m) ;
        else
            return QueryExecutionFactory.create(q, m, init) ;
    }

}
