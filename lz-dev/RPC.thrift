namespace java rpc.thrift

struct Item {
  2: list<string> strings
}

service S1 {
    void ping(),       
    i64 beginRead(),
    i64 inc(1: i64 arg) 

    // does not wait for any response at all. Oneway methods MUST be void.
    oneway void deliver(1: Item item)
}