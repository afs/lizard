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

package lizard.conf.assembler;

import lizard.conf.dataset.ConfigLizardDataset ;
import lizard.conf.dataset.LzBuildClient ;
import lizard.query.LizardQuery ;
import lizard.query.LzDataset ;
import lizard.sys.Lizard ;

import com.hp.hpl.jena.assembler.Assembler ;
import com.hp.hpl.jena.assembler.Mode ;
import com.hp.hpl.jena.query.Dataset ;
import com.hp.hpl.jena.query.DatasetFactory ;
import com.hp.hpl.jena.rdf.model.Property ;
import com.hp.hpl.jena.rdf.model.Resource ;
import com.hp.hpl.jena.sparql.core.DatasetGraph ;
import com.hp.hpl.jena.sparql.core.assembler.AssemblerUtils ;
import com.hp.hpl.jena.sparql.core.assembler.DatasetAssembler ;

import org.seaborne.dboe.base.file.Location ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

/** Create a Lizard dataset */
public class LizardAssembler extends DatasetAssembler {
    private static Logger log = LoggerFactory.getLogger(LizardAssembler.class) ;
    
    @Override
    public Dataset createDataset(Assembler a, Resource root, Mode mode)
    {
        LizardQuery.init() ;
        return make(root) ;
    }

  /* Example:
  <r> rdf:type lizard:Dataset ;
       ...      
     */

    
    static Dataset make(Resource root) {
        // two versions: in-line RDF (old style) or YAML configuration (new style).
        
        Lizard.init(); 
        LzDataset lz = ConfigLizardDataset.buildDataset(root) ;
        lz.start();
        log.warn("** In-memory journal") ;
        DatasetGraph dsg = LzBuildClient.datasetGraph(lz, Location.mem()) ;
        AssemblerUtils.setContext(root, dsg.getContext());
        return DatasetFactory.create(dsg) ;
    }
}
