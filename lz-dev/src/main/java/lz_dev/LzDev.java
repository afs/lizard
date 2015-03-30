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
import java.nio.file.Paths ;
import java.util.ArrayList ;
import java.util.List ;
import java.util.Map ;
import java.util.concurrent.TimeUnit ;

import lizard.cluster.Cluster ;
import lizard.conf.Configuration ;
import lizard.conf.dataset.LzBuildClient ;
import lizard.index.TServerIndex ;
import lizard.node.ClusterNodeTable ;
import lizard.node.NodeTableRemote ;
import lizard.node.TServerNode ;
import lizard.query.LzDataset ;
import lizard.sys.Deploy ;
import lizard.sys.Deployment ;
import lizard.system.LizardException ;
import lizard.system.Pingable ;
import migrate.Q ;
import org.apache.curator.framework.CuratorFramework ;
import org.apache.curator.framework.recipes.locks.InterProcessLock ;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex ;
import org.apache.curator.test.TestingServer ;
import org.apache.jena.atlas.io.IO ;
import org.apache.jena.atlas.io.IndentedWriter ;
import org.apache.jena.atlas.lib.FileOps ;
import org.apache.jena.atlas.lib.Lib ;
import org.apache.jena.atlas.lib.StrUtils ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.apache.jena.fuseki.cmd.FusekiCmd ;
import org.apache.jena.fuseki.server.FusekiEnv ;
import org.apache.jena.riot.RDFDataMgr ;
import org.seaborne.dboe.base.file.Location ;
import org.seaborne.dboe.transaction.txn.TransactionalComponent ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import org.yaml.snakeyaml.Yaml ;

import com.hp.hpl.jena.query.* ;
import com.hp.hpl.jena.rdf.model.Model ;
import com.hp.hpl.jena.sparql.util.QueryExecUtils ;


public class LzDev {
    static { LogCtl.setLog4j(); } 
    public static Logger log = LoggerFactory.getLogger("Main") ;

    static String confNode          = Q.filename(Setup.confDir, "conf-node.ttl") ;
    static String confIndex         = Q.filename(Setup.confDir, "conf-index.ttl") ;
    static String confDataset       = Q.filename(Setup.confDir, "conf-dataset.ttl") ;
    static Model configurationModel = Q.readAll(confNode, confIndex, confDataset) ;
    static Configuration config     = Configuration.fromModel(configurationModel) ;
    
    static String deploymentFile        = Q.filename(Setup.confDir, "deploy-jvm.ttl") ;
    
    static NodeTableRemote ntr = null ;
    //static TupleIndexRemote tir = null ;
    static int counter = 0 ;

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
    
    public static void main(String[] args) {
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
        
        //mainFuseki(args) ;
        
        try { main$(args) ; }
        catch (Exception ex) { 
            System.out.flush() ;
            System.err.println(ex.getMessage()) ;
            ex.printStackTrace(System.err);
            System.exit(0) ;
        }
    }
    
    public static void main$(String[] args) {
        {
            config.print() ;
            Deployment deployment = Deployment.parse(config, deploymentFile) ;
            deployment.print(); 
            System.exit(1) ;
        }
        log.info("SERVERS") ;
        try { 
            Deployment deployment = Deploy.deployServers(config, deploymentFile);
        } catch ( LizardException ex) {
            System.err.println(ex.getMessage());
            System.exit(0) ;
        }

        log.info("DATASET") ;
        LzDataset lz = buildDataset(config) ;
        Dataset ds = LzBuildClient.dataset(lz, Location.mem()) ;
        
        ds.asDatasetGraph().find().forEachRemaining(System.out::println);
        
        
        List<TransactionalComponent> tComp = new ArrayList<>() ;
        
        log.info("TRANSACTIONS") ;
        
        // Can we add the base instance or the cluster versions?
        // Cluster must expose their units.
        // Cluster is actually just the distributor and not involved otherwise (?)
        // Issue : deciding who it talks to to reduce network coordination (?)
        //   Not for W, only R.
        //   Can we do R simply by using "current R transaction"?  

        ds.begin(ReadWrite.WRITE) ;
        RDFDataMgr.read(ds, "Large.ttl");
        ds.commit() ;
        ds.end() ;
    }

    // -------- Dataset
    private static LzDataset buildDataset(Configuration config) {
        LzDataset lz = Local.buildDataset(configurationModel) ;
        return lz ;
    }
    
    private static void ping(LzDataset lz) {
        lz.getComponents().stream().forEach(c -> {
            //System.out.println("Component: "+c.getClass().getTypeName()) ;
            if ( c instanceof Pingable ) {
                Pingable p = (Pingable)c ;
                p.ping();
            }
        }) ;
    }
    
    private static void load(Dataset ds, String data) {        
        log.info("LOAD") ;
        if ( data != null ) {
            // Making loading quieter.
            LogCtl.set(ClusterNodeTable.class, "WARN") ;
            LogCtl.set(TServerNode.class, "WARN") ;
            LogCtl.set(TServerIndex.class, "WARN") ;

            RDFDataMgr.read(ds, data) ;

            LogCtl.set(ClusterNodeTable.class, "INFO") ;
            LogCtl.set(TServerNode.class, "INFO") ;
            LogCtl.set(TServerIndex.class, "INFO") ;
        }
    }

    // -------- Query
    private static void performQuery(Dataset ds) {
        log.info("QUERY") ;
        //            Quack.setVerbose(true) ;
        //            ARQ.setExecutionLogging(InfoLevel.NONE);

        String qs = StrUtils.strjoinNL("PREFIX : <http://example/> SELECT * "
                                       , "{ ?s :p ?o }" 
                                       //, "{ ?x :k ?k . ?x :p ?v . }"
                                       //, "{ :x1 :p ?x . ?x :p ?v . }"
                                       // Filter placement occurs?
                                       //, "{ ?x :k ?k . ?x :p ?v . FILTER(?k = 2) }"
                                       //,"ORDER BY ?x"
            ) ;
        //"{ ?x :k ?k }" ;

        if ( true ) {
            LogCtl.set("lizard", "info") ;
            //                LogCtl.disable("lizard.comms.common.tio") ;
            //                LogCtl.disable("lizard.comms.server") ;
            //                LogCtl.disable("lizard.comms.client") ;
        }

        Query q = QueryFactory.create(qs) ;
        System.out.println() ;
        System.out.print(q);
        System.out.println() ;
        int N = 1 ;// inProcess ? 1 : 20 ;
        for ( int i = 0 ; i < N ; i++ ) {
            doOne("Lizard", ds, q) ;
            if ( i != N-1 ) Lib.sleep(3000) ;
        }

        if ( true ) {
            Dataset dsStd = RDFDataMgr.loadDataset("D.ttl") ;
            doOne("ARQ", dsStd, q) ;
        }

    }

    private static void doOne(String label, Dataset ds, Query query) {
        QueryExecution qExec = QueryExecutionFactory.create(query, ds) ;
        log.info("---- {}", label) ;
        QueryExecUtils.executeQuery(query, qExec);
    }

    public static void mainFuseki(String[] args) {
        System.setProperty("FUSEKI_HOME", "/home/afs/Jena/jena-fuseki2/jena-fuseki-core/") ;
        FusekiEnv.FUSEKI_BASE = Paths.get("setup-simple/run").toAbsolutePath() ;
        FileOps.ensureDir(FusekiEnv.FUSEKI_BASE.toString()) ;
        FusekiCmd.main("--conf=setup-simple/fuseki.ttl") ;
        System.exit(0) ;
    }
}
