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

package lizard.conf;

import java.util.ArrayList;
import java.util.List ;

// Here : cf ConfCluster which is cluster wide.
public class ConfDeploy {
    public ConfCluster confCluster = null ;
    public ConfZookeeper localZk = null ;
    public List<ConfNodeTableElement> ntReplicas = new ArrayList<>();
    public List<ConfIndexElement> idxReplicas  = new ArrayList<>();
    public ConfDataset confDataset = null ;
}
