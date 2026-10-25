import io.github.lordnavycrypto.gugugaga.ai.FactionPolicy;
import io.github.lordnavycrypto.gugugaga.ai.FactionPolicy.Side;
public final class FactionPolicySmoke {
    static void check(boolean b,String what){if(!b)throw new AssertionError(what);}
    public static void main(String[] args) {
        check(FactionPolicy.decode(-1)==Side.UNDECIDED,"Invalid faction neutral");
        check(FactionPolicy.decode(500)==Side.UNDECIDED,"Unrecognized faction neutral");
        for(Side s:Side.values()){
            check(FactionPolicy.decode(s.ordinal())==s,"Identity persists");
            check(!FactionPolicy.name(s).isBlank(),"Meaningful faction label");
            check(FactionPolicy.reveal(s,0).contains("Three pieces"),"No spoilers before clues");
            check(FactionPolicy.reveal(s,3).length()>20,"Uncover distinct narrative");
        }
        check(!FactionPolicy.reveal(Side.GUARDIAN,3)
                .equals(FactionPolicy.reveal(Side.REDACTOR,3)),"Side choices differ");
        System.out.println("GUGUGAGA_V07_FACTIONS_PASS three optional alignments");
    }
}
