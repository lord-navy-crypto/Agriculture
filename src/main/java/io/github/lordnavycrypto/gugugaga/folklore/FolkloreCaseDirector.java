package io.github.lordnavycrypto.gugugaga.folklore;

import io.github.lordnavycrypto.gugugaga.folklore.FolkloreEncounterPolicy.Creature;
import io.github.lordnavycrypto.gugugaga.folklore.FolkloreEncounterPolicy.Evidence;
import io.github.lordnavycrypto.gugugaga.folklore.FolkloreEncounterPolicy.Instrument;
import io.github.lordnavycrypto.gugugaga.folklore.FolkloreEncounterPolicy.Resolution;
import io.github.lordnavycrypto.gugugaga.folklore.FolkloreEncounterPolicy.Stage;
import io.github.lordnavycrypto.gugugaga.folklore.OriginalTrioCasePolicy.Actor;
import io.github.lordnavycrypto.gugugaga.folklore.OriginalTrioCasePolicy.Beat;

import java.util.EnumSet;
import java.util.Set;

/**
 * One director per Minecraft dimension (owned by its server instance).
 * ONE active related creature group/case only. It has no game dependency:
 * a future runtime must validate loaded chunks, player consent and the
 * physical item interaction BEFORE calling these methods.
 *
 * Not a world-persistent SavedData yet. Restart/persistence QA is a
 * separate acceptance gate, never implied by this pure-Java prototype.
 */
public final class FolkloreCaseDirector {
    private static final class ActiveCase {
        final Creature creature;
        final long startedTick;
        final EnumSet<Instrument> used=EnumSet.noneOf(Instrument.class);
        final EnumSet<Evidence> clues=EnumSet.noneOf(Evidence.class);
        Stage stage=Stage.UNDISCOVERED;
        Beat trioBeat=Beat.FIRST_SIGHTING;

        ActiveCase(Creature creature,long startedTick) {
            this.creature=creature;
            this.startedTick=startedTick;
        }
    }

    private ActiveCase active;
    private long lastFinishedTick=Long.MIN_VALUE;

    public boolean active() { return active!=null; }

    /** Server-side case identity; do not reveal to client UI until identified. */
    public Creature activeCaseType() {
        return active==null?null:active.creature;
    }

    /** One coherent three-actor story, not three random unrelated monsters. */
    public Beat originalTrioBeat() {
        return active!=null && active.creature==Creature.ORIGINAL_GUGU_TRIO
                ?active.trioBeat:null;
    }

    /**
     * Call ONLY after a server-side real entity interaction/gaze validator.
     * The original case requires Gugu -> Lapis -> Yellow narrative evidence.
     */
    public boolean witnessOriginalActor(Actor observed) {
        if(active==null || active.creature!=Creature.ORIGINAL_GUGU_TRIO
                || observed==null)return false;
        Beat next=OriginalTrioCasePolicy.witness(active.trioBeat,observed);
        if(next==active.trioBeat)return false;
        active.trioBeat=next;
        return true;
    }
    public Stage stage() {
        return active==null?Stage.UNDISCOVERED:active.stage;
    }
    public EnumSet<Evidence> collected() {
        return active==null?EnumSet.noneOf(Evidence.class)
                :EnumSet.copyOf(active.clues);
    }
    public Set<Creature> possibleCreatures() {
        return active==null?EnumSet.noneOf(Creature.class)
                :FolkloreEncounterPolicy.candidates(collected());
    }

    /**
     * Called only after the server verifies a qualified real player.
     * Identity is chosen by the SERVER, never supplied by a client packet.
     */
    public boolean attemptStart(long nowTick,long seed,int chance,
            boolean playerInSurvival,boolean overworld,boolean night,
            boolean quiet,boolean serverEnabled) {
        return attemptStart(nowTick,seed,chance,playerInSurvival,overworld,night,
                quiet,serverEnabled,false);
    }

    /** Biome eligibility matters when the server selects a case. */
    public boolean attemptStart(long nowTick,long seed,int chance,
            boolean playerInSurvival,boolean overworld,boolean night,
            boolean quiet,boolean serverEnabled,boolean coldBiome) {
        if(nowTick<0)return false;
        long since=lastFinishedTick==Long.MIN_VALUE
                ?FolkloreEncounterPolicy.MIN_COOLDOWN_TICKS
                :Math.max(0,nowTick-lastFinishedTick);
        if(!FolkloreEncounterPolicy.mayStart(active(),playerInSurvival,
                overworld,night,quiet,serverEnabled,since,chance))return false;
        active=new ActiveCase(FolkloreEncounterPolicy.chooseForBiome(seed,coldBiome),nowTick);
        return true;
    }

    /**
     * The scan returns in-world fictional evidence only. Repeating the same
     * tool does not add a new clue or bypass multi-instrument deduction.
     */
    public Evidence probe(Instrument instrument) {
        if(active==null || active.stage==Stage.IDENTIFIED)return null;
        if(!active.used.add(instrument))return null;
        active.stage=Stage.INVESTIGATING;
        Evidence evidence=FolkloreEncounterPolicy.read(active.creature,instrument);
        active.clues.add(evidence);
        return evidence;
    }

    public boolean identify(Creature proposed) {
        if(active==null || active.stage!=Stage.INVESTIGATING)return false;
        if(!FolkloreEncounterPolicy.canIdentify(active.clues,active.used,proposed))
            return false;
        active.stage=Stage.IDENTIFIED;
        return true;
    }

    /** Wrong virtual seal/method does not clear the case. */
    public boolean resolve(Resolution method,boolean playerWithinCase,long nowTick) {
        if(active==null)return false;
        boolean correct=FolkloreEncounterPolicy.canResolve(active.stage,
                active.creature,active.creature,method,playerWithinCase,true);
        if(!correct)return false;
        // Evidence identification alone cannot skip the authored three-role
        // story. Wrong-role/duplicate clicks are ignored by witnessOriginalActor.
        if(active.creature==Creature.ORIGINAL_GUGU_TRIO
                && !OriginalTrioCasePolicy.mayConclude(active.trioBeat))
            return false;
        finish(nowTick);
        return true;
    }

    /** A case cannot occupy the world indefinitely if players leave. */
    public boolean expire(long nowTick) {
        if(active==null)return false;
        if(nowTick<active.startedTick
                || nowTick-active.startedTick<FolkloreEncounterPolicy.MAX_CASE_DURATION_TICKS)
            return false;
        finish(nowTick);
        return true;
    }

    public boolean abandon(long nowTick) {
        if(active==null)return false;
        finish(nowTick);
        return true;
    }

    private void finish(long nowTick) {
        active=null;
        lastFinishedTick=nowTick;
    }
}
