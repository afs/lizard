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

package lz_dev ;

public class PROJECT_Lizard {
    // Tuple hierarchy.
    // Avoid copy - tuple + set colmap.
    
    // NEXT
    //   Implement carried over transaction in NodeTbaleHandler and IndexHandler 
    
    /*
     *  index, node "clear"
     *  
     *  Ping does not ping every server (does it matter?) 
     *  README to wiki
     *  Documentation
     *  Running with different config files.
     *    lz server --deploy $LZ_DEPLOY $LZ_CONF  
     *    LZ_CONF LZ_DEPLOY
     *    
     *  lizard-cmds - deployed version
     *  lz-dev/bin
     */
    
    
    // TransactionalBase : tests for detach after commit, around end(). 
    
    
    // TxnHandler to have transaction mapping help code.
    // TClientTxn -> TxnClient c.f. TxnHandler.
    
    // LzTxnId vs TxnId
    // Make TxnId and interface
    // Make TransactionCoordinator take a factory
    //   Zookeeper allocation for lizard.
    
    // Thrift : Other servers  - TNonblockingServer+TFramedTransport 
    
    /*
     * Project setup:
     * Data access (index, quack-like)
     *   Where does this go? -> Index?
     *   
     * Failures
     *   Protocol message "go away" to simulate failures 
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

    /*
     * 1. index to index copy "tdbidxcopy --loc=DB SPO POS no overwrite 2.
     * splitter : conf+existing TDB -> set of directories. 3. zloader
     */

    // Static explain.
    // In node space?

    // Better explain for joins.

    // ** NodeId
    // ids: 80bit = 2+8 = 1+9
    // One byte: 2 bits
    // Literal inline: 6bits + 8 bytes
    // Special Ids for rdf: rdfs: (xsd:)
}
