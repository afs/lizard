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

package lizard.comms.thrift;

import lizard.system.Component ;
import lizard.system.ComponentBase ;
import lizard.system.LizardException ;
import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.thrift.TException ;
import org.apache.thrift.protocol.TProtocol ;
import org.apache.thrift.transport.TSocket ;
import org.apache.thrift.transport.TTransport ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

public class ThriftClient extends ComponentBase implements Component {

    private static Logger log = LoggerFactory.getLogger(ThriftClient.class) ; 
    
    private final String host ;
    private final int port ;

    private TProtocol protocol ;

    public ThriftClient(String host, int port) {
        this.host = host ;
        this.port = port;
        super.setLabel(host+":"+port) ;
    }
    
    public String getRemoteHost()   { return host ; }
    public int getRemotePort()      { return port ; }
    
    @Override
    public void start() {
        if ( super.isRunning() ) {
            FmtLog.error(log, "Already started (%s)", getLabel()) ;
            return ;
        }
        FmtLog.debug(log, "Start: %s", getLabel()) ;
        @SuppressWarnings("resource")
        TTransport transport = new TSocket("localhost", port) ;
        try {
            transport.open() ;
            protocol = ThriftLib.protocol(transport) ;
        }
        catch (TException e) {
            transport.close() ;
            throw new LizardException(e) ;
        }
    }

    public void close() {
        protocol.getTransport().close() ;
    }
    
    
    @Override
    public void stop() {
        close() ;
        super.stop() ;
    }
    
    public TProtocol protocol() {
        return protocol ;
    }
}
