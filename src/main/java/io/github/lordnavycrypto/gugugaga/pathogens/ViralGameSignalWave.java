package io.github.lordnavycrypto.gugugaga.pathogens;
import java.util.*;
import java.util.function.Predicate;

/**
 * Small fictional Minecraft DATA-wave pass, not real virus propagation.
 * Only 8 VIRUS-labelled channels can emit; emitted scores are invented.
 * Snapshot input prevents a new destination from acting in this same frame.
 * Both source and destination must be active and already chunk-loaded
 * according to the caller; at most 64 sources / 1024 world cells.
 */
public final class ViralGameSignalWave {
    private ViralGameSignalWave(){}
    public static final int MAX_SOURCES=64;
    public record Frame(Map<PathogenSpatialFieldMath.Cell,int[]> cells,
                        int nextCursor,int visited,int emitted,int newCells) {
        public Frame{cells=PathogenSpatialFieldMath.immutableCopy(cells);}
    }
    public static Frame advance(
            Map<PathogenSpatialFieldMath.Cell,int[]> before,
            int cursor,int budget,long gameFrame,
            Predicate<PathogenSpatialFieldMath.Cell> loadedActive){
        Objects.requireNonNull(before);
        Objects.requireNonNull(loadedActive);
        if(gameFrame<0||budget<1||budget>MAX_SOURCES||
                before.size()>PathogenSpatialFieldMath.MAX_CELLS)
            throw new IllegalArgumentException("Invalid game wave budget");
        var snapshot=PathogenSpatialFieldMath.immutableCopy(before);
        var visit=PathogenRouteFairScheduler.next(snapshot.keySet(),cursor,budget);
        if(snapshot.isEmpty())return new Frame(Map.of(),0,0,0,0);
        Map<PathogenSpatialFieldMath.Cell,int[]> result=new HashMap<>();
        snapshot.forEach((cell,levels)->result.put(cell,levels.clone()));
        int emitted=0,created=0;
        for(var sourceCell:visit.cells()){
            if(!loadedActive.test(sourceCell))continue;
            int[] source=snapshot.get(sourceCell);
            int start=PathogenRouteFairScheduler.startChannel(sourceCell,gameFrame);
            for(int offset=0;offset<15;offset++){
                int channel=(start+offset)%15;
                if(!ViralGameSignalPolicy.isVirusChannel(channel)||
                        source[channel]<ViralGameSignalPolicy.MIN_SOURCE_GAME_SIGNAL)
                    continue;
                var destination=sourceCell.adjacent(
                    ViralGameSignalPolicy.direction(sourceCell,channel,gameFrame));
                if(!loadedActive.test(destination))continue;
                int[] target=result.get(destination);
                if(target==null &&
                        result.size()>=PathogenSpatialFieldMath.MAX_CELLS)
                    continue;
                int beforeLevel=target==null?0:target[channel];
                int next=ViralGameSignalPolicy.nextLevel(channel,
                        source[channel],beforeLevel);
                if(next>beforeLevel){
                    if(target==null){
                        target=new int[15];
                        result.put(destination,target);
                        created++;
                    }
                    target[channel]=next;
                    emitted++;
                }
                break; // At most one numeric event per source and tick.
            }
        }
        return new Frame(result,visit.nextCursor(),visit.cells().size(),
                emitted,created);
    }
}
