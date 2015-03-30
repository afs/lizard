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

import java.util.ArrayList ;
import java.util.List ;
import java.util.Map ;

import org.apache.jena.atlas.lib.DS ;

public class Conf2 {
    public static class ConfDeploy {
        public ConfCluster confCluster ;
        public Map<? extends Deployable, NetAddr> deployment ;    // Find from hostname.
    }
    
    public static class ConfDeployLocal {}
    
    public interface Deployable {}
    
    /** Static description configuration */
    public static class ConfCluster {
        public List<NetAddr> zkServer = new ArrayList<>() ; 
        public ConfDataset dataset ;
    }
    
    public static class ConfDataset {
        public List<ConfIndex> indexes = DS.list() ;
        public ConfNodeTable nodeTable ; 
    }
    
    public static class ConfIndex { 
        public String indexOrder ;
        public int readQuorum ;
        public int writeQuorum ;
    }
    
    public static class NetAddr {
        public final String hostname ;
        public final int port ;
        public NetAddr(String hostname, int port) {
            this.hostname = hostname ;
            this.port = port ;
        }
    }
    
    public static class ConfNodeTable { 
        public int readQuorum ;
        public int writeQuorum ;
    }
    
    public static class ConfZookeeper implements Deployable {
        public int port ;
        public String zkConfDir ;
    }

    public static class ConfQueryServer implements Deployable { 
    }
    
    public static class ConfIndexReplica implements Deployable { 
    }
    
    public static class ConfNodeTableReplica implements Deployable { 
    }
}

