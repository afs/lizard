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

import java.io.InputStream ;
import java.util.List ;
import java.util.Map ;

import org.apache.jena.atlas.io.IO ;
import org.apache.jena.atlas.io.IndentedWriter ;
import org.apache.jena.atlas.lib.FileOps ;
import org.yaml.snakeyaml.Yaml ;


public class LzConf {
    public static void print(Object obj) {
        print (IndentedWriter.stdout, obj) ;
        IndentedWriter.stdout.flush();
    }
    
    public static void print(IndentedWriter w, Object obj) {
        if ( obj == null ) {
            w.print("<<null>>");
            return ;
        }
        
        if ( obj instanceof List ) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>)obj ;
            w.print("(\n");
            w.incIndent();
            list.forEach( x-> {
                print(w,x) ;   
                w.println();
            }) ;
            w.decIndent();
            w.print(")");
        } else if ( obj instanceof Map ) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>)obj ;
            w.print("{ ");
            w.incIndent();
            map.keySet().forEach( k-> {
                w.printf("%-8s : ", k) ;
                Object v = map.get(k) ;
                if ( compound(v) )
                    w.println();
                print(w, v) ;
                w.println();
            }) ;
            w.decIndent();
            w.print("}");
            //w.println();
        } else {
            w.printf("%s[%s]",obj,obj.getClass().getName()) ;
        }
    }
    
    public static boolean compound(Object obj) {
        return obj instanceof List<?> || obj instanceof Map<?,?> ;
    }
    
    public static Object getField(Object x, String obj, String field) {
        System.out.println("getField: "+obj+"->"+field) ;
        Object z1 = get1(x, obj) ;
        //System.out.println("getField: z1="+z1) ;
        return get1(z1, field) ;
    }
    
    public static Object get(Object obj, String ... path) {
        //System.out.println("get: "+Arrays.asList(path)) ;
        Object x = obj ;
        for ( String c : path )
            x = get1(obj, c) ;
        return x ;
    }
    
    private static Object get1(Object obj, String step ) {
        //System.out.println("get1: "+obj) ;
        //System.out.println("get1:: "+step) ;
       
        if ( ! ( obj instanceof Map ) ) {
            System.err.println("Not a map : "+obj) ;
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>)obj ;
        //System.out.println("get1>> "+map.get(step)) ;
        return map.get(step) ;
    }
    
    public static void main(String[] args) throws Exception {
        FileOps.clearAll("DB"); 
        main.MainLzFuseki.main("setup-simple/fuseki.ttl");
        System.exit(0) ;
        
        
        InputStream inYaml = IO.openFile("data.yaml") ;
        
        Object x = new Yaml().load(inYaml) ;
        System.out.println(x);
        System.out.println("<<<<-------------------------------------------------");
        print(x) ;
        System.out.println() ;
        System.out.println(">>>>-------------------------------------------------");
        
        // Access language :
        //  !global
        //  -link
        //  @array
        
        // can be lists.
        System.out.println(getField(x, "dataset", "nodes")) ;
        System.out.println() ;
        System.out.println(get(x, "dataset", "nodetable", "servers" )) ;
        
        System.exit(0) ;
    }
}
