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


public class ConfZookeeper implements Deployable {
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

    public void print(IndentedWriter out) {
        out.print("zookeeper:"+port) ;
    }
}
