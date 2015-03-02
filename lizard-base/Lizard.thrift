namespace java lizard.api.TLZ

// ---- Common

struct TLZ_TupleNodeId {
1: required i64 S ;
2: required i64 P ;
3: required i64 O ;
4: optional i64 G ;
}

// This is used in both directions
struct TLZ_Ping {
1: required i64 marker ;
}

// ---- Transaction

struct TLZ_TxnBegin {
1: required i64              generation ;
}

struct TLZ_TxnPrepare {
1: required i64              generation ;
}

struct TLZ_TxnCommit {
1: required i64              generation ;
}

struct TLZ_TxnEnd {
1: required i64              generation ;
}

// ---- Index
// -- Index Request

enum TLZ_IndexName {
  SPO , POS , PSO , OSP
}

struct TLZ_ShardIndex {
1:  required TLZ_IndexName indexName ;
2:  required i32 shardId ;
}

struct TLZ_SubjectPredicateList {
1: required i64 subject ;
2: list<i64> predicates ;
}


struct TLZ_IdxRequest {
1: required i64              requestId ;
2: required i64              generation ;
3: required TLZ_ShardIndex   index ;
4: optional TLZ_TupleNodeId  pattern ;
5: optional TLZ_SubjectPredicateList subPreds ;
6: optional TLZ_TupleNodeId  addTuple ;
7: optional TLZ_TupleNodeId  deleteTuple ;
8: optional TLZ_Ping         ping ;
}

// -- IndexReply

struct TLZ_SubjectPredicateObjectList {
1: required i64 subject ;
2: list <i64> predicates ;
3: list <i64> objects ;
}

struct TLZ_IdxReply {
1: required i64 requestId ;
2: optional list<TLZ_SubjectPredicateObjectList> entities ;
3: optional list<TLZ_TupleNodeId> tuples ;
4: optional bool yesOrNo ;
}

// ---- Node
struct TLZ_Node {
1: required string nodeStr ;
}

struct TLZ_NodeId {
1: required i64 nodeId ;
}

// Batch?

struct TLZ_NodeRequest {
1:  required i64            requestId ;
2:  required i64            generation ;
3:  optional TLZ_Node       findByNode ;
4:  optional TLZ_Node       allocNodeId ;
5:  optional TLZ_NodeId     findByNodeId ;
6:  optional TLZ_Ping       ping ;
}

struct TLZ_NodeReply {
1: required i64             requestId ;
2: optional TLZ_NodeId      allocId ;
3: optional TLZ_Node        foundNode ;
}

// Local Variables:
// tab-width: 2
// indent-tabs-mode: nil
// comment-default-style: "//"
// End:
