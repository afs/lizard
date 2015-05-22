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
    // Tasks
    
    // Rework building, using new configuration.
    // Delete:replace cluster/lizard/conf,sys etc.
    // BuildContext
    //   Location,
    //   Coordinator
    //   StoreParams
    //   UUID
    // ==> to Mantis, TDB2 etc
    // tdbdev.ComponentIdMgr
    
    
    // Share Thrift encoding with RIOT
    // ThriftConvert is better that ThriftLib?
    
    // From TDB2 - combined transaction and DatasetGraphTDB.
    // NodeTableNative2, NodeTableTLZ
    // Use TDB2, not TDB?
    
    // Scaling:
    // A - Fix locks on ThriftLib.
    // B - Batching updates (and flow reversal)
    // C - Block allocate txn ids but still need the lock.
    
    // Should all servers on one machine share a TransactionCoordinator?
    //   And update talsk to the TransactionCoordinator?
    
    // Thriftlib: Specific encodings: integer, double, etc. 
    
    // Node caching
    //  1 - move to hash NodeId
    //  2 - move to 10 byte NodeIds
    //  Cache abort isa commit!
    
    // ---- Configuration
    // Robust parsing of YAML/RDF configurations.
    // Delete! conf1 :: lizard-cluster:lizard.conf.*
    // Component naming:
    // location/Index/...
    // location/Nodes/...
    
    // Lz2BuildZk : one local zookeeper - currently ignoring configuration details 
    // Many front end query servers.
    // Many Zookeepers
    
    // Location = disk.
    //  Journals for NT and IDX shards
    
    // Fuseki integration
    // ---- Configuration
    
    // Batching in loads.
    //   add - find/flush - end|commit/flush
    // **** Caching nodes info - cache needs to drop back on abort.
    
    // Check/get LZ_* commands working.
    // LZ_Deploy + s-put.
    
    // Very large scans. Batching in replies?

    // Configuration
    // ConfigLizardDataset.buildDataset = location.
    
    // 1: Global Write txn id using zk
    
    // 2: Build-deploy tidy up
    // Deployment.parse -> Deploy.deploy(deployment) ;

    // LZ_Fuseki
    
    // Deploy specialized Fuseki
    //    Fuseki - fixed mode - one config file, no runtime additions.
    //    Fuseki embedded
    //    Fuseki webapps from jar? with "-server"
    //    #include for assemblers
    // 3: Log4j
    // 4: AIO

    // Allocate write transaction ids in blocks of 100.
    
    // Tuple hierarchy cleanup.
    // Avoid copy - tuple + set colmap.
    
    //  ZK transaction ids.
    //  prepare-abort testing.
    
    // Mantis: Component has W lifecycle and an R lifecycle
    
    // Journal.create to check for in-memory locations.
    
    // 
    // README to wiki

    // Thrift : Other servers  - TNonblockingServer+TFramedTransport 
    
    //
    // Failures
    //   Protocol message "go away" to simulate failures 

    //
    // 1. RemoteAcces improvements. Include a filter? predicate list Jump start
    //   find(...) from S=foo ==> filter between X and Y
    // Stream find()

    // Parallel operation.
    
    //
    // index to index copy "tdbidxcopy --loc=DB SPO POS no overwrite 2.
    // splitter : conf+existing TDB -> set of directories. 3. zloader

    // ** NodeId
    // ids: 80bit = 2+8 = 1+9
    // One byte: 2 bits
    // Literal inline: 6bits + 8 bytes
    // Special Ids for rdf: rdfs: (xsd:)
}
