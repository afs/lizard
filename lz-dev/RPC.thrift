namespace java pingpong

service S1 {
    void ping(),       
    i64 beginRead()

    // does not wait for any response at all. Oneway methods MUST be void.
    //    oneway void zip()
}