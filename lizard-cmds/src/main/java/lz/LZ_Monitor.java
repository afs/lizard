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

import arq.cmd.CmdException ;
import arq.cmdline.CmdGeneral ;
import lizard.cluster.Cluster ;
import org.apache.jena.atlas.lib.Lib ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

public class LZ_Monitor extends CmdGeneral {
    static { LogCtl.setCmdLogging(); }
    
    private static Logger log = LoggerFactory.getLogger(LZ_Monitor.class) ;
    private String connectString = "localhost:2181" ;
    
    public static void main(String ...args) {
        new LZ_Monitor(args).mainRun();
    }

    protected LZ_Monitor(String[] argv) {
        super(argv) ;
    }

    @Override
    protected String getSummary() {
        return "monitor [Zookeeper ConnectString]";
    }

    
    @Override
    protected void processModulesAndArgs() {
        if ( super.getPositional().size() > 1 )
            throw new CmdException("Too many arguments") ;
        if ( super.getPositional().size() == 1 )
            connectString = super.getArg(0) ;
    }

    @Override
    protected void exec() {
         try {
            Cluster.createSystem(connectString) ;
            Cluster.monitor() ; 
            for ( ;; )
                Lib.sleep(10000) ;
//            Cluster.close();
//            Lib.sleep(1000) ;
//            System.out.println("DONE");
        } catch (Exception ex) {}
    }

    @Override
    protected String getCommandName() {
        return "monitor" ;
    }
}
