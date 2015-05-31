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

import java.util.* ;
import java.util.stream.Collectors ;

import lizard.build.LzDatasetDetails ;

import org.apache.jena.atlas.lib.Tuple ;
import org.apache.jena.graph.Node ;
import org.apache.jena.graph.Triple ;
import org.apache.jena.riot.other.BatchedStreamRDF ;
import org.apache.jena.riot.system.StreamRDF ;
import org.apache.jena.sparql.core.Quad ;
import org.seaborne.tdb2.store.DatasetGraphTDB ;
import org.seaborne.tdb2.store.NodeId ;
import org.seaborne.tdb2.store.nodetable.NodeTable ;

/**
 * @see BatchedStreamRDF BatchedStreamRDF, which batches by subject
 */
public class StreamRDFBatchSplit implements StreamRDF {
    protected static NodeId placeholder = NodeId.create(-7) ;
    protected final List<Triple> triples ;
    protected final List<Tuple<NodeId>> tuples ;
    protected final Map<Node, NodeId> mapping ;
    
    private final int batchSize ;
    private final LzDatasetDetails details ;
    private final DatasetGraphTDB dsg ;
    
    public StreamRDFBatchSplit(DatasetGraphTDB dsg, int batchSize) {
        this.dsg = dsg ;
        this.batchSize = batchSize ;
        this.triples = new ArrayList<>(batchSize) ;
        this.tuples = new ArrayList<>(triples.size()) ;
        this.mapping = new HashMap<>(2*batchSize) ;
        this.details = new LzDatasetDetails(dsg) ;
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
        if ( triples.size() >= batchSize )
            processBatch() ;
    }

    protected void processBatch() {
        // Do this by filling the cache.
        Set<Node> required = mapping.keySet() ;
        List<Node> nodes = new ArrayList<>() ;
        // There is a change cache spills will mess the world up.
        // Keep private copy then mass fill the cache?

        // Check cache.
        for ( Node n : required ) {
            if ( details.ntCache.getNodeIdForNodeCache(n) == null ) {
                nodes.add(n) ;
            }
        }
        
        // XXX Temp assume cache
        /*List<NodeId> nodeIds = details.ntCluster.bulkNodeToNodeId(nodes, true) ; */
        // Fill cache.
        details.ntCluster.bulkNodeToNodeId(nodes, true) ;
        
        // ---- Add triples as tuples
        
        //dsg.getTripleTable().addAll(triples) ;
        
        if ( true ) {
            // Copy :-| though oddly cache friendly :-)
            convert(triples, tuples, details.ntTop) ;
            dsg.getTripleTable().getNodeTupleTable().getTupleTable().addAll(tuples);
//            for ( Tuple<NodeId> tuple : tuples )
//                dsg.getTripleTable().getNodeTupleTable().getTupleTable().add(tuple);
        } else {
            for ( Triple triple : triples ) {
                dsg.getTripleTable().add(triple); 
            }
        }
        triples.clear();
        tuples.clear() ;
        mapping.clear();
        
        // Eventually, put triples on a work queue.
    }
   
    // check for duplicate code
    private static List<Tuple<NodeId>> convert(List<Triple> triples, NodeTable nodeTable) {
        return triples.stream().map(t->
                Tuple.createTuple
                (nodeTable.getAllocateNodeId(t.getSubject()),
                 nodeTable.getAllocateNodeId(t.getPredicate()),
                 nodeTable.getAllocateNodeId(t.getObject())))
         .collect(Collectors.toList()) ;
    }
    
    private static void convert(List<Triple> triples, List<Tuple<NodeId>> tuples, NodeTable nodeTable) {
        // Slightly faster.  But larger batches?
        for ( Triple t : triples ) {
            Tuple<NodeId> x = Tuple.createTuple
                    (nodeTable.getAllocateNodeId(t.getSubject()),
                     nodeTable.getAllocateNodeId(t.getPredicate()),
                     nodeTable.getAllocateNodeId(t.getObject())) ;
             tuples.add(x) ;
        }
        
//        triples.stream().map(t->
//                  Tuple.createTuple
//                  (nodeTable.getAllocateNodeId(t.getSubject()),
//                   nodeTable.getAllocateNodeId(t.getPredicate()),
//                   nodeTable.getAllocateNodeId(t.getObject())))
//                .collect(Collectors.toCollection(()->tuples)) ;
    }
    
    
    private void processNode(Node node) {
        
        if ( mapping.containsKey(node)) 
            return ;
        
        //if ( details.ntCache.containsNode(node) )
        
        if ( NodeId.hasInlineDatatype(node) ) {
            NodeId nodeId = NodeId.inline(node) ;
            if ( nodeId != null )
                return ;
        }
        // if in cache
        //   return ;
        mapping.put(node, placeholder) ;
    }
    
    @Override
    public void quad(Quad quad) {}

    @Override
    public void base(String base) {}

    @Override
    public void prefix(String prefix, String iri) {}

    @Override
    public void finish() { 
        if ( ! triples.isEmpty() )
            processBatch() ;
    }

}

