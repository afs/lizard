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

package lz_dev;

import org.apache.jena.riot.system.StreamRDF ;
import org.apache.jena.riot.system.StreamRDFWrapper ;

public class StreamRDFDepth extends StreamRDFWrapper {
    //DatasetChanges - version for add only? 
    private int depth = 0 ; 
    
    public StreamRDFDepth(StreamRDF other) {
        super(other) ;
    }
    
    @Override
    public void start() {
        if ( depth == 0 )
            super.start() ;
        depth++ ;
    }

    @Override
    public void finish() { 
        depth-- ;
        if ( depth == 0 )
            super.finish() ;
    }
}
