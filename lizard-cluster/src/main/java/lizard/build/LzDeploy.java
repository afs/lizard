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

import lizard.cluster.Cluster ;
import lizard.cluster.Platform ;
import lizard.conf.* ;
import lizard.query.LzDataset ;
import lizard.system.LizardException ;
import org.apache.jena.dboe.base.file.Location ;
import org.apache.jena.query.Dataset ;
import org.apache.jena.tdb2.setup.StoreParams ;
import org.slf4j.Logger ;

public class LzDeploy {
    
    static Logger log = Config.logConf ;
    
    public static void deployZookeeper(ConfCluster confCluster) {
        if ( confCluster.zkServer.isEmpty() )
            throw new LizardException("No zookeeper server described") ;

        ConfZookeeper confZK = confCluster.zkServer.get(0) ;
        // Connect
        Cluster.createSystem(confZK.connectString());
    }

    /** Deploy the server-side of a configuration here */
    public static void deployServers(ConfCluster confCluster, String vnode) {
        deployZookeeper(confCluster) ;
        String clusterString = vnode ;
        Cluster.addMember(clusterString) ;
        Location baseLocation = null ;
        
        VNode here = confCluster.placements.get(vnode) ;
        if ( here.localFileRoot == null )
            baseLocation = Location.mem() ;
        else
            baseLocation = Location.create(here.localFileRoot) ; 

        Platform platform = new Platform() ;
        StoreParams params = StoreParams.getDftStoreParams() ; 
        
        // NetHost for the local vNode.
        NetHost hereHost = NetHost.create(vnode) ;
        // Each server has it's own journal - it'll be part of the distributed transaction.
        LzBuilderNodeServer.build(platform, baseLocation, params, confCluster, hereHost);
        LzBuilderIndexServer.build(platform, baseLocation, params, confCluster, hereHost);
        platform.start(); 
    }
    
    public static LzDataset deployLzDataset(ConfCluster confCluster, String vnode) {
        Location baseLocation = null ;
//        if ( confCluster.fileroot == null )
//            baseLocation = Location.mem() ;
//        else
//            baseLocation = Location.create(confCluster.fileroot) ;
        // XXX Currently no local state.
        baseLocation = Location.mem() ;
        Location locationQueryServer = baseLocation.getSubLocation("query") ;
        LzDataset lzdsg = LzBuilderDataset.build(confCluster, locationQueryServer, vnode) ;
        return lzdsg ;
    }
    
    public static Dataset deployDataset(ConfCluster confCluster, String vnode) {
        LzDataset lzdsg = deployLzDataset(confCluster, vnode) ;
        if ( lzdsg == null )
            return null ;
        return LzBuilderDataset.dataset(lzdsg) ;
    }

    public static VNode findVNode(ConfCluster confCluster, String vnodeName) {
        return confCluster.placements.get(vnodeName) ;
    }
    
}

