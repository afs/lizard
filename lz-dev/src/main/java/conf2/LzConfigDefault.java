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

package conf2;

import org.apache.jena.atlas.lib.ColumnMap ;
import conf2.conf.* ;

/** An assortment of ConfCluster configurations */ 
public class LzConfigDefault {

    // Setup for development : one of each, all one JVM; one local zookeeper.
    public static ConfCluster setup_mem_local() {
        int zkPort = 2188 ;
    
        // Dataset
        ConfNodeTable confNT = new ConfNodeTable(1, 1) ;
        ConfIndex posIdx =  new ConfIndex(new ColumnMap("SPO", "POS"), "POS", 1, 1) ;
        ConfIndex psoIdx =  new ConfIndex(new ColumnMap("SPO", "PSO"), "PSO", 1, 1) ;
        ConfDataset confDatabase = new ConfDataset(confNT, posIdx, psoIdx) ;
        
        // Shards
        ConfIndexElement posIdx1 = new ConfIndexElement(posIdx.indexOrder+"-1", "POS-1", posIdx, NetAddr.create("localhost", 2010)) ;
        ConfIndexElement psoIdx1 = new ConfIndexElement(psoIdx.indexOrder+"-1", "PSO-1", psoIdx, NetAddr.create("localhost", 2012)) ;
        ConfNodeTableElement nt1 = new ConfNodeTableElement("Nodes-1", "N1", confNT, NetAddr.create("localhost", 2014)) ;
    
        // The zookeeper server.
        ConfZookeeper confZookeeper = ConfZookeeper.create(zkPort, null) ;
    
        // Cluster
        ConfCluster confCluster = new ConfCluster(confDatabase) ;
        confCluster.zkServer.add(confZookeeper) ;
        confCluster.addIndexElements(posIdx1, psoIdx1) ;
        confCluster.addNodeElements(nt1) ;
        return confCluster ;
    }

}

