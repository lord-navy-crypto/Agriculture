# Modpack Compatibility Matrix / 整合包兼容矩阵

| Component | CurseForge project:file | Target | Role |
|---|---:|---|---|
| JEI | 238222:7391682 | NeoForge 1.21.1 | Recipe browser |
| Jade | 324717:8591319 | NeoForge 1.21.1 | In-world information |
| Cloth Config | 348521:5729127 | NeoForge 1.21.1 | Config UI library |
| GeckoLib | 388172:8893490 | NeoForge 1.21.1 | Animated entity library |
| FerriteCore | 429235:7524151 | NeoForge 1.21.1 | Memory optimization |
| Regions Unexplored | 659110:9252522 | NeoForge 1.21.1 | Overworld/Nether biome expansion |
| YACL | 667299:7305639 | NeoForge 1.21.1 | Config UI library |
| ModernFix | 790626:9309399 | NeoForge 1.21.1 | Performance and memory |
| Fusion | 854949:8822088 | NeoForge 1.21.1 | Connected texture library |
| Lithostitched | 936015:9170709 | NeoForge 1.21.1 | Worldgen library |
| Pehkui | 319596:5446174 | NeoForge 1.21.1 | Cave Dweller dependency |
| Cave Dweller NeoForged | 1642074:9408240 | NeoForge 1.21.1 | Cave entity horror |
| The Boy and the Bath | 1033010:7909611 | NeoForge 1.21.1 | Stalking entity horror |
| The Growling Man | 1681256:8805311 | NeoForge 1.21.1 | Stalking entity horror |
| Silent Caves | 1502980:7975686 | NeoForge 1.21.1 | Optional WIP cave entity |
| HORROR - The Knocking | 1725406:9053738 | NeoForge 1.21.1 | 3D environmental audio |
| HORROR - Footsteps | 1731290:9285310 | NeoForge 1.21.1 | Environmental event audio |
| HORROR - Chest Opener | 1725693:9074561 | NeoForge 1.21.1 | Container-based ambient event |
| Serene Seasons | 291874:5661337 | NeoForge 1.21.1 | Separate seasonal profile |
| GlitchCore | 955399:8109792 | NeoForge 1.21.1 | Serene Seasons dependency |
| Seasonal Survival | 1786974:9480925 | NeoForge 1.21.1 | Separate spring/four-season profile |

## Integration gates

1. **Loader gate:** all selected files must resolve for Minecraft 1.21.1 / NeoForge 21.1.x.
2. **Dependency gate:** GeckoLib 4.9.3 and Lithostitched 1.8.1 are shared; Pehkui is creature-profile-only; GlitchCore is Serene-profile-only.
3. **Worldgen gate:** create a fresh world and verify RealScale terrain, biome placement, Nether biomes, structure placement, and End generation. Never reuse a pre-generated test world to judge biome placement.
4. **Entity gate:** test spawn density, despawn behavior, chunk unload/reload, and server tick time with each horror profile separately before enabling all of them together.
5. **Season gate:** never combine Serene Seasons and Seasonal Survival. Validate each against Agriculture's internal calendar independently.
6. **Server gate:** run a dedicated-server boot and client join test for every profile.
7. **Release gate:** package only after all four custom JARs are present and SHA-256 hashes are recorded.
