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

package conf2;

import lizard.conf.assembler.AssemblerYaml ;
import lizard.conf.assembler.VocabLizard ;
import lizard.query.LizardQuery ;
import lizard.system.LzLog ;

import org.apache.jena.assembler.Assembler ;

public class Lizard2 {
    static { init() ; } // Via assembler.
    public synchronized static void init() {
        LzLog.logSystem.info("Lizard.init") ;
        //VocabLizard.init();
        Assembler.general.implementWith(VocabLizard.lzDataset, new AssemblerYaml()) ;
        LizardQuery.init(); 
    }
}

