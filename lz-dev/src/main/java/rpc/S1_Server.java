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

package rpc;

import org.apache.thrift.TException ;

public class S1_Server implements rpc.thrift.S1.Iface{

    @Override
    public void ping() throws TException {
        System.err.println("Ping") ;
    }

    @Override
    public long beginRead() throws TException {
        return 0 ;
    }

    @Override
    public long inc(long arg) throws TException {
        System.err.println("inc") ;
        return arg + 1 ; 
    }

}

