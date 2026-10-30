package io.github.lordnavycrypto.gugugaga.agriculture;

import net.minecraft.nbt.CompoundTag;

/**
 * Evidence-informed, deliberately NON-clinical nutrition game model.
 *
 * All 27 established keys remain unchanged. Index 4 (water) and 26
 * (sodium) are read from the separate v3.7 water/salt system, never
 * independently penalized as micronutrients. The remaining 25 have their
 * own synthetic intake target, abstract reserve, depletion, recovery and
 * sustained-shortfall window.
 *
 * Different kinetics are inspired by nutrient storage (e.g. B1 vs B12);
 * game days and points are not real physiological half-lives, mg, RDA,
 * laboratory cutoffs or a disease diagnosis.
 */
public final class NutritionPhysiologyV39 {
    private NutritionPhysiologyV39() {}
    public static final int COUNT=27;
    public static final int WATER_INDEX=4;
    public static final int SODIUM_INDEX=26;
    private static final String LAST_DAY="v39_last_processed_day";
    private static final String DAILY="v39_daily_intake_";
    private static final String LOW_DAYS="v39_shortfall_days_";
    private static final int MAX_DAILY=48;
    private static final int LOW_RESERVE=32;
    private static final int WATCH_RESERVE=45;
    // Synthetic game-day depletion points. B1=4; B12=1.
    // Vitamin A, D, E, K, calcium and B12 are slower reserve systems.
    private static final int[] DAILY_LOSS={
        4,3,3,2,0,1,4,3,3,3,3,3,2,1,4,1,1,2,
        1,2,3,3,2,1,1,1,0
    };
    // Balanced meal contribution points required per game day, not RDAs.
    private static final int[] TARGET={
        6,5,5,4,0,3,3,3,3,3,3,3,3,3,3,3,3,3,
        3,3,3,3,3,3,3,3,0
    };
    // Consecutive low-reserve days before severe *game warning*.
    private static final int[] LATENCY={
        3,4,5,6,0,16,3,4,5,5,6,6,8,32,4,18,18,14,
        20,12,6,5,8,20,18,20,0
    };
    // Recovery is slower for systems with longer reserve depletion.
    private static final int[] REPLENISH={
        5,4,4,4,0,2,5,5,5,5,5,5,3,2,5,2,2,3,
        2,3,4,4,4,2,2,2,0
    };

    public static boolean linked(int i) {
        return i==WATER_INDEX || i==SODIUM_INDEX;
    }
    public static int requiredDays(int i) {
        return LATENCY[i];
    }
    private static String key(int i) {return NutritionProfilesV35.NUTRIENTS[i];}
    private static int clamp(int value) {return Math.max(0,Math.min(100,value));}
    private static int clampDays(int days) {return Math.max(0,Math.min(60,days));}
    private static int stored(CompoundTag data,int i) {return clamp(data.getInt(key(i)));}
    public static int shortfallDays(CompoundTag data,int i) {
        return linked(i)?0:clampDays(data.getInt(LOW_DAYS+i));
    }

    /** Food is accumulated; not instantly converted into a healthy organ.
     * Only server-side completed eating events may call this method. */
    public static void consume(CompoundTag data,int[] points) {
        for(int i=0;i<COUNT;i++) {
            if(linked(i))continue;
            int add=Math.max(0,Math.min(10,points[i]));
            if(add==0)continue;
            int old=Math.max(0,Math.min(MAX_DAILY,data.getInt(DAILY+i)));
            data.putInt(DAILY+i,Math.min(MAX_DAILY,old+add));
        }
    }

    /** Bounded catch-up: if the chunk/server was idle for a long period,
     * never pretend the character spent that whole offline interval hungry.
     * Likewise /time set cannot instantly manufacture nutritional disease. */
    public static void advance(CompoundTag data,long worldDay) {
        if(!data.contains(LAST_DAY)) {
            data.putLong(LAST_DAY,worldDay);
            for(int i=0;i<COUNT;i++)if(!linked(i))data.putInt(LOW_DAYS+i,0);
            return; // migration grace period: preserve all 27 previous scores
        }
        long last=data.getLong(LAST_DAY);
        if(worldDay<=last) {
            if(worldDay<last)data.putLong(LAST_DAY,worldDay);
            return;
        }
        // One processed day at most per observation. No unobserved backfill.
        data.putLong(LAST_DAY,worldDay);
        for(int i=0;i<COUNT;i++) {
            if(linked(i))continue;
            int consumed=Math.max(0,Math.min(MAX_DAILY,data.getInt(DAILY+i)));
            int current=stored(data,i);
            int next;
            if(consumed>=TARGET[i]) {
                // No immediate max score; reserves refill across days.
                next=clamp(current+Math.min(REPLENISH[i],
                        1+consumed/TARGET[i]));
            } else {
                // Proportional shortage: missing all inputs drains fastest.
                int loss=Math.max(1, DAILY_LOSS[i]*(TARGET[i]-consumed)/TARGET[i]);
                next=clamp(current-loss);
            }
            data.putInt(key(i),next);
            int prior=shortfallDays(data,i);
            if(consumed<TARGET[i] && next<LOW_RESERVE)prior=clampDays(prior+1);
            else if(consumed>=TARGET[i] || next>=WATCH_RESERVE)
                prior=clampDays(prior-2);
            data.putInt(LOW_DAYS+i,prior);
            data.remove(DAILY+i);
        }
    }

    /** -2 sustained shortage, -1 watch, 0 maintained. No "superhuman" 1. */
    public static int stage(CompoundTag data,int i) {
        if(linked(i))return 0;
        int level=stored(data,i);
        int days=shortfallDays(data,i);
        if(level<LOW_RESERVE && days>=LATENCY[i])return -2;
        return level<WATCH_RESERVE?-1:0;
    }
}
