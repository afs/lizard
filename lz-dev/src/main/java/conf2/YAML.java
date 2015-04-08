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

package conf2;

import java.util.List ;
import java.util.Map ;

import org.apache.jena.atlas.io.IndentedWriter ;

/** Helper code for YAML */
public class YAML {

    private static boolean compound(Object obj) {
        return obj instanceof List<?> || obj instanceof Map<?,?> ;
    }

    public static Object getField(Object x, String obj, String field) {
        System.out.println("getField: "+obj+"->"+field) ;
        Object z1 = get1(x, obj) ;
        return get1(z1, field) ;
    }

    /** Access : 
     * "foo" is an object in the rot object
     * ".field" is fieled of the current object 
     * @param root
     * @param path
     */
    public static Object get(Object root, String ... path) {
        //System.out.println("get: "+Arrays.asList(path)) ;
    
        Object x = root ;
        for ( String c : path ) {
            if ( c.startsWith(".") )
                x = get1(x, c.substring(1)) ;
            else
                x = get1(root, c) ;
        }
        return x ;
    }

    /** Get field of object */
    private static Object get1(Object obj, String step ) {
        if ( ! ( obj instanceof Map ) ) {
            System.err.println("Not a map : "+obj) ;
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>)obj ;
        return map.get(step) ;
    }

    public static void printYAML(Object obj) {
        printYAML(IndentedWriter.stdout, obj) ;
        IndentedWriter.stdout.flush();
    }

    public static void printYAML(IndentedWriter w, Object obj) {
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
                printYAML(w,x) ;   
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
                printYAML(w, v) ;
                w.println();
            }) ;
            w.decIndent();
            w.print("}");
            //w.println();
        } else {
            w.printf("%s[%s]",obj,obj.getClass().getName()) ;
        }
    }

}

