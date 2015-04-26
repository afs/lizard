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

package lizard.system;

import java.nio.file.Files ;
import java.nio.file.Path ;
import java.nio.file.Paths ;
import java.util.Arrays ;
import java.util.List ;

import org.apache.jena.riot.RDFDataMgr ;
import org.apache.jena.riot.RiotNotFoundException ;

import org.apache.jena.rdf.model.Model ;
import org.apache.jena.rdf.model.ModelFactory ;

/** General utilities for Lizard.
 *  Not Lizard specific and might usefully migrate to Jena proper.
 */

public class LzLib {
    public static Model readAll(String ... files) {
        return readAll(Arrays.asList(files)) ;
    }

    public static Model readAll(List<String> files) {
        return readAll(ModelFactory.createDefaultModel(), files) ;
    }
    
    public static Model readAll(Model model, List<String> files) {
        for ( String fn : files ) {
            Path p = Paths.get(fn) ; 
            if ( ! Files.exists(p) ) {
                throw new RiotNotFoundException("File not found: "+fn) ;
            }
        }
        
        for ( String fn : files ) {
            RDFDataMgr.read(model, fn);    
        }
        return model ;
    }
}
