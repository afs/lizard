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

package lizard.conf.dataset;

import java.util.List ;

import lizard.system.Component ;
import lizard.system.LifeCycle ;

import com.hp.hpl.jena.sparql.core.DatasetGraph ;

public class LizardDataset implements LifeCycle {
    private DatasetGraph dsg ;
    private List<Component> startables ;
    private boolean started = false ;

    public LizardDataset(DatasetGraph dsg, List<Component> startables) {
        this.dsg = dsg ;
        this.startables = startables ;
    }

    public DatasetGraph getDataset() { return dsg ; }
    
    @Override
    public void start() {
        startables.stream().forEach(s -> s.start());
        started = true ;
    }

    @Override
    public void stop() {
        startables.stream().forEach(s -> s.stop());
        started = false ;
    }

    @Override
    public boolean isRunning() {
        return started ;
    }

    @Override
    public boolean hasFailed() {
        return false ;
    }

    @Override
    public void setStatus(Status status) {}
}
