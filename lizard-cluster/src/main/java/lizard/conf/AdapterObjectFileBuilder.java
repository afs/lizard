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

import lizard.adapters.AdapterObjectFile ;
import org.apache.jena.atlas.logging.Log ;
import org.seaborne.dboe.base.file.BufferChannel ;
import org.seaborne.dboe.base.file.FileFactory ;
import org.seaborne.dboe.base.file.FileSet ;
import org.seaborne.dboe.base.objectfile.ObjectFile ;
import org.seaborne.dboe.trans.data.TransObjectFile ;

class AdapterObjectFileBuilder implements com.hp.hpl.jena.tdb.setup.ObjectFileBuilder {
    @Override
    public com.hp.hpl.jena.tdb.base.objectfile.ObjectFile buildObjectFile(com.hp.hpl.jena.tdb.base.file.FileSet fileSet, String ext) {
        FileSet fs = new FileSet(fileSet.getBasename()) ;
        String filename = fileSet.filename(ext) ;
        ObjectFile of = ( fileSet.isMem() ) 
            ? FileFactory.createObjectFileMem(filename) 
            : FileFactory.createObjectFileDisk(filename) ;
        BufferChannel chan = FileFactory.createBufferChannelMem() ;
        Log.warn(this,  "Ad-hoc BufferChannel for TransObjectFile") ;
        TransObjectFile tof = new TransObjectFile(of, chan, 0) ;
        return new AdapterObjectFile(tof) ;
    }
}
