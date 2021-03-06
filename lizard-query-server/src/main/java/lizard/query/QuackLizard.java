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

import org.apache.jena.sparql.engine.ExecutionContext ;
import org.apache.jena.sparql.engine.main.OpExecutor ;
import org.apache.jena.sparql.engine.main.OpExecutorFactory ;
import org.apache.jena.tdb2.store.DatasetGraphTDB ;
import org.seaborne.dboe.engine.Quack ;
import org.seaborne.dboe.engine.explain.Explain2 ;
import org.seaborne.dboe.engine.tdb.* ;
import org.seaborne.dboe.engine.tdb.OpExecutorQuackTDB.OpExecSetup ;

public class QuackLizard {

    private static OpExecSetup setupLizard = new OpExecSetup() {
        @Override
        public AccessorTDB initAccessor(DatasetGraphTDB dsgtdb) {
            return AccessorRemote.create(new StorageTDB(dsgtdb)) ;
        }

        @Override
        public Planner initPlanner(DatasetGraphTDB dsgtdb, AccessorTDB accessor) {
            return new PlannerPredObjList(accessor) ;
        }
    } ;
    
    public static final OpExecutorFactory factoryLizard = new OpExecutorFactory() {
        @Override
        public OpExecutor create(ExecutionContext execCxt) {
            Explain2.explain(Quack.quackExec, "Lizard") ;
            return new OpExecutorQuackTDB(execCxt, setupLizard) ;
        }
    } ;
}
