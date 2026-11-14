# The Beautiful Unknown — CurseForge Pack Family / CurseForge 整合包系列

Target: Minecraft 1.21.1 · NeoForge 21.1.259 · Java 21.

## English

### Profiles
- **Core** — four owner-built modules, existing terrain/biome stack, travel, kitchen, comfort, redstone/performance and client UI support.
- **Nightfall Creatures** — Core plus the stalking-entity horror profile.
- **Liminal Horror** — Core plus environmental horror events.
- **Nightfall** — combined creature and liminal profiles.
- **Serene Seasons** — Core plus Serene Seasons and GlitchCore.
- **Spring Season** — Core plus Seasonal Survival.

### New modules in this manifest revision
- **Travel and comfort:** Waystones, Balm, Comforts, Carry On.
- **Kitchen:** Cooking for Blockheads (Balm is included as a required library).
- **Performance:** Embeddium, Alternate Current, Clumps, Entity Culling, ImmediatelyFast.
- **Client quality-of-life:** Mouse Tweaks and BetterF3.
- Client-only/performance add-ons are optional in the manifests. Sound Physics Remastered, map/navigation and wildlife candidates remain in the next compatibility batch until their exact file references are finalized.

### Survival and nutrition integration
- Agriculture remains authoritative for its existing water/salt system and 25 nutrient reserves.
- The N-key Nutrition Mode toggle is designed to pause only nutrient intake/depletion/effects while preserving stored reserves; it must not disable water/salt.
- Tough As Nails remains in the compatibility queue until its exact package source and configuration are verified.
- If a separate temperature/thirst mod is enabled later, its thirst feature must not run alongside Agriculture's water/salt subsystem. Prefer a single thirst authority.

### Compatibility rules
- Keep the existing RealScale terrain and biome generation modules unchanged. Do not add another broad terrain overhaul.
- Separate dimensions are tested independently from the Overworld generator.
- Waystones should be configured with suitable costs/cooldowns to preserve travel and exploration.
- Run a fresh-world test, a dedicated-server boot, and a client join before release.
- Only mark the package complete when all four owner-built JARs and SHA-256 hashes are present.

### Owner-built modules
The final exported ZIP must include these four JARs under `overrides/mods/`:
- Backroom Agriculture (includes THRESHOLD)
- RealScale Terrain
- Redstone Systems Engineering
- Swarm Mobs

The packer only reports a complete export after all four JARs and their SHA-256 hashes are present.

## 中文

目标环境：Minecraft 1.21.1 · NeoForge 21.1.259 · Java 21。

### 版本
- **Core 基础版**：四个自研模组、现有地形/群系系统、旅行、厨房、舒适度、红石/性能和客户端界面支持。
- **Nightfall Creatures 生物恐怖版**：基础版加潜伏型恐怖实体。
- **Liminal Horror 氛围恐怖版**：基础版加环境恐怖事件。
- **Nightfall 完整恐怖版**：合并两组恐怖内容。
- **Serene Seasons 版**：基础版加 Serene Seasons 与 GlitchCore。
- **Spring Season 版**：基础版加 Seasonal Survival。

### 本轮新增模组
- **旅行与舒适度：** Waystones、Balm、Comforts、Carry On。
- **厨房：** Cooking for Blockheads（已加入必需前置 Balm）。
- **性能：** Embeddium、Alternate Current、Clumps、Entity Culling、ImmediatelyFast。
- **客户端体验：** Mouse Tweaks、BetterF3。
- 客户端/性能扩展在清单中标为可选。Sound Physics Remastered、地图/导航和野生动物候选放在下一批兼容性核验中，待精确文件引用确认后再加入。

### 生存与营养系统整合
- Agriculture 继续负责现有水/盐系统和 25 项营养储备。
- N 键营养模式开关只暂停营养摄入、消耗和反馈效果，并保留已有储备；不能关闭水/盐系统。
- Tough As Nails 暂留在兼容性队列，等待确认准确的打包来源和配置。
- 如果以后启用独立体温/口渴模组，其口渴机制不能与 Agriculture 水/盐系统同时运行。必须明确唯一的口渴系统。

### 兼容性规则
- 保留现有 RealScale 地形和群系模块，不再添加另一套大范围地形改良。
- 独立维度与主世界生成器分开测试。
- Waystones 应配置合适的成本和冷却，保留旅行与探索体验。
- 发布前必须进行新世界生成、专用服务器启动和客户端加入测试。
- 只有四个自研 JAR 和 SHA-256 校验值齐全后，才能标记为完整整合包。

### 自研模组
最终导出的 ZIP 必须在 `overrides/mods/` 中包含四个 JAR：
- Backroom Agriculture（含 THRESHOLD）
- RealScale Terrain
- Redstone Systems Engineering
- Swarm Mobs

只有四个 JAR 和对应 SHA-256 校验值全部齐备后，打包脚本才会标记导出完整。
