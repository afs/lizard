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

import java.io.PrintStream ;
import java.util.Collection ;
import java.util.List ;
import java.util.stream.Collectors ;

import lizard.conf.ConfigLib ;
import lizard.conf.Configuration ;
import lizard.conf.dataset.LzDatasetDesc ;
import lizard.conf.index.IndexServer ;
import lizard.conf.node.NodeServer ;
import lizard.system.LzLib ;

import org.apache.jena.rdf.model.Model ;

/** deployment descriptor */
public class Deployment {
    public final Collection<IndexServer> indexServers ;
    public final Collection<NodeServer> nodeServers ;
    public final LzDatasetDesc datasetDesc ;
    
    public static Deployment parse(Configuration config, String deploymentFile) {
        Model model = LzLib.readAll(deploymentFile) ;
        
        List<NodeServer> nodeServers = ConfigLib.dataServers(model, ":NodeServer").values().stream()
            .map(ds -> { return config.getConfNode().findNodeServer(ds.resource);})
            .collect(Collectors.toList()) ;
        List<IndexServer> indexServers = ConfigLib.dataServers(model, ":IndexServer").values().stream()
            .map(ds -> { return config.getConfIndex().findIndexServer(ds.resource);})
            .collect(Collectors.toList()) ;
        
        // ConfigLib.zkServers
        LzDatasetDesc desc = config.getConfDataset().descDataset() ;
        return new Deployment(indexServers, nodeServers, desc) ;
    }
    
    public Deployment(Collection<IndexServer> indexServers, Collection<NodeServer> nodeServers, LzDatasetDesc datasetDesc) {
        this.indexServers = indexServers ;
        this.nodeServers = nodeServers ;
        this.datasetDesc = datasetDesc ;
    }
    
    public void print() {
        print(System.out) ;
    }
        
    public void print(PrintStream ps) {
        ps.println("Deploy: Index Servers:");
        indexServers.forEach(x->ps.printf("    <%s>:\"%s\"\n",x.resource, x.name)) ;
        ps.println("Deploy: Node Servers:");
        nodeServers.forEach(x->ps.printf("    <%s>:\"%s\"\n",x.resource, x.name)) ;
        if ( datasetDesc != null ) {
            ps.println("Deploy: Dataset");
            ps.printf("    <%s>:\"%s\"\n", datasetDesc.resource, datasetDesc.name) ;
        }
    }
}
