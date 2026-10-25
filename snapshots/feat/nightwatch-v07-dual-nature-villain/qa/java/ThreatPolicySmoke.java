import io.github.lordnavycrypto.gugugaga.ai.ThreatPolicy;
import io.github.lordnavycrypto.gugugaga.ai.ThreatPolicy.Mood;

/** Pure Java 21 hostility safety, warnings, expiration and damage cap. */
public final class ThreatPolicySmoke {
    static void check(boolean ok,String why){if(!ok)throw new AssertionError(why);}
    public static void main(String[] args){
        check(ThreatPolicy.fromOrdinal(-1)==Mood.CALM,"Invalid mood must be calm");
        check(ThreatPolicy.fromOrdinal(99)==Mood.CALM,"Corrupt saved mood must be calm");
        check(ThreatPolicy.afterTime(Mood.ANGRY,
                ThreatPolicy.ANGER_LIFETIME_TICKS-1)==Mood.ANGRY,"Anger persists briefly");
        check(ThreatPolicy.afterTime(Mood.ANGRY,
                ThreatPolicy.ANGER_LIFETIME_TICKS)==Mood.CALM,"Anger recovers");
        check(ThreatPolicy.afterTime(Mood.CORRUPTED,
                ThreatPolicy.CORRUPTION_LIFETIME_TICKS)==Mood.CALM,"Corruption recovers");
        check(ThreatPolicy.afterTime(Mood.CALM,99999)==Mood.CALM,"Calm stays calm");
        for(Mood mood:Mood.values()){
            check(ThreatPolicy.damage(mood)>=0.0f
                    && ThreatPolicy.damage(mood)<=2.0f,"Damage remains modest");
            for(int ticks=0;ticks<=180;ticks+=10)for(int cd=0;cd<=180;cd+=15) {
                check(!ThreatPolicy.canStrike(mood,false,true,false,true,4.0,ticks,cd),
                        "Hostility OFF must disable every strike");
                check(!ThreatPolicy.canStrike(mood,true,false,false,true,4.0,ticks,cd),
                        "Non-survival target must not be struck");
                check(!ThreatPolicy.canStrike(mood,true,true,true,true,4.0,ticks,cd),
                        "Quiet mode is an absolute opt-out");
                check(!ThreatPolicy.canStrike(mood,true,true,false,false,4.0,ticks,cd),
                        "Cannot strike through walls");
            }
        }
        check(!ThreatPolicy.canStrike(Mood.CALM,true,true,false,true,4.0,200,200),
                "Calm never attacks");
        check(!ThreatPolicy.canStrike(Mood.ANGRY,true,true,false,true,4.0,29,200),
                "Warning must last 30 ticks");
        check(!ThreatPolicy.canStrike(Mood.ANGRY,true,true,false,true,4.0,30,74),
                "Attack cannot bypass 75-tick cooldown");
        check(!ThreatPolicy.canStrike(Mood.ANGRY,true,true,false,true,7.0,30,75),
                "No unfair ranged attacks");
        check(!ThreatPolicy.canStrike(Mood.CORRUPTED,true,true,false,true,
                Double.NaN,200,200),"No non-finite range");
        check(ThreatPolicy.canStrike(Mood.ANGRY,true,true,false,true,4.0,30,75),
                "Telegraphed optional angry strike");
        check(ThreatPolicy.canStrike(Mood.CORRUPTED,true,true,false,true,4.0,30,75),
                "Telegraphed optional corrupted strike");
        System.out.println("GUGUGAGA_V07_THREAT_PASS bounded opt-in reversible states");
    }
}
