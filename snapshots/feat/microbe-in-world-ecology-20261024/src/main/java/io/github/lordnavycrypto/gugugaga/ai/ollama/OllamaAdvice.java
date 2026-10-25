package io.github.lordnavycrypto.gugugaga.ai.ollama;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/** An inert shadow recommendation: never issues an entity command. */
public record OllamaAdvice(Mode mode, double intensity, String rationale) {
    public enum Mode { CALM, OBSERVE, SUSPENSE }

    public static OllamaAdvice parse(String raw) {
        JsonObject obj = JsonParser.parseString(raw).getAsJsonObject();
        Mode mode;
        try {
            mode = Mode.valueOf(obj.get("mode").getAsString());
        } catch (RuntimeException invalid) {
            mode = Mode.CALM;
        }
        double intensity;
        try {
            intensity = obj.get("intensity").getAsDouble();
        } catch (RuntimeException invalid) {
            intensity = 0.0;
        }
        if (!Double.isFinite(intensity)) intensity = 0.0;
        intensity = Math.max(0.0, Math.min(1.0, intensity));
        String reason = obj.has("rationale") && obj.get("rationale").isJsonPrimitive()
                ? obj.get("rationale").getAsString() : "";
        reason = reason.replaceAll("[\\r\\n\\t]", " ");
        if (reason.length() > 180) reason = reason.substring(0, 180);
        return new OllamaAdvice(mode, intensity, reason);
    }
}
