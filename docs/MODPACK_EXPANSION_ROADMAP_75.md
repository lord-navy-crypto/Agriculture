# 75-Module Target / 75 模组目标

## Current state

The Nightfall manifest now contains **30 third-party mod references**. The current development export adds **3 owner-built JARs**, with Redstone Systems Engineering still pending its current verified build artifact. That is **33 direct module entries in the current Nightfall preview**, not a completed 75-mod release.

## Target architecture

The goal is approximately 75 unique modules, counting actual mod projects once rather than counting repeated references across profiles.

| Layer | Target | Purpose |
|---|---:|---|
| Owner-built core | 4 | Agriculture, RealScale, RSE, Swarm Mobs |
| Shared libraries and APIs | 14 | Required dependencies and configuration APIs |
| Natural survival and ecology | 8 | Seasons, temperature, wildlife, crops and kitchen |
| Dimensions and travel | 6 | Separate dimensions, navigation and travel tools |
| Structures and exploration | 12 | Settlements, ruins and exploration sites after content review |
| Horror creatures and events | 12 | Stalkers, cave entities, ambient events and audio |
| Performance and client quality | 12 | Rendering, memory, entity culling, inventory and map UI |
| Decoration and interaction | 7 | Furniture, storage, campsites and environmental details |
| **Target** | **75** | |

## Next research queue — not yet installed

These are candidates for exact Minecraft 1.21.1 NeoForge file/dependency verification, not confirmed installations:

- **Survival/weather:** Tough As Nails, Weather2, CoroUtil.
- **Atmosphere:** AmbientSounds, Presence Footsteps, Effective, Falling Leaves, Particular, Dynamic Lights.
- **Food/status UI:** AppleSkin.
- **Navigation/UI:** Explorer's Compass, Better Advancements, Controlling, Shulker Box Tooltip.
- **Libraries:** Architectury API, Resourceful Lib, Bookshelf, Collective, Placebo, SuperMartijn642 Core Lib, Patchouli.
- **Structures:** Structory, Explorify, Dungeons and Taverns, Additional Structures.
- **Decoration:** Macaw's Furniture, Macaw's Doors, Macaw's Windows, Macaw's Roofs, Macaw's Bridges, Macaw's Fences and Walls, Handcrafted, Chipped, Decorative Blocks, FramedBlocks, Another Furniture.
- **Horror:** The Cave Crawler, The Midnight Lurker NF, From The Caves, Goatman, I Heard It Too.

## Hard rules

1. No additional broad Overworld terrain overhaul. RealScale remains authoritative for terrain, biomes and mineral distribution.
2. Dimension additions must use separate dimension registries and be tested without replacing RealScale's Overworld pipeline.
3. Agriculture remains authoritative for water/salt and its 25 nutrient reserves. Nutrition Mode OFF pauses only the nutrient subsystem.
4. Do not run two thirst systems simultaneously. Tough As Nails stays pending configuration review.
5. Seasonal systems remain separate profiles; do not load Serene Seasons and Seasonal Survival together.
6. Every candidate must pass loader, dependency, client/server, fresh-world, spawn-density and performance checks before entering a manifest.
7. A profile is not a release until all four owner-built JARs are present and both client and dedicated server pass startup/join tests.
