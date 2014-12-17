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

package lizard.conf.node;

import java.util.* ;
import java.util.Map.Entry ;

import lizard.cluster.Platform ;
import lizard.conf.Config ;
import lizard.conf.ConfigLib ;
import lizard.conf.DataServer ;
import lizard.conf.LzBuild ;
import lizard.node.ClusterNodeTable ;
import lizard.node.DistributorNodesReplicate ;
import lizard.node.NodeTableRemote ;
import lizard.node.TServerNode ;
import lizard.system.Component ;
import lizard.system.LizardException ;
import migrate.Q ;
import org.apache.jena.atlas.lib.StrUtils ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.jena.riot.RDFDataMgr ;
import org.slf4j.Logger ;

import com.hp.hpl.jena.query.QuerySolution ;
import com.hp.hpl.jena.rdf.model.Model ;
import com.hp.hpl.jena.rdf.model.Resource ;
import com.hp.hpl.jena.tdb.base.file.Location ;
import com.hp.hpl.jena.tdb.store.nodetable.NodeTable ;
import com.hp.hpl.jena.tdb.store.nodetable.NodeTableInline ;

/** Configuration of NodeTables */
public class ConfigNode {
    public static Logger logConf = Config.logConf ;
    
    private static String prefixes = Config.prefixes ;
    
    // The configuration.
    private Model                       model ; 
    private Map<Resource, NodeService>  nodeServiceDecl ;
    private Map<Resource, NodeServer>   nodeServerDecl ;
    
    public static ConfigNode create(String config) { 
        Model m = RDFDataMgr.loadModel(config) ;
        return create(m) ;
    }
    
    public static ConfigNode create(Model model) {
        return new ConfigNode(model) ;
    }
    
    /*package*/ ConfigNode(Model model) {
        nodeServiceDecl = findNodeServices(model) ;
        nodeServerDecl  = findNodeServers(nodeServiceDecl, model) ;
        
        if ( nodeServiceDecl.size() != 1 ) {
            throw new LizardException("No NodeService found in configuration") ;
        }
        
        checkNodeServices(nodeServiceDecl, nodeServerDecl) ;
    }

    public Collection<NodeService> nodeServices() {
        return nodeServiceDecl.values() ;
    }
    
    public Collection<NodeServer> nodeServers() {
        return nodeServerDecl.values() ;
    }

    public NodeService findNodeService(Resource r) {
        return nodeServiceDecl.get(r) ;
    }
    
    public NodeServer findNodeServer(Resource r) {
        return nodeServerDecl.get(r) ;
    }

    
    /** Build directly, locally */
    @Deprecated
    public static void buildServers(ConfigNode conf, Platform platform, Location location) {
        for ( NodeServer ns : conf.nodeServers() ) {
            Location loc = location.getSubLocation(ns.name) ;
            TServerNode srv = buildNodeServer(ns, loc) ;
            platform.add(srv) ;
        }
    }
    
    // XXX Put in BuildNode??
    
    /** Build directly, locally */
    @Deprecated
    public static TServerNode buildNodeServer(NodeServer ds, Location loc) {
        FmtLog.info(logConf, "buildNodeServer: %s %s", ds.port, loc) ;
        NodeTable nt = LzBuild.createNodeTable(loc) ;
        TServerNode serverNode = TServerNode.create(ds.port, nt) ;
        return serverNode ; 
    }
    
    /** Build a Cluster node table, from the configuration */
    public NodeTable buildNodeTable(List<Component> clientStartables) {
        NodeTable nt = buildNodeTable(nodeServers(), clientStartables) ;
        //nt = stackNodeTable(nt) ;
        return nt ;
    }

    /** Build a Cluster node table, from a node service description and a list of servers */
    private static ClusterNodeTable buildNodeTable(Collection<NodeServer> servers, List<Component> clientStartables) {
        DistributorNodesReplicate dist = new DistributorNodesReplicate() ;
        List<NodeTableRemote> nodeTables = new ArrayList<>() ;
        for ( NodeServer nodeServer : servers ) {
            NodeTableRemote nt = NodeTableRemote.create(nodeServer.hostname, nodeServer.port) ;
            logConf.info("Add NT "+nt) ;
            nodeTables.add(nt) ;
            clientStartables.add(nt) ;
        }
            
        dist.add(nodeTables) ;
        ClusterNodeTable cnt = new ClusterNodeTable(dist) ;
        return cnt ;
    }

    private static Map<Resource, NodeService> findNodeServices(Model m) {
        String qsNodeServices = StrUtils.strjoinNL(prefixes,
                                                   "SELECT * {",
                                                    " ?svc a :NodeService ;",
                                                    "    OPTIONAL { ?svc :name ?name } ",
                                                    "    OPTIONAL { ?svc :servers ?sList }",
                                                    "}") ;
        Map<Resource, NodeService> svcs = new LinkedHashMap<>() ;
        for ( QuerySolution row : Q.queryToList(m, qsNodeServices) ) {
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
            NodeService nSvc = new NodeService(svc, name, sList) ;
            FmtLog.debug(logConf, "Node service %s", nSvc.name) ;
            svcs.put(svc, nSvc) ;
        }
        return svcs ; 
    }

