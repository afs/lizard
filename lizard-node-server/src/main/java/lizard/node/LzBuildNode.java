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

package lizard.node;

import com.hp.hpl.jena.tdb.base.file.FileSet ;
import com.hp.hpl.jena.tdb.base.file.Location ;
import com.hp.hpl.jena.tdb.base.objectfile.ObjectFile ;
import com.hp.hpl.jena.tdb.base.record.RecordFactory ;
import com.hp.hpl.jena.tdb.index.Index ;
import com.hp.hpl.jena.tdb.setup.BuilderStdDB ;
import com.hp.hpl.jena.tdb.setup.NodeTableBuilder ;
import com.hp.hpl.jena.tdb.setup.ObjectFileBuilder ;
import com.hp.hpl.jena.tdb.setup.StoreParams ;
import com.hp.hpl.jena.tdb.store.nodetable.NodeTable ;
import com.hp.hpl.jena.tdb.store.nodetable.NodeTableCache ;
import com.hp.hpl.jena.tdb.store.nodetable.NodeTableInline ;
import com.hp.hpl.jena.tdb.store.nodetable.NodeTableNative ;
import com.hp.hpl.jena.tdb.sys.Names ;
import com.hp.hpl.jena.tdb.sys.SystemTDB ;

import org.apache.jena.atlas.lib.NotImplemented ;

public class LzBuildNode {
    //private static ObjectFileBuilder objectFileBuilder          = new BuilderStdDB.ObjectFileBuilderStd() ;
    //private static NodeTableBuilder nodeTableBuilder       = new BuilderStdDB.NodeTableBuilderStd(indexBuilder, objectFileBuilder) ;    
    
    public static NodeTable createNodeTable(Location location) {
        FileSet fsNodeToId = new FileSet(location, Names.indexNode2Id) ;
        FileSet fsId2Node = new FileSet(location, Names.indexId2Node) ;
        StoreParams params2 = StoreParams.builder()
            .node2NodeIdCacheSize(-1)
            .nodeId2NodeCacheSize(-1)
            .nodeMissCacheSize(-1)
            .build() ;
        
//        NodeTable nt = nodeTableBuilder.buildNodeTable(fsNodeToId, fsId2Node, params2) ;
//        return nt ;
        RecordFactory recordFactory = new RecordFactory(SystemTDB.LenNodeHash, SystemTDB.SizeOfNodeId) ;
        Index idx = LzBuild.foo(fsNodeToId, recordFactory, params2) ;
        
//        Index idx = indexBuilder.buildIndex(fsIndex, recordFactory, params) ;
//        ObjectFile objectFile = objectFileBuilder.buildObjectFile(fsObjectFile, Names.extNodeData) ;
        
        ObjectFile objectFile = null ;
        if ( true ) throw new NotImplemented("LzBuildNode") ;
        NodeTable nodeTable = new NodeTableNative(idx, objectFile) ;
        nodeTable = NodeTableCache.create(nodeTable, 
                                          params2.getNode2NodeIdCacheSize(),
                                          params2.getNodeId2NodeCacheSize(),
                                          params2.getNodeMissCacheSize()) ;
        nodeTable = NodeTableInline.create(nodeTable) ;
        return nodeTable ;
    }
