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

import java.io.InputStream ;
import java.util.List ;
import java.util.Map ;

import lizard.cluster.Platform ;
import lizard.conf.dataset.LzDatasetDesc ;
import lizard.conf.index.IndexServer ;
import lizard.conf.node.NodeServer ;
import lizard.sys.Deployment ;
import conf2.Conf2.* ;

import org.apache.jena.atlas.io.IO ;
import org.apache.jena.atlas.io.IndentedWriter ;
import org.yaml.snakeyaml.Yaml ;

public class LzConf {
    
    public static void main(String[] args) throws Exception {
        int zkPort = 2188 ;
        
        // Describe the cluster, not deployment details.
        ConfNodeTable confNT = new ConfNodeTable(1, 1) ;
        ConfIndex posIdx =  new ConfIndex("POS", 1, 1) ;
        ConfIndex psoIdx =  new ConfIndex("PSO", 1, 1) ;
        ConfDataset confDatabase = new ConfDataset(confNT, posIdx, psoIdx) ;
        
        // Shards
        ConfIndexElement posIdx1 = new ConfIndexElement(posIdx, NetAddr.create("localhost", 2010)) ;
        ConfIndexElement psoIdx1 = new ConfIndexElement(psoIdx, NetAddr.create("localhost", 2012)) ;
        ConfNodeTableElement nt1 = new ConfNodeTableElement(confNT, NetAddr.create("localhost", 2014)) ;

        ConfCluster confCluster = new ConfCluster(confDatabase) ;
        // The zookeeper servers.
        confCluster.zkServer.add(NetAddr.create("localhost", zkPort)) ;
        confCluster.addIndexElements(posIdx1, psoIdx1) ;
        confCluster.addNodeElements(nt1) ;
        
        // Rewrite any host names. 
        
        // The deployment here.
        ConfZookeeper confZooKeeper = new ConfZookeeper(zkPort, "zkConf") ; 
        new ConfQueryServer() ;
        new ConfDeploy() ;
        
//        public static Deployment parse(Configuration config, String deploymentFile) {
//            Model model = LzLib.readAll(deploymentFile) ;
//            
//            List<NodeServer> nodeServers = ConfigLib.dataServers(model, ":NodeServer").values().stream()
//                .map(ds -> { return config.getConfNode().findNodeServer(ds.resource);})
//                .collect(Collectors.toList()) ;
//            List<IndexServer> indexServers = ConfigLib.dataServers(model, ":IndexServer").values().stream()
//                .map(ds -> { return config.getConfIndex().findIndexServer(ds.resource);})
//                .collect(Collectors.toList()) ;
//            
//            // ConfigLib.zkServers
//            LzDatasetDesc desc = config.getConfDataset().descDataset() ;
//            return new Deployment(indexServers, nodeServers, desc) ;
//        }
        
        List<NodeServer> nodeServers = null ;
//        NodeServer ns = null ;
//        ns.ref ;        
//        ns.data ;       ** Deployment matter
//        ns.hostname ;   ** NetAddr
//        ns.name ;
//        ns.nodeService ;
//        ns.port ;
        
        List<IndexServer> indexServers = null ;
//        IndexServer idxSrv = null ;
//        idxSrv.data ;
//        idxSrv.hostname ;
//        idxSrv.indexService ;
//        idxSrv.name ;
//        idxSrv.port ;
        
        LzDatasetDesc desc = null ;
        
        Deployment deployment = new Deployment(indexServers, nodeServers, desc) ;
        //deployment.datasetDesc ;
        //deployment.indexServers ;
        //deployment.nodeServers ;
        
    }

    public static Platform build(ConfDeploy deployment) {
        Platform platform = new Platform() ;
        //platform.add(null);
        
        return platform ;
//        Platform platform = new Platform() ;
//        deployment.nodeServers.stream().forEach(ns -> {
//            log.info("Build N: " + ns.resource) ;
//            buildNodeServer(ns, platform) ;
//        }) ;
//
//        deployment.indexServers.forEach(idx -> {
//            log.info("Build I: " + idx.resource) ;
//            buildIndexServer(idx, platform) ;
//        }) ;
//
//        Location location = Location.mem();
//        ConfigLizardDataset.buildDataset(location, deployment.datasetDesc) ;
//        
//        platform.start() ;
//        return platform ;
    }
    
    
    public static void mainYAML(String[] args) throws Exception {
        InputStream inYaml = IO.openFile("data.yaml") ;
        
        Object x = new Yaml().load(inYaml) ;
        System.out.println(x);
        System.out.println("<<<<-------------------------------------------------");
        print(x) ;
        System.out.println() ;
        System.out.println(">>>>-------------------------------------------------");
        
        // Access language :
        //  !global
        //  -link
        //  @array
        
        // can be lists.
        System.out.println(getField(x, "dataset", "nodes")) ;
        System.out.println() ;
        System.out.println(get(x, "dataset", "nodetable", "servers" )) ;
        
        System.exit(0) ;
    }

    public static void print(Object obj) {
        print (IndentedWriter.stdout, obj) ;
        IndentedWriter.stdout.flush();
    }
    
    public static void print(IndentedWriter w, Object obj) {
        if ( obj == null ) {
            w.print("<<null>>");
            return ;
        }
        
        if ( obj instanceof List ) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>)obj ;
            w.print("(\n");
            w.incIndent();
            list.forEach( x-> {
                print(w,x) ;   
                w.println();
            }) ;
            w.decIndent();
            w.print(")");
        } else if ( obj instanceof Map ) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>)obj ;
            w.print("{ ");
            w.incIndent();
            map.keySet().forEach( k-> {
                w.printf("%-8s : ", k) ;
                Object v = map.get(k) ;
                if ( compound(v) )
                    w.println();
                print(w, v) ;
                w.println();
            }) ;
            w.decIndent();
            w.print("}");
            //w.println();
        } else {
            w.printf("%s[%s]",obj,obj.getClass().getName()) ;
        }
    }
    
    public static boolean compound(Object obj) {
        return obj instanceof List<?> || obj instanceof Map<?,?> ;
    }
    
    public static Object getField(Object x, String obj, String field) {
        System.out.println("getField: "+obj+"->"+field) ;
        Object z1 = get1(x, obj) ;
        //System.out.println("getField: z1="+z1) ;
        return get1(z1, field) ;
    }
    
    public static Object get(Object obj, String ... path) {
        //System.out.println("get: "+Arrays.asList(path)) ;
        Object x = obj ;
        for ( String c : path )
            x = get1(obj, c) ;
        return x ;
    }
    
    private static Object get1(Object obj, String step ) {
        //System.out.println("get1: "+obj) ;
        //System.out.println("get1:: "+step) ;
       
        if ( ! ( obj instanceof Map ) ) {
            System.err.println("Not a map : "+obj) ;
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>)obj ;
        //System.out.println("get1>> "+map.get(step)) ;
        return map.get(step) ;
    }
}