    // XXX See ConfigIndex.findIndexServer
    private Map<Resource, NodeServer> findNodeServers(Map<Resource, NodeService> nodeServiceDecl, Model m) {
        String qsNodeServers= StrUtils.strjoinNL(prefixes,
                                                   "SELECT * {",
                                                    "  ?nServer a :NodeServer ;",
                                                    "    OPTIONAL { ?nServer :name ?name }",
                                                    // Unnecessary - in the deployment file.
                                                    "    OPTIONAL { ?nServer :hostname ?hostname }",
                                                    "    OPTIONAL { ?nServer :port ?port }",
                                                    "    OPTIONAL { ?nServer :data ?data }",
                                                    "}") ;
        Map<Resource, NodeServer> nodeServers = new LinkedHashMap<>() ;
        for ( QuerySolution row : Q.queryToList(m, qsNodeServers) ) {
            Resource nServer = row.getResource("nServer") ;
            if ( nodeServers.containsKey(nServer) )
                throw new LizardException("Malform declaration for: "+nServer) ;
            String name = Q.getStringOrNull(row, "name") ;
            if ( name == null)
                throw new LizardException("No name for NodeServer: "+nServer) ;
            String hostname = Q.getStringOrNull(row, "hostname") ;
            if ( hostname == null)
                throw new LizardException("No hostname for NodeServer: "+nServer) ;
            Long port = Q.getIntegerOrNull(row, "port") ;
            if ( port == null)
                throw new LizardException("No port for NodeServer: "+nServer) ;
            // Optional
            String dataDir = Q.getStringOrNull(row, "data") ;
            
            NodeService nodeService = findNodeService(nodeServiceDecl, nServer) ;
            if ( nodeService == null )
                throw new LizardException("No NodeService for NodeServer: "+nServer) ;
            
            NodeServer nodeServer  = new NodeServer(nServer, name, nodeService, hostname, port.intValue(), dataDir) ;
            FmtLog.debug(logConf, "Node server: %s", nodeServer) ;
            nodeServers.put(nServer, nodeServer) ;
        }
 
        return nodeServers ; 
    }
    
    private static NodeService findNodeService(Map<Resource, NodeService> nodeServiceDecl, Resource nodeServer) {
        return nodeServiceDecl.values().stream()
            .filter(ns -> ns.servers.contains(nodeServer))
            .findFirst().orElse(null) ;
    }
    
    /** Check NodeService, NodeServer declarations link up */ 
    private static void checkNodeServices(Map<Resource, NodeService> nodeServiceDecl,
                                          Map<Resource, NodeServer> nodeServerDecl) {
        for ( Entry<Resource, NodeService> e : nodeServiceDecl.entrySet() ) {
            Resource r = e.getKey() ;
            NodeService nodeSvc = e.getValue() ;
            if ( nodeSvc.servers.isEmpty() )    
                FmtLog.warn(logConf, "No node servers for node service %s", nodeSvc.name) ;
        }

        for ( Entry<Resource, NodeServer> e : nodeServerDecl.entrySet() ) {
            Resource r = e.getKey() ;
            NodeServer nodeServer = e.getValue() ;
            checkNodeServerHasNodeService(nodeServer, nodeServiceDecl) ;
        }
    }

    /** Check one NodeServer is mentioned once and only once in the Node Services */ 
    private static void checkNodeServerHasNodeService(NodeServer nodeServer, Map<Resource, NodeService> nodeServiceDecl) {
        // (Typically, there is only one node service) 
        NodeService nodeSvc = null ;
        for ( NodeService nSvc : nodeServiceDecl.values() ) {
            if ( nSvc.servers.contains(nodeServer.resource) ) {
                if ( nodeSvc != null )
                    throw new LizardException("Multiple node services for node server : "+nodeServer) ;
            }
            nodeSvc = nSvc ;
        }
        
        if ( nodeSvc == null ) {
            throw new LizardException("No node service for node server : "+nodeServer) ;
        } 
    }
    

    public static NodeTable stackNodeTable(NodeTable nodeTable) {
        //nodeTable = NodeTableCache.create(nodeTable, 100, 100, 100) ; 
        nodeTable =  NodeTableInline.create(nodeTable) ;
        return nodeTable ;
    }
    
    public static void printConfiguration(String configFile) {
        Model model = RDFDataMgr.loadModel(configFile) ; 
        ConfigNode conf = new ConfigNode(model) ;
        print(conf) ;
    }
        
    public static void print(ConfigNode conf) {
        System.out.println("Node services: ") ;
        Q.printMap(conf.nodeServiceDecl) ;
        System.out.println("Node servers: ") ;
        Q.printMap(conf.nodeServerDecl) ;
    }
}
