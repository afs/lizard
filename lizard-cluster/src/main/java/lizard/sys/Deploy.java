/*
 *  Copyright 2014 Andy Seaborne
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

package lizard.sys;

import lizard.cluster.Platform ;
import lizard.conf.Configuration ;
import lizard.conf.LzBuild ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

public class Deploy {
    public static Logger log = LoggerFactory.getLogger("Deploy") ;
    /** Deploy a deployment */
    public static void deploy(Deployment deployment) {
        Platform platform = new Platform() ;
        deployment.nodeServers.stream().forEach(ns -> {
            log.info("Build N: " + ns.resource) ;
            LzBuild.buildNodeServer(ns, platform) ;
        }) ;

        deployment.indexServers.forEach(idx -> {
            log.info("Build I: " + idx.resource) ;
            LzBuild.buildIndexServer(idx, platform) ;
        }) ;

        platform.start() ;
    }

    /*8 Deploy data servers */
    public static void deplyServers(Configuration config, String deploymentFile) {
        Deployment deployment = Deployment.parse(config, deploymentFile) ;
        //System.out.println(deployment.indexServers) ;
        //System.out.println(deployment.nodeServers) ;
        deploy(deployment) ;
    }

}
