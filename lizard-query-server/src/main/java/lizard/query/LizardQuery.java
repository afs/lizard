/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lizard.query;

import org.apache.jena.engine.Quack ;
import org.apache.jena.engine.Quack2 ;

public class LizardQuery {
    
    private static boolean initialized = false ;
    static { initialization1() ; }
    
    private static synchronized void initialization1()
    {
        // Called at start.
        if ( initialized )
            return ;
        initialized = true ;
        // query engine.
        Quack2.init() ;
        // This would be global and all TDB. 
        //Quack.setOpExecutorFactory(null) ;
    }
    
    /** called when Fuseki integrates Lizard */
    public static void init() { } 
}

