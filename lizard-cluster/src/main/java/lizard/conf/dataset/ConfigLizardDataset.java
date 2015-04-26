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

package lizard.conf.dataset;

import java.util.ArrayList ;
import java.util.List ;
import java.util.Map ;

import lizard.conf.Config ;
import lizard.conf.ConfigLib ;
import lizard.conf.assembler.VocabLizard ;
import lizard.conf.index.ConfigIndex ;
import lizard.conf.index.IndexService ;
import lizard.conf.node.ConfigNode ;
import lizard.query.LzDataset ;
import lizard.system.Component ;
import lizard.system.LizardException ;

import org.apache.jena.rdf.model.Model ;
import org.apache.jena.rdf.model.Resource ;
import org.apache.jena.sparql.ARQException ;
import org.apache.jena.sparql.core.assembler.DatasetAssemblerVocab ;
import org.apache.jena.sparql.util.TypeNotUniqueException ;
import org.apache.jena.sparql.util.graph.GraphUtils ;
import org.apache.jena.tdb.store.DatasetGraphTDB ;
import org.apache.jena.tdb.store.nodetable.NodeTable ;
import org.apache.jena.tdb.store.tupletable.TupleIndex ;

import org.apache.jena.atlas.logging.Log ;
import org.seaborne.dboe.base.file.Location ;
import org.seaborne.dboe.transaction.Transactional ;
import org.seaborne.dboe.transaction.txn.TransactionCoordinator ;
import org.seaborne.dboe.transaction.txn.TransactionalBase ;
import org.seaborne.dboe.transaction.txn.journal.Journal ;
import org.slf4j.Logger ;

public class ConfigLizardDataset {
    public static Logger logConf = Config.logConf ;

    private final Model model ;
    private final Resource root ;
    private final Map<Resource, LzDatasetDesc> datasets ;
    
    public static ConfigLizardDataset create(Model model) {
        return new ConfigLizardDataset(model) ;
    }

    ConfigLizardDataset(Model model) {
        this.model = model ;
        this.root = findRoot(model) ;
        this.datasets = findDatasets(model) ;
    }
    
    private Resource findRoot(Model model2) {
        try {
            Resource root = GraphUtils.findRootByType(model, VocabLizard.lzDataset) ;
            if ( root == null )
                throw new LizardException("No lizard:Dataset") ;
            return root ;
        } catch (TypeNotUniqueException ex)
        { throw new ARQException("Multiple types for: "+DatasetAssemblerVocab.tDataset) ; }
    }

    private Map<Resource, LzDatasetDesc> findDatasets(Model model) {
        return ConfigLib.datasets(model) ;
    }

    public LzDataset buildDataset() {
        return buildDataset(root) ;
    }
    
    public static LzDataset buildDataset(Resource lzdsgRoot) {
        Model m = lzdsgRoot.getModel() ;
        
        Map<Resource, LzDatasetDesc> x = ConfigLib.datasets(m) ;
        if ( x.size() == 0 )
            throw new LizardException("No LzDataset description") ;
        if ( x.size() > 1 )
            throw new LizardException("More than one LzDataset description") ;
        LzDatasetDesc desc = x.entrySet().stream().findFirst().get().getValue() ;
        LzDataset lizard = buildDataset(Location.mem(), desc) ;
        return lizard ; 
    }
    
    public static LzDataset buildDataset(Location location, LzDatasetDesc desc) {
        if ( location.isMem() )
            Log.warn(ConfigLizardDataset.class, "In-memory journal") ;
        Journal journal = Journal.create(location) ;
        TransactionCoordinator txnCoord = new TransactionCoordinator(journal) ;
        Transactional trans = new TransactionalBase(txnCoord) ;

        List<Component> startables = new ArrayList<>() ;
        
        Model m = desc.resource.getModel() ;
        //????
        ConfigNode cn = ConfigNode.create(desc.resource.getModel()) ;
        NodeTable nt = cn.buildNodeTable(startables) ;
        
        ConfigIndex ci = ConfigIndex.create(m) ;
        TupleIndex[] indexes = new TupleIndex[ci.indexServices().size()] ;
        int i = 0 ;
        for ( IndexService idxSvc : ci.indexServices() ) {
            TupleIndex idx = ci.buildIndex(idxSvc, startables) ;
            indexes[i++] = idx ;
        }
        
        DatasetGraphTDB dsg = LzBuildClient.createDataset(Location.mem(), indexes, nt) ;
        LzDataset lizard = new LzDataset(dsg, startables) ;
        return lizard ;
    }

    public LzDatasetDesc descDataset() {
        return datasets.get(root) ;
    }
}
