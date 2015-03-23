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

package lz_dev;

import org.seaborne.dboe.transaction.Transactional ;

import com.hp.hpl.jena.query.ReadWrite ;

// Not needed at present. DatasetGraphLz can do it.
class AdapterTransactional implements Transactional {
    private final Transactional transactional ;

    AdapterTransactional(org.seaborne.dboe.transaction.Transactional transactional) {
        this.transactional = transactional ;
    }

    @Override
    public void begin(ReadWrite readWrite)  { transactional.begin(readWrite); }

    @Override
    public void commit()                    { transactional.commit(); }

    @Override
    public void abort()                     { transactional.abort(); }

    @Override
    public void end()                       { transactional.end(); }

}
