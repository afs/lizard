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
 
package lizard.api;
import org.apache.thrift.TException ;
import org.slf4j.Logger ;

import lizard.api.TLZ.NodeCtl ;

public abstract class NodeHandler implements NodeCtl.Iface {

    protected boolean active = true ;
    @Override
    public void ping() throws TException {
        checkActive() ;
        log().info("ping") ;
    }

    @Override
    public void stop() throws TException {
        active = false ;
        log().info("stop") ;
    }
    
    protected abstract Logger log() ;
    protected abstract Logger logtxn() ;
    protected abstract String getLabel() ;
    protected void checkActive() throws TException {
        if ( ! active )
            throw new TException("Not active") ;
    }

}

