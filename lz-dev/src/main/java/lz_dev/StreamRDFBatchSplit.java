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
import org.seaborne.tdb2.store.tupletable.TupleTable ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

/**
 * @see BatchedStreamRDF BatchedStreamRDF, which batches by subject
 */
public class StreamRDFBatchSplit implements StreamRDF {
    private static Logger log = LoggerFactory.getLogger(StreamRDFBatchSplit.class) ;
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
        //log.info("Triple: "+triple) ;
        processNode(triple.getSubject()) ;
        processNode(triple.getPredicate()) ;
        processNode(triple.getObject()) ;
        triples.add(triple) ;
        if ( triples.size() >= batchSize )
            processBatch() ;
    }

    int batchNumber = 1 ;
    
    protected void processBatch() {
        //if ( batchNumber < 10 )
        //FmtLog.info(log, ">>processBatch: [%d]->%d", batchNumber, triples.size()) ;
        batchNumber++ ;
        
        // Do this by filling the cache.
        Set<Node> required = mapping.keySet() ;
        // There is a change cache spills will mess the world up.
        // Keep private copy then mass fill the cache?

        boolean executeBatchNodesPhase = true ;
        boolean executeIndexPhase = true ;
        // Derived control.
        boolean batchUpdateIndexes = true ;
        
        if ( executeBatchNodesPhase )
            // Check this is a cache node table.
            batchUpdateNodes(required, details) ;
        
        if ( executeIndexPhase ) {
            if ( batchUpdateIndexes )
                batchUpdateIndexes(dsg, details, triples, /*tuples*/null) ;
            else
                incrementalUpdateIndexes(triples, dsg) ;
        }
        triples.clear();
        tuples.clear() ;
        //FmtLog.info(log, "<<processBatch") ;
        mapping.clear();
        System.exit(0);
    }
   
    private static void incrementalUpdateIndexes(List<Triple> triples, DatasetGraphTDB dsg) {
        for ( Triple triple : triples ) {
            dsg.getTripleTable().add(triple); 
        }
    }

    /** This files the cache so that the tuples adds are faster */ 
    private static void batchUpdateNodes(Set<Node> required, LzDatasetDetails details) {
        List<Node> nodes = new ArrayList<>() ;
        // Resolve NodeIds
        
        // ** Move this into cache - code. 
        
        for ( Node n : required ) {
            // 
            if ( details.ntCache.getNodeIdForNodeCache(n) == null /* set input - no need :: && ! nodes.contains(n) /* Not good?*/ )
                nodes.add(n) ;
        }
        log.info("Batch nodes: "+nodes.size()) ;
        // This drops into the default method.
        details.ntTop.bulkNodeToNodeId(nodes, true) ;
        
        // Check
        // Resolve NodeIds
        for ( Node n : required ) {
            if ( details.ntCache.getNodeIdForNodeCache(n) == null  )
                log.info("Not in cache: "+n) ;
        }
        
        
        
        //details.ntCluster.bulkNodeToNodeId(nodes, true) ;
    }

    private static void batchUpdateIndexes(DatasetGraphTDB dsg, LzDatasetDetails details, List<Triple> batchTriples, List<Tuple<NodeId>> tuples) {
        // Copy :-|
        if ( tuples == null )
            tuples = new ArrayList<>(batchTriples.size()) ;
        convert(batchTriples, tuples, details.ntTop) ;
        log.info("Batch triples: "+tuples.size()) ;
        
        TupleTable tupleTable = dsg.getTripleTable().getNodeTupleTable().getTupleTable() ;
        tupleTable.addAll(tuples);
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

