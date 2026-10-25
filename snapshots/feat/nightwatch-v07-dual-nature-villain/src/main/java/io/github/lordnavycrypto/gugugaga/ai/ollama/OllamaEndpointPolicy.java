package io.github.lordnavycrypto.gugugaga.ai.ollama;

import java.net.URI;
import java.util.Locale;

/**
 * Safe, local-only Ollama policy inherited conceptually from SWARM-MOBS.
 * Never scans ports, processes, files, or LAN peers.
 */
public final class OllamaEndpointPolicy {
    public static final String DEFAULT_ENDPOINT = "http://127.0.0.1:11434";

    private OllamaEndpointPolicy() {}

    public static URI api(String endpoint, String route) {
        if (!"/api/tags".equals(route) && !"/api/chat".equals(route)) {
            throw new IllegalArgumentException("Unapproved Ollama API route");
        }
        URI base = URI.create(endpoint == null || endpoint.isBlank()
                ? DEFAULT_ENDPOINT : endpoint);
        String host = base.getHost() == null ? "" : base.getHost().toLowerCase(Locale.ROOT);
        if (!"http".equalsIgnoreCase(base.getScheme())
                || !(host.equals("127.0.0.1") || host.equals("localhost")
                    || host.equals("::1") || host.equals("[::1]"))
                || base.getRawUserInfo() != null
                || base.getRawPath() != null && !base.getRawPath().isEmpty()
                || base.getRawQuery() != null || base.getRawFragment() != null
                || (base.getPort() != -1 && base.getPort() != 11434)) {
            throw new IllegalArgumentException("Ollama access must be local loopback HTTP on port 11434");
        }
        return URI.create(base.toString().replaceAll("/+$", "") + route);
    }
}
