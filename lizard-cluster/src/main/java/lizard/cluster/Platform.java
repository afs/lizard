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

import java.util.LinkedHashSet ;
import java.util.Set ;

import lizard.system.Component ;
import lizard.system.ComponentBase ;
import lizard.system.LifeCycle ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

public class Platform implements LifeCycle {
    
    private static Logger log = LoggerFactory.getLogger(Platform.class) ;
    
    private Set<Component> components = new LinkedHashSet<>() ;
    private ComponentBase lifecycle = new ComponentBase() ;
    
    @Override
    public void start() {
        for ( Component s : components ) {
            s.start() ;
        }
        lifecycle.start() ;
    }

    @Override
    public void stop() {
        for ( Component s : components )
            s.stop() ;
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
