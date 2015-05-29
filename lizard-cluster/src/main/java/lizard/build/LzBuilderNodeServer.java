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

package lizard.build;

import lizard.cluster.Platform ;
import lizard.conf.ConfCluster ;
import lizard.conf.ConfNodeTableElement ;
import lizard.conf.Config ;
import lizard.conf.NetHost ;
import lizard.node.TServerNode ;

import org.apache.jena.atlas.logging.FmtLog ;
import org.seaborne.dboe.base.file.Location ;
import org.seaborne.dboe.transaction.txn.TransactionCoordinator ;
import org.seaborne.dboe.transaction.txn.TransactionalBase ;
import org.seaborne.dboe.transaction.txn.TransactionalSystem ;
import org.seaborne.tdb2.setup.TDBBuilder ;
import org.seaborne.tdb2.setup.StoreParams ;
import org.seaborne.tdb2.store.nodetable.NodeTable ;
import org.slf4j.Logger ;

public class LzBuilderNodeServer {
    private static Logger logConf = Config.logConf ;    
    
    public static void build(Platform platform, Location baseLocation, StoreParams params, ConfCluster confCluster, NetHost here) {
        confCluster.eltsNodeTable.stream()
            .filter(x -> x.netAddr.sameHost(here))
            .forEach(x -> {
                buildNodeServer(platform, baseLocation, params, x) ;
            }) ;
    }

    public static TServerNode buildNodeServer(Platform platform, Location baseLocation, StoreParams params, ConfNodeTableElement x) {
        Location location = baseLocation.getSubLocation(x.data) ;
        int port = x.netAddr.port ;
        FmtLog.info(logConf, "buildNodeServer: %s %s", port, location) ;
        TransactionCoordinator coord = TDBBuilder.buildTransactionCoordinator(location) ;
        TDBBuilder builder = TDBBuilder.create(coord, location, params) ;
        NodeTable nt = builder.buildNodeTable(params.getNodeTableBaseName()) ;
        TransactionalSystem txnSystem = new TransactionalBase(x.toString(), coord) ;
        TServerNode serverNode = TServerNode.create(txnSystem, port, nt) ;
        platform.add(serverNode) ;
        return serverNode ;
    }
}
