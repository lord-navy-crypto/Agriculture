package io.github.lordnavycrypto.gugugaga.agriculture;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/**
 * Explicit positive/negative potion-effect mapping for all 27 established
 * nutrition indicators. Effects are short Minecraft gameplay analogies,
 * not claims that nutrient adequacy grants superhuman abilities.
 *
 * Five distinct positive and four distinct negative effect types may be
 * active simultaneously. All 27 mappings are displayed in the GUI; strongest
 * positive reserves and most severe/longest shortages win limited active slots.
 * Water and sodium's negative effects remain owned by WaterSaltV37.
 */
public final class NutritionEffectsV310 {
    private NutritionEffectsV310() {}
    private record Pair(Holder<MobEffect> positive,String positiveKey,
                        Holder<MobEffect> negative,String negativeKey) {}
    private record Candidate(Holder<MobEffect> effect,int priority,int duration) {}

    private static final Pair[] PAIRS={
        new Pair(MobEffects.MOVEMENT_SPEED,"speed",MobEffects.MOVEMENT_SLOWDOWN,"slowness"),
        new Pair(MobEffects.DAMAGE_BOOST,"strength",MobEffects.WEAKNESS,"weakness"),
        new Pair(MobEffects.ABSORPTION,"absorption",MobEffects.HUNGER,"hunger"),
        new Pair(MobEffects.REGENERATION,"regeneration",MobEffects.HUNGER,"hunger"),
        new Pair(MobEffects.DOLPHINS_GRACE,"dolphins_grace",MobEffects.MOVEMENT_SLOWDOWN,"slowness"),
        new Pair(MobEffects.NIGHT_VISION,"night_vision",MobEffects.DARKNESS,"darkness"),
        new Pair(MobEffects.DIG_SPEED,"haste",MobEffects.DIG_SLOWDOWN,"mining_fatigue"),
        new Pair(MobEffects.MOVEMENT_SPEED,"speed",MobEffects.MOVEMENT_SLOWDOWN,"slowness"),
        new Pair(MobEffects.DAMAGE_RESISTANCE,"resistance",MobEffects.WEAKNESS,"weakness"),
        new Pair(MobEffects.DIG_SPEED,"haste",MobEffects.DIG_SLOWDOWN,"mining_fatigue"),
        new Pair(MobEffects.DAMAGE_BOOST,"strength",MobEffects.WEAKNESS,"weakness"),
        new Pair(MobEffects.DIG_SPEED,"haste",MobEffects.DIG_SLOWDOWN,"mining_fatigue"),
        new Pair(MobEffects.ABSORPTION,"absorption",MobEffects.WEAKNESS,"weakness"),
        new Pair(MobEffects.HEALTH_BOOST,"health_boost",MobEffects.WEAKNESS,"weakness"),
        new Pair(MobEffects.REGENERATION,"regeneration",MobEffects.WEAKNESS,"weakness"),
        new Pair(MobEffects.JUMP,"jump_boost",MobEffects.WEAKNESS,"weakness"),
        new Pair(MobEffects.ABSORPTION,"absorption",MobEffects.WEAKNESS,"weakness"),
        new Pair(MobEffects.DAMAGE_RESISTANCE,"resistance",MobEffects.WEAKNESS,"weakness"),
        new Pair(MobEffects.DAMAGE_RESISTANCE,"resistance",MobEffects.WEAKNESS,"weakness"),
        new Pair(MobEffects.MOVEMENT_SPEED,"speed",MobEffects.MOVEMENT_SLOWDOWN,"slowness"),
        new Pair(MobEffects.DIG_SPEED,"haste",MobEffects.DIG_SLOWDOWN,"mining_fatigue"),
        new Pair(MobEffects.JUMP,"jump_boost",MobEffects.WEAKNESS,"weakness"),
        new Pair(MobEffects.REGENERATION,"regeneration",MobEffects.WEAKNESS,"weakness"),
        new Pair(MobEffects.MOVEMENT_SPEED,"speed",MobEffects.MOVEMENT_SLOWDOWN,"slowness"),
        new Pair(MobEffects.DAMAGE_RESISTANCE,"resistance",MobEffects.WEAKNESS,"weakness"),
        new Pair(MobEffects.DAMAGE_BOOST,"strength",MobEffects.WEAKNESS,"weakness"),
        new Pair(MobEffects.JUMP,"jump_boost",MobEffects.DIG_SLOWDOWN,"mining_fatigue")
    };

    private static final int POSITIVE_LIMIT=5;
    private static final int NEGATIVE_LIMIT=4;
    private static final int POSITIVE_THRESHOLD=65;
    private static final int POSITIVE_DURATION=220;
    private static final int WATCH_DURATION=80;
    private static final int SUSTAINED_DURATION=220;

    public static int size(){return PAIRS.length;}
    public static String positiveKey(int index){return PAIRS[index].positiveKey();}
    public static String negativeKey(int index){return PAIRS[index].negativeKey();}

    public static void apply(ServerPlayer player,CompoundTag data){
        int water=WaterSaltV37.water(player);
        int sodium=WaterSaltV37.salt(player);
        // Water/salt penalties have first priority. Do not pile nutrition
        // penalties on top of an active hydration/electrolyte problem.
        if(water<=15 || sodium<=8)return;

        List<Candidate> positive=new ArrayList<>();
        List<Candidate> negative=new ArrayList<>();
        for(int i=0;i<PAIRS.length;i++){
            int score=i==NutritionPhysiologyV39.WATER_INDEX?water:
                    i==NutritionPhysiologyV39.SODIUM_INDEX?sodium:
                    clamp(data.getInt(NutritionProfilesV35.NUTRIENTS[i]));
            Pair pair=PAIRS[i];
            // Hydration and sodium use the established v3.7 meters. Their
            // deficiency effects are already applied by WaterSaltV37.
            if(i==NutritionPhysiologyV39.WATER_INDEX){
                if(score>=POSITIVE_THRESHOLD)
                    positive.add(new Candidate(pair.positive(),score,POSITIVE_DURATION));
                continue;
            }
            if(i==NutritionPhysiologyV39.SODIUM_INDEX){
                if(score>=35 && score<=75)
                    positive.add(new Candidate(pair.positive(),100-Math.abs(55-score),POSITIVE_DURATION));
                continue;
            }
            int stage=NutritionPhysiologyV39.stage(data,i);
            if(stage==0 && score>=POSITIVE_THRESHOLD){
                positive.add(new Candidate(pair.positive(),score,POSITIVE_DURATION));
            }else if(stage<0){
                int days=NutritionPhysiologyV39.shortfallDays(data,i);
                int severity=(stage==-2?10000:1000)+(45-score)*10+days;
                int duration=stage==-2?SUSTAINED_DURATION:WATCH_DURATION;
                negative.add(new Candidate(pair.negative(),severity,duration));
            }
        }
        applyLimited(player,positive,POSITIVE_LIMIT);
        applyLimited(player,negative,NEGATIVE_LIMIT);
    }

    private static void applyLimited(ServerPlayer player,List<Candidate> candidates,int limit){
        candidates.sort(Comparator.comparingInt(Candidate::priority).reversed());
        Set<Holder<MobEffect>> used=new HashSet<>();
        int count=0;
        for(Candidate candidate:candidates){
            if(!used.add(candidate.effect()))continue;
            player.addEffect(new MobEffectInstance(candidate.effect(),
                    candidate.duration(),0,true,false,true));
            if(++count>=limit)break;
        }
    }
    private static int clamp(int value){return Math.max(0,Math.min(100,value));}
}
