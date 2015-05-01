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

import lizard.conf.Configuration ;
import lizard.sys.Deploy ;
import lizard.sys.Deployment ;
import arq.cmd.ArgDecl ;
import arq.cmd.CmdException ;
import arq.cmdline.CmdGeneral ;

import org.apache.jena.atlas.lib.Lib ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

/** Take a general configuration file and deployment file
 *  for this machine, build servers and run here.
 */
public class LZ_Deploy extends CmdGeneral {
    static { LogCtl.setCmdLogging(); }
    
    public static Logger log        = LoggerFactory.getLogger("Lizard") ;  
    public static Logger logConf    = LoggerFactory.getLogger("Conf") ;
    protected ArgDecl argDeploy     = new ArgDecl(ArgDecl.HasValue, "deploy", "server") ;
    private Deployment deployment = null ;
    
    public static void main(String ...args) {
        new LZ_Deploy(args).mainRun() ;
    }
    
    protected LZ_Deploy(String[] argv) {
        super(argv) ;
        super.add(argDeploy, argDeploy.getKeyName(), "Deployment description");
    }
    
    @Override
    protected String getSummary() {
        return "deploy: --deploy=deploymentFile configFiles ..." ;
    }

    @Override
    protected void processModulesAndArgs() {
        if ( ! super.contains(argDeploy) )
            throw new CmdException("Required: --deploy") ;
        
        if ( super.getValues(argDeploy).size() > 1 )
            throw new CmdException("Required: exactly one --deploy") ;

        String deploymentFile = super.getValue(argDeploy) ;
        String [] a = super.getPositional().toArray(new String[0]) ;
        Configuration config = Configuration.fromFile(a) ;
        deployment = Deployment.parse(config, deploymentFile) ; 
    }

    @Override
    protected void exec() {
        try {
            Deploy.deploy(deployment) ;
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
