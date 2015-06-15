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

package lizard.node ;

import org.apache.jena.riot.thrift.TRDF ;
import org.apache.jena.riot.thrift.wire.RDF_Term ;
import org.apache.thrift.TException ;
import org.apache.thrift.protocol.TProtocol ;
import org.seaborne.dboe.base.file.BinaryDataFile ;
import org.seaborne.tdb2.TDBException ;
import org.seaborne.tdb2.store.nodetable.TReadAppendFileTransport ;

/** Thrift items :store and retrieve */ 

public class ThriftObjectFile {
    // Write buffering is done in the underlying BinaryDataFile
    BinaryDataFile diskFile ;
    private TReadAppendFileTransport transport ;
    private final TProtocol protocol ;
    
    public ThriftObjectFile(BinaryDataFile objectFile) {
        try {
            this.diskFile = objectFile ;
            transport = new TReadAppendFileTransport(diskFile) ;
            if ( ! transport.isOpen() )
                transport.open(); 
            this.protocol = TRDF.protocol(transport) ;
        }
        catch (Exception ex) {
            throw new TDBException("ThriftObjectFile", ex) ;
        }
    }

    public long writeToTable(RDF_Term term) {
        try {
            long x = diskFile.length() ;
            term.write(protocol) ;
            return x ;
        } catch (Exception ex) {
            throw new TDBException("ThriftObjectFile/Write", ex) ;
        }
    }

    public RDF_Term readFromTable(long posn) {
        // Assumes external synchronization.
        try {
            transport.readPosition(posn) ;
            RDF_Term term = new RDF_Term() ;
            term.read(protocol) ;
            return term ;
        } catch (TException ex) {
            throw new TDBException("ThriftObjectFile/Read", ex) ;
        }
    }

    public void flush() {
        try { transport.flush(); }
        catch (Exception ex) { throw new TDBException("ThriftObjectFile", ex) ; }
    }

    public void close() {
        if ( transport.isOpen() ) {
            try { transport.close() ; }
            catch (Exception ex) { throw new TDBException("ThriftObjectFile", ex) ; }
        }
    }
}
