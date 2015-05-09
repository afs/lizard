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

package lizard.adapters;

import java.util.Iterator ;

import org.apache.jena.atlas.iterator.Iter ;

import org.apache.jena.tdb.base.record.Record ;
import org.apache.jena.tdb.base.record.RecordFactory ;
import org.apache.jena.tdb.index.Index ;
// And don't import any "dboe"

public class AdapterIndex implements Index {
    private final org.seaborne.dboe.index.Index rIndex ;
    private final org.seaborne.dboe.base.record.RecordFactory dboeFactory ;
    private final RecordFactory tdbFactory ;

    public AdapterIndex(org.seaborne.dboe.index.Index rangeIndex) {
        rIndex = rangeIndex ;
        dboeFactory = rIndex.getRecordFactory() ;
        tdbFactory = new RecordFactory(dboeFactory.keyLength(), dboeFactory.valueLength()) ;
    }
    
    public org.seaborne.dboe.index.Index getUnderlyingIndex()   { return rIndex ; }
    
    private Record convertToTDB(org.seaborne.dboe.base.record.Record r) {
        return A.convertToTDB(tdbFactory, r) ;
    }
    
    private org.seaborne.dboe.base.record.Record convertToMantis(Record r) {
        return A.convertToMantis(dboeFactory, r) ;
    }

    @Override
    public Record find(Record record)
    { return convertToTDB(rIndex.find(convertToMantis(record))) ; }
    
    @Override
    public boolean contains(Record record)
    { return rIndex.contains(convertToMantis(record)) ; }
    
    @Override
    public boolean add(Record record)
    { return rIndex.insert(convertToMantis(record)) ; }
    
    @Override
    public boolean delete(Record record)
    { return rIndex.delete(convertToMantis(record)) ; }
    
    @Override
    public Iterator<Record> iterator()
    { return convertIterToTDB(rIndex.iterator()) ; }
    
    private Iterator<Record> convertIterToTDB(Iterator<org.seaborne.dboe.base.record.Record> iter) {
        return Iter.map(iter, r->convertToTDB(r)) ;
    }
    
    @Override
    public boolean isEmpty()
    { return rIndex.isEmpty() ; }
    
    @Override
    public void clear()
    { rIndex.clear() ; }
    
    @Override
    public void sync()
    { rIndex.sync() ; }
    
    @Override
    public void close()
    { rIndex.close() ; }

    public org.seaborne.dboe.index.Index getWrapped()
    { return rIndex ; }
    
    @Override
    public RecordFactory getRecordFactory()
    { return tdbFactory ; }

    @Override
    public void check()
    { rIndex.check() ; }

    @Override
    public long size()
    { return rIndex.size() ; }
}

