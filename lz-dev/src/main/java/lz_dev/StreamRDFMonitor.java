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

import com.hp.hpl.jena.graph.Triple ;
import com.hp.hpl.jena.sparql.core.Quad ;

import org.apache.jena.atlas.logging.ProgressLogger ;
import org.apache.jena.riot.system.StreamRDF ;
import org.apache.jena.riot.system.StreamRDFWrapper ;

public class StreamRDFMonitor extends StreamRDFWrapper {
    
    private final ProgressLogger monitor ;
    
    public StreamRDFMonitor(StreamRDF other, ProgressLogger monitor) {
        super(other) ;
        this.monitor = monitor ;
    }
    
    @Override
    public void start() {
        monitor.startMessage(); 
        monitor.start();
        super.start() ;
        
    }

    @Override
    public void triple(Triple triple) {
        tick() ;
        super.triple(triple) ;
    }

    @Override
    public void quad(Quad quad) {
        tick() ;
        super.quad(quad);
    }

    @Override
    public void finish() { 
        super.finish();
        monitor.finish();
        monitor.finishMessage(); 
    }
    
    private void tick() { monitor.tick() ; }
}

