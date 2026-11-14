# The Beautiful Unknown — CurseForge Pack Family / CurseForge 整合包系列

## English

Target: Minecraft 1.21.1 · NeoForge 21.1.259 · Java 21.

### Profiles

- **Core** — RealScale/custom-module base, Regions Unexplored biome layer, shared libraries and performance support.
- **Nightfall Creatures** — Core plus Cave Dweller, The Boy and the Bath, The Growling Man, and optional Silent Caves.
- **Liminal Horror** — Core plus The Knocking, Footsteps, and Chest Opener ambient events.
- **Nightfall** — Combines the creature and liminal profiles.
- **Serene Seasons** — Core plus Serene Seasons and GlitchCore.
- **Spring Season** — Core plus Seasonal Survival.

### Compatibility rules

- Lithostitched 1.8.1 (NeoForge 21.1) is now included in every profile. It satisfies the Agriculture and Regions Unexplored minimum dependency of 1.7.11 / 1.7.9+.
- GeckoLib 4.9.3 is shared by the custom Agriculture/RSE stack and the animated horror entities.
- Cave Dweller requires Pehkui; Pehkui 3.8.3 (NeoForge 1.21.1) is included only in the creature profiles.
- Serene Seasons and Seasonal Survival are deliberately separate profiles. Do not install both at once: both own the seasonal calendar and climate changes.
- Agriculture also has its own 32-day calendar. The two seasonal profiles are integration-test variants until crop, temperature, foliage, and calendar behavior are verified together.
- Regions Unexplored is the additional Overworld/Nether biome layer. RealScale remains the primary terrain and dimension-generation layer. Per the project owner's in-game test, biome expansion can coexist with RealScale; the full pack still requires fresh-world generation smoke tests.
- Horror additions are kept separate from the normal world profile. Start with default spawn settings, then tune rates after checking entity density and server tick time.
- Silent Caves is marked optional because its project is still described as WIP.
- CurseForge manifests reference files hosted by their authors; this repository does not rehost third-party JARs.

### Owner-built modules

The final exported ZIP must also include the four owner-built JARs under `overrides/mods/`:
- Backroom Agriculture (includes THRESHOLD)
- RealScale Terrain
- Redstone Systems Engineering
- Swarm Mobs

The packer will only call the export complete after all four JARs and their SHA-256 hashes are present.

## 中文

目标环境：Minecraft 1.21.1 · NeoForge 21.1.259 · Java 21。

### 整合包版本

- **Core 基础版**：RealScale/自研模组基础、Regions Unexplored 群系扩展、公共依赖和性能支持。
- **Nightfall Creatures 生物恐怖版**：增加 Cave Dweller、The Boy and the Bath、The Growling Man，以及可选的 Silent Caves。
- **Liminal Horror 心理氛围版**：增加敲门、脚步声和箱子异常事件。
- **Nightfall 完整恐怖版**：合并生物恐怖和心理氛围两组内容。
- **Serene Seasons 版**：增加 Serene Seasons 和 GlitchCore。
- **Spring Season 版**：增加 Seasonal Survival。

### 兼容性规则

- 所有版本都加入 Lithostitched 1.8.1（NeoForge 21.1），满足 Agriculture 和 Regions Unexplored 对 1.7.11 / 1.7.9+ 的最低依赖。
- GeckoLib 4.9.3 作为自研农业/RSE 和动画恐怖实体的共同依赖。
- Cave Dweller 需要 Pehkui；Pehkui 3.8.3（NeoForge 1.21.1）只加入恐怖生物版本。
- Serene Seasons 和 Seasonal Survival 分成独立版本，不要同时安装，因为两个模组都控制季节日历和气候变化。
- Agriculture 自身也有 32 天月相日历。两个季节版本在完成作物、温度、植被和日历联测前，都属于集成测试版本。
- Regions Unexplored 负责额外的主世界/下界群系；RealScale 仍然是地形和维度生成的主要控制层。根据项目所有者的游戏内测试，群系扩展可与 RealScale 共存；整合包仍需在全新存档中做完整生成冒烟测试。
- 恐怖扩展与普通生存基础版分开。先使用默认生成参数，再根据实体密度和服务器 tick 时间调整生成率。
- Silent Caves 项目仍标记为 WIP，因此在清单中设为可选。
- CurseForge 清单引用作者托管的文件；本仓库不会重新托管第三方 JAR。

### 自研模组

最终导出的 ZIP 还必须在 `overrides/mods/` 中包含四个自研 JAR：
- Backroom Agriculture（含 THRESHOLD）
- RealScale Terrain
- Redstone Systems Engineering
- Swarm Mobs

只有四个 JAR 和对应 SHA-256 校验值全部齐备后，打包脚本才会将导出标记为完整。
