package io.github.lordnavycrypto.gugugaga.agriculture;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

/**
 * Explanatory group view over 25 independently tracked nutrient stores.
 * Individual nutrient states are never averaged away. Water and sodium remain
 * owned by the independent v3.7 water/salt system.
 */
public final class NutritionFeedbackV38 {
    private NutritionFeedbackV38() {}
    public static final int GROUP_COUNT=5;
    public static final int SNAPSHOT_SIZE=GROUP_COUNT*3+27*2;
    public static final int NUTRIENT_STATES_OFFSET=GROUP_COUNT*3;
    public static final int NUTRIENT_DAYS_OFFSET=NUTRIENT_STATES_OFFSET+27;
    private static final int[][] GROUPS={
        {0,1,2,6,7,8,9,10,11},
        {3,5,14,16,17,22,24},
        {12,13,19,20},
        {15,18,25},
        {21,23}
    };
    private static int clamp(int v){return Math.max(0,Math.min(100,v));}
    private static int[] values(CompoundTag tag){
        int[] out=new int[27];
        for(int i=0;i<out.length;i++)
            out[i]=clamp(tag.getInt(NutritionProfilesV35.NUTRIENTS[i]));
        return out;
    }
    public static int score(int[] values,int group){
        int sum=0;
        for(int i:GROUPS[group])sum+=clamp(values[i]);
        return sum/GROUPS[group].length;
    }
    public static int[] snapshot(CompoundTag data){
        int[] out=new int[SNAPSHOT_SIZE];
        int[] values=values(data);
        for(int g=0;g<GROUP_COUNT;g++){
            out[g]=score(values,g);
            int state=0,maximumDays=0;
            for(int i:GROUPS[g]){
                int single=NutritionPhysiologyV39.stage(data,i);
                if(single<state)state=single;
                maximumDays=Math.max(maximumDays,
                        NutritionPhysiologyV39.shortfallDays(data,i));
            }
            out[GROUP_COUNT+g]=state;
            out[GROUP_COUNT*2+g]=maximumDays;
        }
        for(int i=0;i<27;i++){
            out[NUTRIENT_STATES_OFFSET+i]=NutritionPhysiologyV39.stage(data,i);
            out[NUTRIENT_DAYS_OFFSET+i]=NutritionPhysiologyV39.shortfallDays(data,i);
        }
        return out;
    }
    /** Compatibility signature; legacyVaried is deliberately ignored. */
    public static void apply(ServerPlayer player,CompoundTag tag,boolean legacyVaried){
        NutritionEffectsV310.apply(player,tag);
    }
}
