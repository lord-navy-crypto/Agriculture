import io.github.lordnavycrypto.gugugaga.ai.HauntingCuePolicy;
import io.github.lordnavycrypto.gugugaga.ai.HauntingCuePolicy.Cue;
import io.github.lordnavycrypto.gugugaga.ai.ObserverBrain.Phase;

public final class HauntingCuePolicySmoke {
    private static void yes(boolean okay,String message) {
        if(!okay)throw new AssertionError(message);
    }
    private static Cue sample(Phase phase,boolean enabled,boolean eligible,
            boolean watched,double d2,int phaseTicks,long ticks,long seed) {
        return HauntingCuePolicy.choose(phase,enabled,eligible,watched,
                d2,phaseTicks,ticks,seed);
    }
    public static void main(String[] args) {
        yes(sample(Phase.OBSERVE,true,true,true,144,50,240,0)
                ==Cue.DISTANT_CHIME,"Long watched observer should chime rarely");
        yes(sample(Phase.OBSERVE,true,true,true,144,50,210,3)
                ==Cue.DISTANT_CHIME,"UUID stagger must align with 10-tick cadence");
        yes(sample(Phase.APPROACH,true,true,false,144,60,160,0)
                ==Cue.SOFT_STEP,"Unwatched approach can whisper a step");
        yes(sample(Phase.RETREAT,true,true,true,9,60,300,0)
                ==Cue.FAINT_PULSE,"Retreat can emit a quiet pulse");
        for(Phase phase:Phase.values())for(long tick=0;tick<12000;tick+=10) {
            yes(sample(phase,false,true,true,144,60,tick,0)==Cue.NONE,
                    "Audio OFF must suppress all audio cues");
            yes(sample(phase,true,false,true,144,60,tick,0)==Cue.NONE,
                    "Quiet/non-survival player must never trigger cues");
            yes(sample(phase,true,true,true,40*40,60,tick,0)==Cue.NONE,
                    "No cues can target people more than 32 blocks away");
        }
        for(long ticks=0;ticks<12000;ticks+=10) {
            yes(sample(Phase.DORMANT,true,true,true,144,100,ticks,1)==Cue.NONE,
                    "Dormant must not make unnatural sounds");
            yes(sample(Phase.APPROACH,true,true,true,144,60,ticks,0)==Cue.NONE,
                    "Watched approach must remain silent");
        }
        for(Cue cue:Cue.values())
            yes(HauntingCuePolicy.cappedVolume(cue)<=0.26f
                    && HauntingCuePolicy.cappedVolume(cue)>=0.0f,
                    "Avoid high volume/startle spikes");
        int chimes=0,steps=0,pulses=0;
        for(long tick=0;tick<12000;tick+=10) {
            if(sample(Phase.OBSERVE,true,true,true,144,80,tick,0)
                    ==Cue.DISTANT_CHIME)chimes++;
            if(sample(Phase.APPROACH,true,true,false,144,80,tick,0)
                    ==Cue.SOFT_STEP)steps++;
            if(sample(Phase.RETREAT,true,true,false,144,80,tick,0)
                    ==Cue.FAINT_PULSE)pulses++;
        }
        yes(chimes==50 && steps==75 && pulses==40,
                "Cue frequency must remain sparse and deterministic");
        System.out.println("NIGHTWATCH_V03_HAUNTING_CUE_PASS chimes="+chimes
                +" steps="+steps+" pulses="+pulses);
    }
}
