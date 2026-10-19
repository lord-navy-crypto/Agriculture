import io.github.lordnavycrypto.gugugaga.folklore.FolkloreCaseDirector;
import io.github.lordnavycrypto.gugugaga.folklore.FolkloreEncounterPolicy;
import io.github.lordnavycrypto.gugugaga.folklore.FolkloreEncounterPolicy.Creature;
import io.github.lordnavycrypto.gugugaga.folklore.FolkloreEncounterPolicy.Evidence;
import io.github.lordnavycrypto.gugugaga.folklore.FolkloreEncounterPolicy.Instrument;
import io.github.lordnavycrypto.gugugaga.folklore.FolkloreEncounterPolicy.Resolution;
import io.github.lordnavycrypto.gugugaga.folklore.FolkloreEncounterPolicy.Stage;

public final class FolkloreCaseDirectorSmoke {
    private static void check(boolean ok,String why) {
        if(!ok)throw new AssertionError(why);
    }

    private static void exercise(Creature creature) {
        FolkloreCaseDirector director=new FolkloreCaseDirector();
        long seed=creature.ordinal();
        long now=200000;
        check(director.attemptStart(now,seed,0,true,true,true,false,true),"case starts");
        check(director.active(),"active case");
        check(director.possibleCreatures().size()==3,"all candidates before evidence");
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
        check(!director.identify(Creature.values()[(creature.ordinal()+1)%3]),
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
        check(director.resolve(creature.resolution,true,now+100),
                "correct resolution clears case");
        check(!director.active(),"no case after resolution");
        check(!director.attemptStart(now+101,seed,0,true,true,true,false,true),
                "cooldown prevents case chain");
        long resumed=now+100+FolkloreEncounterPolicy.MIN_COOLDOWN_TICKS;
        check(director.attemptStart(resumed,seed,0,true,true,true,false,true),
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
        System.out.println("FOLKLORE_CASE_DIRECTOR_OK: all 3 full virtual cases, "+
                "one active group, duplicate tool, evidence, wrong guesses, "+
                "wrong seals, consent, cooldown and expiry");
    }
}
