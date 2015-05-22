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

import java.io.File ;
import java.io.FilenameFilter ;
import java.nio.file.FileSystems ;
import java.nio.file.PathMatcher ;
import java.nio.file.Paths ;
import java.util.ArrayList ;
import java.util.Arrays ;
import java.util.List ;

import lizard.system.LzLib ;
import arq.cmd.ArgDecl ;
import arq.cmd.CmdException ;
import arq.cmdline.CmdGeneral ;
import arq.cmdline.ModQueryIn ;

import org.apache.jena.atlas.logging.LogCtl ;
import org.apache.jena.query.* ;
import org.apache.jena.rdf.model.Model ;
import org.apache.jena.sparql.core.DatasetGraph ;
import org.apache.jena.sparql.util.QueryExecUtils ;
import org.seaborne.dboe.engine.explain.ExplainCategory ;

public class LZ_Query extends CmdGeneral {
    static { LogCtl.setCmdLogging(); }
    protected ModQueryIn modQuery   = new ModQueryIn(Syntax.syntaxARQ) ;
    // May be repeated.
    protected ArgDecl argConf       = new ArgDecl(ArgDecl.HasValue, "--conf") ;
    
    private List<String> confFiles  = null ; 
    
    public static void main(String ...args) {
        new LZ_Query(args).mainRun();
    }
    
    protected LZ_Query(String[] argv) {
        super(argv) ;
        super.add(argConf) ;
        super.addModule(modQuery) ;
    }

    @Override
    protected String getSummary() {
        return "lz query --conf 1*configFiles ['query'|--query QueryFile]" ;
    }

    @Override
    protected String getCommandName() {
        return "lz query" ;
    }

    @Override
    protected void processModulesAndArgs() {
        if ( ! super.contains(argConf) )
            throw new CmdException("No configuration provided (--conf expected)") ;
        confFiles = new ArrayList<>() ;
        super.getValues(argConf).forEach( a -> {
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + a);
            FilenameFilter ff = (d,p) -> {
                if ( p.equals(".") || p.equals("..") )
                    return false ;
                return matcher.matches(Paths.get(p)) ;
            } ;
            confFiles.addAll(Arrays.asList(new File(".").list(ff))) ;
        }) ;
        if ( confFiles.size() == 0 )
            throw new CmdException("No configuration provided") ;
        if ( modQuery.getQuery() == null )
            throw new CmdException("No query provided") ;
    }

    @Override
    protected void exec() {
        ExplainCategory lizardClient    = ExplainCategory.create("lizard-client") ;
        ExplainCategory lizardComms     = ExplainCategory.create("lizard-comms") ;
        ExplainCategory lizardCluster   = ExplainCategory.create("lizard-cluster") ; 
        
        try {
            Model m = LzLib.readAll(confFiles) ;
            if ( true ) throw new CmdException("Needs updatring to new configuation and deployment") ;
//            ConfigLizardDataset cf ;
//            try {
//                cf = ConfigLizardDataset.create(m) ;
//            } catch (LizardException ex) {
//                RDFDataMgr.write(System.err, m, Lang.TTL) ;
//                System.exit(1) ;
//                return ;
//            }
//            
//            LzDataset lzdsg = cf.buildDataset() ;
//            lzdsg.start() ;
//            // "start" in getDataset?
            DatasetGraph dsg = null ; // lzdsg.getDataset() ;
            Dataset ds = DatasetFactory.create(dsg) ;

            Query query = modQuery.getQuery() ;
            if ( super.isVerbose() )
                System.out.println(query) ;
            QueryExecution qExec = QueryExecutionFactory.create(query, ds) ;
            QueryExecUtils.executeQuery(query, qExec);
        } catch (Exception ex ) {
            ex.printStackTrace(System.err) ;
            System.exit(0) ;
        }
//        while(true) {
//            Lib.sleep (10000) ;
//        }
    }

}
