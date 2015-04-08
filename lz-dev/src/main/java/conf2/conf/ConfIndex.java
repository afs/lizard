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

package conf2.conf;

import org.apache.jena.atlas.io.IndentedWriter ;
import org.apache.jena.atlas.lib.ColumnMap ;

public class ConfIndex { 
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

    public void print(IndentedWriter out) {
        out.print(indexOrder) ;
    }
}
