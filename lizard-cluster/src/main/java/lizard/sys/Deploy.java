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

package lizard.sys;

import org.apache.jena.tdb.store.nodetable.NodeTable ;
import org.apache.jena.tdb.store.tupletable.TupleIndex ;

import lizard.adapters.A ;
import lizard.cluster.Platform ;
import lizard.conf.Config ;
import lizard.conf.Configuration ;
import lizard.conf.LzBuildDBOE ;
import lizard.conf.dataset.ConfigLizardDataset ;
import lizard.conf.index.IndexServer ;
import lizard.conf.node.NodeServer ;
import lizard.index.TServerIndex ;
import lizard.node.TServerNode ;

import org.apache.jena.atlas.logging.FmtLog ;
import org.seaborne.dboe.base.file.Location ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

public class Deploy {
    public static Logger log = LoggerFactory.getLogger("Deploy") ;
    /** Deploy a deployment */
    public static Platform deploy(Deployment deployment) {
        Platform platform = new Platform() ;
        deployment.nodeServers.stream().forEach(ns -> {
            log.info("Build N: " + ns.resource) ;
            buildNodeServer(ns, platform) ;
        }) ;

        deployment.indexServers.forEach(idx -> {
            log.info("Build I: " + idx.resource) ;
            buildIndexServer(idx, platform) ;
        }) ;

        Location location = Location.mem();
        ConfigLizardDataset.buildDataset(location, deployment.datasetDesc) ;
        
        platform.start() ;
        return platform ;
    }

    /** Deploy data servers */
    public static Deployment deployServers(Configuration config, String deploymentFile) {
        Deployment deployment = Deployment.parse(config, deploymentFile) ;
        //System.out.println(deployment.indexServers) ;
        //System.out.println(deployment.nodeServers) ;
        deploy(deployment) ;
        return deployment ;
    }

    /** Build index server, locally */ 
    private static void buildIndexServer(IndexServer idxSvc, Platform platform) {
        // XXX Check this code isn't duplicated somewhere.
        Location location = Location.create(idxSvc.data) ;
        FmtLog.info(Config.logConf, "BuildIndexServer: %s %s", idxSvc.port, location) ;
        String indexOrder = idxSvc.indexService.indexOrder ;
        TupleIndex index = LzBuildDBOE.createTupleIndex(A.convert(location), indexOrder, "Idx"+indexOrder) ;
        TServerIndex serverIdx = TServerIndex.create(idxSvc.port, index) ;
        platform.add(serverIdx) ;
    }
    
    /** Build noder server, locally */
    private static void buildNodeServer(NodeServer ns, Platform platform) {
        Location location = Location.create(ns.data) ;
        FmtLog.info(Config.logConf, "buildNodeServer: %s %s", ns.port, location) ;
        NodeTable nt = LzBuildDBOE.createNodeTable(A.convert(location)) ;
        TServerNode serverNode = TServerNode.create(ns.port, nt) ;
        platform.add(serverNode) ;
    }
}
