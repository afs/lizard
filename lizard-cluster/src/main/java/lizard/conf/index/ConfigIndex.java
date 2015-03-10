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

import java.util.* ;
import java.util.Map.Entry ;

import lizard.cluster.Platform ;
import lizard.conf.Config ;
import lizard.conf.ConfigLib ;
import lizard.conf.DataServer ;
import lizard.index.ClusterTupleIndex ;
import lizard.index.DistributorTuplesReplicate ;
import lizard.index.TServerIndex ;
import lizard.index.TupleIndexRemote ;
import lizard.system.Component ;
import lizard.system.LizardException ;
import migrate.Q ;
import org.apache.jena.atlas.lib.ColumnMap ;
import org.apache.jena.atlas.lib.StrUtils ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.jena.riot.RDFDataMgr ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import com.hp.hpl.jena.query.QuerySolution ;
import com.hp.hpl.jena.rdf.model.Model ;
import com.hp.hpl.jena.rdf.model.Resource ;
import com.hp.hpl.jena.tdb.base.file.Location ;
import com.hp.hpl.jena.tdb.setup.Build ;
import com.hp.hpl.jena.tdb.store.tupletable.TupleIndex ;
import com.hp.hpl.jena.tdb.sys.Names ;

/** Configuration of indexes */
public class ConfigIndex {
    // XXX Currently, unsharded.
    public static Logger logConf = LoggerFactory.getLogger("lizard.Config") ;
    
    private static String prefixes = Config.prefixes ;

    // The configuration.
    private Model model ; 
    private Map<Resource, IndexService>      indexServiceDecl ;
    private Map<Resource, IndexServer>       indexServers ;

    public static ConfigIndex create(String config) { 
        Model m = RDFDataMgr.loadModel(config) ;
        return create(m) ;
    }
    
    public static ConfigIndex create(Model model) {
        return new ConfigIndex(model) ;
    }
    
    /*package*/ ConfigIndex(Model model) {
        // Check no index duplicates.
        indexServiceDecl    = findIndexServices(model) ;
        indexServers        = findIndexServers(indexServiceDecl, model) ;
        checkIndexServices(indexServiceDecl, indexServers) ;
        
        if ( indexServiceDecl.size() < 1 ) {
            throw new LizardException("No IndexServices found in configuration") ;
        }
    }

    public Collection<IndexServer> indexServers() {
        return indexServers.values() ;
    }

    public Collection<IndexService> indexServices() {
        return indexServiceDecl.values() ;
    }

    public IndexServer findIndexServer(Resource r) {
        return indexServers.get(r) ;
    }

    public IndexService findIndexServices(Resource r) {
        return indexServiceDecl.get(r) ;
    }

    @Deprecated
    public static void buildServers(ConfigIndex conf, Platform platform, Location location) {
        for ( IndexServer idxServer : conf.indexServers() ) {
            // Find service.
            Location loc = location.getSubLocation(idxServer.name) ;
            IndexService idxSvc = conf.findIndexServiceForIndexServer(idxServer) ; 
            TServerIndex srv = buildIndexServer(idxServer, idxSvc.indexOrder, loc) ;
            platform.add(srv) ;
        }
    }
    
    private IndexService findIndexServiceForIndexServer(IndexServer idxServer) {
        IndexService idxSvc = findIndexService(indexServiceDecl, idxServer.resource) ;
        if ( idxSvc == null )
            throw new LizardException("No index service for node server : "+idxServer) ;
        return idxSvc ;
    }
    
    /** Build directly, locally */
    @Deprecated
    public static TServerIndex buildIndexServer(IndexServer ds, String indexOrder, Location loc) {
        FmtLog.info(logConf, "buildIndexServer: %s %s %s", indexOrder, ds.port, loc) ;
        if ( indexOrder.length() != 3 ) 
            throw new LizardException("Not a triple index") ;
        TupleIndex index = Build.openTupleIndex(loc, "Idx"+indexOrder, Names.primaryIndexTriples, indexOrder) ;
        TServerIndex serverIdx = TServerIndex.create(ds.port, index) ;
        return serverIdx ; 
    }
    
    public TupleIndex buildIndex(IndexService svc, List<Component> clientStartables) {
        String idxOrder = svc.indexOrder ;

        int N = idxOrder.length() ;
        if ( N != 3 && N != 4 )
            FmtLog.warn(logConf, "Strange index size: %d (from '%s')", N, idxOrder) ;
        // The expected shards -- svc.shards
        // The actual shards -- shards
        ColumnMap cmap = new ColumnMap("SPO", idxOrder) ;
        DistributorTuplesReplicate dist = new DistributorTuplesReplicate(cmap) ;  
        List<TupleIndexRemote> indexes = new ArrayList<>() ;
        for ( Resource r : svc.servers ) {
            IndexServer idxServer = indexServers.get(r) ; 
            TupleIndexRemote idx = TupleIndexRemote.create(idxServer.hostname, idxServer.port, idxOrder, cmap) ;
            indexes.add(idx) ;
            clientStartables.add(idx) ;
        }
        dist.add(indexes);
        // All shards, all replicas.
        ClusterTupleIndex tupleIndex = new ClusterTupleIndex(dist, N, cmap, svc.name) ;
        return tupleIndex ;
        
    }
    
