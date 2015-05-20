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

package lizard.cluster;

import java.util.LinkedHashSet ;
import java.util.Set ;

import lizard.system.Component ;
import lizard.system.ComponentBase ;
import lizard.system.LifeCycle ;
import org.seaborne.dboe.base.file.Location ;
import org.seaborne.dboe.transaction.txn.journal.Journal ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

/** Set of server components on one machine (not query engines) */
public class Platform implements LifeCycle {
    
    private static Logger log = LoggerFactory.getLogger(Platform.class) ;
    
    private final Journal journal ;
    private Set<Component> components = new LinkedHashSet<>() ;
    private ComponentBase lifecycle = new ComponentBase() ;
    
    public Platform(Location baseLocation) {
        if ( baseLocation.isMem() )
            log.warn("** In-memory journal") ;
        this.journal = Journal.create(baseLocation) ;
    }
    
    public Journal getJounrnal() {
        return journal ;
    }
    
    @Override
    public void start() {
        components.forEach(s->s.start()) ;
        lifecycle.start() ;
    }

    @Override
    public void stop() {
        components.forEach(s->s.stop()) ;
        lifecycle.stop() ;
    }

    @Override
    public boolean isRunning()  { return lifecycle.isRunning() ; }

    @Override
    public boolean hasFailed()  { return lifecycle.hasFailed() ; }

    @Override
    public void setStatus(Status status) {
        for ( Component s : components )
            s.setStatus(status);
        lifecycle.setStatus(status) ;
    }

    public void add(Component server) {
        components.add(server) ;
    }
}
