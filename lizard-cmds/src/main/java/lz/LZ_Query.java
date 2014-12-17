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

import java.util.List ;

import lizard.conf.dataset.ConfigLizardDataset ;
import lizard.query.LzDataset ;
import lizard.system.LizardException ;
import lizard.system.LzLib ;
import org.apache.jena.atlas.lib.Lib ;
import org.apache.jena.atlas.lib.StrUtils ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.apache.jena.engine.explain.ExplainCategory ;
import org.apache.jena.riot.Lang ;
import org.apache.jena.riot.RDFDataMgr ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import arq.cmd.CmdException ;
import arq.cmdline.CmdGeneral ;

import com.hp.hpl.jena.query.* ;
import com.hp.hpl.jena.rdf.model.Model ;
import com.hp.hpl.jena.sparql.core.DatasetGraph ;
import com.hp.hpl.jena.sparql.util.QueryExecUtils ;
import com.hp.hpl.jena.tdb.TDB ;

public class LZ_Query extends CmdGeneral {
    static { LogCtl.setCmdLogging(); }
    
    public static Logger log        = LoggerFactory.getLogger("Lizard") ;  
    public static Logger logConf    = LoggerFactory.getLogger("Conf") ;
    
    private List<String> confFiles  = null ; 
    
    public static void main(String ...args) {
        new LZ_Query(args).mainRun();
    }
    
    protected LZ_Query(String[] argv) {
        super(argv) ;
    }

    @Override
    protected String getSummary() {
        return "query" ;
    }

    @Override
    protected void processModulesAndArgs() {
        confFiles = super.getPositional() ;
        if ( confFiles.size() == 0 )
            throw new CmdException("No configuration provided") ;
    }

    @Override
    protected void exec() {
        ExplainCategory lizardClient    = ExplainCategory.create("lizard-client") ;
        ExplainCategory lizardComms     = ExplainCategory.create("lizard-comms") ;
        ExplainCategory lizardCluster   = ExplainCategory.create("lizard-cluster") ; 
        
        try {
            log.info("DATASET") ;
            
            
            Model m = LzLib.readAll(confFiles) ;
            ConfigLizardDataset cf ;
            try {
                cf = new ConfigLizardDataset(m) ;
            } catch (LizardException ex) {
                RDFDataMgr.write(System.err, m, Lang.TTL) ;
                System.exit(1) ;
                return ;
            }
            
            LzDataset lzdsg = cf.buildDataset() ;
            lzdsg.start() ;
            // "start" in getDataset?
            DatasetGraph dsg = lzdsg.getDataset() ;
            Dataset ds = DatasetFactory.create(dsg) ;

            log.info("LOAD") ;
            //ds = (Dataset) AssemblerUtils.build(conffile, VocabLizard.lzDataset) ;
            if ( true ) {
                //LogCtl.set(ClusterNodeTable.class, "WARN") ;
                RDFDataMgr.read(ds, "D.ttl") ;
                //LogCtl.set(ClusterNodeTable.class, "INFO") ;
                TDB.sync(ds) ;
            }
            
            log.info("QUERY") ;
//            Quack.setVerbose(true) ;
//            ARQ.setExecutionLogging(InfoLevel.NONE);

            String qs = StrUtils.strjoinNL("PREFIX : <http://example/> SELECT * "
                                           , "{ :s1 ?p ?o }" 
                                           //, "{ ?x :k ?k . ?x :p ?v . }"
                                           //, "{ :x1 :p ?x . ?x :p ?v . }"
                                           // Filter placement occurs?
                                           //, "{ ?x :k ?k . ?x :p ?v . FILTER(?k = 2) }"
                                           //,"ORDER BY ?x"
                ) ;
            //"{ ?x :k ?k }" ;

            if ( true ) {
                LogCtl.set("lizard", "info") ;
                // LogCtl.disable("lizard.comms.common.tio") ;
                // LogCtl.disable("lizard.comms.server") ;
                // LogCtl.disable("lizard.comms.client") ;
            }

            Query q = QueryFactory.create(qs) ;
            System.out.println() ;
            System.out.print(q);
            System.out.println() ;
            int N = true ? 3 : 20 ;
            for ( int i = 0 ; i < N ; i++ ) {
                doOne("Lizard", ds, q) ;
                if ( i != N-1 ) Lib.sleep(3000) ;
            }

            if ( true ) {
                Dataset dsStd = RDFDataMgr.loadDataset("D.ttl") ;
                doOne("ARQ", dsStd, q) ;
            }

        } catch (Exception ex ) {
            ex.printStackTrace(System.err) ;
            System.exit(0) ;
        }
        System.out.println("DONE") ;
//        while(true) {
//            Lib.sleep (10000) ;
//        }
    }

    private static void doOne(String label, Dataset ds, Query query) {
        QueryExecution qExec = QueryExecutionFactory.create(query, ds) ;
        log.info("---- {}", label) ;
        QueryExecUtils.executeQuery(query, qExec);
    }
    
    @Override
    protected String getCommandName() {
        return null ;
    }

}
