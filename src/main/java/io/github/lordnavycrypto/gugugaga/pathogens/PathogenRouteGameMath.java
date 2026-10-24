package io.github.lordnavycrypto.gugugaga.pathogens;

import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Objects;

/**
 * An EVENT moves exactly one abstract Minecraft SIGNAL unit (never a pathogen
 * or infected host) from one loaded 4x4 game cell to another. No real-world
 * biological values, actual airborne dynamics, infection or laboratory model.
 */
public final class PathogenRouteGameMath {
    private PathogenRouteGameMath(){}
    public static final int MAX_ROUTE_STEPS=8;
    public record Transfer(Map<PathogenSpatialFieldMath.Cell,int[]> cells,
                           boolean applied,int sourceBefore,int targetAfter){
        public Transfer{cells=PathogenSpatialFieldMath.immutableCopy(cells);}
    }
    public static Transfer move(Map<PathogenSpatialFieldMath.Cell,int[]> input,
            PathogenSpatialFieldMath.Cell source,PathogenSpatialFieldMath.Cell target,
            int channel,PathogenRoutePolicy15.Route route,boolean bothLoaded) {
        Objects.requireNonNull(input);
        Objects.requireNonNull(source);
        Objects.requireNonNull(target);
        Objects.requireNonNull(route);
        if(channel<0||channel>=15)throw new IllegalArgumentException("Invalid channel");
        int distance=Math.abs((long)source.x()-target.x())+
                     Math.abs((long)source.z()-target.z())>Integer.MAX_VALUE
                     ?Integer.MAX_VALUE:(int)(Math.abs((long)source.x()-target.x())+
                            Math.abs((long)source.z()-target.z()));
        Map<PathogenSpatialFieldMath.Cell,int[]> before=
                PathogenSpatialFieldMath.immutableCopy(input);
        int[] from=before.get(source),to=before.get(target);
        int supply=from==null?0:from[channel],targetLevel=to==null?0:to[channel];
        if(!bothLoaded||source.equals(target)||distance>MAX_ROUTE_STEPS||
                distance==0||!PathogenRoutePolicy15.allows(channel,route)||
                supply==0||targetLevel>=PathogenSpatialFieldMath.MAX_PRESSURE||
                (to==null&&before.size()>=PathogenSpatialFieldMath.MAX_CELLS))
            return new Transfer(before,false,supply,targetLevel);
        Map<PathogenSpatialFieldMath.Cell,int[]> next=new HashMap<>(before);
        int[] left=Arrays.copyOf(from,15);
        int[] right=to==null?new int[15]:Arrays.copyOf(to,15);
        left[channel]--;
        right[channel]++;
        boolean empty=true;
        for(int n:left)if(n!=0){empty=false;break;}
        if(empty)next.remove(source);
        else next.put(source,left);
        next.put(target,right);
        return new Transfer(next,true,supply,right[channel]);
    }
}
