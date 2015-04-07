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

import java.util.ArrayList ;
import java.util.Collections ;
import java.util.List ;

/** Static description configuration */
public class ConfCluster {
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
