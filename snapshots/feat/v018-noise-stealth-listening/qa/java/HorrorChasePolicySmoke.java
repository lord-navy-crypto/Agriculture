import io.github.lordnavycrypto.gugugaga.ai.HorrorChasePolicy;
import io.github.lordnavycrypto.gugugaga.ai.ThreeEntityReactionPolicy;
import io.github.lordnavycrypto.gugugaga.ai.ThreeEntityReactionPolicy.*;

public class HorrorChasePolicySmoke {
    private static void yes(boolean ok,String detail) {
        if(!ok)throw new AssertionError(detail);
    }
    public static void main(String[] args) {
        final double far=Double.POSITIVE_INFINITY;
        yes(ThreeEntityReactionPolicy.gugu(12*12,1)
                ==GuguReaction.FOLLOW_PLAYER,"player outranks Redactor");
        yes(ThreeEntityReactionPolicy.gugu(far,12*12)
                ==GuguReaction.NOTICE_REDACTOR,"Gugu notices Redactor without player");
        yes(ThreeEntityReactionPolicy.redactor(far,8*8,8*8,false)
                ==RedactorAction.REVEAL_AND_APPROACH,"equal attention near both");
        yes(ThreeEntityReactionPolicy.redactor(far,17*17,8*8,false)
                ==RedactorAction.REVEAL_AND_APPROACH,"player can trigger Lapis reveal");
        yes(ThreeEntityReactionPolicy.redactor(far,15*15,11*11,false)
                ==RedactorAction.WATCH_PLAYER,"equal-distance competition selects player");
        yes(ThreeEntityReactionPolicy.redactor(2,2,2,true)
                ==RedactorAction.FLEE_SMILER,"revealed Yellow disrupts Redactor");
        yes(ThreeEntityReactionPolicy.redactor(2,2,2,false)
                !=RedactorAction.FLEE_SMILER,"masked Yellow does not reveal dominance");
        yes(ThreeEntityReactionPolicy.smiler(12*12,1,1)
                ==SmilerAction.CONFRONT_GUGU,"Gugu outranks player and Redactor");
        yes(ThreeEntityReactionPolicy.smiler(far,8*8,1)
                ==SmilerAction.PROTECT_PLAYER,"Smiler supports the player");
        yes(ThreeEntityReactionPolicy.smiler(far,far,1)
                ==SmilerAction.STAND_BY,"Redactor alone does not scare the Smiler");
        yes(ThreeEntityReactionPolicy.smilerCanWardOff(8,160),"near-range intervention");
        yes(!ThreeEntityReactionPolicy.smilerCanWardOff(10,160),"no remote damage");
        yes(HorrorChasePolicy.canStart(true,true,true,12*12,0),"visible chase starts");
        yes(!HorrorChasePolicy.canStart(false,true,true,12*12,0),"no chase without opt-in");
        yes(!HorrorChasePolicy.canStart(true,true,false,12*12,0),"no wallhack");
        yes(!HorrorChasePolicy.canStart(true,true,true,12*12,200),"cooldown");
        yes(HorrorChasePolicy.canContinue(true,true,12*12,200,70),"brief line-of-sight loss");
        yes(!HorrorChasePolicy.canContinue(true,true,12*12,200,80),"hiding works");
        yes(!HorrorChasePolicy.canContinue(true,true,30*30,200,0),"distance escape");
        yes(!HorrorChasePolicy.canContinue(false,true,1,200,0),"leave is immediate");
        yes(HorrorChasePolicy.canSurprise(true,true,true,4*4,1100),"rare close-range scare");
        yes(!HorrorChasePolicy.canSurprise(true,false,true,4*4,1100),"scares optional");
        yes(!HorrorChasePolicy.canSurprise(true,true,false,4*4,1100),"scares require sight");
        long checked=0;
        for(int i=0;i<100000;i++){
            double distance=(i%50)*(i%50);
            if(HorrorChasePolicy.canStart(true,true,true,distance,0))
                yes(distance<=HorrorChasePolicy.DETECTION_RADIUS_SQ,
                    "range bounded");
            if(HorrorChasePolicy.canSurprise(true,true,true,distance,1100))
                yes(distance<=HorrorChasePolicy.SCARE_RADIUS_SQ,
                    "no remote surprise");
            checked++;
        }
        System.out.println("GUGUGAGA_V012_HORROR_POLICY_PASS "+checked);
    }
}
