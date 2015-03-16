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

package lizard.conf;

import lizard.adapters.AdapterRangeIndex ;
import org.seaborne.dboe.base.file.FileSet ;
import org.seaborne.dboe.base.record.RecordFactory ;
import org.seaborne.dboe.index.IndexConst ;
import org.seaborne.dboe.index.RangeIndex ;

class AdapterIndexBuilder implements com.hp.hpl.jena.tdb.index.IndexBuilder {
    @Override
    public com.hp.hpl.jena.tdb.index.Index buildIndex(com.hp.hpl.jena.tdb.base.file.FileSet fileSet, 
                                                      com.hp.hpl.jena.tdb.base.record.RecordFactory recordfactory, 
                                                      com.hp.hpl.jena.tdb.index.IndexParams indexParams) {

        FileSet fs = new FileSet(fileSet.getBasename()) ;
        RecordFactory rf = new RecordFactory(recordfactory.keyLength(), recordfactory.valueLength()) ;
        // Override with defaults.
        RangeIndex ridx = LzBuildDBOE.rangeIndexBuilderDBoe.buildRangeIndex(fs, rf, IndexConst.getDftParams()) ;
        return new AdapterRangeIndex(ridx) ;
    }
}
