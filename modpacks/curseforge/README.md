# The Beautiful Unknown — CurseForge Pack Family / CurseForge 整合包系列

## English

This folder contains four importable CurseForge manifest variants for Minecraft 1.21.1 / NeoForge 21.1.259.

- **Core** — the four custom modules, biome expansion, performance libraries, and required engineering/configuration libraries.
- **Nightfall** — Core plus a creature-focused horror expansion and environmental audio.
- **Serene Seasons** — Core plus Serene Seasons and its required library.
- **Spring Season** — Core plus the separate Seasonal Survival season system.

Do not install Serene Seasons and Seasonal Survival together. The two season variants are deliberately separate. Both external season variants also need in-game validation against Agriculture's built-in 32-day lunar calendar; they are test profiles, not a claim that three season engines have already been unified.

### Custom modules supplied in the final export

The final ZIP's `overrides/mods/` directory is intended to contain the four owner-built JARs:

- Backroom Agriculture (includes THRESHOLD)
- RealScale Terrain
- Redstone Systems Engineering
- Swarm Mobs

### Manual library dependency

Lithostitched 1.7.11 for NeoForge 21.1 is required by Backroom Agriculture and Regions Unexplored. It is not currently represented by a confirmed 1.21.1 NeoForge CurseForge file in this pack manifest, so add it manually from the exact Modrinth version page before launching: https://modrinth.com/mod/lithostitched/version/1.7.11-neoforge-21.1

### World-generation notes

RealScale's own presets and Architecture Library remain the primary world-generation layer. Regions Unexplored is included as an additional biome layer; generation inside each RealScale preset must be checked in a fresh test world before calling the combination stable.

## 中文

本目录包含四个可导入的 CurseForge 整合包清单，目标为 Minecraft 1.21.1 / NeoForge 21.1.259。

- **Core 基础版**：四个自研模组、群系扩展、性能库，以及工程和配置界面所需依赖。
- **Nightfall 夜幕版**：基础版增加专门的恐怖生物扩展和环境音效。
- **Serene Seasons 版**：基础版增加 Serene Seasons 及其依赖。
- **Spring Season 版**：基础版增加独立的 Seasonal Survival 季节系统。

不要同时安装 Serene Seasons 和 Seasonal Survival。两个季节版本故意拆分；它们还需要与 Agriculture 内置的 32 天月相季节系统进行游戏内联测。目前属于测试配置，不代表三个季节系统已经完成统一。

### 最终导出包中的自研模组

最终 ZIP 的 `overrides/mods/` 目录应包含四个自研 JAR：Backroom Agriculture（含 THRESHOLD）、RealScale Terrain、Redstone Systems Engineering 和 Swarm Mobs。

### 手动添加的库依赖

Backroom Agriculture 与 Regions Unexplored 需要 NeoForge 21.1 的 Lithostitched 1.7.11。目前清单中没有确认到对应的 CurseForge 文件，因此导入后需要从上面的精确 Modrinth 版本页面手动添加，再启动游戏。

### 世界生成说明

RealScale 自有预设和 Architecture Library 仍然是主要世界生成层。Regions Unexplored 作为额外群系层加入；在把组合标记为稳定版之前，必须在新测试世界中逐一检查它在各 RealScale 预设中的实际生成情况。
