/*
 *  Copyright 2014 Andy Seaborne
 *
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
 */

package lizard.comms.thrift;

import org.apache.jena.atlas.lib.InternalErrorException ;
import org.apache.thrift.protocol.TCompactProtocol ;
import org.apache.thrift.protocol.TJSONProtocol ;
import org.apache.thrift.protocol.TProtocol ;
import org.apache.thrift.protocol.TTupleProtocol ;
import org.apache.thrift.transport.TTransport ;

public class ThriftLib {

    /** Choose the wire-representation : compact is the normal choice */
    public static TProtocol protocol(TTransport transport) {
        if ( true ) return new TCompactProtocol(transport) ;
        if ( false ) return new TTupleProtocol(transport) ;
        if ( false ) return new TJSONProtocol(transport) ;
        throw new InternalErrorException("No protocol impl choosen") ;
    }
    
}
