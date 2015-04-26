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

import org.apache.jena.tdb.store.nodetable.NodeTableNative ;

/** Version of NodeTableNative that exposes the index and object file. */
public class NodeTableDBOE extends NodeTableNative {
    public NodeTableDBOE(org.apache.jena.tdb.index.Index nodeToId, org.apache.jena.tdb.base.objectfile.ObjectFile objectFile) {
        super(nodeToId, objectFile) ;
    }

    public org.apache.jena.tdb.index.Index getIndex() {
        return super.nodeHashToId ;
    }

    public org.apache.jena.tdb.base.objectfile.ObjectFile getObjectFile() {
        return super.objects ;
    }

    public void set(org.apache.jena.tdb.index.Index nodeToId, org.apache.jena.tdb.base.objectfile.ObjectFile objectFile) {
        super.init(nodeToId, objectFile) ;
    }
}

