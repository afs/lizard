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
    // Multiple deployment.
    // Debug killing a server.
    //   Does not recover.
    // Info begine-end transaction
    //   Second bad operation blocks?
    //   Read quorum vs write quorum
    //   Much better handling of failure cases of distributed transactions. 
    
    // Pref "local" indexes?
    
    // TCP connections:
    //   TCP_QUICKACK (needs reset on every use) and TCP_NODELAY
    
    // *** TxnClient is currently doing the synchonization
    // Sort out ThriftRunnable / Runnable.
    //   maybe a fixed class to catch+convert Thrift to LizardExceptions 
    
    // NodeTableCache and transactions.
    
    // Reduce overheads - wire to node table non-conversion.
    
    // === Tasks
    //   Update replics in parallel.
    
    // TxnClient.
    // TServer(Node|Index) -- what about locking operations?
    
    // THandlerNodeTable can store TLZ forms.
    //   Not a node table but an Index+ObjectFileStorage directly.
    //   Don't decode/encode.
    //     NodeTableNative : BiMap[T]
    
    // Scaling:
    // A - Fix locks on ThriftLib.
    // B - Batching updates (and flow reversal) : switch between R and W modes.
    // C - Block allocate txn ids but still need the lock.
    
    // Node caching
    //  1 - move to hash NodeId
    //  2 - move to 10 byte NodeIds
    // Special Ids for rdf: rdfs: (xsd:) URIs?
    
    // Very large scans. Batching in replies

    // ---- Configuration
    // Robust parsing of YAML/RDF configurations.
    // Component naming:
    // location/Index/...
    // location/Nodes/...
    
    // ---- Configuration
    // Check/get LZ_* commands working.
    // LZ_Deploy + s-put.

    // prepare-abort testing.
    // From Mantis: Component has W lifecycle and an R lifecycle
    
    // README to wiki

    // Thrift : Other servers  - TNonblockingServer+TFramedTransport 
    
    // Failures
    //   Protocol message "go away" to simulate failures 

    // 1. RemoteAcces improvements. Include a filter? predicate list Jump start
    //   find(...) from S=foo ==> filter between X and Y
    // Stream find()
    
    // index to index copy "tdbidxcopy --loc=DB SPO POS no overwrite 2.
    // splitter : conf+existing TDB -> set of directories. 3. zloader
}
