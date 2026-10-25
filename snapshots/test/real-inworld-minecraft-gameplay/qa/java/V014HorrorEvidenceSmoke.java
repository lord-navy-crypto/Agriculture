import io.github.lordnavycrypto.gugugaga.ai.*;
import io.github.lordnavycrypto.gugugaga.ai.HorrorEvidencePolicy.Trace;
import io.github.lordnavycrypto.gugugaga.ai.CinematicSuspensePolicy.Cue;
public final class V014HorrorEvidenceSmoke {
    static void yes(boolean condition,String why){if(!condition)throw new AssertionError(why);}
    public static void main(String[] args){
        yes(HorrorEvidencePolicy.bird(true)==Trace.PURSUING_BIRD,"active bird");
        yes(HorrorEvidencePolicy.bird(false)==Trace.WATCHING_BIRD,"quiet bird");
        yes(HorrorEvidencePolicy.redactor(false)==Trace.BLUE_DISGUISE,"do not unmask Lapis");
        yes(HorrorEvidencePolicy.redactor(true)==Trace.UNCOVERED_REDACTOR,"visible unmask");
        yes(HorrorEvidencePolicy.smiler(false)==Trace.FRIENDLY_SMILER,"friendly smile");
        yes(HorrorEvidencePolicy.smiler(true)==Trace.UNCOVERED_SMILER,"exposed smile");
        for(Trace t:Trace.values())
            yes(!HorrorEvidencePolicy.description(t).isBlank(),"real observation text");
        int trials=0;
        for(Cue cue:Cue.values())for(int i=0;i<100000;i++){
            var outside=DepthSuspensePolicy.adapt(cue,false,i);
            var inside=DepthSuspensePolicy.adapt(cue,true,i);
            yes(outside==cue,"outdoors unchanged");
            yes(inside!=null,"enclosed clue exists");
            yes(CinematicSuspensePolicy.volume(inside)<=CinematicSuspensePolicy.MAX_VOLUME,
                "still quiet");
            if(cue==Cue.NONE || cue==Cue.SILENCE)yes(inside==cue,"true silence");
            trials++;
        }
        System.out.println("GUGUGAGA_V014_EVIDENCE_AND_COVER_PASS "+trials);
    }
}
