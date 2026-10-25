import io.github.lordnavycrypto.gugugaga.ai.HauntDirectorPolicy;
import io.github.lordnavycrypto.gugugaga.ai.HauntDirectorPolicy.Event;

/** Standalone Java 21 diversity, rate, repeat and profile acceptance. */
public final class HauntDirectorPolicySmoke {
    private HauntDirectorPolicySmoke() {}
    private static void yes(boolean test,String message) {
        if(!test)throw new AssertionError(message);
    }
    public static void main(String[] args) {
        for(int i=-100;i<=100;i++)
            yes(HauntDirectorPolicy.intensity(i)>=0
                    && HauntDirectorPolicy.intensity(i)<=2,
                    "Invalid unbounded director intensity");
        yes(HauntDirectorPolicy.intensity(-10)==0,"Lower clamp");
        yes(HauntDirectorPolicy.intensity(150)==2,"Upper clamp");
        for(int i=-10;i<=10;i++)for(Event e:Event.values()) {
            int next=HauntDirectorPolicy.nextTension(i,e);
            yes(next>=0&&next<=3,"Tension must stay [0,3]");
            if(e==Event.SIGHTING)yes(next==0,"Sighting resets dramatic tension");
        }
        int[][] histogram=new int[3][Event.values().length];
        long validated=0;
        for(int mode=0;mode<3;mode++)for(int player=0;player<512;player++) {
            long seed=0x33b9a5d8L^((long)player*0x9e3779b97f4a7c15L);
            Event previous=Event.SILENCE;
            int tension=0;
            for(int attempt=0;attempt<120;attempt++) {
                long tick=(long)(attempt+1)*12000;
                Event e=HauntDirectorPolicy.choose(seed,tick,attempt,
                        tension,mode,previous);
                yes(e!=previous || e==Event.SILENCE,
                        "Non-silence events must not repeat consecutively");
                yes(e==HauntDirectorPolicy.choose(seed,tick,attempt,tension,mode,previous),
                        "Director must be deterministic for replay/debugging");
                yes(HauntDirectorPolicy.volume(e)>=0f
                                && HauntDirectorPolicy.volume(e)<=0.21f,
                        "No sudden volume spikes");
                previous=e;
                tension=HauntDirectorPolicy.nextTension(tension,e);
                histogram[mode][e.ordinal()]++;
                validated++;
            }
        }
        for(int mode=0;mode<3;mode++){
            int used=0;
            for(Event e:Event.values())if(histogram[mode][e.ordinal()]>100)used++;
            yes(used==Event.values().length,
                    "Each mode must include all event types, including silence");
        }
        for(int i=0;i<80000;i++) {
            double a=HauntDirectorPolicy.bearing(i,17);
            yes(Double.isFinite(a)&&a>=0&&a<Math.PI*2,
                    "No invalid event direction on signed hashes");
        }
        yes(HauntDirectorPolicy.nextTension(3,Event.SILENCE)==2,
                "Silence should relieve tension");
        yes(validated>100000,"Director replay coverage inadequate");
        System.out.println("GUGUGAGA_V05_DIRECTOR_PASS iterations="+validated
                +" mode2_visible="+histogram[2][Event.SIGHTING.ordinal()]
                +" mode0_visible="+histogram[0][Event.SIGHTING.ordinal()]);
    }
}
