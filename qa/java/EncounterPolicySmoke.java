import io.github.lordnavycrypto.gugugaga.ai.EncounterPolicy;

public final class EncounterPolicySmoke {
    private static void yes(boolean result,String why) {
        if(!result)throw new AssertionError(why);
    }
    private static boolean may(boolean enabled,boolean overworld,
            boolean survival,boolean quiet,boolean night,int nearby,
            int global,long elapsed,int cooldown) {
        return EncounterPolicy.eligible(enabled,overworld,survival,quiet,night,
                nearby,global,elapsed,cooldown);
    }
    public static void main(String[] args) {
        int cooldown=EncounterPolicy.MIN_COOLDOWN_TICKS;
        yes(!may(false,true,true,false,true,0,0,cooldown,cooldown),"default off");
        yes(!may(true,false,true,false,true,0,0,cooldown,cooldown),"only overworld");
        yes(!may(true,true,false,false,true,0,0,cooldown,cooldown),"no creative/spectator");
        yes(!may(true,true,true,true,true,0,0,cooldown,cooldown),"player opt-out");
        yes(!may(true,true,true,false,false,0,0,cooldown,cooldown),"daylight protected");
        yes(!may(true,true,true,false,true,1,0,cooldown,cooldown),"one observer near player cap");
        yes(!may(true,true,true,false,true,0,8,cooldown,cooldown),"global cap");
        yes(!may(true,true,true,false,true,0,0,cooldown-1,cooldown),"cooldown");
        yes(!may(true,true,true,false,true,0,0,cooldown,12000),"configured cooldown");
        yes(may(true,true,true,false,true,0,0,12000,12000),"eligible after 10m");
        yes(may(true,true,true,false,true,0,7,12000,12000),"last global slot");
        yes(EncounterPolicy.boundedCooldown(0)==cooldown,"never lower than 8 minutes");
        yes(EncounterPolicy.boundedCooldown(Integer.MAX_VALUE)==72000,
                "maximum cooldown bounded to 60 minutes");
        yes(EncounterPolicy.boundedCooldown(12000)==12000,"requested ten minutes preserved");
        System.out.println("ENCOUNTER_POLICY_PASS 14 boundaries");
    }
}
