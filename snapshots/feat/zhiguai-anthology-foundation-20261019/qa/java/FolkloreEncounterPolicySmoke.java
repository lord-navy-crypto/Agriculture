import io.github.lordnavycrypto.gugugaga.folklore.FolkloreEncounterPolicy;
import io.github.lordnavycrypto.gugugaga.folklore.FolkloreEncounterPolicy.Creature;
import io.github.lordnavycrypto.gugugaga.folklore.FolkloreEncounterPolicy.Evidence;
import io.github.lordnavycrypto.gugugaga.folklore.FolkloreEncounterPolicy.Instrument;
import io.github.lordnavycrypto.gugugaga.folklore.FolkloreEncounterPolicy.Resolution;
import io.github.lordnavycrypto.gugugaga.folklore.FolkloreEncounterPolicy.Stage;
import java.util.EnumSet;

public final class FolkloreEncounterPolicySmoke {
    private static void check(boolean condition, String label) {
        if(!condition) throw new AssertionError(label);
    }

    public static void main(String[] args) {
        int validatedPairs=0;
        for(Creature creature:Creature.values()) {
            check(!creature.originalSource.isBlank(), "missing primary source");
            check(!creature.displayName.isBlank(), "missing name");
            for(Instrument first:Instrument.values())
                for(Instrument second:Instrument.values()) {
                    if(first==second)continue;
                    EnumSet<Evidence> clues=EnumSet.of(
                            FolkloreEncounterPolicy.read(creature,first),
                            FolkloreEncounterPolicy.read(creature,second));
                    EnumSet<Instrument> tools=EnumSet.of(first,second);
                    check(clues.size()==2, "two instruments must give different evidence");
                    check(FolkloreEncounterPolicy.canIdentify(clues,tools,creature),
                            "two different probes must identify "+creature);
                    check(!FolkloreEncounterPolicy.canIdentify(clues,EnumSet.of(first),creature),
                            "repeated identical tool cannot finalize");
                    for(Creature wrong:Creature.values())
                        if(wrong!=creature)
                            check(!FolkloreEncounterPolicy.canIdentify(clues,tools,wrong),
                                "false-positive creature match");
                    check(FolkloreEncounterPolicy.canResolve(Stage.IDENTIFIED,
                                    creature,creature,creature.resolution,true,true),
                            "valid virtual sealing/pacification");
                    for(Resolution other:Resolution.values())
                        if(other!=creature.resolution)
                            check(!FolkloreEncounterPolicy.canResolve(Stage.IDENTIFIED,
                                    creature,creature,other,true,true),
                                "wrong resolution must not end case");
                    check(!FolkloreEncounterPolicy.canResolve(Stage.INVESTIGATING,
                                    creature,creature,creature.resolution,true,true),
                            "cannot skip identification");
                    check(!FolkloreEncounterPolicy.canResolve(Stage.IDENTIFIED,
                                    creature,creature,creature.resolution,false,true),
                            "must be at virtual event site");
                    check(!FolkloreEncounterPolicy.canResolve(Stage.IDENTIFIED,
                                    creature,creature,creature.resolution,true,false),
                            "cannot resolve expired case");
                    validatedPairs++;
                }
        }
        check(validatedPairs==18, "all ordered species/tool pairs covered");

        for(int i=0;i<100;i++) {
            boolean eligible=FolkloreEncounterPolicy.mayStart(false,true,true,true,false,
                    true,FolkloreEncounterPolicy.MIN_COOLDOWN_TICKS,i);
            check(eligible==(i<FolkloreEncounterPolicy.CHANCE_PER_CHECK_PERCENT),
                    "fixed bounded random chance");
        }
        check(!FolkloreEncounterPolicy.mayStart(true,true,true,true,false,true,
                Long.MAX_VALUE,0), "never start overlapping group");
        check(!FolkloreEncounterPolicy.mayStart(false,true,true,true,true,true,
                Long.MAX_VALUE,0), "quiet players opt out");
        check(!FolkloreEncounterPolicy.mayStart(false,true,true,true,false,true,
                FolkloreEncounterPolicy.MIN_COOLDOWN_TICKS-1,0),"cooldown");
        check(!FolkloreEncounterPolicy.mayStart(false,false,true,true,false,true,
                Long.MAX_VALUE,0), "no creative/spectator");
        check(!FolkloreEncounterPolicy.mayStart(false,true,false,true,false,true,
                Long.MAX_VALUE,0), "no foreign dimension");
        check(!FolkloreEncounterPolicy.mayStart(false,true,true,true,false,false,
                Long.MAX_VALUE,0), "honor world disable");
        check(!FolkloreEncounterPolicy.mayStart(false,true,true,false,false,true,
                Long.MAX_VALUE,0), "no daytime supernatural case");
        check(FolkloreEncounterPolicy.choose(Long.MIN_VALUE)!=null,"negative seed");
        check(FolkloreEncounterPolicy.choose(Long.MAX_VALUE)!=null,"positive seed");
        System.out.println("FOLKLORE_ENCOUNTER_POLICY_OK: 3 profiles, "+
                validatedPairs+" tool combinations, single-group + cooldown guards");
    }
}
