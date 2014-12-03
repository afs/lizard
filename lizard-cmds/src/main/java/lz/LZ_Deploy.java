/*
 *  Copyright 2014 Andy Seaborne
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

package lz;

import java.util.Collection ;
import java.util.List ;
import java.util.stream.Collectors ;

import lizard.cluster.Platform ;
import lizard.conf.ConfigLib ;
import lizard.conf.Configuration ;
import lizard.conf.LzBuild ;
import lizard.conf.index.ConfigIndex ;
import lizard.conf.index.IndexServer ;
import lizard.conf.node.BuildNode ;
import lizard.conf.node.ConfigNode ;
import lizard.conf.node.NodeServer ;
import lizard.index.TServerIndex ;
import lizard.system.LzLib ;
import org.apache.jena.atlas.lib.Lib ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import arq.cmd.CmdException ;
import arq.cmdline.ArgDecl ;
import arq.cmdline.CmdGeneral ;

import com.hp.hpl.jena.rdf.model.Model ;
import com.hp.hpl.jena.tdb.base.file.Location ;
import com.hp.hpl.jena.tdb.store.tupletable.TupleIndex ;

/** Take a general configuration file and deployment file for this machine
 *  build a dataset and run here.
 *  The lizard version of "arq.sparql". 
 */
public class LZ_Deploy extends CmdGeneral {
    static { LogCtl.setCmdLogging(); }
    
    public static Logger log        = LoggerFactory.getLogger("Lizard") ;  
    public static Logger logConf    = LoggerFactory.getLogger("Conf") ;
    static String confNode          = "conf-node.ttl" ;
    static String confIndex         = "conf-index.ttl" ;
    static String confDataset       = "conf-dataset.ttl" ;
    
    public static void main(String ...args) {
        new LZ_Deploy(args).mainRun() ;
    }
    
    protected ArgDecl argDeploy     = new ArgDecl(ArgDecl.HasValue, "deploy", "server") ;
    
    private ConfigNode configNode = null ;
    private ConfigIndex configIndex = null ;
    private Deployment deployment = null ;
    
    protected LZ_Deploy(String[] argv) {
        super(argv) ;
        super.add(argDeploy, argDeploy.getKeyName(), "Deployment description");
    }
    
    @Override
    protected String getSummary() {
        return "deploy: --deploy config ... other config files ..." ;
    }

    @Override
    protected void processModulesAndArgs() {
        if ( ! super.contains(argDeploy) )
            throw new CmdException("Required: --deploy") ;
        
        if ( super.getValues(argDeploy).size() > 1 )
            throw new CmdException("Required: exactly one --deploy") ;

        String deploymentFile = super.getValue(argDeploy) ;
        
        // Parse
        String [] a = super.getPositional().toArray(new String[0]) ;
        Configuration config = Configuration.fromFile(a) ;
        
        deployment = Deployment.parse(config, deploymentFile) ; 
        configIndex = config.getConfIndex() ;
        configNode = config.getConfNode() ;
    }

    static class Deployment {
        public final Collection<IndexServer> indexServers ;
        public final Collection<NodeServer> nodeServers ;
        
        static Deployment parse(Configuration config, String...files) {
            Model model = LzLib.readAll(files) ;
            
            List<NodeServer> nodeServers = ConfigLib.dataServers(model, ":NodeServer").values().stream()
                .map(ds -> { return config.getConfNode().findNodeServer(ds.resource);})
                .collect(Collectors.toList()) ;
            List<IndexServer> indexServers = ConfigLib.dataServers(model, ":IndexServer").values().stream()
                .map(ds -> { return config.getConfIndex().findIndexServer(ds.resource);})
                .collect(Collectors.toList()) ;
            return new Deployment(indexServers, nodeServers) ;
        }
        
        private Deployment(Collection<IndexServer> indexServers, Collection<NodeServer> nodeServers) {
            this.indexServers = indexServers ;
            this.nodeServers = nodeServers ;
        }
    }

    @Override
    protected void exec() {
        
        try { 
            deploy(deployment) ;
        }
        catch (Throwable th) { th.printStackTrace(System.err) ; }
        finally { System.exit(0) ; }
        // TODO Auto-generated constructor stub

    }


    @Override
    protected String getCommandName() {
        return "lz-deploy" ;
    }


    public static void deploy(Deployment deployment) {
        Platform platform = new Platform() ;
        Location location = Location.mem();

        deployment.nodeServers.stream().forEach(ns -> {
            log.info("Build N: " + ns.resource) ;
            BuildNode.buildNodeServer(ns, platform, location) ;
        }) ;

        deployment.indexServers.forEach(idx -> {
            log.info("Build I: " + idx.resource) ;
            /* BuildIndex. */buildIndexServer(idx, platform, location) ;
        }) ;

        platform.start() ;
        while(true) {
            Lib.sleep(10000) ;
        }
    }
    
    private static void buildIndexServer(IndexServer idxSvc, Platform platform, Location location) {
        String indexOrder = idxSvc.indexService.indexOrder ;
        TupleIndex index = LzBuild.createTupleIndex(location, indexOrder, "Idx"+indexOrder) ;
        TServerIndex serverIdx = TServerIndex.create(idxSvc.port, index) ;
        platform.add(serverIdx) ;
    }
}