    private static Map<Resource, IndexService> findIndexServices(Model m) {
        String qsIndexServices = StrUtils.strjoinNL(Config.prefixes,
                                                    "SELECT * {",
                                                    " ?svc a :IndexService ;",
                                                    "    OPTIONAL { ?svc :name       ?name }",
                                                    "    OPTIONAL { ?svc :indexOrder ?indexOrder }",
                                                    "    OPTIONAL { ?svc :servers    ?sList }",
                                                    "    OPTIONAL { ?svc :data       ?data }",
                                                    "}") ;
        Map<Resource, IndexService> svcs = new LinkedHashMap<>() ;
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
            String dataDir = Q.getStringOrNull(row, "data") ;
            
            List<Resource> sList = Q.listResources(list) ;
            IndexService nSvc = new IndexService(svc, name, indexOrder, sList) ;
            
            if ( logConf.isDebugEnabled() ) {
                if ( dataDir == null )
                    FmtLog.debug(logConf, "Index server: %s", nSvc.name) ;
                else
                    FmtLog.debug(logConf, "Index server: %s [%s]", nSvc.name, dataDir) ;
            }
            svcs.put(svc, nSvc) ;
        }
        return svcs ; 
    }

    
    private Map<Resource, IndexServer> findIndexServers(Map<Resource, IndexService> indexServiceDecl, Model m) {
        Map<Resource, DataServer> dataserver = ConfigLib.dataServers(m, ":IndexServer") ;
        Map<Resource, IndexServer> indexServers = new LinkedHashMap<>() ;

        dataserver.forEach((r,ds) -> {
            IndexService ns = findIndexService(indexServiceDecl, r) ; 
            IndexServer indexServer  = new IndexServer(r, ds.name, ns, ds.hostname, ds.port, ds.data) ;
            indexServers.put(r, indexServer) ;
        });
        return indexServers ;
    }
    
//    private static Map<Resource, IndexServer> findIndexServers(Map<Resource, IndexService> indexServiceDecl, Model m) {
//        // c.f. ConfigLib.dataServers
//        String qsIndexServers= StrUtils.strjoinNL(prefixes,
//                                                  "SELECT * {",
//                                                  "  ?idxServer a :IndexServer ;",
//                                                  "    OPTIONAL { ?idxServer :name ?name }",
//                                                  // Unnecessary - in the deployment file.
//                                                  "    OPTIONAL { ?idxServer :hostname ?hostname }",    
//                                                  "    OPTIONAL { ?idxServer :port ?port }",
//                                                  "    OPTIONAL { ?idxServer :data ?data }",
//                                                  "}") ;
//        Map<Resource, IndexServer> indexServers = new LinkedHashMap<>() ;
//        for ( QuerySolution row : Q.queryToList(m, qsIndexServers) ) {
//            Resource idxServer = row.getResource("idxServer") ;
//            if ( indexServers.containsKey(idxServer) )
//                throw new LizardException("Malform declaration for: "+idxServer) ;
//            String name = Q.getStringOrNull(row, "name") ;
//            if ( name == null)
//                throw new LizardException("No name for IndexServer: "+idxServer) ;
//            String hostname = Q.getStringOrNull(row, "hostname") ;
//            if ( hostname == null)
//                throw new LizardException("No hostname for IndexServer: "+idxServer) ;
//            Long port = Q.getIntegerOrNull(row, "port") ;
//            if ( port == null)
//                throw new LizardException("No port for IndexServer: "+idxServer) ;
//            IndexService indexService = findIndexService(indexServiceDecl, idxServer) ;
//            if ( indexService == null )
//                throw new LizardException("No IndexService for IndexServer: "+idxServer) ;
//            
//            // Optional
//            String dataDir = Q.getStringOrNull(row, "data") ;
//            
//            IndexServer indexServer  = new IndexServer(idxServer, name, indexService, hostname, port.intValue(), dataDir) ;
//            FmtLog.debug(logConf, "Node server: %s", indexServer) ;
//            indexServers.put(idxServer, indexServer) ;
//        }
//
//        return indexServers ; 
//    }
    
    private static IndexService findIndexService(Map<Resource, IndexService> indexServiceDecl, Resource idxServer) {
        return indexServiceDecl.values().stream()
            .filter(idx -> idx.servers.contains(idxServer))
            .findFirst().orElse(null) ;
    }
    
    private static void checkIndexServices(Map<Resource, IndexService> indexServiceDecl,
                                           Map<Resource, IndexServer> indexServerDecl) {
        for ( Entry<Resource, IndexService> e : indexServiceDecl.entrySet() ) {
            Resource r = e.getKey() ;
            IndexService IndexSvc = e.getValue() ;
            if ( IndexSvc.servers.isEmpty() )    
                FmtLog.warn(logConf, "No Index servers for Index service %s", IndexSvc.name) ;
        }

        for ( Entry<Resource, IndexServer> e : indexServerDecl.entrySet() ) {
            Resource r = e.getKey() ;
            IndexServer indexServer = e.getValue() ;
            checkIndexServerHasIndexService(indexServer, indexServiceDecl) ;
        }
    }
    
    /** Check one IndexServer is mentioned once and only once in the Index Services */ 
    private static void checkIndexServerHasIndexService(IndexServer indexServer, Map<Resource, IndexService> indexServiceDecl) {
        // (Typically, there is only one Index service) 
        IndexService indexService = null ;
        for ( IndexService idxSvc : indexServiceDecl.values() ) {
            if ( idxSvc.servers.contains(indexServer) ) {
                if ( indexService != null )
                    throw new LizardException("Multiple index services for index server : "+indexServer) ;
            }
            indexService = idxSvc ;
        }
        
        if ( indexServer == null ) {
            throw new LizardException("No index service for node server : "+indexServer) ;
        } 
    }

    public static void printConfiguration(String configFile) {
        Model model = RDFDataMgr.loadModel(configFile) ; 
        ConfigIndex conf = new ConfigIndex(model) ;
        print(conf) ;
    }
        
    public static void print(ConfigIndex conf) {
        System.out.println("Index services:") ;
        Q.printMap(conf.indexServiceDecl) ;
        System.out.println("Index servers:") ;
        Q.printMap(conf.indexServers) ;
    }
}
