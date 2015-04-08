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

import lizard.system.LizardException ;

import com.hp.hpl.jena.query.Dataset ;
import com.hp.hpl.jena.query.ReadWrite ;

import conf2.build.LzDeploy ;
import conf2.conf.* ;

import org.apache.jena.atlas.io.IO ;
import org.apache.jena.atlas.lib.ColumnMap ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.apache.jena.riot.RDFDataMgr ;
import org.yaml.snakeyaml.Yaml ;

public class LzConf {
    static { LogCtl.setLog4j(); }
    
    static class LzConfigurationException extends LizardException {
        public LzConfigurationException(String msg, Throwable cause)    { super(msg, cause) ; }
        public LzConfigurationException(String msg)                     { super(msg) ; }
        public LzConfigurationException(Throwable cause)                { super(cause) ; }
        public LzConfigurationException()                               { super() ; }
    }
    
    public static void main(String[] args) throws Exception {
        ConfCluster conf = LzConfParser.parseConfFile("config-dev.yaml") ;
        
//        System.out.println(conf) ;
//        System.exit(0) ;
        
        //{ mainYAML(args) ; System.exit(0) ; }
        // The deployment "here".
        NetHost here = NetHost.create("localhost") ;
//        ConfCluster conf = setup1() ;
        
        Dataset ds = LzDeploy.deploy(conf, here);
        
        ds.begin(ReadWrite.WRITE);
        RDFDataMgr.read(ds, "D.ttl");
        ds.commit() ;
        ds.end() ;
        
        ds.begin(ReadWrite.READ);
        ds.asDatasetGraph().find().forEachRemaining(q -> System.out.println(q)) ;
        ds.end() ;
        
        System.exit(0) ;
    }        

    // Setup for development : one of each, all one JVM; one local zookeeper.
    public static ConfCluster setup1() {
        int zkPort = 2188 ;

        // Dataset
        ConfNodeTable confNT = new ConfNodeTable(1, 1) ;
        ConfIndex posIdx =  new ConfIndex(new ColumnMap("SPO", "POS"), "POS", 1, 1) ;
        ConfIndex psoIdx =  new ConfIndex(new ColumnMap("SPO", "PSO"), "PSO", 1, 1) ;
        ConfDataset confDatabase = new ConfDataset(confNT, posIdx, psoIdx) ;
        
        // Shards
        ConfIndexElement posIdx1 = new ConfIndexElement(posIdx.indexOrder+"-1", posIdx, NetAddr.create("localhost", 2010)) ;
        ConfIndexElement psoIdx1 = new ConfIndexElement(psoIdx.indexOrder+"-1", psoIdx, NetAddr.create("localhost", 2012)) ;
        ConfNodeTableElement nt1 = new ConfNodeTableElement("Nodes-1", confNT, NetAddr.create("localhost", 2014)) ;

        // The zookeeper server.
        ConfZookeeper confZookeeper = ConfZookeeper.create(zkPort, null) ;

        // Cluster
        ConfCluster confCluster = new ConfCluster(confDatabase) ;
        confCluster.zkServer.add(confZookeeper) ;
        confCluster.addIndexElements(posIdx1, psoIdx1) ;
        confCluster.addNodeElements(nt1) ;
        return confCluster ;
    }
    
    public static void mainYAML(String[] args) throws Exception {
        InputStream inYaml = IO.openFile("data.yaml") ;
        
        Object x = new Yaml().load(inYaml) ;
//        System.out.println(x);
//        System.out.println("<<<<-------------------------------------------------");
//        print(x) ;
//        System.out.println() ;
//        System.out.println(">>>>-------------------------------------------------");
        
        // Access language :
        //  !global
        //  -link
        //  @array
        
        // can be lists.
        System.out.println(YAML.getField(x, "dataset", "nodes")) ;
        System.out.println() ;
        System.out.println(YAML.get(x, "dataset", ".nodes", "nodetable", ".servers" )) ;
        
        System.out.println(YAML.get(x, "query", ".a", ".b", ".c" )) ;
        
        // indexes ; 
        //    dataset.indexes->array
        // index shards
        
        
        System.exit(0) ;
    }
    
    //  .field
    //  /object
    //  

}
