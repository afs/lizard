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
 
package lz_dev;

import java.io.IOException ;
import java.nio.file.DirectoryStream ;
import java.nio.file.Files ;
import java.nio.file.Path ;
import java.nio.file.Paths ;
import java.util.ArrayList ;
import java.util.List ;

import lz.lz ;

import org.apache.jena.atlas.logging.LogCtl ;

public class LzMain {
    static { LogCtl.setLog4j(); }
    public static void main(String[] argv) throws IOException {
        
        List<String> args = new ArrayList<>() ;
        args.add("deploy") ;
        args.add("--server=vnode2") ;
        args.add("--fuseki=3030") ;
        
        try ( DirectoryStream<Path> directory = Files.newDirectoryStream(Paths.get("setup2"), "conf*ttl") ) {
            directory.forEach(p->args.add(p.toString())); 
        }
        
        
        lz.main(args.toArray(new String[0])) ;
    }
}

