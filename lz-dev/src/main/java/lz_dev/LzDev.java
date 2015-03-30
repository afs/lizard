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

import java.nio.file.Paths ;
import java.util.concurrent.Semaphore ;

import com.hp.hpl.jena.query.* ;
import com.hp.hpl.jena.rdf.model.Model ;
import com.hp.hpl.jena.sparql.util.QueryExecUtils ;

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

import org.apache.curator.test.TestingServer ;
import org.apache.jena.atlas.lib.FileOps ;
import org.apache.jena.atlas.lib.Lib ;
import org.apache.jena.atlas.lib.StrUtils ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.apache.jena.atlas.logging.ProgressLogger ;
import org.apache.jena.fuseki.cmd.FusekiCmd ;
import org.apache.jena.fuseki.server.FusekiEnv ;
import org.apache.jena.riot.RDFDataMgr ;
import org.apache.jena.riot.system.StreamRDF ;
import org.apache.jena.riot.system.StreamRDFLib ;
import org.seaborne.dboe.base.file.Location ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

public class LzDev {
    static { LogCtl.setLog4j(); } 
    public static Logger log = LoggerFactory.getLogger("Main") ;

    static String confDir           = "setup-disk" ;
    static String confNode          = Q.filename(confDir, "conf-node.ttl") ;
    static String confIndex         = Q.filename(confDir, "conf-index.ttl") ;
    static String confDataset       = Q.filename(confDir, "conf-dataset.ttl") ;
    static Model configurationModel = Q.readAll(confNode, confIndex, confDataset) ;
    static Configuration config     = Configuration.fromModel(configurationModel) ;
    
    static String deploymentFile        = Q.filename(confDir, "deploy-jvm.ttl") ;
    
    static NodeTableRemote ntr = null ;
    //static TupleIndexRemote tir = null ;
    static int counter = 0 ;

    public static void main(String[] args) {
        FileOps.clearAll("DB");
        
        try { main$(args) ; }
        catch (Exception ex) { 
            System.out.flush() ;
            System.err.println(ex.getMessage()) ;
            ex.printStackTrace(System.err);
            System.exit(0) ;
        }
    }

    public static void main$(String[] args) {
        log.info("SERVERS") ;
        try { 
            Deployment deployment = Deploy.deployServers(config, deploymentFile);
        } catch ( LizardException ex) {
            System.err.println(ex.getMessage());
            System.exit(0) ;
        }

        // Init a simple ZK
        int zkPort = 2281 ;
        zookeeper(zkPort) ;
        String zkConnect = "localhost:"+zkPort ;
        Cluster.createSystem(zkConnect);
        
        // Multiple query servers?
        log.info("DATASET") ;
        LzDataset lz = buildDataset(config) ;
        Dataset ds = LzBuildClient.dataset(lz, Location.mem()) ;

        // Do a long, slow load.
        
        Runnable r = ()->load(ds,"/home/afs/Datasets/BSBM/bsbm-5m.nt.gz") ;
        new Thread(r).start() ;
        Lib.sleep(1000);

        log.info("QUERY") ;
        ds.begin(ReadWrite.READ) ;
        performQuery(ds);
        //txn.prepare(); 
        ds.end() ;
        
        LogCtl.set("lizard", "info");
        LogCtl.set("org.seaborne", "info");
        
        Cluster.close();
        System.exit(0) ;
    }

    public static void async(Runnable r) {
        Semaphore semaStart = new Semaphore(0, true) ;
        Semaphore semaFinish = new Semaphore(0, true) ;
        Runnable r2 = () -> {
            semaStart.acquireUninterruptibly(); 
            r.run();
            semaFinish.release(1);
        } ;
        new Thread(r2).start();
        semaStart.release(1);
        semaFinish.acquireUninterruptibly();
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
    
    private static void load(Dataset ds, String datafile) {        
        log.info("LOAD") ;
        if ( datafile != null ) {
            // Making loading quieter.
            LogCtl.set(ClusterNodeTable.class, "WARN") ;
            LogCtl.set(TServerNode.class, "WARN") ;
            LogCtl.set(TServerIndex.class, "WARN") ;

            ds.begin(ReadWrite.WRITE) ;
            StreamRDF s = StreamRDFLib.dataset(ds.asDatasetGraph()) ;
            ProgressLogger plog = new ProgressLogger(LoggerFactory.getLogger("LOAD"), 
                                                     "Triples", 50000, 10) ;
            s = new StreamRDFMonitor(s, plog) ; 
            RDFDataMgr.parse(s, datafile);
            ds.commit();
            ds.end() ;

            LogCtl.set(ClusterNodeTable.class, "INFO") ;
            LogCtl.set(TServerNode.class, "INFO") ;
            LogCtl.set(TServerIndex.class, "INFO") ;
        }
    }

    // -------- Query
    private static void performQuery(Dataset ds) {
        //            Quack.setVerbose(true) ;
        //            ARQ.setExecutionLogging(InfoLevel.NONE);

        String qs = StrUtils.strjoinNL("PREFIX : <http://example/>"
                                      ,"SELECT (count(*) AS ?C)  "
                                      ,"{ ?s ?p ?o }" ) ;
        Query q = QueryFactory.create(qs) ;
        performQuery(ds, q);
    }
    
    private static void performQuery(Dataset ds, Query q) {
        System.out.println() ;
        System.out.print(q);
        System.out.println() ;
        int N = 1 ;// inProcess ? 1 : 20 ;
        for ( int i = 0 ; i < N ; i++ ) {
            doOne("Lizard", ds, q) ;
            if ( i != N-1 ) Lib.sleep(3000) ;
        }
    }

    private static void doOne(String label, Dataset ds, Query query) {
        QueryExecution qExec = QueryExecutionFactory.create(query, ds) ;
        log.info("---- {}", label) ;
        QueryExecUtils.executeQuery(query, qExec);
    }
    
    static TestingServer zkTestServer;
    public static void zookeeper(int port) {
        try { zkTestServer = new TestingServer(port) ; }
        catch (Exception e) { e.printStackTrace(); }
    }

    public static void mainFuseki(String[] args) {
        System.setProperty("FUSEKI_HOME", "/home/afs/Jena/jena-fuseki2/jena-fuseki-core/") ;
        FusekiEnv.FUSEKI_BASE = Paths.get("setup-simple/run").toAbsolutePath() ;
        FileOps.ensureDir(FusekiEnv.FUSEKI_BASE.toString()) ;
        FusekiCmd.main("--conf=setup-simple/fuseki.ttl") ;
        System.exit(0) ;
    }
}
