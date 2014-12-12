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

package lz;

import org.apache.jena.atlas.logging.LogCtl ;

// Done by script?
public class lz {
    static { LogCtl.setLog4j(); }
    
    public static void main(String... $args) {
        if ( $args.length < 1) {
            System.err.println("Subcommands: deploy query");
            CmdError("lz: no subcommand") ;
        }

        String[] args = new String[$args.length-1] ;
        System.arraycopy($args, 1, args, 0, $args.length-1);
        String subcmd = $args[0] ; 
        
        switch ( subcmd ) {
            case "deploy": { new LZ_Deploy(args).mainRun() ; break ; }
            case "query":  { new LZ_Query(args).mainRun();   break ; }
            case "monitor":  { new LZ_Monitor(args).mainRun();   break ; }
                
            default:
                CmdError("lz; no such subcommand: "+subcmd) ;
                break ;
        }
    }
    
    private static void CmdError(String string, Object ...args) {
        System.err.printf(string, args) ;
        if ( ! string.endsWith("\n") )
            System.err.println() ;
        System.exit(99) ;
    }

}
