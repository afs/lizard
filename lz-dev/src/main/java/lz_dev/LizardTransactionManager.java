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

import java.util.ArrayList ;
import java.util.List ;

import lizard.system.LzTxnId ;

import com.hp.hpl.jena.query.ReadWrite ;

import org.seaborne.dboe.transaction.Transactional ;

// NOT CURRENTLY IN-PLAN
/** Lizard cluster-wide controller */
public class LizardTransactionManager implements Transactional {
    // This is close to, but not quite the same, as other compounds structures. 
    List<Transactional> components = new ArrayList<>() ;
//    ComponentGroup components = new ComponentGroup() ;
    
    //Transaction transaction = new Transaction(this, txnId, readWrite, dataVersion, sysTransList) ;
    
    private LizardTransactionManager() {
        
    }
    
    // current read txn.
    

    public Transactional getReadHandle() { return null ; }
    
    private ThreadLocal<Long> current ;
    
    // Wrap each component in a component that mamanges it local-remote-ness.
    // Then a std TransactionCoordinator 
    
    @Override
    public void begin(ReadWrite readWrite) {
        
        LzTxnId x = LzTxnId.alloc() ;
        
        // Grab the system wide lock.
        components.forEach(c->c.begin(readWrite));
        // Release the system wide lock.
        //public Transaction(TransactionCoordinator txnMgr, TxnId txnId, ReadWrite readWrite, long dataEpoch, List<SysTrans> components) {
    }

    @Override
    public void commit() {}

    @Override
    public void abort() {}

    @Override
    public void end() {}
    
    
}

