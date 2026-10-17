package io.github.lordnavycrypto.gugugaga.ai.ollama;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/** Explicitly-triggered, asynchronous loopback-only API calls. */
public final class OllamaClient {
    private static final Duration TIMEOUT = Duration.ofSeconds(4);
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

    public CompletableFuture<List<String>> installedModels() {
        HttpRequest req = HttpRequest.newBuilder(
                OllamaEndpointPolicy.api(OllamaEndpointPolicy.DEFAULT_ENDPOINT, "/api/tags"))
                .timeout(TIMEOUT).header("Accept", "application/json").GET().build();
        return http.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenApply(OllamaClient::validBody)
                .thenApply(body -> {
                    JsonObject obj = JsonParser.parseString(body).getAsJsonObject();
                    List<String> names = new ArrayList<>();
                    if (obj.has("models") && obj.get("models").isJsonArray()) {
                        for (JsonElement element : obj.getAsJsonArray("models")) {
                            if (!element.isJsonObject()) continue;
                            JsonObject model = element.getAsJsonObject();
                            JsonElement name = model.has("name") ? model.get("name") : model.get("model");
                            if (name != null && name.isJsonPrimitive()) {
                                String value = name.getAsString();
                                if (validModelName(value)) names.add(value);
                            }
                            if (names.size() >= 64) break;
                        }
                    }
                    return List.copyOf(names);
                });
    }

    public CompletableFuture<OllamaAdvice> advise(String model, int activeObservers, String phaseSummary) {
        if (!validModelName(model)) {
            return CompletableFuture.failedFuture(
                    new IllegalArgumentException("Choose an installed local model first"));
        }
        JsonObject schema = new JsonObject();
        schema.addProperty("type", "object");
        schema.addProperty("additionalProperties", false);
        JsonObject properties = new JsonObject();
        JsonObject mode = new JsonObject();
        mode.addProperty("type", "string");
        JsonArray allowed = new JsonArray();
        for (String value : List.of("CALM", "OBSERVE", "SUSPENSE")) allowed.add(value);
        mode.add("enum", allowed);
        properties.add("mode", mode);
        JsonObject intensity = new JsonObject();
        intensity.addProperty("type", "number");
        intensity.addProperty("minimum", 0.0);
        intensity.addProperty("maximum", 1.0);
        properties.add("intensity", intensity);
        JsonObject rationale = new JsonObject();
        rationale.addProperty("type", "string");
        rationale.addProperty("maxLength", 180);
        properties.add("rationale", rationale);
        schema.add("properties", properties);
        JsonArray required = new JsonArray();
        for (String value : List.of("mode", "intensity", "rationale")) required.add(value);
        schema.add("required", required);

        JsonArray messages = new JsonArray();
        JsonObject sys = new JsonObject();
        sys.addProperty("role", "system");
        sys.addProperty("content", "You are a Minecraft suspense pacing adviser. Return ONLY one high-level "
                + "recommendation based on aggregate observer counts. Never suggest player coordinates, attacks, "
                + "per-tick actions, block edits, or unavoidable jumpscares. Prefer CALM when uncertain.");
        messages.add(sys);
        JsonObject user = new JsonObject();
        user.addProperty("role", "user");
        user.addProperty("content", "Nearby observer count=" + Math.max(0, Math.min(32, activeObservers))
                + "; states=" + phaseSummary.substring(0, Math.min(96, phaseSummary.length()))
                + ". Recommend pacing mode, intensity 0..1, short rationale.");
        messages.add(user);
        JsonObject payload = new JsonObject();
        payload.addProperty("model", model);
        payload.addProperty("stream", false);
        payload.add("messages", messages);
        payload.add("format", schema);
        JsonObject options = new JsonObject();
        options.addProperty("temperature", 0);
        payload.add("options", options);
        HttpRequest req = HttpRequest.newBuilder(
                OllamaEndpointPolicy.api(OllamaEndpointPolicy.DEFAULT_ENDPOINT, "/api/chat"))
                .timeout(TIMEOUT)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();
        return http.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenApply(OllamaClient::validBody)
                .thenApply(body -> {
                    JsonObject root = JsonParser.parseString(body).getAsJsonObject();
                    JsonObject message = root.getAsJsonObject("message");
                    if (message == null || !message.has("content")) {
                        throw new IllegalArgumentException("Ollama response has no message content");
                    }
                    return OllamaAdvice.parse(message.get("content").getAsString());
                });
    }

    /**
     * Separate voluntary local character conversation. Only the explicit,
     * sanitized player's /gugugaga talk input is sent to the LOCAL server's
     * 127.0.0.1 Ollama, if an operator enables both local-AI switches.
     * No server names, player identities, coordinates, chat history, or
     * gameplay commands are transmitted. Model output is text only.
     */
    public CompletableFuture<String> chat(String model,String userMessage) {
        if(!validModelName(model))
            return CompletableFuture.failedFuture(
                    new IllegalArgumentException("Select a locally installed model"));
        String question=io.github.lordnavycrypto.gugugaga.ai.GuguDialoguePolicy
                .cleanInput(userMessage);
        if(question.isBlank())
            return CompletableFuture.failedFuture(
                    new IllegalArgumentException("A short question is required"));
        JsonArray messages=new JsonArray();
        JsonObject instruction=new JsonObject();
        instruction.addProperty("role","system");
        instruction.addProperty("content",
                "You are Gugu-Gaga, a fictional penguin-like Minecraft mystery character. "
                + "Reply in the user's language with a short, charming, slightly eerie line. "
                + "No threats, violence, claims about the real world, private data, "
                + "system alerts, player tracking, commands, or external links. "
                + "You only speak in game chat. Maximum 140 characters.");
        messages.add(instruction);
        JsonObject user=new JsonObject();
        user.addProperty("role","user");
        user.addProperty("content",question);
        messages.add(user);
        JsonObject payload=new JsonObject();
        payload.addProperty("model",model);
        payload.addProperty("stream",false);
        payload.add("messages",messages);
        JsonObject options=new JsonObject();
        options.addProperty("temperature",0.4);
        options.addProperty("num_predict",90);
        payload.add("options",options);
        HttpRequest req=HttpRequest.newBuilder(
                OllamaEndpointPolicy.api(OllamaEndpointPolicy.DEFAULT_ENDPOINT,
                        "/api/chat"))
                .timeout(TIMEOUT).header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();
        return http.sendAsync(req,HttpResponse.BodyHandlers.ofString())
                .thenApply(OllamaClient::validBody)
                .thenApply(body->{
                    JsonObject response=JsonParser.parseString(body).getAsJsonObject();
                    JsonObject message=response.getAsJsonObject("message");
                    if(message==null||!message.has("content"))
                        throw new IllegalArgumentException("Empty local model response");
                    String answer=io.github.lordnavycrypto.gugugaga.ai.GuguDialoguePolicy
                            .safeAnswer(message.get("content").getAsString());
                    if(answer.isBlank())
                        throw new IllegalArgumentException("No character reply");
                    return answer;
                });
    }

    public static boolean validModelName(String name) {
        return name != null && name.matches("[A-Za-z0-9._:/-]{1,96}");
    }

    private static String validBody(HttpResponse<String> response) {
        if (response.statusCode() != 200)
            throw new IllegalStateException("Ollama returned HTTP " + response.statusCode());
        String body = response.body();
        if (body == null || body.length() > 65536)
            throw new IllegalStateException("Ollama returned an unexpectedly large response");
        return body;
    }
}
