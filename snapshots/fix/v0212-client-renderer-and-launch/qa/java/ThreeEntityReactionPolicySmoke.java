import io.github.lordnavycrypto.gugugaga.ai.ThreeEntityReactionPolicy;
import io.github.lordnavycrypto.gugugaga.ai.ThreeEntityReactionPolicy.RedactorAction;
import io.github.lordnavycrypto.gugugaga.ai.ThreeEntityReactionPolicy.SmilerAction;
import io.github.lordnavycrypto.gugugaga.ai.ThreeEntityReactionPolicy.GuguReaction;

/** Standalone Java21 proof of three-character priority, never a quest test. */
public final class ThreeEntityReactionPolicySmoke {
    private static void yes(boolean ok,String explanation) {
        if(!ok)throw new AssertionError(explanation);
    }
    public static void main(String[] args){
        double far=Double.POSITIVE_INFINITY;
        yes(ThreeEntityReactionPolicy.redactor(far,far)==RedactorAction.IDLE_AS_LAPIS,
                "Redactor should be an innocent-looking Lapis when alone");
        yes(ThreeEntityReactionPolicy.redactor(far,20*20)==RedactorAction.WATCH_GUGU,
                "Redactor targets Gugu over remote players");
        yes(ThreeEntityReactionPolicy.redactor(far,6*6)==RedactorAction.REVEAL_AND_APPROACH,
                "Redactor reveals by Gugu");
        yes(ThreeEntityReactionPolicy.redactor(23*23,2*2)==RedactorAction.FLEE_SMILER,
                "Yellow forces Redactor to flee even when corruption is possible");
        yes(ThreeEntityReactionPolicy.redactor(24*24,0)==RedactorAction.FLEE_SMILER,
                "Fear at precise boundary");
        yes(ThreeEntityReactionPolicy.redactor(24*24+1,0)
                        ==RedactorAction.REVEAL_AND_APPROACH,
                "Fear outside bounds no longer blocks Gugu interaction");
        yes(ThreeEntityReactionPolicy.smiler(12*12)==SmilerAction.WATCH_GUGU,
                "Smiler observes bird and not the player");
        yes(ThreeEntityReactionPolicy.smiler(far)==SmilerAction.STAND_BY,
                "Smiler doesn't endlessly pursue");
        yes(ThreeEntityReactionPolicy.gugu(12*12)==GuguReaction.NOTICE_REDACTOR,
                "Gugu recognizes direct intruder but not the Yellow's secret");
        yes(ThreeEntityReactionPolicy.gugu(far)==GuguReaction.NORMAL,
                "Gugu keeps original AI when alone");
        yes(!ThreeEntityReactionPolicy.mayCorrupt(true,true,1,999999),
                "Redactor can never corrupt while escaping Yellow");
        yes(!ThreeEntityReactionPolicy.mayCorrupt(false,false,1,999999),
                "Lapis mask alone cannot corrupt");
        yes(!ThreeEntityReactionPolicy.mayCorrupt(false,true,1,699),
                "Corruption cooldown");
        yes(ThreeEntityReactionPolicy.mayCorrupt(false,true,2*2,700),
                "Bounded up-close corruption works");
        yes(!ThreeEntityReactionPolicy.mayCorrupt(false,true,4*4,700),
                "No remote corruption");
        for(double invalid:new double[]{Double.NaN,-1,Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY}) {
            yes(ThreeEntityReactionPolicy.redactor(invalid,invalid)
                    ==RedactorAction.IDLE_AS_LAPIS,"Nonfinite target cannot trigger");
            yes(!ThreeEntityReactionPolicy.mayCorrupt(false,true,invalid,999999),
                    "Invalid squared-distance must be ignored");
        }
        long checked=0;
        for(int i=0;i<120000;i++) {
            double smiler=(i%32)*(i%32);
            double bird=(i%45)*(i%45);
            RedactorAction a=ThreeEntityReactionPolicy.redactor(smiler,bird);
            if(smiler<=ThreeEntityReactionPolicy.SMILER_TERROR_RADIUS_SQ)
                yes(a==RedactorAction.FLEE_SMILER,
                        "Flight must dominate reveal and corruption in every case");
            checked++;
        }
        System.out.println("GUGUGAGA_V08_RELATIONSHIP_PASS "+checked+" prioritizations");
    }
}
