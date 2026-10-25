import io.github.lordnavycrypto.gugugaga.ai.ollama.OllamaEndpointPolicy;
import java.net.URI;

public final class OllamaEndpointPolicySmoke {
    private static void valid(String base, String route) {
        URI u = OllamaEndpointPolicy.api(base, route);
        if (!u.getPath().equals(route)) throw new AssertionError("Unexpected Ollama route " + u);
    }
    private static void deny(String base) {
        try {
            OllamaEndpointPolicy.api(base, "/api/tags");
            throw new AssertionError("Remote/unsafe Ollama URL allowed: " + base);
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }
    public static void main(String[] unused) {
        valid("http://127.0.0.1:11434", "/api/tags");
        valid("http://localhost:11434", "/api/chat");
        valid("http://[::1]:11434", "/api/tags");
        deny("http://example.com:11434");
        deny("http://192.168.1.2:11434");
        deny("https://127.0.0.1:11434");
        deny("file:///tmp/foo");
        deny("http://localhost:9999");
        deny("http://localhost:11434/overridden");
        deny("http://localhost:11434?redirect=evil");
        deny("http://user:secret@127.0.0.1:11434");
        try {
            OllamaEndpointPolicy.api(null, "/api/delete");
            throw new AssertionError("Unapproved Ollama route allowed");
        } catch (IllegalArgumentException expected) {
            // expected
        }
        System.out.println("OllamaEndpointPolicySmoke: 12 checks passed");
    }
}
