# Gugu-Gaga Virus Mod — Clean Rewrite (0.1 dev)

Minecraft 1.21.1 / NeoForge 21.1.259 / Java 21.

**New direction:** playable in-world infection game engine, not the previous informational 0–15 pathogen-map overlay. This branch is rebuilt from the bare main baseline.

## Current prototype
A server-side animal infection model runs directly on actual loaded vanilla Minecraft animal entities. It supports four in-game states: **Susceptible → Exposed → Infectious → Recovered (SEIR)**.

A seeded infectious animal can expose nearby susceptible animals. Later game frames advance the exposure stage into an infectious stage, which can produce another generation of contacts, before recovery. ENCHANT particles mark infectious animals.

Run as a player:
```text
/gugugaga infection status
```
Operators can start or reset a demonstration:
```text
/gugugaga infection seed
/gugugaga infection clear
```

Status is dimension-scoped. The rewrite has no persistent infected-entity save data yet; multiplayer, actual in-game optics and save/restart are not fully validated. Contact range and transition times are **gameplay settings, not specific real pathogens' biological rates**.

## Build
Use Java 21 and Gradle 9.2.1:
```sh
gradle --no-daemon build
```
The packaged JAR is under `build/libs/`. NeoForge's plugin downloads the necessary Minecraft dependencies.

## Archived legacy development
The original V1–V21 code, research, tests, and history remain at:
`archive/pathogen-v1-v21-20261025` (commit `ad618120795fc699949aa008a45db94882b3e7db`).

PR #44 was closed **without merging**. The old prototype and files are intact. Do not treat old 0–15 numerical visual indicators as part of this rewrite.

See `docs/REWRITE_V0_1_ACCEPTANCE.md` for test coverage and known limits.
