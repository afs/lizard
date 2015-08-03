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

import java.io.InputStream ;
import java.util.List ;
import java.util.Objects ;

import lizard.conf.* ;

import org.apache.jena.atlas.io.IO ;
import org.apache.jena.atlas.lib.ColumnMap ;
import org.apache.jena.atlas.lib.NotImplemented ;
import org.seaborne.dboe.sys.Names ;
import org.yaml.snakeyaml.Yaml ;

/** Parser for thr YAML-based format */ 
public class LzConfParserYAML {
    public static final String objDataset   = "dataset" ;
    public static final String objSparql    = "sparql" ;
    public static final String objCluster   = "cluster" ;
    public static final String objNodeTable = "nodetable" ;
    public static final String objVNode     = "vnodes" ;
    
    public static final String fIndexes     = ".indexes" ;
    public static final String fServers     = ".servers" ;
    public static final String fNodes       = ".nodes" ;
    public static final String fZookeeper   = ".zookeeper" ;
    public static final String fFileroot    = ".fileroot" ;
    public static final String fName        = ".name" ;
    public static final String fVName       = ".vname" ;
    public static final String fHostname    = ".hostname" ;
    public static final String fPort        = ".port" ;
    public static final String fData        = ".data" ;
    
    @SuppressWarnings("unchecked")
    public static ConfCluster parseConfFile(String clusterConfFilename, String layoutFilename) {
        VNodeLayout vnodeLayout = parseLayout(layoutFilename) ;
        InputStream inYaml = IO.openFile(clusterConfFilename) ;
        Object root = new Yaml().load(inYaml) ;
        // structure --
        //   cluster:
        //   sparql:
        //   dataset:
        
        List<String> zkServers = (List<String>)YAML.get(root, objCluster, fZookeeper) ;
        if ( zkServers != null ) {
            // @@
        }
        
        Object sparql = YAML.get(root, objSparql) ;
        if ( sparql != null )
            System.err.println("Configuration: sparql mentioned, not implemented") ;
        
        ConfCluster conf = new ConfCluster(new ConfDataset(null));
        conf.placements.putAll(vnodeLayout);
        
        String fileroot = (String)YAML.get(root, objCluster, fFileroot) ;
        conf.fileroot = fileroot ;
        
        Object dataset = YAML.get(root, objDataset) ;
        List<Object> indexes = (List<Object>)YAML.get(dataset, fIndexes) ;  // array.
        String nodes = (String)YAML.get(dataset, fNodes) ;      // name = "nodetable"
        if ( ! Objects.equals(nodes, objNodeTable) )
            throw new LzConfigurationException("Node table not called '"+objNodeTable+"'") ;
        
        for ( Object idx : indexes) {
            Object x = YAML.get(root, (String)idx) ;
            parseConfIndex(conf, x, root) ;
        }
        Object x = YAML.get(root, nodes) ;
        parseNodeTable(conf, x, root);
        
        // The zookeeper server.
        // @@
        ConfZookeeper confZookeeper = ConfZookeeper.create(2188, null) ;
        conf.zkServer.add(confZookeeper) ;
        return conf ;
    }
    
    /** Parse the layout file */
    @SuppressWarnings("unchecked")
    private static VNodeLayout parseLayout(String layoutFilename) {
        InputStream inYaml = IO.openFile(layoutFilename) ;
        Object root = new Yaml().load(inYaml) ;
        VNodeLayout vnodeLayout = new VNodeLayout() ;
        List<Object> vnodes = (List<Object>)YAML.get(root, objVNode) ;
        vnodes.forEach(vnode->{
            String vname = (String)YAML.get(vnode, fVName) ;
            String hostname = (String)YAML.get(vnode, fHostname) ;
            int port = (Integer)YAML.get(vnode, fPort) ;
            VNode vn = new VNode(vname, NetAddr.create(hostname, port)) ;
            vnodeLayout.put(vname, vn) ;
        }) ;
        return vnodeLayout ;
    }

    public static void parseConfIndex(ConfCluster confCluster, Object index, Object root) {
        String indexorder = (String)YAML.get(index, fName) ;
        
        String primary = null ;
        if ( indexorder.length() == 3 ) 
            primary = Names.primaryIndexTriples ;
        else if ( indexorder.length() == 4 )
            primary = Names.primaryIndexQuads ;
        else
            throw new LzConfigurationException("Bad index name: "+indexorder) ; 
        @SuppressWarnings("unchecked")

        List<Object> servers = (List<Object>)YAML.get(index, fServers) ;
        int N = servers.size() ;
        ConfIndex confIndex = new ConfIndex(new ColumnMap(primary, indexorder), indexorder, 1, N) ;
        
        for ( int i = 0 ; i < N ; i++ ) {
            Object server = servers.get(i) ;
            // @@ YAML.indirect
            server = YAML.get(root, (String)server);
            VNodeAddr addr = shard(server) ;
            String data = (String)YAML.get(server, fData);
            ConfIndexElement idx = new ConfIndexElement(confIndex.indexOrder+"-"+(i+1), data, confIndex, addr) ;
            confCluster.eltsIndex.add(idx) ;
        }
        
        confCluster.dataset.indexes.add(confIndex) ;
    }

    public static void parseNodeTable(ConfCluster confCluster, Object nodetable, Object root) {
        @SuppressWarnings("unchecked")
        List<Object> servers = (List<Object>)YAML.get(nodetable, fServers) ;
        int N = servers.size() ;
        ConfNodeTable confNT = new ConfNodeTable(1, N) ;
        
        for ( int i = 0 ; i < N ; i++ ) {
            Object server = servers.get(i) ;
            server = YAML.get(root, (String)server);
            VNodeAddr addr = shard(server) ;
            String data = (String)YAML.get(server, fData);
            ConfNodeTableElement elt = new ConfNodeTableElement("Nodes-"+(i+1), data, confNT, addr) ;
            confCluster.eltsNodeTable.add(elt) ;
        }
        confCluster.dataset.nodeTable = confNT ;
    }

    public static VNodeAddr shard(Object shard) {
        String host = (String)YAML.get(shard, fHostname) ;
        int port = (Integer)YAML.get(shard, fPort) ;
        return VNodeAddr.create(host, port) ;  
    }
    
    public static void parsePlacement(ConfCluster confCluster, String file) {
        throw new NotImplemented() ;
    }
}
