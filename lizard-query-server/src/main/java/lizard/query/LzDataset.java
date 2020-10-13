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

package lizard.query;

import java.util.List ;

import lizard.system.Component ;
import lizard.system.LifeCycle ;
import org.apache.jena.dboe.transaction.txn.TransactionCoordinator ;
import org.apache.jena.tdb2.store.DatasetGraphTDB ;

/** The query platform for Lizard - a dataset and the local components (clients to index and nodes) */ 
public class LzDataset implements LifeCycle {
    private DatasetGraphTDB dsg ;
    private List<Component> components ;
    private boolean started = false ;
    private TransactionCoordinator txnCoord ;

    public LzDataset(TransactionCoordinator txnCoord, DatasetGraphTDB dsg, List<Component> components) {
        this.txnCoord = txnCoord ;
        this.dsg = dsg ;
        this.components = components ;
    }

    public DatasetGraphTDB getDataset() { return dsg ; }
    
    public TransactionCoordinator getTxnMgr() {
        return txnCoord ;
    }

    public List<Component> getStartables() {
        return components ;
    }

    public List<Component> getComponents() {
        return this.components ;
    }

    @Override
    public void start() {
        txnCoord.start();
        components.stream().forEach(s -> s.start() );
        started = true ;
    }

    @Override
    public void stop() {
        components.stream().forEach(s -> s.stop());
        started = false ;
        txnCoord.shutdown();
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
