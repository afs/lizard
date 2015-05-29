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

package lz_dev;

import java.io.InputStream ;

import lizard.build.LzDeploy ;
import lizard.conf.ConfCluster ;
import lizard.conf.NetHost ;
import lizard.conf.parsers.LzConfParserRDF ;
import lizard.conf.parsers.LzConfParserYAML ;
import lizard.conf.parsers.YAML ;
import lizard.system.LizardException ;
import migrate.Q ;

import org.apache.jena.query.Dataset ;
import org.apache.jena.query.ReadWrite ;
import org.apache.jena.rdf.model.Model ;

import conf2.LzConfigDefault ;

import org.apache.jena.atlas.io.IO ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.apache.jena.riot.RDFDataMgr ;
import org.yaml.snakeyaml.Yaml ;

public class LzConfMain {
    static { LogCtl.setLog4j(); }
    
    static class LzConfigurationException extends LizardException {
        public LzConfigurationException(String msg, Throwable cause)    { super(msg, cause) ; }
        public LzConfigurationException(String msg)                     { super(msg) ; }
        public LzConfigurationException(Throwable cause)                { super(cause) ; }
        public LzConfigurationException()                               { super() ; }
    }
    
    public static void main(String[] args) throws Exception {
        ConfCluster conf = null ;
        
        if ( true ) {
            String dir = "setup-simple" ;
            Model model = Q.readAll
                (dir+"/conf-dataset.ttl"
                ,dir+"/conf-index.ttl"
                ,dir+"/conf-node.ttl"
                    ) ;

            conf =  LzConfParserRDF.parseConfFile(model) ;
            System.out.println("== RDF") ;
            System.out.println(conf) ;
        }
        if ( true ) {
            conf = LzConfParserYAML.parseConfFile("config-dev.yaml") ;
            System.out.println("== YAML") ;
            System.out.println(conf) ;
            
        }

        if ( false )
            conf = LzConfigDefault.setup_mem_local() ;
        
        if ( conf == null ) 
            System.err.println("No configuration") ;
        
        System.out.println("== Using") ;
        System.out.println(conf) ;
        System.out.println("==") ;
//        System.exit(0) ;
        
        // The deployment "here".
        NetHost here = NetHost.create("localhost") ;
        LzDeploy.deployServers(conf, here);
        Dataset ds = LzDeploy.deployDataset(conf, here) ;
        
        // Specialized assembler.
        // Fire off fuseki.

        
        
        if ( false ) {
            
        } else {
            ds.begin(ReadWrite.WRITE);
            RDFDataMgr.read(ds, "D.ttl");
            ds.commit() ;
            ds.end() ;

            ds.begin(ReadWrite.READ);
            ds.asDatasetGraph().find().forEachRemaining(q -> System.out.println(q)) ;
            ds.end() ;
        }
        
        System.exit(0) ;
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
