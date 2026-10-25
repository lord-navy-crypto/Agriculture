import io.github.lordnavycrypto.gugugaga.ai.MimicDeceptionPolicy;
import io.github.lordnavycrypto.gugugaga.ai.SmilerTrustPolicy;
public final class V015DeceptionTrustPolicySmoke {
    static void yes(boolean ok,String reason){if(!ok)throw new AssertionError(reason);}
    public static void main(String[] args){
        yes(MimicDeceptionPolicy.shouldCreep(true,true,true,false,14*14,80),"ignored mask moves");
        yes(!MimicDeceptionPolicy.shouldCreep(true,true,true,true,14*14,80),"stared at mask freezes");
        yes(!MimicDeceptionPolicy.shouldCreep(true,true,false,false,14*14,80),"walls prevent targeting");
        yes(!MimicDeceptionPolicy.shouldCreep(true,false,true,false,14*14,80),"quiet and nonparticipants safe");
        yes(!MimicDeceptionPolicy.shouldCreep(false,true,true,false,14*14,80),"revealed cannot mimic");
        yes(MimicDeceptionPolicy.canShowTell(true,true,true,true,10*10,840),"rare observable tell");
        yes(!MimicDeceptionPolicy.canShowTell(true,true,true,true,10*10,839),"tell cooldown");
        yes(!MimicDeceptionPolicy.canShowTell(true,true,true,false,10*10,840),"no tell when unwatched");
        yes(SmilerTrustPolicy.canRequestAid(true,true,true,10*10,15*15,300),"genuine help request");
        yes(!SmilerTrustPolicy.canRequestAid(false,true,true,10*10,15*15,300),"nonparticipant");
        yes(!SmilerTrustPolicy.canRequestAid(true,false,true,10*10,15*15,300),"aid optout");
        yes(!SmilerTrustPolicy.canRequestAid(true,true,false,10*10,15*15,300),"no telepathic call");
        yes(!SmilerTrustPolicy.canRequestAid(true,true,true,10*10,15*15,299),"call cooldown");
        yes(SmilerTrustPolicy.afterFavor(4,1300)==5,"favor progression");
        yes(SmilerTrustPolicy.afterFavor(5,99999)==5,"favor hard cap");
        yes(SmilerTrustPolicy.afterFavor(2,0)==2,"no favor spam");
        long trials=0;
        for(int i=0;i<120000;i++){
            double d=(i%30)*(i%30);
            boolean a=MimicDeceptionPolicy.shouldCreep(true,true,true,false,d,80);
            if(a)yes(d>=MimicDeceptionPolicy.CREEP_MIN_SQ
                    && d<=MimicDeceptionPolicy.CREEP_MAX_SQ,"creep range");
            int favor=SmilerTrustPolicy.afterFavor(i%9-3,1300);
            yes(favor>=0 && favor<=SmilerTrustPolicy.MAX_FAVORS,"favor bounds");
            yes(!SmilerTrustPolicy.advice(favor).isBlank(),"nonempty advice");
            trials++;
        }
        System.out.println("GUGUGAGA_V015_DECEPTION_TRUST_PASS "+trials);
    }
}
