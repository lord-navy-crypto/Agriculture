import io.github.lordnavycrypto.gugugaga.ai.CorruptionArcPolicy;
import io.github.lordnavycrypto.gugugaga.ai.MimicAttackPolicy;
import io.github.lordnavycrypto.gugugaga.ai.SmilerGiftPolicy;
import io.github.lordnavycrypto.gugugaga.ai.ThreeEntityReactionPolicy;

public final class AdvancedCharacterPolicySmoke {
    static void yes(boolean b,String msg){if(!b)throw new AssertionError(msg);}
    public static void main(String[] args) {
        for(int i=-20;i<3000;i++) {
            var phase=CorruptionArcPolicy.stage(true,i);
            yes(phase.ordinal()>=0&&phase.ordinal()<=3,"Bounded corruption stage");
            yes(CorruptionArcPolicy.stage(false,i)
                    ==CorruptionArcPolicy.Stage.CLEAR,"Healing resets visual blackening");
        }
        yes(CorruptionArcPolicy.stage(true,0)==CorruptionArcPolicy.Stage.CLEAR,
                "No instant jump scare after corruption");
        yes(CorruptionArcPolicy.stage(true,200)==CorruptionArcPolicy.Stage.FRACTURED,
                "Stage 1 has visible deviation");
        yes(CorruptionArcPolicy.stage(true,750)==CorruptionArcPolicy.Stage.POSSESSED,
                "Stage 2 is possessed");
        yes(CorruptionArcPolicy.stage(true,1600)==CorruptionArcPolicy.Stage.BLACKENED,
                "Blackening is a later stage");
        yes(CorruptionArcPolicy.clampStage(444)==3,"Stage enum damage control");

        yes(MimicAttackPolicy.shouldRevealToPlayer(true,3*3,true,true,false),
                "Close player sees Lapis mask fall");
        yes(!MimicAttackPolicy.shouldRevealToPlayer(true,3*3,false,true,false),
                "Wall prevents mask revelation");
        yes(!MimicAttackPolicy.shouldRevealToPlayer(true,3*3,true,true,true),
                "Smiler terror overrides player investigation");
        yes(!MimicAttackPolicy.shouldRevealToPlayer(true,Double.NaN,true,true,false),
                "Invalid distance rejected");
        yes(!MimicAttackPolicy.mayHit(true,false,false,true,false,true,
                2*2,1000,1000),"No opt-in, no hit");
        yes(!MimicAttackPolicy.mayHit(false,true,false,true,false,true,
                2*2,1000,1000),"Lapis mask cannot attack");
        yes(!MimicAttackPolicy.mayHit(true,true,true,true,false,true,
                2*2,1000,1000),"Smiler nearby prevents attack");
        yes(!MimicAttackPolicy.mayHit(true,true,false,true,true,true,
                2*2,1000,1000),"Quiet player excluded");
        yes(!MimicAttackPolicy.mayHit(true,true,false,false,false,true,
                2*2,1000,1000),"Creative/spectator excluded");
        yes(!MimicAttackPolicy.mayHit(true,true,false,true,false,false,
                2*2,1000,1000),"Cannot attack through walls");
        yes(!MimicAttackPolicy.mayHit(true,true,false,true,false,true,
                2*2,39,1000),"Requires full warning");
        yes(!MimicAttackPolicy.mayHit(true,true,false,true,false,true,
                2*2,40,99),"Attack cooldown always respected");
        yes(!MimicAttackPolicy.mayHit(true,true,false,true,false,true,
                3*3,40,100),"No out of range strikes");
        yes(MimicAttackPolicy.mayHit(true,true,false,true,false,true,
                2*2,40,100),"Optional fair melee strike");
        yes(ThreeEntityReactionPolicy.redactor(4,0)
                ==ThreeEntityReactionPolicy.RedactorAction.FLEE_SMILER,
                "Flee supersedes mimic strike and guardian corruption");

        yes(SmilerGiftPolicy.mayHelp(false,true,0,-1),"First voluntary help");
        yes(!SmilerGiftPolicy.mayHelp(true,true,7200,-1),
                "Revealed Smiler is no longer a helper");
        yes(!SmilerGiftPolicy.mayHelp(false,false,7200,-1),
                "Creative/spectator cannot farm item reward");
        yes(!SmilerGiftPolicy.mayHelp(false,true,3599,0),"No torch farm");
        yes(SmilerGiftPolicy.mayHelp(false,true,3600,0),
                "After 3min help permitted");
        yes(SmilerGiftPolicy.TORCH_COUNT<=2,"Small bounded help");
        long tested=0;
        for(int i=0;i<150000;i++){
            double d=(i%19)*(i%19);
            boolean reveal=MimicAttackPolicy.shouldRevealToPlayer(true,d,
                    i%4!=0,i%7!=0,i%9==0);
            if(i%9==0)yes(!reveal,"Smiler blocks redactor reveal");
            boolean hit=MimicAttackPolicy.mayHit(true,i%2==0,i%3==0,
                    i%5!=0,i%7==0,i%11!=0,d,i%100,i%151);
            if(i%3==0)yes(!hit,"Smiler always blocks redactor's strike");
            tested++;
        }
        System.out.println("GUGUGAGA_V09_ADVANCED_MODES_PASS trials="+tested);
    }
}
