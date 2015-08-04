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

package lizard.conf.parsers;

import java.util.* ;
import java.util.concurrent.atomic.AtomicInteger ;

import lizard.conf.* ;
import lizard.system.LizardException ;
import migrate.Q ;

import org.apache.jena.atlas.lib.StrUtils ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.jena.query.QuerySolution ;
import org.apache.jena.rdf.model.Model ;
import org.apache.jena.rdf.model.Resource ;
import org.apache.jena.riot.system.PrefixMap ;
import org.apache.jena.riot.system.PrefixMapFactory ;
import org.seaborne.dboe.sys.Names ;
import org.slf4j.Logger ;

public class LzConfParserRDF {

    private static Logger logConf = Config.logConf ;
    
    /** Read RDF format */
    public static ConfCluster parseConfFile(Model model) {
        ConfCluster confCluster = new ConfCluster(new ConfDataset(null));
        parseZookeeper(model, confCluster) ;
        parsePlacements(model, confCluster) ;
        
        // Index and nodetable.
        indexServers(model, confCluster) ;
        nodeServers(model, confCluster) ;
        
        // Dataset : checking.
        LzDatasetDesc ldd = dataset(model, confCluster) ;
        
        // ?? Check network address against placements.
        return confCluster ;
    }
    
    private static void parseZookeeper(Model model, ConfCluster confCluster) {
        String qs = StrUtils.strjoinNL(prefixes,
                                       "SELECT * {",
                                       ":zookeeper :server ?X"  ,
                                       "    OPTIONAL { ?X  :hostname ?hostname }",
                                       "    OPTIONAL { ?X  :port ?port }",
                                       "}") ;
        Map<String, VNode> placements = new HashMap<>() ;
        for ( QuerySolution row : Q.queryToList(model, qs) ) {
            String host = Q.getStringOrNull(row, "hostname") ;
            if ( host == null )
                throw new LizardException("No zookeeper host") ;
            String p = Q.getStringOrNull(row, "port") ;
            if ( p == null )
                throw new LizardException("No zookeeper port") ;
            int port = Integer.parseInt(p) ;
            ConfZookeeper confZK = ConfZookeeper.create(host, port) ;
            confCluster.zkServer.add(confZK) ;
        }
    }

    public static void parsePlacements(Model model, ConfCluster confCluster) {
        Map<String, VNode> placements = placements(model) ;
        confCluster.placements.putAll(placements);
    }
    
    private static Map<String, VNode> placements(Model model) {
        String qs = StrUtils.strjoinNL(prefixes,
                                       "SELECT * {",
                                       " ?vNode :vname ?vname ." ,
                                       "    OPTIONAL { ?vNode  :hostname ?hostname }",
                                       "    OPTIONAL { ?vNode  :port ?port }",
                                       "}") ;
        Map<String, VNode> placements = new HashMap<>() ;
        for ( QuerySolution row : Q.queryToList(model, qs) ) {
            String vname = Q.getStringOrNull(row, "vname") ;
            if ( vname == null )
                throw new LizardException("No vname") ;
            String host = Q.getStringOrNull(row, "hostname") ;
            if ( host == null )
                throw new LizardException(vname+" : No host") ;
            String p = Q.getStringOrNull(row, "port") ;
            if ( p == null )
                throw new LizardException(vname+" : No port") ;
            int port = Integer.parseInt(p) ;
            
            NetAddr addr = NetAddr.create(host, port) ;
            VNode vNode = new VNode(vname, addr) ;
            placements.put(vname, vNode) ;
        }
        return placements ;
    }

    private static void nodeServers(Model model, ConfCluster confCluster) {
        Map<Resource, DataServer> servers = dataServers(model, ":NodeServer") ;
        Map<Resource, ConfNodeTable> indexes = findNodeServices(model, confCluster, servers) ;

    }

    private static void indexServers(Model model, ConfCluster confCluster) {
        Map<Resource, DataServer> servers = dataServers(model, ":IndexServer") ;
        Map<Resource, ConfIndex> indexes = findIndexServices(model, confCluster, servers) ;
    }
    
    private static LzDatasetDesc dataset(Model model, ConfCluster confCluster) {
        Map<Resource, LzDatasetDesc> datasets = datasets(model) ;
        if ( datasets.size() == 0 )
            return null ;
        if ( datasets.size() > 1 )
            throw new LizardException("Multiple dataset descriptions") ;
        Optional<LzDatasetDesc> ldd = datasets.values().stream().findFirst() ;
        return ldd.get() ; 
    }

