# The Beautiful Unknown — Modpack Profiles

Target baseline: Minecraft 1.21.1 · NeoForge 21.1.259 · Java 21.

## Profiles
- **Core** — shared libraries and performance baseline.
- **Exploration** — structure redesigns, travel/navigation, animals and vanilla-like mob additions. No replacement terrain generator is included.
- **Dimensions Experimental** — Exploration plus Dimensional Doors 6.2.1. Its 1.21.1 NeoForge file is Beta, so keep it isolated during testing.
- **Nightfall Creatures** — cave/stalking horror entities.
- **Nightfall Liminal** — ambient horror events and audio.
- **Nightfall** — combined horror profile.
- **Tough Survival Test** — Tough As Nails in a separate test profile. Its 1.21.1 file is Beta and its thirst system overlaps with Agriculture's hydration/salt system; do not combine both thirst systems until configured and tested.
- **Serene Seasons** — a separate seasonal profile with GlitchCore.
- **Spring Season** — a separate Seasonal Survival profile.

Do not install Serene Seasons and Seasonal Survival together.

## Four owner-built JARs
The exported development ZIPs place these four JARs under `overrides/mods/`:
- Backroom Agriculture 4.0.0-alpha.1
- RealScale 2.0.0-preview.2-test.6
- Redstone Systems Engineering 1.0.20-alpha
- Swarm Mobs 0.12.0-alpha.10

Third-party mods are represented by CurseForge project/file IDs in each manifest and are resolved by the CurseForge launcher.

## Important build status
The included Agriculture JAR is the last successful 4.0.0-alpha.1 artifact. The newer N-key Nutrition Mode ON/OFF source branch has not completed CI/build yet. The switch is therefore **not verified as present in the included JAR**.

## World-generation policy
RealScale remains the terrain foundation. Added modules focus on structures, navigation, dimensions, and ecosystem content rather than replacing the terrain generator. Use a fresh world for structure/dimension tests.

## Test gates
1. Import one profile at a time into CurseForge.
2. Launch client and verify required dependencies resolve.
3. Start a dedicated server with the same manifest and mods.
4. Test structures/dimensions in a fresh world.
5. Test each horror profile separately before enabling the combined profile.
6. Test Tough As Nails separately from Agriculture hydration/salt.
7. Test the two seasonal systems separately.
8. Do not call any profile stable until client, server, worldgen, and chunk reload tests pass.
