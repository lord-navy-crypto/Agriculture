import io.github.lordnavycrypto.gugugaga.folklore.FolkloreCaseDirector;
import io.github.lordnavycrypto.gugugaga.folklore.FolkloreEncounterPolicy;
import io.github.lordnavycrypto.gugugaga.folklore.FolkloreEncounterPolicy.Creature;
import io.github.lordnavycrypto.gugugaga.folklore.FolkloreEncounterPolicy.Evidence;
import io.github.lordnavycrypto.gugugaga.folklore.FolkloreEncounterPolicy.Instrument;
import io.github.lordnavycrypto.gugugaga.folklore.FolkloreEncounterPolicy.Resolution;
import io.github.lordnavycrypto.gugugaga.folklore.FolkloreEncounterPolicy.Stage;
import io.github.lordnavycrypto.gugugaga.folklore.OriginalTrioCasePolicy;
import io.github.lordnavycrypto.gugugaga.folklore.OriginalTrioCasePolicy.Actor;
import io.github.lordnavycrypto.gugugaga.folklore.OriginalTrioCasePolicy.Beat;

public final class FolkloreCaseDirectorSmoke {
    private static void check(boolean ok,String why) {
        if(!ok)throw new AssertionError(why);
    }

    private static void exercise(Creature creature) {
        FolkloreCaseDirector director=new FolkloreCaseDirector();
        long seed=creature.ordinal();
        long now=200000;
        check(director.attemptStart(now,seed,0,true,true,true,false,true,true),
                "case starts (cold biome for original group)");
        check(director.active(),"active case");
        check(director.activeCaseType()==creature,"correct seed selected");
        check(director.possibleCreatures().size()==Creature.values().length,
                "all four candidates before evidence");
        check(!director.attemptStart(now+20,seed+1,0,true,true,true,false,true),
                "no overlapping second group");
        check(!director.identify(creature),"cannot guess before using tools");
        Evidence first=director.probe(Instrument.COPPER_BELL);
        check(first!=null,"first instrument evidence");
        check(director.probe(Instrument.COPPER_BELL)==null,
                "repeated probe yields no new evidence");
        check(!director.identify(creature),"one clue cannot finalize");
        Evidence second=director.probe(Instrument.REVEAL_MIRROR);
        check(second!=null && second!=first,"second distinct instrument evidence");
        check(director.collected().size()==2,"two clues stored");
        check(!director.identify(Creature.values()[(creature.ordinal()+1)%Creature.values().length]),
                "wrong identification rejected");
        check(director.identify(creature),"correct identification");
        check(director.stage()==Stage.IDENTIFIED,"identified state");
        check(director.probe(Instrument.LUOPAN)==null,
                "do not mutate case after identification");
        check(!director.resolve(creature.resolution,false,now+100),
                "cannot resolve from outside case");
        for(Resolution wrong:Resolution.values())
            if(wrong!=creature.resolution)
                check(!director.resolve(wrong,true,now+100),
                        "wrong fictional method must fail");
        if(creature==Creature.ORIGINAL_GUGU_TRIO) {
            check(director.originalTrioBeat()==Beat.FIRST_SIGHTING,
                    "original three-actor case starts from Gugu");
            check(!director.resolve(creature.resolution,true,now+100),
                    "must not skip original narrative");
            check(!director.witnessOriginalActor(Actor.YELLOW_SMILER),
                    "Smiler cannot skip Lapis and Gugu");
            check(!director.witnessOriginalActor(Actor.LAPIS_REDACTOR),
                    "Lapis cannot appear before first Gugu evidence");
            check(director.witnessOriginalActor(Actor.GUGU_GAGA),
                    "observing real Gugu progresses scene");
            check(!director.witnessOriginalActor(Actor.GUGU_GAGA),
                    "same actor cannot be counted twice");
            check(!director.resolve(creature.resolution,true,now+100),
                    "one original actor does not complete case");
            check(director.witnessOriginalActor(Actor.LAPIS_REDACTOR),
                    "recognize Lapis as Redactor");
            check(!director.resolve(creature.resolution,true,now+100),
                    "two original actors are not all evidence");
            check(director.witnessOriginalActor(Actor.YELLOW_SMILER),
                    "Yellow Smiler completes narrative observations");
            check(director.originalTrioBeat()==Beat.TRUTH_READY,
                    "all three witness scenes required");
        } else check(director.originalTrioBeat()==null,
                "ancient case has no original-only scenes");
        check(director.resolve(creature.resolution,true,now+100),
                "correct resolution clears case");
        check(!director.active(),"no case after resolution");
        check(!director.attemptStart(now+101,seed,0,true,true,true,false,true),
                "cooldown prevents case chain");
        long resumed=now+100+FolkloreEncounterPolicy.MIN_COOLDOWN_TICKS;
        check(director.attemptStart(resumed,seed,0,true,true,true,false,true,true),
                "new case after cooldown");
        check(!director.expire(resumed+100),"not expired instantly");
        check(director.expire(resumed+
                FolkloreEncounterPolicy.MAX_CASE_DURATION_TICKS),
                "stale case expires without player intervention");
        check(!director.active(),"expiry cleans global slot");
        check(director.collected().isEmpty(),"no evidence leaked to new case");
    }

    public static void main(String[] args) {
        for(Creature creature:Creature.values())exercise(creature);
        System.out.println("FOLKLORE_CASE_DIRECTOR_OK: all four virtual cases including trio, "+
                "one active group, duplicate tool, evidence, wrong guesses, "+
                "wrong seals, consent, cooldown and expiry");
    }
}
