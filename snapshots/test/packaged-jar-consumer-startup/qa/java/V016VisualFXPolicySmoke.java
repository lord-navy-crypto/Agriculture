import io.github.lordnavycrypto.gugugaga.ai.CinematicVfxPolicy;
public final class V016VisualFXPolicySmoke {
    static void ok(boolean yes,String why){if(!yes)throw new AssertionError(why);}
    public static void main(String[] args){
        long checks=0;
        for(int n=0;n<100000;n++) {
            for(var event:CinematicVfxPolicy.Event.values()) {
                int count=CinematicVfxPolicy.count(event,true,true,true);
                ok(count>0 && count<=4,"particle budget");
                ok(CinematicVfxPolicy.count(event,false,true,true)==0,"disabled");
                ok(CinematicVfxPolicy.count(event,true,false,true)==0,"not actual event");
                ok(CinematicVfxPolicy.count(event,true,true,false)==0,"line-of-sight");
                checks++;
            }
        }
        ok(CinematicVfxPolicy.count(null,true,true,true)==0,"null neutral");
        System.out.println("GUGUGAGA_V016_PARTICLE_BUDGET_PASS "+checks);
    }
}
