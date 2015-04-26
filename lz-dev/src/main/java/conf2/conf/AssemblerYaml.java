/**
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

package conf2.conf;

import lizard.query.LizardQuery ;
import lizard.sys.Lizard ;

import org.apache.jena.atlas.lib.FileOps ;

import org.apache.jena.assembler.Assembler ;
import org.apache.jena.assembler.Mode ;
import org.apache.jena.assembler.exceptions.AssemblerException ;
import org.apache.jena.query.Dataset ;
import org.apache.jena.rdf.model.Property ;
import org.apache.jena.rdf.model.Resource ;
import org.apache.jena.sparql.core.assembler.DatasetAssembler ;

import conf2.LzConfigDefault ;
import conf2.build.LzDeploy ;
import conf2.parsers.LzConfParserYAML ;

public class AssemblerYaml extends DatasetAssembler {
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
        
        Property property = root.getModel().createProperty("urn:lizard:", "configuration") ;
        
        if ( ! root.hasProperty(property) )
            throw new AssemblerException(root, "Missing the Lizard config file via "+property) ;  
        
        String confFile  = root.getProperty(property).getString() ;
        
        if ( ! FileOps.exists(confFile) )
            throw new AssemblerException(root, "No such file: "+confFile) ;
        
        ConfCluster conf = 
            confFile.isEmpty() ? LzConfigDefault.setup_mem_local() 
                               : LzConfParserYAML.parseConfFile(confFile) ;
        NetHost here = NetHost.create("localhost") ;
        Dataset ds = LzDeploy.deploy(conf, here);
        return ds ;
    }
}

