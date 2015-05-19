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

package lizard.index;

import java.util.Iterator ;

import org.apache.jena.atlas.lib.Tuple ;
import org.seaborne.dboe.engine.access.AccessData ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import org.seaborne.tdb2.store.NodeId ;

/** Adapter from TClientIndex to AccessData<NodeId> */

public class RemoteAccessData implements AccessData<NodeId> {
    
    // XXX This extra class from TupleIndexRenote to TClientIndex makes index
    // more complicted than node tables.  Maybe they are?
    static long counter = 0 ;
    static Logger log = LoggerFactory.getLogger(RemoteAccessData.class) ;
    
    private final TClientIndex client ;
    
    public RemoteAccessData(TClientIndex conn) {
        this.client = conn ;
    }
    
    @Override
    public Iterator<Tuple<NodeId>> accessTuples(Tuple<NodeId> pattern) {
        return client.find(pattern) ;
        
//        TLZ_TupleNodeId t = TLZlib.build(pattern) ;
//        long id = (++counter) ; 
//        List<Tuple<NodeId>> results = null ;
//        log.debug("RemoteAccessor.find - "+results.size()) ;
//        return results.iterator() ;
    }
}
