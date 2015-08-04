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

package lizard.conf.assembler;

import lizard.build.LzDeploy ;
import lizard.conf.ConfCluster ;
import lizard.conf.parsers.LzConfParserYAML ;
import lizard.query.LizardQuery ;
import lizard.sys.Lizard ;

import org.apache.jena.assembler.Assembler ;
import org.apache.jena.assembler.Mode ;
import org.apache.jena.assembler.exceptions.AssemblerException ;
import org.apache.jena.atlas.lib.FileOps ;
import org.apache.jena.query.Dataset ;
import org.apache.jena.rdf.model.Property ;
import org.apache.jena.rdf.model.Resource ;
import org.apache.jena.sparql.core.assembler.DatasetAssembler ;

public class AssemblerYaml extends DatasetAssembler {
    @Override
    public Dataset createDataset(Assembler a, Resource root, Mode mode)
    {
        LizardQuery.init() ;
        return make(root) ;
    }

    
    static Dataset make(Resource root) {
        // YAML configuration
        Lizard.init(); 
        Property pConfiguration = root.getModel().createProperty("urn:lizard:", "configuration") ;
        Property pLayout = root.getModel().createProperty("urn:lizard:", "configuration") ;
        
        if ( ! root.hasProperty(pConfiguration) )
            throw new AssemblerException(root, "Missing the Lizard config file via "+pConfiguration) ;  
        if ( ! root.hasProperty(pLayout) )
            throw new AssemblerException(root, "Missing the Lizard config file via "+pLayout) ;  
        
        String confFile  = root.getProperty(pConfiguration).getString() ;
        String layoutFile  = root.getProperty(pLayout).getString() ;
        
        if ( ! FileOps.exists(confFile) )
            throw new AssemblerException(root, "No such file: "+confFile) ;
        if ( ! FileOps.exists(layoutFile) )
            throw new AssemblerException(root, "No such file: "+confFile) ;
        
        ConfCluster conf = LzConfParserYAML.parseConfFile(confFile,layoutFile) ;
        String here = "here" ;
        Dataset ds = LzDeploy.deployDataset(conf, here);
        return ds ;
    }
}

