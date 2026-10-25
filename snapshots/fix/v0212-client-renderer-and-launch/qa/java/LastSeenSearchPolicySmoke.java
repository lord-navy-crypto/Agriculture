import io.github.lordnavycrypto.gugugaga.ai.LastSeenSearchPolicy;
import io.github.lordnavycrypto.gugugaga.ai.HorrorChasePolicy;

public final class LastSeenSearchPolicySmoke {
    static void yes(boolean b,String reason){ if(!b)throw new AssertionError(reason); }
    public static void main(String[] args){
        yes(LastSeenSearchPolicy.useLastSeen(false,true,10,true),
                "obscured chase uses remembered position");
        yes(!LastSeenSearchPolicy.useLastSeen(true,true,10,true),
                "visible chase uses the target directly");
        yes(!LastSeenSearchPolicy.useLastSeen(false,false,10,true),
                "no omniscience without position memory");
        yes(!LastSeenSearchPolicy.useLastSeen(false,true,80,true),
                "stop chasing after four seconds unseen");
        yes(LastSeenSearchPolicy.beginSearch(true,80,true,true),
                "short investigation at last sighting");
        yes(!LastSeenSearchPolicy.beginSearch(true,80,true,false),
                "leave / invalid game state cancels search");
        yes(!LastSeenSearchPolicy.beginSearch(true,80,false,true),
                "no remembered position, no search");
        yes(LastSeenSearchPolicy.SEARCH_TICKS<=HorrorChasePolicy.COOLDOWN_TICKS,
                "search bounded by existing chase cooldown");
        System.out.println("GUGUGAGA_V013_LAST_SEEN_SEARCH_PASS");
    }
}
