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

import java.nio.ByteBuffer ;
import java.util.Iterator ;

import org.apache.jena.atlas.lib.Pair ;
import org.seaborne.dboe.base.block.Block ;
import org.seaborne.dboe.base.objectfile.ObjectFile ;

public class AdapterObjectFile implements com.hp.hpl.jena.tdb.base.objectfile.ObjectFile {
    private final ObjectFile objFile ;
    public AdapterObjectFile(ObjectFile objFile) {
        this.objFile = objFile ;
    }
    
    public ObjectFile getUnderlyingObjectFile( ) { return objFile ; } 

    @Override
    public void sync() { objFile.sync(); }

    @Override
    public void close() { objFile.close(); }

    @Override
    public String getLabel() { return objFile.getLabel() ; }

    @Override
    public com.hp.hpl.jena.tdb.base.block.Block allocWrite(int bytesSpace) { return convert(objFile.allocWrite(bytesSpace)) ; }

    private com.hp.hpl.jena.tdb.base.block.Block convert(Block block) {
        return new com.hp.hpl.jena.tdb.base.block.Block(block.getId(),block.getByteBuffer()) ;
    }

    private Block convert(com.hp.hpl.jena.tdb.base.block.Block block) {
        return new Block(block.getId(),block.getByteBuffer()) ;
    }

    @Override
    public void completeWrite(com.hp.hpl.jena.tdb.base.block.Block buffer) { objFile.completeWrite(convert(buffer)); } 

    @Override
    public void abortWrite(com.hp.hpl.jena.tdb.base.block.Block buffer) { objFile.abortWrite(convert(buffer)); } 

    @Override
    public long write(ByteBuffer buffer) { return objFile.write(buffer) ; }

    @Override
    public ByteBuffer read(long id) { return objFile.read(id) ; }

    @Override
    public long length() { return objFile.length() ; }

    @Override
    public boolean isEmpty() { return objFile.isEmpty() ; }

    @Override
    public void reposition(long id) { objFile.reposition(id); } 

    @Override
    public void truncate(long size) { objFile.truncate(size); } 

    @Override
    public Iterator<Pair<Long, ByteBuffer>> all() { return objFile.all() ; }

}
