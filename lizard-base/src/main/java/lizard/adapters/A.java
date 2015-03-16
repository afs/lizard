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

import com.hp.hpl.jena.tdb.sys.Names ;

import org.seaborne.dboe.base.file.FileSet ;
import org.seaborne.dboe.base.file.Location ;
import org.seaborne.dboe.base.record.Record ;
import org.seaborne.dboe.base.record.RecordFactory ;

public class A {
    public static FileSet convert(com.hp.hpl.jena.tdb.base.file.FileSet fileSet) {
        Location loc = convert(fileSet.getLocation()) ;
        return new FileSet(loc, fileSet.getBasename()) ;
    }
    
    public static Location convert(com.hp.hpl.jena.tdb.base.file.Location location) {
        if ( location.isMemUnique() )
            // Call once.
            return Location.mem() ;
        if ( location.isMem() ) {
            String x = location.getDirectoryPath() ;
            x = x.substring(Names.memName.length()+1) ;
            return Location.mem(x) ;
        }
        return Location.create(location.getDirectoryPath()) ;
    }
    
    public static com.hp.hpl.jena.tdb.base.record.Record convertToTDB(com.hp.hpl.jena.tdb.base.record.RecordFactory tdbFactory, Record r) {
        if ( r == null )
            return null ;
        return tdbFactory.create(r.getKey(), r.getValue()) ;
    }
    
    public static Record convertToMantis(RecordFactory dboeFactory, com.hp.hpl.jena.tdb.base.record.Record r) {
        if ( r == null )
            return null ;
        return dboeFactory.create(r.getKey(), r.getValue()) ;
    }


}

