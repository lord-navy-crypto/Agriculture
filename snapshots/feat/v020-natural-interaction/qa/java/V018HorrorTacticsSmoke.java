import io.github.lordnavycrypto.gugugaga.ai.HorrorHearingPolicy;
import io.github.lordnavycrypto.gugugaga.ai.HorrorListeningPolicy;
public final class V018HorrorTacticsSmoke {
    static void ok(boolean value,String what){if(!value)throw new AssertionError(what);}
    public static void main(String[] args){
        ok(HorrorHearingPolicy.hearsRunner(true,true,true,false,0.2,13*13,0),"noisy hides");
        ok(!HorrorHearingPolicy.hearsRunner(true,true,true,true,0.2,13*13,0),"sneak hides");
        ok(!HorrorHearingPolicy.hearsRunner(true,true,true,false,0,13*13,0),"standing hides");
        ok(!HorrorHearingPolicy.hearsRunner(true,true,true,false,0.2,19*19,0),"outside range");
        ok(!HorrorHearingPolicy.hearsRunner(true,true,true,false,0.2,13*13,9),"vertical sound fades");
        ok(!HorrorHearingPolicy.hearsRunner(false,true,true,false,0.2,13*13,0),"never stalk excluded users");
        ok(!HorrorHearingPolicy.hearsRunner(true,false,true,false,0.2,13*13,0),"visible not secretly heard");
        ok(!HorrorListeningPolicy.mayListen(true,false,1200,-1),"crouch required");
        ok(HorrorListeningPolicy.mayListen(true,true,1200,-1),"first listen");
        ok(!HorrorListeningPolicy.mayListen(true,true,1300,1200),"listen cooldown");
        ok(HorrorListeningPolicy.mayListen(true,true,1500,1200),"listen resumes");
        ok(HorrorListeningPolicy.direction(0,1,0,10)==HorrorListeningPolicy.Direction.AHEAD,"ahead");
        ok(HorrorListeningPolicy.direction(0,1,0,-10)==HorrorListeningPolicy.Direction.BEHIND,"behind");
        ok(HorrorListeningPolicy.direction(0,1,10,0)==HorrorListeningPolicy.Direction.RIGHT,"right");
        ok(HorrorListeningPolicy.direction(0,1,-10,0)==HorrorListeningPolicy.Direction.LEFT,"left");
        for(int i=0;i<200000;i++){
            int d=i%35;
            double sq=d*d;
            boolean hear=HorrorHearingPolicy.hearsRunner(true,true,true,false,0.08,sq,0);
            ok(hear==(d<=18),"distance boundary");
            ok(!HorrorHearingPolicy.hearsRunner(true,true,false,false,0.08,sq,0),"walking silently hides");
            ok(!HorrorHearingPolicy.hearsRunner(true,true,true,true,0.08,sq,0),"crouching safe");
            for(var src:HorrorListeningPolicy.Source.values()){
                var side=HorrorListeningPolicy.Direction.values()[i%4];
                String msg=HorrorListeningPolicy.hint(src,side,sq);
                ok(!msg.isBlank() && msg.length()<95,"short nonspoiler evidence");
            }
        }
        System.out.println("GUGUGAGA_V018_HEARING_LISTENING_PASS 200000");
    }
}
