package io.github.lordnavycrypto.gugugaga.ai;

/**
 * A separate escalating VISUAL state under the existing reversible v0.7
 * CORRUPTED mood. No new Minecraft damage or unsolicited player effects.
 * Stages are computed from age of the corruption, not NBT payload tricks.
 */
public final class CorruptionArcPolicy {
    public enum Stage { CLEAR, FRACTURED, POSSESSED, BLACKENED }
    public static final int FRACTURE_TICKS=20*8;
    public static final int POSSESS_TICKS=20*35;
    public static final int BLACKEN_TICKS=20*75;
    private CorruptionArcPolicy(){}
    public static Stage stage(boolean corrupted,int activeTicks) {
        if(!corrupted)return Stage.CLEAR;
        int ticks=Math.max(0,activeTicks);
        if(ticks>=BLACKEN_TICKS)return Stage.BLACKENED;
        if(ticks>=POSSESS_TICKS)return Stage.POSSESSED;
        return ticks>=FRACTURE_TICKS?Stage.FRACTURED:Stage.CLEAR;
    }
    public static int clampStage(int ordinal) {
        return Math.max(0,Math.min(Stage.values().length-1,ordinal));
    }
}
