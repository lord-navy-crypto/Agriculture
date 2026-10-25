# Optional Ollama integration — ported from SWARM-MOBS design

Original reference project: https://github.com/lord-navy-crypto/SWARM-MOBS
Relevant source: `src/main/java/dev/swarmmobs/ai/ollama`, `docs/OLLAMA_SHADOW.md`.

## What works
- Offline by default: **no Ollama runtime dependency** and no auto connection on boot.
- Entirely optional `/gugugaga ai` commands for server operators (permission level 2).
- Explicit `detect` / `models` probes the local `/api/tags` endpoint.
- Choose a named installed local model, then request a one-off structured `/api/chat` recommendation.
- Advice uses only an aggregate nearby observer count and state counts. No usernames, player positions, files or saved worlds are transmitted.
- Server-side asynchronous HTTP, 4-second timeout, no blocking of the game tick.
- Single in-flight request limit, strict loopback allowlist, fixed HTTP port, no redirects, strict output sanitization.
- Model failure, missing Ollama or AI disabled: **the normal deterministic observer behavior continues untouched**.

## Commands
```text
/gugugaga ai status
/gugugaga ai detect
/gugugaga ai models
/gugugaga ai on
/gugugaga ai model llama3.2:3b
/gugugaga ai shadow
/gugugaga ai off
```

The model name is only an example; `models` lists names you actually have installed.

Server config is `gugugaga-server.toml` under the world server config directory (NeoForge SERVER config), keys `local_ollama.enabled` and `local_ollama.model`.

## Important scope
This is *shadow mode*, inspired by SWARM-MOBS. Its suggestion is reported to command operator but **is not applied to movement, attacks, blocks or player state**. The non-AI observer is the only gameplay controller.

A future separately opt-in `assist` mode may apply a very narrow validated pacing recommendation (cooldown range, TTL, hold-time hysteresis) *after* benchmarks and explicit gameplay tests. Until then do not mislabel shadow requests as active AI control.

Network requests originate on the physical **server** host. A separate multiplayer client installing Ollama locally does not automatically expose it to the dedicated server; we intentionally do not scan or contact LAN IPs.

## Privacy/security
Fixed `http://127.0.0.1:11434`, `/api/tags` and `/api/chat` only. No cloud API keys, no port discovery, no file reading, no hidden process checks, and no background inference. Commands are restricted to operators.
