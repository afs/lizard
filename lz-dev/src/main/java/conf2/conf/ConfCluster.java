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

import java.io.StringWriter ;
import java.util.ArrayList ;
import java.util.Collections ;
import java.util.List ;

import conf2.LzConfParser ;

import org.apache.jena.atlas.io.IndentedWriter ;

/** Static description configuration */
public class ConfCluster {
    public final List<ConfZookeeper> zkServer = new ArrayList<>() ;
    public final ConfDataset   dataset ;
    public final List<ConfNodeTableElement> eltsNodeTable = new ArrayList<>() ;
    public final List<ConfIndexElement> eltsIndex = new ArrayList<>() ;
    public String fileroot = null ;
    
    public ConfCluster(ConfDataset dataset) {
        this.dataset = dataset ;
    }

    public void addIndexElements(ConfIndexElement...idxElts) {
        Collections.addAll(eltsIndex, idxElts) ;
    }

    public void addNodeElements(ConfNodeTableElement...ntElts) {
        Collections.addAll(eltsNodeTable, ntElts) ;
    }
    
    @Override
    public String toString() {
        StringWriter sw = new StringWriter() ;
        IndentedWriter out = new IndentedWriter(sw) { } ;
        print(out) ;
        out.flush() ;
        return sw.toString() ; 
    }

    public void print(IndentedWriter out) {
        // YAMP.printfield
        out.print(LzConfParser.objCluster) ;
        out.println(":") ;
       
        if ( fileroot != null ) {
            out.incIndent();
            out.print(LzConfParser.fFileroot) ;
            out.print(":") ;
            out.println(fileroot) ;
            out.decIndent();
        }
        
        out.incIndent();
        out.print(LzConfParser.fZookeeper.substring(1));
        out.print(": ") ;
        out.print("[ ") ;
        zkServer.stream().forEach(zk -> { zk.print(out); out.print(", "); }) ;
        out.print("]") ;
        out.decIndent();
        out.println() ;
        
        out.print(LzConfParser.objDataset);
        out.println(":") ;
        out.incIndent();
        out.print("indexes: [") ;
        eltsIndex.stream().map(elt -> elt.conf).distinct().forEach(idx -> {
            idx.print(out);
            out.print(", ");
        }) ;
        out.print("]") ;
        out.println() ;
        
        out.print("nodes: ") ;
        out.print("[ ") ;
        eltsNodeTable.stream().map(elt -> elt.conf).distinct().forEach(nt -> {
            nt.print(out);
            out.print(", ");
        }) ;
        out.print("]") ;
        
        
        out.decIndent();
        out.println() ;
        
    }
    
}
