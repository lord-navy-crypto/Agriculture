import io.github.lordnavycrypto.gugugaga.ai.ObserverBrain.Phase;
import io.github.lordnavycrypto.gugugaga.ai.WitnessJournalPolicy;
import io.github.lordnavycrypto.gugugaga.ai.WitnessJournalPolicy.Evidence;

/** No Minecraft required; deterministic journal story and opt-out acceptance. */
public final class WitnessJournalPolicySmoke {
    private WitnessJournalPolicySmoke(){}
    private static void yes(boolean expression,String message) {
        if(!expression)throw new AssertionError(message);
    }
    public static void main(String[] args) {
        int mask=0;
        Evidence first=WitnessJournalPolicy.observe(Phase.OBSERVE,true,16*16,false);
        yes(first==Evidence.THE_GAZE,"Direct gaze clue");
        yes(WitnessJournalPolicy.isNew(mask,first),"First witness unlock");
        mask=WitnessJournalPolicy.unlock(mask,first);
        yes(!WitnessJournalPolicy.isNew(mask,first),"No duplicate clue from stationary watching");
        yes(WitnessJournalPolicy.discovered(mask)==1,"First page unlocked");

        Evidence second=WitnessJournalPolicy.observe(Phase.APPROACH,false,12*12,false);
        yes(second==Evidence.THE_FOOTSTEPS,"Only unseen pursuit gives footsteps");
        mask=WitnessJournalPolicy.unlock(mask,second);
        yes(WitnessJournalPolicy.discovered(mask)==2,"Second page unlocked");

        Evidence last=WitnessJournalPolicy.observe(Phase.RETREAT,true,8*8,true);
        yes(last==Evidence.THE_RETREAT,"Transition to retreat adds third clue");
        mask=WitnessJournalPolicy.unlock(mask,last);
        yes(mask==WitnessJournalPolicy.ALL_BITS,"All three fragments collected");
        yes(WitnessJournalPolicy.discovered(mask)==3,"Completed chapter");
        for(int repeat=0;repeat<100000;repeat++) {
            int before=mask;
            mask=WitnessJournalPolicy.unlock(mask,first);
            mask=WitnessJournalPolicy.unlock(mask,second);
            mask=WitnessJournalPolicy.unlock(mask,last);
            yes(mask==before,"Bounded three-bit record must never grow");
        }
        for(Phase phase:Phase.values()) {
            for(double d2:new double[]{0.0,4,25,35,36,49,64,196,576,784,1024,1025,10000,Double.NaN}) {
                Evidence cue=WitnessJournalPolicy.observe(phase,true,d2,false);
                yes(cue!=Evidence.THE_FOOTSTEPS,"Watched player cannot unlock unseen pursuit");
                yes(cue!=Evidence.THE_RETREAT,"No withdrawal without a transition");
                cue=WitnessJournalPolicy.observe(phase,false,d2,false);
                yes(cue!=Evidence.THE_RETREAT,"No withdrawal unless newly retreated");
            }
        }
        yes(WitnessJournalPolicy.observe(Phase.OBSERVE,true,33*33,false)==Evidence.NONE,
                "Do not record observations outside 32 blocks");
        yes(WitnessJournalPolicy.observe(Phase.DORMANT,true,16*16,false)==Evidence.NONE,
                "Dormant must not fabricate sightings");
        yes(WitnessJournalPolicy.observe(Phase.RETREAT,true,8*8,false)==Evidence.NONE,
                "Already-retreating observer cannot repeatedly unlock clue");
        yes(WitnessJournalPolicy.observe(Phase.APPROACH,true,12*12,false)==Evidence.NONE,
                "Unseen pursuit clue cannot be earned when watched");
        yes(WitnessJournalPolicy.observe(Phase.OBSERVE,true,-1,false)==Evidence.NONE,
                "Invalid distance rejected");
        yes(WitnessJournalPolicy.discovered(255)==3,
                "Extra bits ignored for forward-compatible persistence");
        for(Evidence clue:Evidence.values()) {
            if(clue==Evidence.NONE)continue;
            yes(!WitnessJournalPolicy.name(clue).isEmpty(),"Clue name is human-readable");
            yes(!WitnessJournalPolicy.description(clue).isEmpty(),"Clue prose exists");
        }
        System.out.println("NIGHTWATCH_WITNESS_JOURNAL_PASS three bounded nonviolent clues");
    }
}
