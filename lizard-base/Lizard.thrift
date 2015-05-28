namespace java lizard.api.TLZ

// ---- Admin 
service Ping { void ping() }

// Used in the standalone server only.
struct TLZ_Ping {
1: required i64 marker ;
}

// ---- Transaction
typedef i64 TxnId

service NodeCtl {
    void ping()
    oneway void stop()
}

service TxnCtl extends NodeCtl {
    void txnBeginRead(1: TxnId txnId)
    void txnBeginWrite(1: TxnId txnId)
    void txnPrepare(1: TxnId txnId)
    void txnCommit(1: TxnId txnId)
    void txnAbort(1: TxnId txnId)
    void txnEnd(1: TxnId txnId)
}

// ---- Index

struct TLZ_TupleNodeId {
1: required i64 S ;
9: required i64 P ;
3: required i64 O ;
4: optional i64 G ;
}

enum TLZ_IndexName { SPO , POS , PSO , OSP }

struct TLZ_ShardIndex {
1:  required TLZ_IndexName indexName ;
9:  required i32 shardId ;
}

struct TLZ_SubjectPredicateList {
1: required i64 subject ;
9: list<i64> predicates ;
}

enum TLZ_PatchAction { ADD , DEL }

struct TLZ_PatchEntry {
1: required TLZ_PatchAction      action ;
9: required TLZ_TupleNodeId      tuple ;
}

struct TLZ_Patch {
1: required list<TLZ_PatchEntry> entities ;
}

// ---- Index
service TLZ_Index extends TxnCtl {
    void idxAdd (1: i64 requestId, 9: TxnId txnId, 3: TLZ_ShardIndex shard, 4: TLZ_TupleNodeId tuple)
    void idxDelete(1: i64 requestId, 9: TxnId txnId, 3: TLZ_ShardIndex shard, 4: TLZ_TupleNodeId tuple)
    list<TLZ_TupleNodeId> idxFind(1: i64 requestId, 9: TxnId txnId, 3: TLZ_ShardIndex shard, 4: TLZ_TupleNodeId pattern)


    // TLZ_SubjectPredicateList find(X)
    // patch
}

// ---- Node
// From jena-arq//BinaryRDF

// ==== RDF Term Definitions 

// struct TLZ_RDF_IRI {
// 1: required string iri
// }

# A prefix name (abbrev for an IRI)
struct TLZ_RDF_PrefixName {
1: required string prefix ;
2: required string localName ;
}

// struct TLZ_RDF_BNode {
//   // Maybe support (or even insist) on a global unique identifier e.g. UUID
//   // long mostSig
//   // long leastSig
// 1: required string label
// }

// Common abbreviated for datatypes and other URIs?
// union with additional values. 

struct TLZ_RDF_Literal {
1: required string  lex ;
2: optional string  langtag ;
3: optional string  datatype ;          // Either 3 or 4 but UNION is heavy.
4: optional TLZ_RDF_PrefixName dtPrefix ;   // datatype as prefix name
}

struct TLZ_RDF_Decimal {
1: required i64  value ;
2: required i32  scale ;
}

struct TLZ_RDF_VAR {
1: required string name ;
}

struct TLZ_RDF_ANY { }

struct TLZ_RDF_UNDEF { }

struct TLZ_RDF_REPEAT { }

union TLZ_RDF_Term {
1: string               iri
2: string               bnode
3: TLZ_RDF_Literal      literal     # Full form lexical form/datatype/langtag
4: TLZ_RDF_PrefixName   prefixName 
5: TLZ_RDF_VAR          variable
6: TLZ_RDF_ANY          any
7: TLZ_RDF_UNDEF        undefined
8: TLZ_RDF_REPEAT       repeat
# Value forms of literals.
10: i64                 valInteger
11: double              valDouble
12: TLZ_RDF_Decimal     valDecimal
}

// === Stream RDF items 

struct TLZ_RDF_Triple {
1: required TLZ_RDF_Term S
2: required TLZ_RDF_Term P
3: required TLZ_RDF_Term O
}

struct TLZ_RDF_Quad {
1: required TLZ_RDF_Term S
2: required TLZ_RDF_Term P
3: required TLZ_RDF_Term O
4: optional TLZ_RDF_Term G
}

# Prefix declaration
struct TLZ_RDF_PrefixDecl {
1: required string prefix ;
2: required string uri ;
}

union TLZ_RDF_StreamRow {
# No base - no relative URI resolution.
1: TLZ_RDF_PrefixDecl   prefixDecl
2: TLZ_RDF_Triple       triple
3: TLZ_RDF_Quad         quad
}

// ==== SPARQL Result Sets

struct TLZ_RDF_VarTuple {
1: list<TLZ_RDF_VAR> vars
}

struct TLZ_RDF_DataTuple {
1: list<TLZ_RDF_Term> row
}

// // ==== RDF Patch
// 
// # Includes 
// # Prefix declaration
// 
// enum TLZ_RDF_Patch {
//      ADD, 
//      ADD_NO_OP,         // ADD recorded that had no effect
//      DELETE, 
//      DELETE_NO_OP       // DELETE recorded that had no effect
// }

// Local Variables:
// tab-width: 2
// indent-tabs-mode: nil
// comment-default-style: "//"
// End:


struct TLZ_NodeId {
1: required i64 nodeId ;
}

service TLZ_NodeTable extends TxnCtl {
    TLZ_NodeId allocNodeId(1: i64 requestId, 9: TxnId txnId, 3: TLZ_RDF_Term node)
    TLZ_NodeId findByNode(1: i64 requestId, 9: TxnId txnId, 3: TLZ_RDF_Term node)
    TLZ_RDF_Term   findByNodeId(1: i64 requestId, 9: TxnId txnId, 3: TLZ_NodeId nodeId)
    list<TLZ_NodeId> allocNodeIds(1: i64 requestId, 9: TxnId txnId, 3: list<TLZ_RDF_Term> nodes)
}

// Local Variables:
// tab-width: 2
// indent-tabs-mode: nil
// comment-default-style: "//"
// End:
