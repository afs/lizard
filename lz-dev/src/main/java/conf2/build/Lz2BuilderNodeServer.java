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

import lizard.adapters.A ;
import lizard.cluster.Platform ;
import lizard.conf.Config ;
import lizard.conf.LzBuildDBOE ;
import lizard.node.TServerNode ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.seaborne.dboe.base.file.Location ;
import org.slf4j.Logger ;

import com.hp.hpl.jena.tdb.store.nodetable.NodeTable ;

import conf2.conf.ConfCluster ;
import conf2.conf.ConfNodeTableElement ;
import conf2.conf.NetHost ;

public class Lz2BuilderNodeServer {
    private static Logger logConf = Config.logConf ;    
    
    public static void build(Platform platform, Location location, ConfCluster confCluster, NetHost here) {
        confCluster.eltsNodeTable.stream()
            .filter(x -> x.netAddr.sameHost(here))
            .forEach(x -> {
                buildNodeServer(platform, location, x) ;
            }) ;
    }

    private static TServerNode buildNodeServer(Platform platform, Location location, ConfNodeTableElement x) {
        Location loc = location.getSubLocation(x.data) ;
        int port = x.netAddr.port ;
        FmtLog.info(logConf, "buildNodeServer: %s %s", port, loc) ;
        NodeTable nt = LzBuildDBOE.createNodeTable(A.convert(loc)) ;
        TServerNode serverNode = TServerNode.create(port, nt) ;
        platform.add(serverNode) ;
        return serverNode ;
    }
}

