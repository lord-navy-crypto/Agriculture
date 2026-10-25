import io.github.lordnavycrypto.gugugaga.ai.HauntGameplayPolicy;
import io.github.lordnavycrypto.gugugaga.ai.HauntGameplayPolicy.Act;
import io.github.lordnavycrypto.gugugaga.ai.HauntGameplayPolicy.Evidence;

public final class HauntGameplayPolicySmoke {
    static void yes(boolean b,String why){if(!b)throw new AssertionError(why);}
    public static void main(String[] args) {
        yes(HauntGameplayPolicy.advance(Act.STILLNESS,699,Evidence.NONE,false)
                ==Act.STILLNESS,"Wait for full quiet opening");
        yes(HauntGameplayPolicy.advance(Act.STILLNESS,700,Evidence.NONE,false)
                ==Act.WHISPERS,"First ambient stage");
        yes(HauntGameplayPolicy.advance(Act.WHISPERS,1799,Evidence.NONE,false)
                ==Act.WHISPERS,"No premature witness");
        yes(HauntGameplayPolicy.advance(Act.WHISPERS,1800,Evidence.NONE,false)
                ==Act.WITNESS,"Active witness encounter");
        yes(HauntGameplayPolicy.advance(Act.WITNESS,0,Evidence.NONE,true)
                ==Act.WITNESS,"Must identify actual creature");
        yes(HauntGameplayPolicy.advance(Act.WITNESS,0,Evidence.GUGU_GAGA,false)
                ==Act.WITNESS,"Clues require explicit player action");
        yes(HauntGameplayPolicy.advance(Act.WITNESS,0,Evidence.GUGU_GAGA,true)
                ==Act.FALSE_SAFETY,"Finding Gugu unlocks the next encounter");
        yes(HauntGameplayPolicy.advance(Act.FALSE_SAFETY,0,
                Evidence.GUGU_GAGA,true)==Act.FALSE_SAFETY,
                "Gugu alone does not resolve false safety");
        yes(HauntGameplayPolicy.advance(Act.FALSE_SAFETY,0,
                Evidence.LAPIS_REDACTOR,true)==Act.RESPITE,
                "Unmasking Lapis completes live playable loop");
        yes(HauntGameplayPolicy.advance(Act.FALSE_SAFETY,0,
                Evidence.YELLOW_SMILER,true)==Act.RESPITE,
                "Yellow can satisfy alternative observation");
        yes(HauntGameplayPolicy.advance(Act.RESPITE,1399,Evidence.NONE,false)
                ==Act.RESPITE,"Respite should never be skipped");
        yes(HauntGameplayPolicy.advance(Act.RESPITE,1400,Evidence.NONE,false)
                ==Act.STILLNESS,"Horror loop remains replayable, not a story ending");
        for(int flags=0;flags<64;flags++) {
            boolean eligible=HauntGameplayPolicy.playerEligible(
                    (flags&1)!=0,(flags&2)!=0,(flags&4)!=0,
                    (flags&8)!=0,(flags&16)!=0,(flags&32)!=0);
            yes(eligible==(flags==59),"Only fully opted-in unquiet players can play");
        }
        yes(!HauntGameplayPolicy.nearEvidence(1,false),
                "Investigation requires sight");
        yes(!HauntGameplayPolicy.nearEvidence(Double.NaN,true),
                "Invalid distances never count");
        yes(!HauntGameplayPolicy.nearEvidence(145,true),
                "Beyond 12 blocks never counts");
        yes(HauntGameplayPolicy.nearEvidence(144,true),
                "12-block maximum inclusive");
        for(Act act:Act.values()){
            float volume=HauntGameplayPolicy.cueVolume(act);
            yes(volume>=0f&&volume<=0.18f,"No high-volume event");
        }
        long trials=0;
        for(int i=0;i<100_000;i++){
            for(Act stage:Act.values()) {
                boolean cue=HauntGameplayPolicy.shouldCue(stage,i*20,i);
                if(cue)yes(stage!=Act.STILLNESS&&stage!=Act.RESPITE,
                        "No sound during intentional rest");
                trials++;
            }
        }
        System.out.println("GUGUGAGA_V010_PLAYABLE_HORROR_PASS trials="+trials);
    }
}
