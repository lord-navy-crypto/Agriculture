# Rewrite v0.1 — directly playable infection game engine

This is a clean new code line, not the archived V1–V21 documentation project.

## Implemented
- A real executable Minecraft-only **S → E → I → R** stage model on vanilla animal entity UUIDs.
- Every 20 Minecraft game ticks, at most 64 real nearby vanilla animals are sampled from already loaded regions.
- A seeded **INFECTIOUS** animal can put nearby **SUSCEPTIBLE** animals into **EXPOSED** state via 3-D in-game contact detection; exposure progresses to infectious and then recovered on bounded game timers.
- New exposures cannot contact-infect other animals during the same update frame (snapshot-based transition).
- INFECTIOUS animals emit bounded vanilla ENCHANT particles.
- Numeric and stage state live in-memory, organized by Minecraft server and dimension. Server shutdown clears the state; off-screen animals are not continuously simulated. **No persistence is implemented yet.**
- No blocks, new pathogen entities, extra save schema, or external network services.

## Operator commands
In a disposable Creative-mode or test world containing two or more vanilla animals:
```mcfunction
/gugugaga infection seed
/gugugaga infection status
/gugugaga infection clear
```

`seed` finds the nearest living animal within 6 blocks and starts its *fictional in-game* infectious state. `status` reports S/E/I/R counts for active animals in this dimension. `clear` clears active dimension memory. Seed and clear require operator permission 2; status is readable by a player.

## Math and limits
The simulation checks 3-D distances using actual loaded Minecraft animal positions. Every game simulation frame, each infectious animal can expose susceptible animals within **3.5 in-game blocks**; only a beginning-of-frame infectious animal may do so. New exposures enter EXPOSED age 0, remain there for 3 frames, then turn INFECTIOUS; an infectious state lasts 8 frames before RECOVERED. These numbers are **Minecraft rules**, not imported pathogen infectiousness parameters or clinically realistic timings.

Default sampled host cap: 64 per dimension. Max exposure checks: 4096; visual events: eight per dimension per frame. No player infection, no forced chunk loading, no actual medical effects or disease rates.

## Acceptance
- Pure Java21 `InfectionEngineSmoke` checks initial and second-generation contact, explicit latency, resolution to recovery, false cross-distance contact, fairness under deterministic UUID order, duplicate protection and pairing caps.
- `.github/workflows/rewrite-build.yml` compiles the simulation core and NeoForge jar, and uploads the built mod.
- **Still required**: real in-game command smoke, dedicated server startup, save/reload design, multi-client behavior and tracking across chunk unloads.

## Project structure and rollback
Old snapshot branch: `archive/pathogen-v1-v21-20261025` @ `ad618120795fc699949aa008a45db94882b3e7db`. The old PR #44 is closed without merge. New project branch is `rewrite/virus-mod-core-20261025` originating at the pre-existing minimal `main`. Do not merge old archives into the rewrite.
