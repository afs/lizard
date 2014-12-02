/*
 *  Copyright 2013, 2014 Andy Seaborne
 *
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
 */

package lz_dev ;

public class PROJECT_Lizard {
    /*
     * Data access (index, quack-like)
     *   Where does this go? -> Index?
     *   
     * Should the client side of node/index actually be in query-server?
     *   No - isolated testing for node and index need the client side.
     *   Yes - no need for index-server to depend on quack
     *   Or quack should be split?
     *   ** Or More of the Index table client shoud be in query-server.
     *   To many modules results in opaque chaos. 
     * 
     * Failures
     *   Protocol message "go away" 
     * 
     * lizard-config and build
     *   In lizard-cluster? Separate?
     * 
     * ** Sharding - need a better bit mixer.
     *    Fnv 
     * 
     * curator.apache.org
     * Discovery?
     *   Join/leave/membership
     * 
     * V1 update - patch only?
     * 
     * Transaction promotion.
     * 
     * NodeTable perf tests Node BPT is the slow point. Object file already has
     * a write buffer.
     * 
     * Separate components.
     * 
     * Build: node server node client index server index client
     * 
     * Query server
     * Platforms.
     */

    /*
     * 1. RemoteAcces improvements. Include a filter? predicate list Jump start
     * find(...) from S=foo ==> filter between X and Y
     * 
     * 2. Modular Tests
     * 
     * 3. Index server partial joins.
     */

    // Co-locate nodes and indexes? for filters!

    // Parallel operation.
    
    // Special kinds for RemoteAccessData and RemoteNode that are sync call
    // throughs. Parallel work pool versions?

    // Concurrency policy

    // **** TOOLS ****

    /*
     * 1. index to index copy "tdbidxcopy --loc=DB SPO POS no overwrite 2.
     * splitter : conf+existing TDB -> set of directories. 3. zloader
     */

    // Zookeeper

    // Static explain.
    // In node space?

    // Better explain for joins.

    // Elsewhere
    // TDB system configuration

    // Binary Node table.

    // **** PLATFORM ****

    // Minimum Viable Release

    // AREAS
    // Join, Cluster, (MVCC), Loader, NodeTable

    // ** NodeId
    // ids: 80bit = 2+8 = 1+9
    // One byte: 2 bits
    // Literal inline: 6bits + 8 bytes
    // Special Ids for rdf: rdfs: (xsd:)

    // Literal - normal, long, inline
    enum NType {
        IRI, BNODE, GRAPH, LITERAL, LITERAL_LONG, LITERAL_INLINE
    }
    // leave space : e.g. "graph", "list"
}
