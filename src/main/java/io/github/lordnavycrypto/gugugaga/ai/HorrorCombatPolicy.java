package io.github.lordnavycrypto.gugugaga.ai;

/**
 * Fairness rules for the three horror actors: ordinary player weapons can
 * stagger/reveal an actor, but cannot permanently kill it. A lethal hit
 * triggers an escape/recovery window; commands and non-player world damage
 * are deliberately outside this policy.
 */
public final class HorrorCombatPolicy {
    public static final int PLAYER_HIT_GRACE_TICKS = 20 * 8;

    private HorrorCombatPolicy() {}

    public static boolean shouldBlockPlayerHit(boolean playerAttack, int graceTicks) {
        return playerAttack && graceTicks > 0;
    }

    public static boolean shouldEscapeLethalHit(boolean playerAttack,
            float health, float incomingDamage, int graceTicks) {
        return playerAttack && graceTicks <= 0
                && Float.isFinite(health) && Float.isFinite(incomingDamage)
                && health > 0.0f && incomingDamage >= health;
    }

    public static float recoveryHealth(float maxHealth, float fraction) {
        if (!Float.isFinite(maxHealth) || maxHealth <= 0.0f) return 1.0f;
        float safeFraction = Float.isFinite(fraction)
                ? Math.max(0.05f, Math.min(1.0f, fraction)) : 0.5f;
        return Math.max(1.0f, Math.min(maxHealth, maxHealth * safeFraction));
    }
}
