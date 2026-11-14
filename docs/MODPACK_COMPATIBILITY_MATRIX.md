# Modpack Compatibility Matrix / 整合包兼容矩阵

Target: Minecraft 1.21.1 / NeoForge / Java 21.

## Shared base dependencies
JEI, Jade, Cloth Config, GeckoLib, FerriteCore, YACL, ModernFix, Fusion, Lithostitched.

## Newly added exact CurseForge files

| Module | Project ID | File ID | Role | Notes |
|---|---:|---:|---|---|
| Balm | 531761 | 9406246 | Library | Required by Waystones and Cooking for Blockheads |
| Cooking for Blockheads | 231484 | 7966112 | Kitchen/recipe interface | Balm dependency included |
| Waystones | 245755 | 8969751 | Travel network | Balm dependency included |
| Comforts | 276951 | 7515858 | Sleeping bags/hammocks | NeoForge 1.21.1 |
| Carry On | 274259 | 8512888 | Carry blocks/entities | NeoForge 1.21.1 |
| Embeddium | 908741 | 6118392 | Client rendering performance | Optional |
| Alternate Current | 548115 | 5665477 | Redstone performance | NeoForge 1.21.x |
| Clumps | 256717 | 5623731 | XP orb performance | NeoForge 1.21.1 |
| Entity Culling | 448233 | 8924414 | Client rendering performance | Optional |
| ImmediatelyFast | 686911 | 5608731 | Client rendering performance | Optional |
| Mouse Tweaks | 60089 | 5637846 | Inventory UI | Optional client |
| BetterF3 | 401648 | 5873258 | Debug HUD | Optional client beta |

## Integration gates

1. **Loader gate:** selected files must resolve for Minecraft 1.21.1 / NeoForge 21.1.x.
2. **Dependency gate:** Balm is included for both Waystones and Cooking for Blockheads.
3. **Worldgen gate:** keep RealScale as the only broad terrain overhaul; test added dimensions separately.
4. **Performance gate:** test Embeddium, Entity Culling and ImmediatelyFast together on the actual client; keep them optional until the graphics stack passes.
5. **Survival gate:** Agriculture owns water/salt and the 25 nutrient reserves. Do not run two thirst systems simultaneously.
6. **Nutrition gate:** N-key Nutrition Mode OFF pauses only nutrient intake/depletion/effects and preserves stored reserves; water/salt remains active.
7. **Server gate:** filter client-only UI/rendering modules out of the dedicated-server install.
8. **Release gate:** package only after all four custom JARs are present and SHA-256 hashes are recorded.
