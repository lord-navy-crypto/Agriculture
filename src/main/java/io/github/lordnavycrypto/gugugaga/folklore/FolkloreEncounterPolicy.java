package io.github.lordnavycrypto.gugugaga.folklore;

import java.util.EnumSet;
import java.util.Set;

/**
 * Pure rules for a future server-owned anthology encounter director.
 *
 * A group is ONE active case, potentially including several related actors:
 * never start unrelated cases at the same time in a dimension.
 *
 * Daoist-inspired instruments below are fictional Minecraft mechanics,
 * NOT assertions that supernatural measurements work in the real world.
 * Evidence is deliberately ambiguous until at least two different tools agree.
 */
public final class FolkloreEncounterPolicy {
    private FolkloreEncounterPolicy() {}

    public enum Creature {
        CHANGYOU("山海经·南山经", "长右", Resolution.CALM_WATERS),
        LILI("山海经·南山经", "狸力", Resolution.SETTLE_EARTH),
        PAINTED_SPIRIT("聊斋志异·画皮", "画皮之影", Resolution.BREAK_ILLUSION);

        public final String originalSource;
        public final String displayName;
        public final Resolution resolution;

        Creature(String originalSource, String displayName, Resolution resolution) {
            this.originalSource=originalSource;
            this.displayName=displayName;
            this.resolution=resolution;
        }
    }

    public enum Instrument { LUOPAN, COPPER_BELL, REVEAL_MIRROR }
    public enum Evidence {
        WATER_RESONANCE, EARTH_RESONANCE, FOURFOLD_CALL,
        HOLLOW_FOOTPRINT, FALSE_VOICE, REFLECTION_RIPPLE
    }
    public enum Resolution { CALM_WATERS, SETTLE_EARTH, BREAK_ILLUSION }
    public enum Stage { UNDISCOVERED, INVESTIGATING, IDENTIFIED, RESOLVED }

    public static final int CASE_CHECK_INTERVAL_TICKS = 20 * 30;
    public static final int MIN_COOLDOWN_TICKS = 20 * 60 * 8;
    public static final int MAX_CASE_DURATION_TICKS = 20 * 60 * 12;
    public static final int CHANCE_PER_CHECK_PERCENT = 3;

    /**
     * World/realm-wide one-case cap. Existing encounter prevents a second
     * group, no matter how many players stand nearby.
     */
    public static boolean mayStart(boolean hasActiveCase, boolean playerInSurvival,
            boolean inOverworld, boolean isNight, boolean playerQuiet, boolean allowedByServer,
            long ticksSinceLastCase, int randomPercent) {
        return !hasActiveCase && playerInSurvival && inOverworld && isNight
                && !playerQuiet && allowedByServer
                && ticksSinceLastCase >= MIN_COOLDOWN_TICKS
                && randomPercent >= 0 && randomPercent < CHANCE_PER_CHECK_PERCENT;
    }

    /** Stable choice so each case can be reconstructed by a server seed. */
    public static Creature choose(long seed) {
        Creature[] kinds=Creature.values();
        return kinds[(int)Math.floorMod(seed,kinds.length)];
    }

    /**
     * Different instruments reveal different ambiguous fingerprints.
     * Each creature is uniquely identifiable after evidence from ANY TWO
     * distinct instruments, but no single tool is sufficient to finalize.
     */
    public static Evidence read(Creature creature, Instrument instrument) {
        return switch(creature) {
            case CHANGYOU -> switch(instrument) {
                case LUOPAN -> Evidence.WATER_RESONANCE;
                case COPPER_BELL -> Evidence.FOURFOLD_CALL;
                case REVEAL_MIRROR -> Evidence.REFLECTION_RIPPLE;
            };
            case LILI -> switch(instrument) {
                case LUOPAN -> Evidence.EARTH_RESONANCE;
                case COPPER_BELL -> Evidence.FOURFOLD_CALL;
                case REVEAL_MIRROR -> Evidence.HOLLOW_FOOTPRINT;
            };
            case PAINTED_SPIRIT -> switch(instrument) {
                case LUOPAN -> Evidence.HOLLOW_FOOTPRINT;
                case COPPER_BELL -> Evidence.FALSE_VOICE;
                case REVEAL_MIRROR -> Evidence.REFLECTION_RIPPLE;
            };
        };
    }

    public static EnumSet<Creature> candidates(Set<Evidence> evidence) {
        EnumSet<Creature> found=EnumSet.noneOf(Creature.class);
        for(Creature creature:Creature.values()) {
            boolean matches=true;
            for(Evidence clue:evidence) {
                boolean exists=false;
                for(Instrument tool:Instrument.values()) {
                    if(read(creature,tool)==clue) {
                        exists=true;
                        break;
                    }
                }
                if(!exists) {
                    matches=false;
                    break;
                }
            }
            if(matches)found.add(creature);
        }
        return found;
    }

    public static boolean canIdentify(Set<Evidence> evidence, Set<Instrument> toolsUsed,
            Creature proposed) {
        return evidence.size()>=2 && toolsUsed.size()>=2
                && candidates(evidence).equals(EnumSet.of(proposed));
    }

    public static boolean canResolve(Stage stage, Creature realCreature,
            Creature playerIdentified, Resolution chosenResolution,
            boolean playerWithinEvent, boolean caseStillActive) {
        return stage==Stage.IDENTIFIED && caseStillActive && playerWithinEvent
                && realCreature==playerIdentified
                && realCreature.resolution==chosenResolution;
    }
}
