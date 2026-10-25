package io.github.lordnavycrypto.gugugaga.ai;

/**
 * Four-act non-quest horror loop. A repeatable GAMEPLAY cycle, NOT
 * a story ending, and never real-world fourth-wall manipulation.
 *
 * All numbers are server ticks. Early phases prepare the player through
 * restrained atmosphere. Progress depends on an actual nearby entity
 * and a voluntary investigate action, rather than teleporting a player.
 */
public final class HauntGameplayPolicy {
    public enum Act { STILLNESS, WHISPERS, WITNESS, FALSE_SAFETY, RESPITE }
    public enum Evidence { NONE, GUGU_GAGA, LAPIS_REDACTOR, YELLOW_SMILER }
    public static final int WHISPERS_AFTER_TICKS=20*35;
    public static final int WITNESS_AFTER_TICKS=20*90;
    public static final int RESPITE_TICKS=20*70;
    public static final int CUE_INTERVAL_TICKS=20*40;
    // A scripted witness is independent from the rare ambient director's 8-minute timer.
    public static final int STAGE_ACTOR_FIRST_TICKS=20*8;
    public static final int STAGE_ACTOR_RETRY_TICKS=20*20;
    public static final double EVIDENCE_RADIUS_SQ=12.0*12.0;
    private HauntGameplayPolicy(){}

    public static Act advance(Act prior,int activeTicks,Evidence evidence,
            boolean playerInvestigated) {
        if(prior==Act.STILLNESS && activeTicks>=WHISPERS_AFTER_TICKS)
            return Act.WHISPERS;
        if(prior==Act.WHISPERS && activeTicks>=WITNESS_AFTER_TICKS)
            return Act.WITNESS;
        if(prior==Act.WITNESS && playerInvestigated && evidence==Evidence.GUGU_GAGA)
            return Act.FALSE_SAFETY;
        if(prior==Act.FALSE_SAFETY && playerInvestigated
                && (evidence==Evidence.LAPIS_REDACTOR
                || evidence==Evidence.YELLOW_SMILER))
            return Act.RESPITE;
        if(prior==Act.RESPITE && activeTicks>=RESPITE_TICKS)
            return Act.STILLNESS;
        return prior;
    }

    public static boolean playerEligible(boolean survival,boolean night,
            boolean quiet,boolean overworld,boolean joined,boolean serverEnabled) {
        return survival&&night&&!quiet&&overworld&&joined&&serverEnabled;
    }

    /** Retry only when a joined player is actively waiting for an on-stage actor. */
    public static boolean shouldSeekStageActor(Act act,int actTicks) {
        return (act==Act.WITNESS || act==Act.FALSE_SAFETY)
                && actTicks>=STAGE_ACTOR_FIRST_TICKS
                && (actTicks-STAGE_ACTOR_FIRST_TICKS)%STAGE_ACTOR_RETRY_TICKS==0;
    }

    public static boolean nearEvidence(double distanceSquared,boolean lineOfSight) {
        return lineOfSight&&Double.isFinite(distanceSquared)
                && distanceSquared>=0&&distanceSquared<=EVIDENCE_RADIUS_SQ;
    }

    /** A bounded low-volume cue when the active player opted in. */
    public static float cueVolume(Act act) {
        return switch(act) {
            case STILLNESS,RESPITE -> 0f;
            case WHISPERS -> 0.12f;
            case WITNESS -> 0.18f;
            case FALSE_SAFETY -> 0.14f;
        };
    }

    public static boolean shouldCue(Act act,int actTicks,long playerStagger) {
        return cueVolume(act)>0 && actTicks>0 && actTicks%CUE_INTERVAL_TICKS==0
                && Math.floorMod(playerStagger,3L)!=2L;
    }
}
