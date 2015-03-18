namespace java lizard.api.TLZ

// ---- Admin 
service Ping { void ping() }

// Used in the standalone server only.
struct TLZ_Ping {
1: required i64 marker ;
}

// ---- Transaction
typedef i64 TxnId

service TxnCtl {
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
    void idxPing()    
    bool idxAdd (1: i64 requestId, 9: TxnId txnId, 3: TLZ_ShardIndex shard, 4: TLZ_TupleNodeId tuple)
    bool idxDelete(1: i64 requestId, 9: TxnId txnId, 3: TLZ_ShardIndex shard, 4: TLZ_TupleNodeId tuple)
    list<TLZ_TupleNodeId> idxFind(1: i64 requestId, 9: TxnId txnId, 3: TLZ_ShardIndex shard, 4: TLZ_TupleNodeId pattern)


    // TLZ_SubjectPredicateList find(X)
    // patch
}

// ---- Node
struct TLZ_Node {
1: required string nodeStr ;
}

struct TLZ_NodeId {
1: required i64 nodeId ;
}

service TLZ_NodeTable extends TxnCtl {
    void nodePing() 
    TLZ_NodeId allocNodeId(1: i64 requestId, 9: TxnId txnId, 3: TLZ_Node node)
    TLZ_NodeId findByNode(1: i64 requestId, 9: TxnId txnId, 3: TLZ_Node node)
    TLZ_Node   findByNodeId(1: i64 requestId, 9: TxnId txnId, 3: TLZ_NodeId nodeId)
}

// Local Variables:
// tab-width: 2
// indent-tabs-mode: nil
// comment-default-style: "//"
// End:
