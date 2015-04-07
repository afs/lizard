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

import lizard.cluster.Cluster ;
import lizard.cluster.Platform ;
import lizard.query.LzDataset ;
import org.apache.jena.atlas.io.IO ;
import org.apache.jena.atlas.io.IndentedWriter ;
import org.apache.jena.atlas.lib.ColumnMap ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.apache.jena.riot.RDFDataMgr ;
import org.seaborne.dboe.base.file.Location ;
import org.yaml.snakeyaml.Yaml ;

import com.hp.hpl.jena.query.Dataset ;
import com.hp.hpl.jena.query.ReadWrite ;

import conf2.build.Lz2BuildZk ;
import conf2.build.Lz2BuilderDataset ;
import conf2.build.Lz2BuilderIndexServer ;
import conf2.build.Lz2BuilderNodeServer ;
import conf2.conf.* ;

public class LzConf {
    static { LogCtl.setLog4j(); }
    
    
    public static void main(String[] args) throws Exception {
        
        { mainYAML(args) ; System.exit(0) ; }
        
        int zkPort = 2188 ;
        
        // Configuration.
        // Describe the cluster, not deployment details.
        ConfNodeTable confNT = new ConfNodeTable(1, 1) ;
        ConfIndex posIdx =  new ConfIndex(new ColumnMap("SPO", "POS"), "POS", 1, 1) ;
        ConfIndex psoIdx =  new ConfIndex(new ColumnMap("SPO", "PSO"), "PSO", 1, 1) ;
        ConfDataset confDatabase = new ConfDataset(confNT, posIdx, psoIdx) ;
        
        // Shards
        ConfIndexElement posIdx1 = new ConfIndexElement(posIdx.indexOrder+"-1", posIdx, NetAddr.create("localhost", 2010)) ;
        ConfIndexElement psoIdx1 = new ConfIndexElement(psoIdx.indexOrder+"-1", psoIdx, NetAddr.create("localhost", 2012)) ;
        ConfNodeTableElement nt1 = new ConfNodeTableElement("Nodes-1", confNT, NetAddr.create("localhost", 2014)) ;

        ConfCluster confCluster = new ConfCluster(confDatabase) ;

        // The zookeeper server(s).
        ConfZookeeper confZookeeper = ConfZookeeper.create(zkPort, null) ;
        
        confCluster.zkServer.add(confZookeeper) ;
        confCluster.addIndexElements(posIdx1, psoIdx1) ;
        confCluster.addNodeElements(nt1) ;
        
        // Rewrite any host names. 
        
        // The deployment "here".
        NetHost here = NetHost.create("localhost") ;
        
        // Deploy
        //public static Platform build(ConfDeploy deployment) {

        // ConfDeploy
        // ConfQueryServer = ConfDataset + 
        
        {
            Location locationDataServers = Location.mem() ;
            Platform platform = new Platform() ;
            Lz2BuilderNodeServer.build(platform, locationDataServers, confCluster, here); 
            Lz2BuilderIndexServer.build(platform, locationDataServers, confCluster, here);
            platform.start(); 
        }
        {   
            ConfZookeeper confZooKeeper = ConfZookeeper.create(zkPort, "zkConf") ;
            String zkConnect = Lz2BuildZk.zookeeper(confCluster, here) ;
            Cluster.createSystem(zkConnect);
        }
        
        Dataset ds = null ;
        {
            Location locationQueryServer = Location.mem() ; 
            LzDataset lzdsg = Lz2BuilderDataset.build(confCluster, confDatabase, locationQueryServer) ;

            lzdsg.getStartables().forEach(s -> {
                s.start();
            });
            ds = Lz2BuilderDataset.dataset(lzdsg, locationQueryServer) ;
        }
        ds.begin(ReadWrite.WRITE);
        RDFDataMgr.read(ds, "D.ttl");
        ds.commit() ;
        ds.end() ;
        
        ds.begin(ReadWrite.READ);
        ds.asDatasetGraph().find().forEachRemaining(q -> System.out.println(q)) ;
        ds.end() ;
        
        System.exit(0) ;
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