    /** Extract all dataset declarations (often and normally, one) */
    private static Map<Resource, LzDatasetDesc> datasets(Model model) {
        String qsDatasets = StrUtils.strjoinNL(Config.prefixes,
                                               "SELECT * { ?lz a lizard:Dataset ; }") ;
        Map<Resource, LzDatasetDesc> datasets = new HashMap<>() ;
        for ( QuerySolution row : Q.queryToList(model, qsDatasets) ) {
            
            Resource lz = row.getResource("lz") ;
            if ( datasets.containsKey(lz) )
                throw new LizardException("Multiple rows about "+lz) ;
            LzDatasetDesc desc = dataset(lz) ;
            datasets.put(lz, desc) ;
        }
        
        return datasets ;
    }
    
    public static LzDatasetDesc dataset(Resource lz) {
        String qsDatasets = StrUtils.strjoinNL(Config.prefixes,
                                               "SELECT * {",
                                               " ?lz a lizard:Dataset ;",
                                               "    OPTIONAL { ?lz :name      ?name }",
                                               "    OPTIONAL { ?lz :indexes   ?indexes }",
                                               "    OPTIONAL { ?lz :nodetable ?nodes }",
                                               "}") ;
        
         List<QuerySolution> rows = Q.queryToList(lz.getModel(), qsDatasets, "lz", lz) ;
         if ( rows.size() != 1 )
             throw new LizardException("Multiple dataset descriptions") ;
         QuerySolution row = rows.get(0) ;
         String name = Q.getStringOrNull(row, "name") ;
         if ( name == null ) {
             if ( lz.isAnon() )
                 name = "dataset" ;
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
         return desc ;
        
    }
    
    private static Map<Resource, ConfIndex> findIndexServices(Model m, ConfCluster confCluster, Map<Resource, DataServer> servers) {
        String qsIndexServices = StrUtils.strjoinNL(Config.prefixes,
                                                    "SELECT * {",
                                                    " ?svc a :IndexService ;",
                                                    "    OPTIONAL { ?svc :name       ?name }",
                                                    "    OPTIONAL { ?svc :indexOrder ?indexOrder }",
                                                    "    OPTIONAL { ?svc :servers    ?sList }",
                                                    "}") ;
        Map<Resource, ConfIndex> svcs = new LinkedHashMap<>() ;
        for ( QuerySolution row : Q.queryToList(m, qsIndexServices) ) {
            Resource svc = row.getResource("svc") ;
            if ( svcs.containsKey(svc) )
                throw new LizardException("Malform declaration for: "+svc) ;
            String name = Q.getStringOrNull(row, "name") ;
            if ( name == null ) 
                throw new LizardException("No name for IndexService: "+svc) ;
            Resource list = Q.getResourceOrNull(row, "sList") ;
            if ( list == null ) 
                throw new LizardException(name+" : No server list for IndexService") ;
            String indexOrder = Q.getStringOrNull(row, "indexOrder") ;
            if ( indexOrder == null ) 
                throw new LizardException(name+" : No index order for IndexService") ;
            
            List<Resource> sList = Q.listResources(list) ;
            
            int N = indexOrder.length() ;
            String primary = null ;
            if ( indexOrder.length() == 3 ) 
                primary = Names.primaryIndexTriples ;
            else if ( indexOrder.length() == 4 )
                primary = Names.primaryIndexQuads ;
            else
                throw new LzConfigurationException("Bad index name: "+indexOrder) ; 
            
            ConfIndex confIndex = new ConfIndex(null, indexOrder, 1, N) ;
            FmtLog.debug(logConf, "IndexService: <%s>:%s", svc.getURI(), confIndex.indexOrder) ;
            
            // Process servers.
            AtomicInteger integer = new AtomicInteger(0) ;
            sList.forEach(svr -> {
                DataServer ds = servers.get(svr) ;
                if ( ds == null )
                    throw new LzConfigurationException("No server found: "+indexOrder+" : "+svr) ;
                int i = integer.incrementAndGet() ;
                ConfIndexElement idx = new ConfIndexElement(ds.name, /*confIndex.indexOrder+"-"+i,*/ ds.data, confIndex, ds.addr) ;
                confCluster.eltsIndex.add(idx) ;
            });
            confCluster.dataset.indexes.add(confIndex) ;
            svcs.put(svc, confIndex) ;
        }
        return svcs ; 
    }

    private static Map<Resource, ConfNodeTable> findNodeServices(Model model, ConfCluster confCluster, Map<Resource, DataServer> servers) {
        String qsNodeServices = StrUtils.strjoinNL(prefixes,
                                                   "SELECT * {",
                                                   " ?svc a :NodeService ;",
                                                   "    OPTIONAL { ?svc :name ?name } ",
                                                   "    OPTIONAL { ?svc :servers ?sList }",
                                                   "}") ;
        Map<Resource, ConfNodeTable> svcs = new LinkedHashMap<>() ;
        for ( QuerySolution row : Q.queryToList(model, qsNodeServices) ) {
            //@@ Check one nodetable.
            Resource svc = row.getResource("svc") ;
            if ( svcs.containsKey(svc) )
                throw new LizardException("Malform declaration for: "+svc) ;
            String name = Q.getStringOrNull(row, "name") ;
            if ( name == null )
                throw new LizardException("No name for NodeService: "+svc) ;
            Resource list = Q.getResourceOrNull(row, "sList") ;
            if ( list == null ) 
                throw new LizardException(name+" : No shard list for NodeService") ;
            List<Resource> sList = Q.listResources(list) ;

            FmtLog.debug(logConf, "Node service <%s>:%s", svc.getURI(), name) ;

            int N = sList.size() ;
            ConfNodeTable confNode = new ConfNodeTable(1, N) ;
            // Process servers.
            AtomicInteger integer = new AtomicInteger(0) ;
            sList.forEach(svr -> {
                DataServer ds = servers.get(svr) ;
                if ( ds == null )
                    throw new LzConfigurationException("No server found: "+svr) ;
                int i = integer.incrementAndGet() ;
                ConfNodeTableElement nt  = new ConfNodeTableElement(ds.name, ds.data, confNode, ds.addr) ;
                confCluster.eltsNodeTable.add(nt) ;
            });
            confCluster.dataset.nodeTable = confNode ;
            svcs.put(svc, confNode) ;
        }
        return svcs ; 
    }


    /** Extract all data servers */
    private static Map<Resource, DataServer> dataServers(Model model, String resourceType) {
        String qs = StrUtils.strjoinNL(prefixes,
                                       "SELECT * {",
                                       " ?nServer a "+resourceType ,
                                       "    OPTIONAL { ?nServer :name     ?name }",
                                       "    OPTIONAL { ?nServer :hostname ?host }",
                                       "    OPTIONAL { ?nServer :port     ?port }",
                                       "    OPTIONAL { ?nServer :data     ?data }",
                                       "}") ;
        Map<Resource, DataServer> servers = new HashMap<>() ;
        for ( QuerySolution row : Q.queryToList(model, qs) ) {
            Resource svr = row.getResource("nServer") ;
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
    
            VNodeAddr addr = VNodeAddr.create(host, port) ;
            DataServer ds = new DataServer(svr, name, data, addr) ;
            servers.put(svr, ds) ;
        }
        return servers ;
    }

    private static final String prefixes = StrUtils.strjoinNL
        ("PREFIX :          <urn:lizard:>",
         "PREFIX lizard:    <urn:lizard:ns#>",
         "PREFIX list:      <http://jena.apache.org/ARQ/list#>" ,
         "PREFIX xsd:       <http://www.w3.org/2001/XMLSchema#>",
         "PREFIX rdf:       <http://www.w3.org/1999/02/22-rdf-syntax-ns#>",
         "PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#>",
         "") ;
    
    private static PrefixMap confPrefixes = PrefixMapFactory.create() ;
    static {
        confPrefixes.add("",        "urn:lizard:") ;
        confPrefixes.add("lizard",  "urn:lizard:ns#") ;
        confPrefixes.add("list",    "http://jena.apache.org/ARQ/list#") ;
        confPrefixes.add("xsd",     "http://www.w3.org/2001/XMLSchema#") ;
        confPrefixes.add("rdf",     "http://www.w3.org/1999/02/22-rdf-syntax-ns#") ;
        confPrefixes.add("rdfs",    "http://www.w3.org/2000/01/rdf-schema#") ;
    }

    static class DataServer {
        public final Resource resource ;
        public final String name ;
        public final String data ;
        public final VNodeAddr addr ;

        public DataServer(Resource r, String name, String data, VNodeAddr addr) {
            this.resource = r ;
            this.name = name ;
            this.data = data ;
            this.addr = addr ;
        }
        
        @Override
        public String toString() {
            if ( data == null )
                return name ;
            return name+" ["+data+"]" ;
        } 
    }
}
