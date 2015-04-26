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

import lizard.api.TLZ.TLZ_Index ;
import lizard.comms.thrift.ThriftServer ;

import org.apache.jena.tdb.store.tupletable.TupleIndex ;

import org.apache.jena.atlas.logging.FmtLog ;
import org.apache.thrift.protocol.TCompactProtocol ;
import org.apache.thrift.server.TServer ;
import org.apache.thrift.server.TThreadPoolServer ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;


public class TServerIndex extends ThriftServer
{
    private static Logger log = LoggerFactory.getLogger(TServerIndex.class) ;
    private TupleIndex index ;
    
    public static TServerIndex create(int port, TupleIndex index) {
        return new TServerIndex(port, index) ;
    }

    private TServerIndex(int port, TupleIndex index) {
        super(port) ;
        setLabel("IndexServer["+port+"]") ;
        this.index = index ;
    }

    @Override
    public void start() {
        FmtLog.info(log, "Start index server: port = %d", getPort()) ;
        // Semapahores to sync.
        //Semaphore sema = new Semaphore(0) ;
        
        new Thread(()-> {
            try {
                TLZ_Index.Iface handler = new THandlerTupleIndex(getLabel(), index) ;
                TLZ_Index.Processor<TLZ_Index.Iface> processor = new TLZ_Index.Processor<TLZ_Index.Iface>(handler);
                TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverTransport) ;
                args.processor(processor) ;
                args.inputProtocolFactory(new TCompactProtocol.Factory()) ;
                args.outputProtocolFactory(new TCompactProtocol.Factory()) ;
                TServer server = new TThreadPoolServer(args);
                FmtLog.info(log, "Started index server: port = %d", getPort()) ;
                //sema.release(1);
                server.serve();
                FmtLog.info(log, "Finished index server: port = %d", getPort()) ;
              } catch (Exception e) {
                e.printStackTrace();
              }
        }) .start() ;
        //sema.acquireUninterruptibly(); 
        super.start();
    }
    
}
