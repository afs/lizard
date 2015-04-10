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

package conf2.build;

import lizard.cluster.Cluster ;
import lizard.cluster.Platform ;
import lizard.query.LzDataset ;

import com.hp.hpl.jena.query.Dataset ;

import conf2.conf.ConfCluster ;
import conf2.conf.NetHost ;

import org.seaborne.dboe.base.file.Location ;

public class LzDeploy {
    
    /** Deploy a configuration here */
    public static Dataset deploy(ConfCluster confCluster, NetHost here) {
        String zkConnect = Lz2BuildZk.zookeeper(confCluster, here) ;
        Cluster.createSystem(zkConnect);
        Location baseLocation = null ;
        
        if ( confCluster.fileroot == null )
            baseLocation = Location.mem() ;
        else
            baseLocation = Location.create(confCluster.fileroot) ; 
            
        Platform platform = new Platform() ;
        Lz2BuilderNodeServer.build(platform, baseLocation, confCluster, here); 
        Lz2BuilderIndexServer.build(platform, baseLocation, confCluster, here);
        platform.start(); 

        Dataset ds = null ;

        // @@
        Location locationQueryServer = baseLocation.getSubLocation("query") ;
        LzDataset lzdsg = Lz2BuilderDataset.build(confCluster, locationQueryServer) ;

        lzdsg.getStartables().forEach(s -> {
            s.start();
        });
        ds = Lz2BuilderDataset.dataset(lzdsg, locationQueryServer) ;
        return ds ;
    }
}

