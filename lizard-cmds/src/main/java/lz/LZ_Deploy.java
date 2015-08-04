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

package lz;

import jena.cmd.ArgDecl ;
import jena.cmd.CmdException ;
import jena.cmd.CmdGeneral ;
import lizard.build.LzDeploy ;
import lizard.conf.ConfCluster ;
import lizard.conf.parsers.LzConfParserRDF ;
import lizard.deploy.Deploy ;
import lizard.system.LizardException ;
import migrate.Q ;

import org.apache.jena.atlas.lib.Lib ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.apache.jena.query.Dataset ;
import org.apache.jena.rdf.model.Model ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

/** Take a general configuration file and deployment file
 *  for this machine, build servers and run here.
 *  Optionally, deploy a fuseki server.
 */
public class LZ_Deploy extends CmdGeneral {
    static { LogCtl.setCmdLogging(); }
    public static Logger log        = LoggerFactory.getLogger("Lizard") ;  
    public static Logger logConf    = LoggerFactory.getLogger("Conf") ;
    protected ArgDecl argServer     = new ArgDecl(ArgDecl.HasValue, "server") ;
    protected ArgDecl argFusekiPort = new ArgDecl(ArgDecl.HasValue, "fuseki") ;
    String[] confFiles              = null ;
    String thisVNode                = null ;
    int fusekiPort                  = -1 ;
    
    public static void main(String ...args) {
        new LZ_Deploy(args).mainRun() ;
    }
    
    protected LZ_Deploy(String[] argv) {
        super(argv) ;
        super.add(argServer, argServer.getKeyName(), "VNode name for this node");
        super.add(argFusekiPort, argFusekiPort.getKeyName(), "Fuseki port");
    }
    
    @Override
    protected String getSummary() {
        return "deploy: --server=vnodeToDeploy configFiles ..." ;
    }

    @Override
    protected void processModulesAndArgs() {
        if ( ! super.contains(argServer) )
            throw new CmdException("Required: --server") ;
//        if ( ! super.contains(argZkPort) )
//            throw new CmdException("Required: --zk") ;
        
        if ( super.getValues(argServer).size() > 1 )
            throw new CmdException("Required: exactly one --server") ;
        thisVNode = super.getValue(argServer) ;
        
        if ( super.contains(argFusekiPort) ) {
            if ( super.getValues(argFusekiPort).size() > 1 )
                throw new CmdException("Atmost one --fuseki") ;
            fusekiPort = Integer.parseInt(super.getValue(argFusekiPort)) ;
        }

        confFiles = super.getPositional().toArray(new String[0]) ;
        if ( confFiles.length == 0 )
            throw new CmdException("Required: at least one configuration file") ;
    }

    @Override
    protected void exec() {
        try {
            Model configurationModel = Q.readAll(confFiles) ;
            ConfCluster config = LzConfParserRDF.parseConfFile(configurationModel) ;
            logConf.info("Configuration parsed") ;
            logConf.info("Deploy for "+thisVNode) ;
            try { 
                LzDeploy.deployServers(config, thisVNode);
            } catch ( LizardException ex) {
                System.err.println(ex.getMessage());
                System.exit(0) ;
            }
            logConf.info("Run") ;
            if ( fusekiPort > 0 ) {
                Dataset ds = Deploy.deployDataset(config, thisVNode) ;
                Deploy.runFuseki(ds.asDatasetGraph(), 3030);
                System.exit(0) ;
            }
            while (true) {
                Lib.sleep(10000) ;
            }
        } catch (Throwable th) {
            th.printStackTrace(System.err) ;
        } finally {
            System.exit(0) ;
        }
    }

    @Override
    protected String getCommandName() {
        return "lz-deploy" ;
    }
}
