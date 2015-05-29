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
 
package lizard.build;

import lizard.conf.Config ;
import lizard.index.ClusterTupleIndex ;
import lizard.node.ClusterNodeTable ;

import org.apache.jena.atlas.lib.ArrayUtils ;
import org.seaborne.tdb2.store.DatasetGraphTDB ;
import org.seaborne.tdb2.store.nodetable.NodeTable ;
import org.seaborne.tdb2.store.nodetable.NodeTableCache ;
import org.seaborne.tdb2.store.nodetable.NodeTableInline ;
import org.seaborne.tdb2.store.tupletable.TupleIndex ;

public class LzDatasetDetails {
    public final TupleIndex[] tripleIndexes ;
    public final TupleIndex[] quadIndexes ;
    public final NodeTable ntTop ;
    public NodeTableInline ntInline ;
    public NodeTableCache ntCache ;
    public ClusterNodeTable ntCluster ;
    
    // Triple Indexes
    //   Remote
    // Quad indexes
    //   Remote
    // NodeIndex
    // NodeCache
    //   With "cache only" access
    // Remote node
    
    public LzDatasetDetails(DatasetGraphTDB dsg) {
        ntTop = dsg.getTripleTable().getNodeTupleTable().getNodeTable() ;
        
        tripleIndexes = ArrayUtils.copy(dsg.getTripleTable().getNodeTupleTable().getTupleTable().getIndexes()) ;
        quadIndexes = ArrayUtils.copy(dsg.getQuadTable().getNodeTupleTable().getTupleTable().getIndexes()) ;
        
        fillInNodeTableDetails() ;
        fillInIndexDetails() ;
    }

    private void fillInNodeTableDetails() {
        // Nodetable.
        NodeTable ntx = ntTop ;
        while(ntx.wrapped() != null ) {
            if ( ntx instanceof NodeTableInline ) {
                if ( ntInline != null )
                    Config.logConf.error("Multiple NodeTableInline") ;
                ntInline = (NodeTableInline)ntx ;
            }
            else if ( ntx instanceof NodeTableCache ) {
                if ( ntCache != null )
                    Config.logConf.error("Multiple NodeTableCache") ;
                ntCache = (NodeTableCache)ntx ;
            }
            ntx = ntx.wrapped() ;
        } 
        if ( ntx instanceof ClusterNodeTable ) {
            ntCluster = (ClusterNodeTable)ntx ;
            //ctnt.getDistributor() ;
            // Get replicas and shards.
        }
        if ( ntInline == null )
            Config.logConf.warn("No NodeTableInline") ;
        if ( ntCache == null )
            Config.logConf.warn("No NodeTableCache") ;
        if ( ntCluster == null )
            Config.logConf.warn("No ClusterNodeTable") ;
    }
    
    private void fillInIndexDetails() {
        // Indexes
        for ( TupleIndex idx : tripleIndexes ) {
            if ( idx instanceof ClusterTupleIndex ) {
                ClusterTupleIndex ctIdx = (ClusterTupleIndex)idx ;
                //ctIdx.getDistributor() ;
                // Get replicas and shards.
            }
        }
        
    }
}
