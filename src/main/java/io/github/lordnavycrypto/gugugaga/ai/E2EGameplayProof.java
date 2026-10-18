package io.github.lordnavycrypto.gugugaga.ai;

/** Live, opt-in Minecraft CI telemetry. No gameplay changes, no player data.
 * Disable entirely in shipped/ordinary sessions. System property is supplied
 * only by the isolated GitHub Actions in-world game QA process.
 */
public final class E2EGameplayProof {
    private E2EGameplayProof(){}
    public static void record(String event) {
        if(Boolean.getBoolean("gugugaga.e2e"))
            System.out.println("GUGUGAGA_E2E_"+event);
    }
}
