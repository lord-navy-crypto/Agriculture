import io.github.lordnavycrypto.gugugaga.ai.CinematicSuspensePolicy;
import io.github.lordnavycrypto.gugugaga.ai.HauntGameplayPolicy;

public final class CinematicSuspensePolicySmoke {
    private static void yes(boolean b,String why) { if(!b)throw new AssertionError(why); }
    public static void main(String[] args) {
        long trials=0;
        for(HauntGameplayPolicy.Act act:HauntGameplayPolicy.Act.values()) {
            for(int tick=0;tick<=5600;tick+=20) {
                for(int seed=0;seed<300;seed++) {
                    var a=CinematicSuspensePolicy.choose(act,tick,seed/25,seed*991337L);
                    var b=CinematicSuspensePolicy.choose(act,tick,seed/25,seed*991337L);
                    yes(a==b,"deterministic soundtrack");
                    yes(CinematicSuspensePolicy.volume(a)>=0
                            && CinematicSuspensePolicy.volume(a)<=0.18f,
                            "volume ceiling");
                    if(act==HauntGameplayPolicy.Act.STILLNESS
                            || act==HauntGameplayPolicy.Act.RESPITE)
                        yes(a==CinematicSuspensePolicy.Cue.NONE,"true respite");
                    if(tick%CinematicSuspensePolicy.CUE_PERIOD_TICKS!=0)
                        yes(a==CinematicSuspensePolicy.Cue.NONE,"bounded frequency");
                    trials++;
                }
            }
        }
        yes(CinematicSuspensePolicy.choose(
                HauntGameplayPolicy.Act.WITNESS,0,0,7)
                ==CinematicSuspensePolicy.Cue.NONE,"no initial startle");
        System.out.println("GUGUGAGA_V013_CINEMATIC_PACING_PASS "+trials);
    }
}
