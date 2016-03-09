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
 
package lizard.deploy;

import java.nio.file.Paths ;

import lizard.build.LzBuildZk ;
import lizard.build.LzDeploy ;
import lizard.conf.ConfCluster ;
import lizard.system.LizardException ;
import org.apache.jena.atlas.lib.FileOps ;
import org.apache.jena.atlas.lib.ProgressMonitor ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.jena.fuseki.Fuseki ;
import org.apache.jena.fuseki.jetty.JettyFuseki ;
import org.apache.jena.fuseki.jetty.JettyServerConfig ;
import org.apache.jena.fuseki.server.FusekiEnv ;
import org.apache.jena.fuseki.server.FusekiServerListener ;
import org.apache.jena.fuseki.server.ServerInitialConfig ;
import org.apache.jena.query.Dataset ;
import org.apache.jena.riot.RDFDataMgr ;
import org.apache.jena.riot.system.ProgressStreamRDF ;
import org.apache.jena.riot.system.StreamRDF ;
import org.apache.jena.sparql.core.DatasetGraph ;
import org.seaborne.tdb2.lib.TDBTxn ;
import org.seaborne.tdb2.loader.StreamRDFBatchSplit ;
import org.seaborne.tdb2.store.DatasetGraphTDB ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

public class Deploy {

    static Logger log = LoggerFactory.getLogger("Deploy") ;

    public static void deployZookeer(int port) {
        LzBuildZk.zookeeperSimple(port);
    }
    
    public static void deployServers(ConfCluster config, String vnode) {
        // --- The deployment "here".
        LzDeploy.deployZookeeper(config) ;
        log.info("SERVERS: "+vnode) ;
        try { 
            LzDeploy.deployServers(config, vnode);
        } catch ( LizardException ex) {
            System.err.println(ex.getMessage());
            System.exit(0) ;
        }
    }

    public static Dataset deployDataset(ConfCluster config, String here) {
        log.info("DATASET") ;
        Dataset ds = LzDeploy.deployDataset(config, here) ;
        return ds ;
    }

    public static void bulkLoad(Dataset ds, String ... files) {
        // c.f. TDB2 Loader.bulkLoad (which does not currently batch split).
        DatasetGraphTDB dsg = (DatasetGraphTDB)ds.asDatasetGraph() ;
        StreamRDF s1 = new StreamRDFBatchSplit(dsg, 100) ;
        
        ProgressMonitor plog = ProgressMonitor.create(log, "Triples", 100000, 10) ;
        ProgressStreamRDF sMonitor = new ProgressStreamRDF(s1, plog) ;
        StreamRDF s3 = sMonitor ;
    
        //plog.startMessage(); 
        plog.start(); 
        TDBTxn.executeWrite(ds, () -> {
            for ( String fn : files ) {
                if ( files.length > 1 )
                    FmtLog.info(log, "File: %s",fn);
                RDFDataMgr.parse(s3, fn) ;
            }
        }) ;
        plog.finish();
        plog.finishMessage();
    }

    public static void runFuseki(DatasetGraph dsg, int port) {
        // -- Run fuseki
        //FusekiServer.
        ServerInitialConfig fuConf = new ServerInitialConfig() ;
        fuConf.dsg = dsg ;
        fuConf.datasetPath = "/ds" ;
        fuConf.allowUpdate = true ;
        
        FusekiServerListener.initialSetup = fuConf ;
        System.setProperty("FUSEKI_HOME", "/home/afs/Jena/jena-fuseki2/jena-fuseki-core/") ;
        FusekiEnv.FUSEKI_BASE = Paths.get("run-"+port).toAbsolutePath() ;
        FileOps.ensureDir(FusekiEnv.FUSEKI_BASE.toString()) ;
    
        log.info("FUSEKI: port="+port) ;
        JettyServerConfig   jettyServerConfig = new JettyServerConfig() ;
        jettyServerConfig.port = port ;
        jettyServerConfig.contextPath = "/" ;
        jettyServerConfig.jettyConfigFile = null ;
        jettyServerConfig.pages = Fuseki.PagesStatic ;
        jettyServerConfig.enableCompression = true ;
        jettyServerConfig.verboseLogging = false ;
    
        JettyFuseki.initializeServer(jettyServerConfig) ;
        JettyFuseki.instance.start() ;
        JettyFuseki.instance.join() ;
        System.exit(0) ;
    }
}

