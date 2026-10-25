import io.github.lordnavycrypto.gugugaga.simulation.InfectionEngine;
import io.github.lordnavycrypto.gugugaga.simulation.InfectionEngine.*;
import java.util.*;

public final class InfectionEngineSmoke {
    private static int checks;
    private static void ok(boolean condition,String message){
        checks++;
        if(!condition)throw new AssertionError(message);
    }
    private static UUID id(int n){return new UUID(0,n);}
    private static Host h(int n,Stage s,double x,int age){
        return new Host(id(n),s,age,x,64,0);
    }
    private static Host only(Frame frame,int n){return frame.hosts().get(id(n));}

    public static void main(String[] args){
        var rules=new Config(3,8,3.5,64,4096);
        List<Host> initial=List.of(
            h(1,Stage.INFECTIOUS,0,0),
            h(2,Stage.SUSCEPTIBLE,3,0),
            h(3,Stage.SUSCEPTIBLE,6,0),
            h(4,Stage.SUSCEPTIBLE,80,0));
        Frame first=InfectionEngine.advance(initial,rules);
        ok(first.newExposed()==1,"adjacent animal enters exposed stage");
        ok(only(first,2).stage()==Stage.EXPOSED,
                "real in-game infection status created");
        ok(only(first,3).stage()==Stage.SUSCEPTIBLE,
                "newly exposed host cannot chain in the same frame");
        ok(only(first,4).stage()==Stage.SUSCEPTIBLE,
                "distant animal unaffected");
        ok(first.pairsChecked()<=4096,"pair computation bounded");

        Map<UUID,Host> progress=first.hosts();
        for(int i=0;i<2;i++)progress=InfectionEngine
                .advance(progress.values(),rules).hosts();
        ok(progress.get(id(2)).stage()==Stage.EXPOSED,
                "latent stage persists across multiple game ticks");
        progress=InfectionEngine.advance(progress.values(),rules).hosts();
        ok(progress.get(id(2)).stage()==Stage.INFECTIOUS,
                "exposed host becomes infectious after configured game time");
        ok(progress.get(id(3)).stage()==Stage.SUSCEPTIBLE,
                "new infectious host acts next frame, not retroactively");
        progress=InfectionEngine.advance(progress.values(),rules).hosts();
        ok(progress.get(id(3)).stage()==Stage.EXPOSED,
                "second in-game contact generation is functional");

        var recovery=InfectionEngine.advance(
                List.of(h(20,Stage.INFECTIOUS,0,7)),rules);
        ok(only(recovery,20).stage()==Stage.RECOVERED,
                "infectious game state resolves to recovered");
        ok(recovery.newlyRecovered()==1,"recovery is counted");
        ok(InfectionEngine.advance(recovery.hosts().values(),rules)
                    .hosts().get(id(20)).stage()==Stage.RECOVERED,
                "recovered host does not spontaneously reinfect");

        var reversed=new ArrayList<>(initial);
        Collections.reverse(reversed);
        ok(first.equals(InfectionEngine.advance(reversed,rules)),
                "results independent of entity iteration order");

        Frame empty=InfectionEngine.advance(List.of(),rules);
        ok(empty.hosts().isEmpty()&&empty.pairsChecked()==0,
                "empty world incurs no exposure work");

        var onePair=new Config(3,8,3.5,64,1);
        Frame bounded=InfectionEngine.advance(initial,onePair);
        ok(bounded.pairsChecked()<=1,"max pair budget enforced");
        try {
            InfectionEngine.advance(List.of(h(1,Stage.INFECTIOUS,0,0),
                    h(1,Stage.SUSCEPTIBLE,1,0)),rules);
            throw new AssertionError("duplicate UUID accepted");
        } catch(IllegalArgumentException expected){checks++;}
        try {
            new Config(0,8,3.5,64,4096);
            throw new AssertionError("zero latency accepted");
        } catch(IllegalArgumentException expected){checks++;}

        System.out.println("GUGUGAGA_REWRITE_SEIR_ENGINE_PASS checks="+checks
                +" contact=true latent=true contagious=true recovery=true"
                +" deterministic=true bounded=true");
    }
}
