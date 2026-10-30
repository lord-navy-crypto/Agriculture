package io.github.lordnavycrypto.gugugaga.agriculture;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/**
 * An explanatory group view over 25 independently tracked nutrient stores.
 * This DOES NOT pool iron with calcium, B1 with B12, or average a weak nutrient
 * away: individual states and time windows determine persistent warnings.
 *
 * Water and sodium are linked directly to WaterSaltV37, which already owns
 * the short-term water/salt penalties.
 */
public final class NutritionFeedbackV38 {
    private NutritionFeedbackV38() {}
    public static final int GROUP_COUNT=5;
    // Five aggregate scores, states, days; then individual 27 states, days.
    public static final int SNAPSHOT_SIZE=GROUP_COUNT*3+27*2;
    public static final int NUTRIENT_STATES_OFFSET=GROUP_COUNT*3;
    public static final int NUTRIENT_DAYS_OFFSET=NUTRIENT_STATES_OFFSET+27;

    // Covers 25 modeled nutrient stores exactly once, excluding 4 & 26.
    // Each group is only a navigation/category summary. No group penalties.
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
    /**
     * No magical night-vision or resistance reward. Only a few plausible
     * functional limitations have a very mild, explicitly fictional
     * Minecraft analogue; other shortages are visible in the GUI only.
     * Water/salt effects have priority and no effects stack.
     */
    public static void apply(ServerPlayer player,CompoundTag tag,boolean legacyVaried){
        if(WaterSaltV37.water(player)<=15 || WaterSaltV37.salt(player)<=8)return;
        // Specific persistent shortages, never combined across nutrients.
        // Iron, folate, B12 or thiamin: capacity/energy approximation.
        if(NutritionPhysiologyV39.stage(tag,19)==-2
                ||NutritionPhysiologyV39.stage(tag,12)==-2
                ||NutritionPhysiologyV39.stage(tag,13)==-2
                ||NutritionPhysiologyV39.stage(tag,6)==-2){
            player.addEffect(new MobEffectInstance(
                    MobEffects.DIG_SLOWDOWN,220,0,true,false,true));
        }else if(NutritionPhysiologyV39.stage(tag,0)==-2
                ||NutritionPhysiologyV39.stage(tag,1)==-2){
            player.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SLOWDOWN,220,0,true,false,true));
        }
    }
}
