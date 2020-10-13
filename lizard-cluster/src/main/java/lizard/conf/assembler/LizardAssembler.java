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

import lizard.build.LzBuilderDataset ;
import lizard.conf.ConfCluster ;
import lizard.conf.ConfDataset ;
import lizard.conf.parsers.LzConfParserRDF ;
import lizard.query.LizardQuery ;
import lizard.query.LzDataset ;
import lizard.sys.Lizard ;
import org.apache.jena.assembler.Assembler ;
import org.apache.jena.assembler.Mode ;
import org.apache.jena.dboe.base.file.Location ;
import org.apache.jena.query.Dataset ;
import org.apache.jena.rdf.model.Resource ;
import org.apache.jena.sparql.core.assembler.AssemblerUtils ;
import org.apache.jena.sparql.core.assembler.DatasetAssembler ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

/** Create a Lizard dataset */
public class LizardAssembler extends DatasetAssembler {
    // RDF version
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
        Lizard.init(); 
        ConfCluster confCluster = LzConfParserRDF.parseConfFile(root.getModel()) ; 
        ConfDataset confDataset = confCluster.dataset ;
        Location baseLocation = null ;
//        if ( confCluster.fileroot == null )
//                baseLocation = Location.mem() ;
//        else
//            baseLocation = Location.create(confCluster.fileroot) ;
        
        // XXX $$TEMP$$
        baseLocation = Location.mem() ;
        
        Location locationQueryServer = baseLocation.getSubLocation("query") ;
        log.warn("No 'same vnode' support for datasets yet");
        LzDataset lz = LzBuilderDataset.build(confCluster, locationQueryServer, "$NOWHERE$") ;
        Dataset ds = LzBuilderDataset.dataset(lz) ;
        AssemblerUtils.setContext(root, ds.getContext());
        return ds ;
    }
}
