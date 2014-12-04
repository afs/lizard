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

package lizard.conf.dataset;

import java.util.ArrayList ;
import java.util.List ;

import lizard.conf.Config ;
import lizard.conf.LzBuild ;
import lizard.conf.index.ConfigIndex ;
import lizard.conf.index.IndexService ;
import lizard.conf.node.ConfigNode ;
import lizard.query.LzDataset ;
import lizard.system.Component ;
import lizard.system.LizardException ;
import migrate.Q ;
import org.apache.jena.atlas.lib.StrUtils ;
import org.slf4j.Logger ;

import com.hp.hpl.jena.query.QuerySolution ;
import com.hp.hpl.jena.rdf.model.Model ;
import com.hp.hpl.jena.rdf.model.Resource ;
import com.hp.hpl.jena.sparql.ARQException ;
import com.hp.hpl.jena.sparql.core.DatasetGraph ;
import com.hp.hpl.jena.sparql.core.assembler.DatasetAssemblerVocab ;
import com.hp.hpl.jena.sparql.util.TypeNotUniqueException ;
import com.hp.hpl.jena.sparql.util.graph.GraphUtils ;
import com.hp.hpl.jena.tdb.base.file.Location ;
import com.hp.hpl.jena.tdb.store.nodetable.NodeTable ;
import com.hp.hpl.jena.tdb.store.tupletable.TupleIndex ;

public class ConfigLizardDataset {
    public static Logger logConf = Config.logConf ;
    
//    public static ConfigLizardDataset fromFile(String config) { 
//        Model m = RDFDataMgr.loadModel(config) ;
//        return create(m) ;
//    }
//    
//    public static ConfigLizardDataset create(Model model) {
//        return new ConfigLizardDataset(model) ;
//    }
//    
//    private Map<Resource, Object> datasets ; 
//    private Map<Resource, Object> indexes ; 
//    private Map<Resource, Object> nodeTables ;

    private final Model model ;
    private final Resource root ;
    
    public ConfigLizardDataset(Model model) {
        this.model = model ;
        this.root = findRoot(model) ;
    }
    
    private Resource findRoot(Model model2) {
        Resource lzType = model.createResource("http://jena.apache.org/ns/lizard#Dataset") ;
        try {
            Resource root = GraphUtils.findRootByType(model, lzType) ;
            if ( root == null )
                throw new LizardException("No lizard:Dataset") ;
            return root ;
        } catch (TypeNotUniqueException ex)
        { throw new ARQException("Multiple types for: "+DatasetAssemblerVocab.tDataset) ; }
//      LizardDataset lzDSG = ConfigLizardDataset.buildDataset(root) ;
    }

    public LzDataset buildDataset() {
        return buildDataset(root) ;
    }
    
    public static LzDataset buildDataset(Resource lzdsgRoot) {
        Model m = lzdsgRoot.getModel() ;
    
        String qsIndexServices = StrUtils.strjoinNL(Config.prefixes,
                                                    "SELECT * {",
                                                    " ?lz a lizard:Dataset ;",
                                                    "    OPTIONAL { ?lz :indexes ?indexList }",
                                                    "    OPTIONAL { ?lz :nodetable   ?nodes }",
                                                    "}") ;
        LzDataset lizard = null ;
        
        for ( QuerySolution row : Q.queryToList(m, qsIndexServices, "lz", lzdsgRoot) ) {
            if ( lizard != null )
                throw new LizardException("Multiple declarations") ;
            Resource lz = row.getResource("lz") ;
            if ( ! lz.equals(lzdsgRoot) )
                throw new LizardException("Bad query setup") ;
            Resource idxList = Q.getResourceOrNull(row, "indexList") ;
            if ( idxList == null )
                throw new LizardException("No :indexes in lizard:Dataset description") ;
            List<Resource> indexList = Q.listResources(idxList) ;
            
            Resource nodes = Q.getResourceOrNull(row, "nodes") ;
            if ( nodes == null )
                throw new LizardException("No :nodes in lizard:Dataset description") ;
            
            List<Component> startables = new ArrayList<>() ;
            ConfigNode cn = ConfigNode.create(m) ;
            
            NodeTable nt = cn.buildNodeTable(startables) ;
            
            ConfigIndex ci = ConfigIndex.create(m) ;
            
            TupleIndex[] indexes = new TupleIndex[ci.indexServices().size()] ;
            int i = 0 ;
            for ( IndexService idxSvc : ci.indexServices() ) {
                TupleIndex idx = ci.buildIndex(idxSvc, startables) ;
                indexes[i++] = idx ;
            }
             
            //startables.stream().forEach(s -> s.start()) ;
            
            //System.out.println(indexes) ;
            //System.out.println(nodes) ;
            
            DatasetGraph dsg = LzBuild.createDataset(Location.mem(), indexes, nt) ;
            lizard = new LzDataset(dsg, startables) ;
        }
        
        if ( lizard == null )
            throw new LizardException("Failed to build a dataset") ;
        
        return lizard ;
        
    }
}
