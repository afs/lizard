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

import org.apache.jena.assembler.Assembler ;
import org.apache.jena.assembler.assemblers.AssemblerGroup ;
import org.apache.jena.rdf.model.Resource ;
import org.apache.jena.tdb2.assembler.Vocab ;

public class VocabLizard {
    public final static String namespace = "urn:lizard:ns#" ;
    private static final String NS = namespace ;

    public static String getURI() { return NS ; } 

    // Types
    public static final Resource lzDataset          = Vocab.type(NS, "Dataset") ;

    private static boolean initialized = false ; 
    static synchronized public void init()
    {
        if ( initialized )
            return ;
        registerWith(Assembler.general) ;
        initialized = true ;
    }
    
    static void registerWith(AssemblerGroup g)
    {
        // Wire in the extension assemblers (extensions relative to the Jena assembler framework)
        // Domain and range for properties.
        // Separated and use ja:imports
        assemblerClass(g, lzDataset,            new LizardAssembler()) ;
    }
    
    public static void assemblerClass(AssemblerGroup group, Resource r, Assembler a)
    {
        if ( group == null )
            group = Assembler.general ;
        group.implementWith(r, a) ;
        //assemblerAssertions.add(r, RDFS.subClassOf, JA.Object) ;
    }
}
