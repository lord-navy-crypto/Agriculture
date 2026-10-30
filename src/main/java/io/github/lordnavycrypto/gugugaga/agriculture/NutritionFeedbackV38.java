package io.github.lordnavycrypto.gugugaga.agriculture;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;

/**
 * Five fictional game-play nutrition groups cover all 27 existing indicators.
 *
 * One short positive focus effect OR one short negative focus effect may
 * originate from these groups at a time. Existing food-diversity bonuses and
 * independent v3.7 water/salt survival remain the higher priority.
 *
 * Numbers here are game progression scores, NOT calories, RDAs, nutrient
 * concentrations, medical deficiency thresholds or dietary advice.
 */
public final class NutritionFeedbackV38 {
    private NutritionFeedbackV38() {}
    public static final int GROUP_COUNT=5;
    public static final int SNAPSHOT_SIZE=GROUP_COUNT*3;

    // Exact, disjoint partition of NutritionProfilesV35.NUTRIENTS (0..26).
    // 0 energy, 1 protein/maintenance, 2 B vitamins, 3 plant diversity,
    // 4 minerals/bone-support (includes vitamin D as a game abstraction).
    private static final int[][] GROUPS={
        {0,2},
        {1,12,13,14,19},
        {6,7,8,9,10,11},
        {3,4,5,16,17},
        {15,18,20,21,22,23,24,25,26}
    };
    private static final String LAST_DAY="v38_feedback_day";
    private static final String STREAK_PREFIX="v38_feedback_low_";
    private static final int LOW_THRESHOLD=30;
    private static final int HIGH_THRESHOLD=70;
    private static final int DAYS_TO_PENALTY=3;

    private static int bound(int n) {return Math.max(0,Math.min(100,n));}
    private static int[] values(CompoundTag nutrition) {
        int[] result=new int[NutritionProfilesV35.NUTRIENTS.length];
        for(int i=0;i<result.length;i++)
            result[i]=bound(nutrition.getInt(NutritionProfilesV35.NUTRIENTS[i]));
        return result;
    }
    public static int score(int[] nutrients,int group) {
        int sum=0;
        for(int i:GROUPS[group])sum+=bound(nutrients[i]);
        return sum/GROUPS[group].length;
    }
    public static boolean low(int[] nutrients,int group) {
        int below=0;
        for(int i:GROUPS[group])if(bound(nutrients[i])<LOW_THRESHOLD)below++;
        return below*2>=GROUPS[group].length && score(nutrients,group)<36;
    }
    public static boolean high(int[] nutrients,int group) {
        int count=0;
        for(int i:GROUPS[group])if(bound(nutrients[i])>=60)count++;
        return count*2>=GROUPS[group].length && score(nutrients,group)>=HIGH_THRESHOLD;
    }
    private static String key(int group) {return STREAK_PREFIX+group;}
    public static int days(CompoundTag nutrition,int group) {
        return Math.max(0,Math.min(DAYS_TO_PENALTY,nutrition.getInt(key(group))));
    }
    // Stages: -2 lingering strain, -1 improvement suggested, 0 normal, 1 focus-ready.
    public static int stage(int[] nutrients,int group,int days) {
        if(low(nutrients,group))
            return days>=DAYS_TO_PENALTY?-2:-1;
        if(high(nutrients,group))return 1;
        if(score(nutrients,group)<42)return -1;
        return 0;
    }

    /** New and old player records start with a grace period; worlds do not
     * instantly penalize players for importing an old saved nutrition score. */
    public static void advance(CompoundTag nutrition,long day) {
        if(!nutrition.contains(LAST_DAY)) {
            nutrition.putLong(LAST_DAY,day);
            for(int i=0;i<GROUP_COUNT;i++)nutrition.putInt(key(i),0);
            return;
        }
        long old=nutrition.getLong(LAST_DAY);
        if(day<old) {
            // /time set, not elapsed nutrition time.
            nutrition.putLong(LAST_DAY,day);
            return;
        }
        if(day==old)return;
        int elapsed=(int)Math.min(3L,day-old);
        nutrition.putLong(LAST_DAY,day);
        int[] vals=values(nutrition);
        for(int i=0;i<GROUP_COUNT;i++) {
            nutrition.putInt(key(i),low(vals,i)?
                    Math.min(DAYS_TO_PENALTY,days(nutrition,i)+elapsed):0);
        }
    }
    public static void recoverAfterEating(CompoundTag nutrition) {
        int[] vals=values(nutrition);
        for(int i=0;i<GROUP_COUNT;i++)
            if(!low(vals,i))nutrition.putInt(key(i),0);
    }
    /** 5 group scores, 5 signed stage codes, and 5 elapsed-day counters.
     * Serialized by the established server->client nutrition packet only. */
    public static int[] snapshot(CompoundTag nutrition) {
        int[] out=new int[SNAPSHOT_SIZE];
        int[] vals=values(nutrition);
        for(int i=0;i<GROUP_COUNT;i++) {
            out[i]=score(vals,i);
            out[GROUP_COUNT+i]=stage(vals,i,days(nutrition,i));
            out[GROUP_COUNT*2+i]=days(nutrition,i);
        }
        return out;
    }
    private static Holder<MobEffect> positive(int group) {
        return switch(group) {
            case 0 -> MobEffects.MOVEMENT_SPEED;
            case 1,2 -> MobEffects.DIG_SPEED;
            case 3 -> MobEffects.NIGHT_VISION;
            default -> MobEffects.DAMAGE_RESISTANCE;
        };
    }
    private static Holder<MobEffect> negative(int group) {
        return switch(group) {
            case 0,3 -> MobEffects.MOVEMENT_SLOWDOWN;
            case 4 -> MobEffects.WEAKNESS;
            default -> MobEffects.DIG_SLOWDOWN;
        };
    }
    public static void apply(ServerPlayer player,CompoundTag nutrition,
                             boolean legacyVaried) {
        int[] vals=values(nutrition);
        // Water/salt deficiency already applies short slowness or fatigue.
        // Never amplify or compound that penalty with nutrition penalties.
        boolean waterSaltPriority=WaterSaltV37.water(player)<=15
                ||WaterSaltV37.salt(player)<=8;
        int selectedNegative=-1;
        int selectedPositive=-1;
        for(int i=0;i<GROUP_COUNT;i++) {
            int state=stage(vals,i,days(nutrition,i));
            if(selectedNegative<0 && state==-2)selectedNegative=i;
            if(selectedPositive<0 && state==1)selectedPositive=i;
        }
        if(selectedNegative>=0 && !waterSaltPriority) {
            player.addEffect(new MobEffectInstance(
                    negative(selectedNegative),220,0,true,false,true));
        } else if(selectedNegative<0 && !legacyVaried && !waterSaltPriority
                && selectedPositive>=0) {
            // One bounded positive focus; legacy varied meals already give
            // Haste/Speed and should not produce a large buff stack.
            player.addEffect(new MobEffectInstance(
                    positive(selectedPositive),260,0,true,false,true));
        }
    }
}
