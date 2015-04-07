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

import java.util.* ;

import conf2.Conf2.NetHost ;
import org.apache.jena.atlas.lib.ColumnMap ;
import org.apache.jena.atlas.lib.DS ;

public class Conf2 {
    // Here : cf ConfCluster which is cluster wide.
    public static class ConfDeploy {
        public ConfCluster confCluster ;
        public ConfZookeeper localZk = null ;
        public List<ConfNodeTableElement> ntReplicas = DS.list() ;
        public List<ConfIndexElement> idxReplicas  = DS.list() ;
        public ConfQueryServer queryServer = null ;
    }
    
    public static class ConfDeployLocal {}
    
    public interface Deployable {}
    
    /** Static description configuration */
    public static class ConfCluster {
        public final List<ConfZookeeper> zkServer = new ArrayList<>() ;
        public final ConfDataset   dataset ;
        public final List<ConfNodeTableElement> eltsNodeTable = new ArrayList<>() ;
        public final List<ConfIndexElement> eltsIndex = new ArrayList<>() ;
        
        public ConfCluster(ConfDataset dataset) {
            this.dataset = dataset ;
        }

        public void addIndexElements(ConfIndexElement...idxElts) {
            Collections.addAll(eltsIndex, idxElts) ;
        }

        public void addNodeElements(ConfNodeTableElement...ntElts) {
            Collections.addAll(eltsNodeTable, ntElts) ;
        }
    }
    
    public static class ConfDataset {
        public final List<ConfIndex> indexes = DS.list() ;
        public final ConfNodeTable nodeTable ;
        
        public ConfDataset(ConfNodeTable nodeTable, ConfIndex... indexes) {
            this.nodeTable = nodeTable ;
            this.indexes.addAll(Arrays.asList(indexes)) ;
        }
    }
    
    public static class ConfIndex { 
        public final String indexOrder ;
        public final ColumnMap cmap ;
        public final int readQuorum ;
        public final int writeQuorum ;
        
        public ConfIndex(ColumnMap cmap, String indexOrder, int readQuorum, int writeQuorum) {
            super() ;
            this.indexOrder = indexOrder ;
            this.readQuorum = readQuorum ;
            this.writeQuorum = writeQuorum ;
            this.cmap = cmap ;
        }
    }
    
    // Typed string.
    public static class NetHost {
        public final String hostname ;

        public static NetHost create(String hostname) {
            return new NetHost(hostname) ;
        }

        public NetHost(String hostname) {
            this.hostname = hostname ;
        }

        @Override
        public int hashCode() {
            final int prime = 31 ;
            int result = 1 ;
            result = prime * result + ((hostname == null) ? 0 : hostname.hashCode()) ;
            return result ;
        }

        @Override
        public boolean equals(Object obj) {
            if ( this == obj )
                return true ;
            if ( obj == null )
                return false ;
            if ( getClass() != obj.getClass() )
                return false ;
            NetHost other = (NetHost)obj ;
            if ( hostname == null ) {
                if ( other.hostname != null )
                    return false ;
            } else if ( !hostname.equals(other.hostname) )
                return false ;
            return true ;
        }
    }
    
    public static class NetAddr {

        public static NetAddr create(String hostname, int port) {
            return new NetAddr(hostname, port) ;
        }
        public final String hostname ;
        public final int port ;
        public NetAddr(String hostname, int port) {
            Objects.requireNonNull(hostname, "Hostname is null") ;
            this.hostname = hostname ;
            this.port = port ;
        }
        @Override
        public int hashCode() {
            final int prime = 31 ;
            int result = 1 ;
            result = prime * result + ((hostname == null) ? 0 : hostname.hashCode()) ;
            result = prime * result + port ;
            return result ;
        }
        @Override
        public boolean equals(Object obj) {
            if ( this == obj )
                return true ;
            if ( obj == null )
                return false ;
            if ( getClass() != obj.getClass() )
                return false ;
            NetAddr other = (NetAddr)obj ;
            if ( hostname == null ) {
                if ( other.hostname != null )
                    return false ;
            } else if ( !hostname.equals(other.hostname) )
                return false ;
            if ( port != other.port )
                return false ;
            return true ;
        }
        public boolean sameHost(NetHost here) {
            return sameHost(here.hostname) ;
        }

        public boolean sameHost(String here) {
            return Objects.equals(hostname, here) ;
        }    
    }
    
    public static class ConfNodeTable { 
        public final int readQuorum ;
        public final int writeQuorum ;
        public ConfNodeTable(int readQuorum, int writeQuorum) {
            this.readQuorum = readQuorum ;
            this.writeQuorum = writeQuorum ;
        }
    }
    
    public static class ConfZookeeper implements Deployable {
        public final int port ;
        public final String zkConfDir ;
        
        public static ConfZookeeper create(int port, String zkConfDir) {
            return new ConfZookeeper(port, zkConfDir) ; 
        }
        
        public ConfZookeeper createEphemeral(int port) {
            return new ConfZookeeper(port, null) ; 
        }

        private ConfZookeeper(int port, String zkConfDir) {
            super() ;
            this.port = port ;
            this.zkConfDir = zkConfDir ;
        }
        
        public boolean isEphemeral() {
            return zkConfDir == null ;
        }
    }

    public static class ConfQueryServer implements Deployable { 
    }
    
    public static class ConfElement<X> {
        public final String name ;
        public final X conf ;
        public final NetAddr netAddr ; 
        public ConfElement(String name, X conf, NetAddr netAddr) {
            this.name = name ;
            this.conf = conf ;
            this.netAddr = netAddr ;
        }
    }
    
    public static class ConfIndexElement extends ConfElement<ConfIndex> {
        public ConfIndexElement(String name, ConfIndex index, NetAddr netAddr) {
            super(name, index, netAddr) ;
        }
    }
    
    public static class ConfNodeTableElement extends ConfElement<ConfNodeTable> {
        public ConfNodeTableElement(String name, ConfNodeTable nodetable, NetAddr netAddr) {
            super(name, nodetable, netAddr) ;
        }
    }
}

