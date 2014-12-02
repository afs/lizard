/*
 *  Copyright 2013, 2014 Andy Seaborne
 *
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
 */

package lizard.cluster;

import lizard.system.LizardException ;

/** Operations on the cluster.
 * 
 * There is one cluster configuration reprenting the current cluster
 * and this class provides operations on the current cluster via a global
 * singleton {@linkplain Cluster}.
 * 
 * There may be other "cluster" as changes are prepared but only one
 * is active for query and update, as defined by this class.
 */
public class ClusterCtl {
    private static Cluster current = null ;

    public static Cluster get() { 
        if ( current == null )
            throw new LizardException("No cluster set") ;
        return current ;
    }
    
    public static void set(Cluster cluster) { 
        current = cluster ;
    }
    
}
