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

import java.util.ArrayList ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;

import org.apache.jena.graph.Node ;
import org.apache.jena.graph.Triple ;
import org.apache.jena.riot.other.BatchedStreamRDF ;
import org.apache.jena.riot.system.StreamRDF ;
import org.apache.jena.sparql.core.Quad ;
import org.seaborne.tdb2.store.NodeId ;

/**
 * 
 * 
 * @see BatchedStreamRDF BatchedStreamRDF, which batches by subject
 */
public class StreamRDFBatchSplit implements StreamRDF {
    private static NodeId placeholder = NodeId.NodeIdAny ;
    private final List<Triple> triples ;
    private final Map<Node, NodeId> mapping ;
    
    private final int batchSize ;
    
    StreamRDFBatchSplit(int batchSize) {
        this.batchSize = batchSize ;
        triples = new ArrayList<>(batchSize) ;
        mapping = new HashMap<>(2*batchSize) ;
    }
        
    @Override
    public void start() {}

    @Override
    public void triple(Triple triple) {
        //Find nodes.
        processNode(triple.getSubject()) ;
        processNode(triple.getPredicate()) ;
        processNode(triple.getObject()) ;
        triples.add(triple) ;
        if ( triples.size() >= batchSize ) {
            processBatch() ;
            triples.clear();
            mapping.clear();
        }
    }

    private void processBatch() {
        //getNodeIds() ;
        //translate triples ;
        //updates ;
    }
   

    private void processNode(Node node) {
        mapping.put(node, placeholder) ;
    }
    
    @Override
    public void quad(Quad quad) {}

    @Override
    public void base(String base) {}

    @Override
    public void prefix(String prefix, String iri) {}

    @Override
    public void finish() {}

}

