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
import lizard.build.LzBuildZk ;

import org.apache.jena.atlas.lib.Lib ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

/** Run a simple zookeeper */
public class LZ_Zookeeper extends CmdGeneral {
    static { LogCtl.setCmdLogging(); }
    public static Logger log        = LoggerFactory.getLogger("Lizard") ;  
    public static Logger logConf    = LoggerFactory.getLogger("Conf") ;
    
    protected ArgDecl argPort       = new ArgDecl(ArgDecl.HasValue, "port") ;
    private int port ;
    private final static int dftPort = 2186 ; 
    
    public static void main(String ...args) {
        new LZ_Zookeeper(args).mainRun();
    }

    protected LZ_Zookeeper(String[] argv) {
        super(argv) ;
        super.add(argPort, argPort.getKeyName(), "Port number for the zookeeper instance") ; 
    }

    @Override
    protected String getSummary() {
        return "lz-zookeeper --port PORT";
    }

    
    @Override
    protected void processModulesAndArgs() {
        if ( super.getPositional().size() > 1 )
            throw new CmdException("Too many arguments") ;
        if ( super.contains(argPort) )
            port = Integer.parseInt(getArg(argPort).getValue()) ;
        else
            port = dftPort ;
    }

    @Override
    protected void exec() {
        LogCtl.set("org.apache.zookeeper", "info") ;
        //LogCtl.set("org.apache.curator", "info") ;
        
         try {
             LzBuildZk.zookeeperSimple(port); 
             for ( ;; )
                 Lib.sleep(10000) ;
        } catch (Exception ex) {}
    }

    @Override
    protected String getCommandName() {
        return "lz-zookeeper" ;
    }
}
