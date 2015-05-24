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

package lizard.conf.build;

import lizard.cluster.Cluster ;
import lizard.cluster.Platform ;
import lizard.conf.ConfCluster ;
import lizard.conf.NetHost ;
import lizard.query.LzDataset ;

import org.apache.jena.query.Dataset ;
import org.seaborne.dboe.base.file.Location ;
import org.seaborne.tdb2.setup.StoreParams ;

public class LzDeploy {
    
    /** Deploy the server-side of a configuration here */
    public static void deployServers(ConfCluster confCluster, NetHost here) {
        String zkConnect = LzBuildZk.zookeeper(confCluster, here) ;
        Cluster.createSystem(zkConnect);
        Location baseLocation = null ;
        
        if ( confCluster.fileroot == null )
            baseLocation = Location.mem() ;
        else
            baseLocation = Location.create(confCluster.fileroot) ; 

        Platform platform = new Platform() ;
        StoreParams params = StoreParams.getDftStoreParams() ; 
        
        // Each server has it's own journal - it'll be part of the distributed transaction.
        LzBuilderNodeServer.build(platform, baseLocation, params, confCluster, here);
        LzBuilderIndexServer.build(platform, baseLocation, params, confCluster, here);
        
        platform.start(); 

    }
    
    public static Dataset deployDataset(ConfCluster confCluster, NetHost here) {
        Location baseLocation = null ;
        if ( confCluster.fileroot == null )
            baseLocation = Location.mem() ;
        else
            baseLocation = Location.create(confCluster.fileroot) ; 
        Location locationQueryServer = baseLocation.getSubLocation("query") ;
        LzDataset lzdsg = LzBuilderDataset.build(confCluster, locationQueryServer) ;
        if ( lzdsg == null )
            return null ;
        return LzBuilderDataset.dataset(lzdsg) ;
    }
}

