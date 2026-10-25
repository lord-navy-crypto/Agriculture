import io.github.lordnavycrypto.gugugaga.ai.HorrorCombatPolicy;

public final class HorrorCombatPolicySmoke {
    private HorrorCombatPolicySmoke() {}
    public static void main(String[] args) {
        check(HorrorCombatPolicy.shouldEscapeLethalHit(true, 1.0f, 100.0f, 0),
                "lethal player hit must trigger escape");
        check(!HorrorCombatPolicy.shouldEscapeLethalHit(false, 1.0f, 100.0f, 0),
                "non-player damage is outside player-weapon escape rule");
        check(!HorrorCombatPolicy.shouldEscapeLethalHit(true, 10.0f, 4.0f, 0),
                "ordinary nonlethal hit must damage normally");
        check(!HorrorCombatPolicy.shouldEscapeLethalHit(true, 1.0f, 100.0f, 20),
                "grace period must block a second escape transition");
        check(HorrorCombatPolicy.shouldBlockPlayerHit(true, 20),
                "player damage must be blocked during grace period");
        check(!HorrorCombatPolicy.shouldBlockPlayerHit(false, 20),
                "non-player damage is not blocked by player grace");
        check(HorrorCombatPolicy.recoveryHealth(80.0f, 0.45f) == 36.0f,
                "Gugu recovers to 45 percent");
        check(Math.abs(HorrorCombatPolicy.recoveryHealth(64.0f, 0.40f)-25.6f)<0.001f,
                "Lapis recovers to 40 percent");
        check(Math.abs(HorrorCombatPolicy.recoveryHealth(48.0f,0.60f)-28.8f)<0.001f,
                "Yellow recovers to 60 percent");
        check(HorrorCombatPolicy.recoveryHealth(0.0f, 0.0f) == 1.0f,
                "invalid max health has a safe fallback");
        System.out.println("HorrorCombatPolicySmoke: PASS");
    }
    private static void check(boolean condition, String message) {
        if(!condition)throw new AssertionError(message);
    }
}
