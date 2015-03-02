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

package lz_dev;

import lizard.conf.dataset.ConfigLizardDataset ;
import lizard.query.LzDataset ;
import lizard.system.LizardException ;
import migrate.Q ;
import org.apache.jena.riot.Lang ;
import org.apache.jena.riot.RDFDataMgr ;

import com.hp.hpl.jena.rdf.model.Model ;
import com.hp.hpl.jena.rdf.model.Resource ;
import com.hp.hpl.jena.sparql.ARQException ;
import com.hp.hpl.jena.sparql.core.assembler.DatasetAssemblerVocab ;
import com.hp.hpl.jena.sparql.util.TypeNotUniqueException ;
import com.hp.hpl.jena.sparql.util.graph.GraphUtils ;

/** Setup for query engine and servers */  
public class Local {

    /** Build (and start) a dataset */ 
    public static LzDataset buildDataset(Model model) {
        if ( false )
            RDFDataMgr.write(System.out, model, Lang.TTL);
        
        Resource lzType = model.createResource("urn:lizard:ns#Dataset") ;
        Resource root = null ;
        try {
            root = GraphUtils.findRootByType(model, lzType) ;
            if ( root == null )
                throw new LizardException("No lizard:Dataset") ;
            
        } catch (TypeNotUniqueException ex)
        { throw new ARQException("Multiple types for: "+DatasetAssemblerVocab.tDataset) ; }
        LzDataset lzDSG = ConfigLizardDataset.buildDataset(root) ;
        lzDSG.start() ;
        return lzDSG ;
    }
    /** Build (and start) a dataset */
    public static LzDataset buildDataset(String... confDataset) {
        Model m = Q.readAll(confDataset) ;
        ConfigLizardDataset config = new ConfigLizardDataset(m) ;
        LzDataset lz = config.buildDataset() ;
        lz.start() ;
        return lz ;
    }
}
