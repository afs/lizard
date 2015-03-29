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

package lizard.conf;

import java.io.PrintStream ;
import java.util.List ;

import com.hp.hpl.jena.rdf.model.Model ;
import com.hp.hpl.jena.tdb.base.file.Location ;
import com.hp.hpl.jena.tdb.store.nodetable.NodeTable ;
import com.hp.hpl.jena.tdb.store.nodetupletable.NodeTupleTable ;
import com.hp.hpl.jena.tdb.store.nodetupletable.NodeTupleTableConcrete ;
import com.hp.hpl.jena.tdb.store.tupletable.TupleIndex ;
import com.hp.hpl.jena.tdb.sys.DatasetControlMRSW ;

import lizard.cluster.Platform ;
import lizard.conf.dataset.ConfigLizardDataset ;
import lizard.conf.index.ConfigIndex ;
import lizard.conf.index.IndexService ;
import lizard.conf.node.ConfigNode ;
import lizard.system.Component ;
import migrate.Q ;

/** Cluster configuration - this covers node and index servers,
 * not the (stateless) query server. 
 */
public class Configuration {
    
    public static Configuration fromFile(String... conffile) {
        Model model = Q.readAll(conffile) ;
        return new Configuration(model) ;
    }
    
    public static Configuration fromModel(Model model) {
        return new Configuration(model) ;
    }
    
    private final ConfigIndex   confIndex ;
    private final ConfigNode    confNode ;
    private final ConfigLizardDataset confDataset ;
    
    public Configuration(Model model) {
        confIndex = ConfigIndex.create(model) ;
        confNode  = ConfigNode.create(model) ;
        confDataset = ConfigLizardDataset.create(model) ;
    }
    
    public ConfigIndex getConfIndex() {
        return confIndex ;
    }

    public ConfigNode getConfNode() {
        return confNode ;
    }

    public ConfigLizardDataset getConfDataset() {
        return confDataset ;
    }

    /** Replace with deployment code */
    @Deprecated
    public Platform buildServers(Location location) {
        Platform platform = new Platform();
        ConfigNode.buildServers(confNode, platform, location); 
        ConfigIndex.buildServers(confIndex, platform, location) ;
        return platform ;
    }
    
    public NodeTupleTable buildClients(List<Component> startables) { 
        
        TupleIndex[] indexes = new TupleIndex[confIndex.indexServices().size()] ;
        NodeTable nt = confNode.buildNodeTable(startables) ;

        int i = 0 ;
        for ( IndexService idxSvc : confIndex.indexServices() ) {
            TupleIndex idx = confIndex.buildIndex(idxSvc, startables) ;
           indexes[i++] = idx ; 
        }
        
        NodeTupleTable ntt = new NodeTupleTableConcrete(3, indexes, nt, new DatasetControlMRSW()) ;
        return ntt ;
    }
    
    public void print() {
        print(System.out) ;
    }
        
    public void print(PrintStream ps) {
        confIndex.print(ps);
        confNode.print(ps); 
    }
}
