/*
 *  Copyright 2013, 2014 Andy Seaborne
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

package lz_dev;

import lizard.system.LizardException ;
import lizard.cluster.Platform ;
import lizard.conf.Configuration ;
import lizard.conf.dataset.ConfigLizardDataset ;
import lizard.conf.dataset.LizardDataset ;
import org.apache.jena.riot.RDFDataMgr ;

import com.hp.hpl.jena.query.Dataset ;
import com.hp.hpl.jena.query.DatasetFactory ;
import com.hp.hpl.jena.rdf.model.Model ;
import com.hp.hpl.jena.rdf.model.ModelFactory ;
import com.hp.hpl.jena.rdf.model.Resource ;
import com.hp.hpl.jena.sparql.ARQException ;
import com.hp.hpl.jena.sparql.core.DatasetGraph ;
import com.hp.hpl.jena.sparql.core.assembler.DatasetAssemblerVocab ;
import com.hp.hpl.jena.sparql.util.TypeNotUniqueException ;
import com.hp.hpl.jena.sparql.util.graph.GraphUtils ;
import com.hp.hpl.jena.tdb.base.file.Location ;

/** Setup both query engine and servers, which if that are "localhost" means everything is on one machine */  
public class Local {
    
    /** Build locally (but do not start) servers needed. 
     * @param model
     * @param location
     * @return Platform
     */
    public static Platform buildServers(Model model, Location location) {
        Configuration configuration = new Configuration(model) ;
        Platform platform = configuration.buildServers(location) ;
        return platform ;
    }
    
    /** Build locally (but do not start) servers needed. 
     * @param location
     * @param confFiles
     * @return Platform
     */
    public static Platform buildServers(Location location, String... confFiles) {
        Configuration configuration = Configuration.fromFile(confFiles) ;
        Platform platform = configuration.buildServers(location) ;
        return platform ;
    }
    
    /** Build (and start) a dataset */ 
    /**
     * @param model
     * @return Dataset
     */
    public static Dataset buildDataset(Model model) {
        Resource lzType = model.createResource("http://jena.apache.org/ns/lizard#Dataset") ;
        Resource root = null ;
        try {
            root = GraphUtils.findRootByType(model, lzType) ;
            if ( root == null )
                throw new LizardException("No lizard:Dataset") ;
            
        } catch (TypeNotUniqueException ex)
        { throw new ARQException("Multiple types for: "+DatasetAssemblerVocab.tDataset) ; }
        LizardDataset lzDSG = ConfigLizardDataset.buildDataset(root) ;
        lzDSG.start() ;
        DatasetGraph dsg = lzDSG.getDataset() ;
        Dataset ds = DatasetFactory.create(dsg) ;
        return ds ;
        
    }

    public static DatasetGraph buildDataset(String... confDataset) {
        Model m = readAll(confDataset) ;
        ConfigLizardDataset config = new ConfigLizardDataset(m) ;
        LizardDataset lz = config.buildDataset() ;
        lz.start() ;
        return lz.getDataset() ;
    }
    
    public static Model readAll(String ... files) {
        Model model = ModelFactory.createDefaultModel() ;
        for ( String fn : files ) {
            RDFDataMgr.read(model, fn);    
        }
        return model ;
    }
//       
//      RDFDataMgr.read(model, confNode); 
//      RDFDataMgr.read(model, confIndex);
    
}
