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

import lizard.api.TLZ.TLZ_NodeId ;
import lizard.api.TLZ.TLZ_RDF_Literal ;
import lizard.api.TLZ.TLZ_RDF_Term ;
import lizard.system.LizardException ;

import org.apache.jena.atlas.lib.InternalErrorException ;
import org.apache.jena.datatypes.RDFDatatype ;
import org.apache.jena.datatypes.xsd.XSDDatatype ;
import org.apache.jena.graph.Node ;
import org.apache.jena.graph.NodeFactory ;
import org.apache.jena.rdf.model.AnonId ;
import org.apache.jena.sparql.util.NodeUtils ;
import org.apache.thrift.TException ;
import org.apache.thrift.protocol.* ;
import org.apache.thrift.transport.TTransport ;
import org.seaborne.tdb2.store.NodeId ;

public class ThriftLib {

    /** Choose the wire-representation : compact is the normal choice */
    public static TProtocol protocol(TTransport transport) {
        if ( true ) return new TCompactProtocol(transport) ;
        if ( false ) return new TBinaryProtocol(transport) ;
        if ( false ) return new TTupleProtocol(transport) ;
        if ( false ) return new TJSONProtocol(transport) ;
        throw new InternalErrorException("No protocol impl choosen") ;
    }

    // Wrapper for calls 
    @FunctionalInterface
    public interface ThriftRunnable { void run() throws TException ; }
    @FunctionalInterface
    public interface ThriftCallable<X> { X call() throws TException ; }

    public static void exec(Object lock, ThriftRunnable runnable) {
        synchronized(lock) {
            try { runnable.run() ; } 
            catch (TException ex)   { throw new LizardException(ex) ; }
            catch (Exception ex)    { throw new LizardException("Unexpected exception: "+ex.getMessage(), ex) ; }
        }
    }

    public static <X> X call(Object lock, ThriftCallable<X> callable) {
        synchronized(lock) {
            try { return callable.call() ; } 
            catch (TException ex)   { throw new LizardException(ex) ; }
            catch (Exception ex)    { throw new LizardException("Unexpected exception: "+ex.getMessage(), ex) ; }
        }
    }
    
    /** Node to thrift wire format */
    public static TLZ_RDF_Term encodeToTLZ(Node node) {
        TLZ_RDF_Term term = new TLZ_RDF_Term() ;
        if ( node.isURI() ) {
            term.setIri(node.getURI());
            return term ;
        }
        if ( node.isBlank() ) {
            term.setBnode(node.getBlankNodeLabel()) ;
            return term ;
        }
        if ( node.isLiteral() ) {
            // XXX Specific encodings: integer, double, etc. 
            TLZ_RDF_Literal lit = new TLZ_RDF_Literal() ;
            term.setLiteral(lit) ;
            lit.setLex(node.getLiteralLexicalForm()) ;
            if ( NodeUtils.hasLang(node) )
                lit.setLangtag(node.getLiteralLanguage()) ;
            RDFDatatype dt = node.getLiteralDatatype() ;
            if ( dt != null /* Not RDF 1.1 */ && dt.equals(XSDDatatype.XSDstring) )
                lit.setDatatype(node.getLiteralDatatypeURI()) ;
            return term ;
        }
        throw new LizardException("Unsupported node type: "+node) ;
    }
    
    /** Thrift wire format to Node */
    public static Node decodeFromTLZ(TLZ_RDF_Term tlz_node) {
        if ( tlz_node.isSetIri() )
            return NodeFactory.createURI(tlz_node.getIri()) ;
        if ( tlz_node.isSetBnode() )
            return NodeFactory.createAnon(new AnonId(tlz_node.getBnode())) ;
        if ( tlz_node.isSetLiteral() ) {
            TLZ_RDF_Literal lit = tlz_node.getLiteral() ;
            String lex = lit.getLex() ;
            String lang = lit.getLangtag() ;
            String dt = lit.getDatatype() ;
            RDFDatatype rdt = NodeFactory.getType(dt) ;
            return NodeFactory.createLiteral(lex, lang, rdt) ;
        }
        throw new LizardException("Unrecognized RDF Term: "+tlz_node) ;
    }
    
    /** Thrift wire format to NodeId */
    public static TLZ_NodeId encodeToTLZ(NodeId nid) {
        return new TLZ_NodeId().setNodeId(nid.getId()) ; 
    }
    
    /** Thrift wire format to NodeId */
    public static NodeId decodeFromTLZ(TLZ_NodeId tlzNodeId) {
        long idval = tlzNodeId.getNodeId() ;
        NodeId nid = NodeId.create(idval) ;
        return nid ; 
    }

}
