# Backroom Agriculture v4.4 preview — water assets, brine and impossible architecture

## Actual status / 当前实际状态

This is an **experimental source branch**, not a tested release. It packages 101
original RealScale designs (97 standalone architectural templates and four
48×48 modern city districts). The current RealScale main branch documents 100
architecture templates (97 originally authored + 3 separately licensed imports)
and 4 city quarters. Claims of 140+ current main-branch structures have not
been established here. The three licensed imported schematics are deliberately
not copied. Source generators have been included so the resulting NBT is
reproducible without depending on an external GitHub checkout.

这是实验性源码分支，并非已通过实机验收的正式版。当前移植来源是 RealScale
主分支可核对的 97 个原创建筑加 4 种现代街区（共 101 个）。
原版库还有 3 个单独授权的外部素材，本次不复制；“140 多个”暂未在当前主线得到验证。

## Water and salt / 水盐系统

- The original `sea_salt + minecraft:water_bucket -> brine` recipe is retained.
- New optional recipe: `sea_salt + boiled_water_bottle -> brine`. **This
  crafting route consumes the boiled-water bottle; it does not currently refund
  an empty flask.** The original water-bucket route retains its vanilla empty
  bucket remainder.
- Very rare empty buckets join Level 1 supply loot (1/20 additional-pool
  chance) to let players fill a water bucket from a suitable water source.
- Dedicated 16×16 sprites replace six vanilla water-item placeholders and
  the vanilla potion icon used for Almond Water. The brine ingredient icon is
  rendered as liquid in a jar, not a pile of spices.
- Level 0/1/3/4/5 gain biome-specific water and underwater-fog tints. Level 2
  retains its previously authored water tint.
- All water and nutrient profile IDs, 27 indicator names and save keys are
  unchanged.

保留“海盐 + 原版水桶 -> 盐水”，新增“海盐 + 煮沸水瓶 -> 盐水”的后室
生存配方；后一条路线暂时会消耗水瓶，不退还空壶。Level 1 的补给箱
低概率出现空桶，以支持长期水源采集。不同楼层的水体色调和水下雾效
已有区分，但这不等于开发了全新自定义流体贴图。

## RealScale architecture / 建筑移植

- Template IDs: `threshold:anomaly/architecture/<name>` (97) and
  `threshold:anomaly/urban/<name>` (4).
- Build-time original Python generators write NBT into the mod JAR.
- All templates are available to manually test with `/place template` when
  the JAR builds successfully.
- **Natural generation is optional and OFF by default.** A new-world Data
  Packs menu can enable `Backroom Agriculture | Impossible Architecture
  (Experimental)`. This pack is registered as a non-mandatory SERVER_DATA
  built-in pack.
- In the experimental pack, Level 0 and 1 alone have two very sparse
  random-spread structure sets: original architecture (spacing 96 chunks)
  and cities (spacing 192 chunks). Starts project to **absolute y=82**,
  above existing ceilings at y=72/74. This prevents giant towers intersecting
  lower guaranteed corridors. These are candidate intervals, not guaranteed
  find distances.
- All generated buildings are 1.21.1 vanilla-block NBT. They do not depend
  on RealScale running as a second mod.

可用测试命令（需管理员或创造模式）：

```mcfunction
/place template threshold:anomaly/urban/commercial_skyscrapers
/place template threshold:anomaly/urban/civic_centre
/place template threshold:anomaly/urban/residential_quarter
/place template threshold:anomaly/urban/industrial_edge
/locate structure threshold:anomaly_urban
```

**Important limitation / 重要限制：** The upper anomaly stratum does not yet
have a verified natural entrance or elevator from the inhabited lower maze.
Therefore this branch does **not** claim normally accessible/fully playable
spawning cities. Test the upper layer in Creative/spectator mode and inspect
the giant structures before exposing them as part of survival progression.
Worldgen registrations require real game-server and newly generated chunk
checks. Do not turn this preview pack on for an important existing save.

目前仍缺乏从后室低层进入上层异常城市的已验证自然入口。当前属于可选
生成预览，未完成普通生存模式的全流程可达性测试。请先使用备份测试世界，
绝不能把“资源已打包”误称为“实机已成功生成”。

## Remaining follow-up / 尚未实现

- Porting vanilla Overworld villages, ruins or shipwrecks requires adapting
  their biome, heightmap, loot and terrain expectations to Threshold. Turning
  on vanilla structure sets blindly would place many of them underground or
  break the maze. This integration is not yet committed.
- Distinctive fluids beyond water-item art and biome tints, survival entrance
  generation, physics of tall structures, overlap and performance require
  additional client/server work.
- 3 licensed RealScale templates and any unverified 140+ work from other
  branches are not part of this port.

## Validation / 验收

```sh
python3 qa/validate_water_anomaly_v4_4.py
gradle --no-daemon --stacktrace build
python3 qa/validate_water_anomaly_v4_4.py build/libs/gugugaga-4.0.0-alpha.1.jar
```

The checks verify recipe wiring, namespace/model names, generated PNG
signature/size, 97+4 NBT paths and opt-in dimension overrides. They do not
assert that a Minecraft client actually displayed every sprite or visited
every naturally generated structure.
