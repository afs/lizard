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

package lz_dev;

import org.apache.jena.atlas.logging.LogCtl ;
import org.seaborne.dboe.transaction.TransLogger ;
import org.seaborne.dboe.transaction.Transactional ;
import org.seaborne.dboe.transaction.Txn ;
import org.seaborne.dboe.transaction.txn.TransactionCoordinator ;
import org.seaborne.dboe.transaction.txn.TransactionalComponent ;
import org.seaborne.tdb2.TDBFactory ;
import org.seaborne.tdb2.store.DatasetGraphTDB ;

public class Scratch {

    static { LogCtl.setCmdLogging(); }
    public static void main(String[] args) {
        DatasetGraphTDB dsg = (DatasetGraphTDB)TDBFactory.createDatasetGraph() ;
        Transactional t = dsg ;
        TransactionCoordinator coord = dsg.getTxnSystem().getTxnMgr() ;
        TransactionalComponent foobar = new TransLogger() ;
        coord.add(foobar) ;
        //coord.remove(foobar) ;
        
        
        Txn.executeWrite(dsg, ()-> {
            System.out.println("write") ;
        }) ;
        
        
    }

}

