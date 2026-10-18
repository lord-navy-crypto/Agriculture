import io.github.lordnavycrypto.gugugaga.ai.YellowHelpChatPolicy;
import io.github.lordnavycrypto.gugugaga.ai.GazeInvestigationPolicy;
public final class V020NaturalInteractionSmoke {
    private static void ok(boolean x,String m){if(!x)throw new AssertionError(m);}
    public static void main(String[] args) {
        for(String s:new String[]{"HELP Yellow Face","help yellow face",
                "  HeLp   YELLOW    FACE  ","HELP YellowFace","yellow face help"}) {
            ok(YellowHelpChatPolicy.isHelpRequest(s),"expected: "+s);
        }
        for(String s:new String[]{"HELP","HELP ME","HELP yellow smiling",
                "Yellow Face","Help Yellow Face now","HELP yellow face!","/HELP Yellow Face",
                "HELP Yellow Faces","hello!","HELP yellow face and tell the server"}) {
            ok(!YellowHelpChatPolicy.isHelpRequest(s),"should not capture: "+s);
        }
        ok(!YellowHelpChatPolicy.isHelpRequest(null),"null");
        ok(!YellowHelpChatPolicy.isHelpRequest("A".repeat(10000)),"oversized");
        int focus=0;
        for(int i=0;i<3;i++) {
            focus=GazeInvestigationPolicy.nextTicks(focus,
                    GazeInvestigationPolicy.aimedAt(0.99,3*3,true,true));
            ok(!GazeInvestigationPolicy.complete(focus),"early advance");
        }
        focus=GazeInvestigationPolicy.nextTicks(focus,true);
        ok(GazeInvestigationPolicy.complete(focus),"four seconds");
        ok(GazeInvestigationPolicy.nextTicks(focus,false)==0,"breaking sight clears focus");
        for(int n=0;n<200000;n++) {
            double dot=(n%201)/200.0;
            double dist=(n%30)*(n%30);
            boolean eligible=(n%7)!=0;
            boolean visible=(n%3)!=0;
            boolean aim=GazeInvestigationPolicy.aimedAt(dot,dist,visible,eligible);
            ok(aim==(eligible&&visible&&dot>=GazeInvestigationPolicy.MIN_VIEW_DOT
                    && dist<=144.0),"gaze predicate");
        }
        System.out.println("GUGUGAGA_V020_CHAT_GAZE_PASS 200000");
    }
}
