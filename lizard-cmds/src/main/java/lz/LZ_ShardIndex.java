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

package lz ;

import java.util.Iterator ;

import lizard.index.Shard ;
import org.apache.jena.atlas.lib.ColumnMap ;
import org.apache.jena.atlas.lib.Tuple ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.apache.jena.atlas.logging.ProgressLogger ;
import org.apache.jena.riot.RIOT ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import quack.IndexLib ;
import quack.IndexRef ;
import arq.cmd.CmdException ;
import arq.cmdline.ArgDecl ;
import arq.cmdline.CmdARQ ;

import com.hp.hpl.jena.query.ARQ ;
import com.hp.hpl.jena.tdb.TDB ;
import com.hp.hpl.jena.tdb.base.file.Location ;
import com.hp.hpl.jena.tdb.store.NodeId ;
import com.hp.hpl.jena.tdb.store.tupletable.TupleIndex ;

/** Shard a TDB index. */
public class LZ_ShardIndex extends CmdARQ {
    static { LogCtl.setCmdLogging() ; }
    static Logger log = LoggerFactory.getLogger(LZ_ShardIndex.class) ;

    static final ArgDecl argLocation = new ArgDecl(true, "loc", "location") ;

    static public void main(String... argv) {
        TDB.setOptimizerWarningFlag(false) ;
        new LZ_ShardIndex(argv).mainRun() ;
    }

    protected LZ_ShardIndex(String[] argv) {
        super(argv) ;
        super.modVersion.addClass(ARQ.class) ;
        super.modVersion.addClass(RIOT.class) ;
        super.modVersion.addClass(TDB.class) ;
        super.add(argLocation) ;
    }

    @Override
    protected String getSummary() {
        return getCommandName() + "-loc DIR IndexName | location[name]" ;
    }

    IndexRef idx = null ;
    int numShards = -1 ; 

    @Override
    protected void processModulesAndArgs() {
        if ( super.getNumPositional() != 2 )
            throw new CmdException("Usage: [location[name] | --loc=DIR name] NumShards") ;

        String x = super.getPositionalArg(0) ;
        String y = super.getPositionalArg(1) ;

        if ( super.contains(argLocation) ) {
            Location loc = Location.create(super.getValue(argLocation)) ;
            idx = new IndexRef(loc, x) ;
        } else {
            idx = IndexRef.parse(x) ;
        }

        int N = idx.getIndexName().length() ;
        if ( N != 3 && N != 4 )
            throw new CmdException("Index must 3 or 4 in length : " + idx.getIndexName()) ;
        
        if ( ! idx.exists() )
            throw new CmdException("No such index: "+ idx) ;

        try {
            numShards = Integer.parseInt(y) ;
        } catch (NumberFormatException ex) {
            throw new CmdException("Bad number: "+ y) ;
        }

    }

    @Override
    protected void exec() {   
        //FmtLog.info(log, "dump %s", idx);

        int N = idx.getIndexName().length() ;
        String primaryIndex = IndexLib.choosePrimary(idx); 

        String index = idx.getIndexName().toUpperCase() ;
        TupleIndex tupleIndex = IndexLib.connect(idx, primaryIndex) ;
        ColumnMap mapper = tupleIndex.getColumnMap() ;
        
        TupleIndex[] shards = new TupleIndex[numShards] ;
        
        String fmt = "%02d" ;
        if ( numShards >= 1000 )
            fmt = "%04d" ;
        else if ( numShards >= 100 )
            fmt = "%03d" ;
        
        for ( int i = 0 ; i < numShards ; i++ ) {
            String shardFilename = String.format("%s-"+fmt, idx.getFileName(), i) ; 
            IndexRef idxShard = IndexRef.parse(shardFilename) ;
            shards[i] = IndexLib.connect(idxShard, primaryIndex) ;
        }
        shardTupleIndex(log, tupleIndex, shards) ;
    }
    
    private static int tick = 100000 ;
    private static int superTick = 10 ;

    public static void shardTupleIndex(Logger log, TupleIndex srcIndex, TupleIndex[] shards) {
        ProgressLogger progress = new ProgressLogger(log, "tuples", tick, superTick) ;
        progress.startMessage();
        progress.start(); 
        Iterator<Tuple<NodeId>> srcIter = srcIndex.all() ;
        int N = shards.length ;
        ColumnMap mapper = srcIndex.getColumnMap() ;
        for (; srcIter.hasNext();) {
            Tuple<NodeId> tuple = srcIter.next() ;
            int x = (int)Shard.shardBySubject(mapper, N, tuple) ;
            // This is same order as input.
            shards[x].add(tuple) ;
            progress.tick();
        }
        for ( TupleIndex shard : shards ) {
            shard.sync() ;
        }
        progress.finish() ;
        progress.finishMessage();
    }



}
