# Backroom Agriculture v4.5 — connected anomaly cities and vanilla structure fragments

## Scope / 范围

This is an experimental source branch and draft PR, not a released mod build.
The verified original RealScale migration inventory is 142 source-defined
resources: 97 original NBT architecture templates, four modern city districts,
17 unique geological-wonder feature kinds (10 GeoWonder + 8 EarthWonder with
one shared cooled-lava kind), 15 GrandEcology kinds, five GroundMicrostructure
kinds, two CaveWonderNetwork kinds and two VanillaHabitat kinds. The current migration code now represents all 142 original resources:
97 building templates, four city districts, 17 geological-wonder kinds,
15 ecology kinds, five ground microstructures, two cave networks and two
vanilla habitats. The 97 buildings and four city districts use the built-in six-floor anomaly
pack, enabled by default for new worlds; the other 41 environmental features
are integrated into all six floors through the room sequence with route
protection and floor-aware surface handling. Levels 0 and 1 retain the full
feature and structure pool at a lower deterministic frequency. Compilation
and live server validation are still required before this migration can be
called fully verified.
Three separately licensed GDMC templates are additional and excluded from
the 142-original-resource count; they are not copied in this stage.

这是实验性开发分支，不是正式发布版。已核对的 RealScale 原创资源总数为 142：
97 个原创建筑 NBT、4 个现代城市街区、17 种去重后的地质奇观、15 种生态特征、
5 种地面微结构、2 种洞穴网络和 2 种原生栖息地结构。
当前迁移代码已经覆盖全部 142 种原创资源：
97 个建筑模板、4 个现代城市街区、17 种地质奇观、15 种生态特征、
5 种地面微结构、2 种洞穴网络和 2 种原生栖息地。
97 个建筑和 4 个城市街区通过默认启用的六层异常数据包生成；
其余 41 种环境特征全部接入 Level 0-5 的世界生成序列，并加入路线保护和地板适配。
Level 0、1 保留完整内容池，只降低确定性生成频率，不限制任何结构种类。
但仍需完成编译和真实服务器验收，才能称为完全验证。
另有 3 个单独授权的 GDMC 模板，不计入这 142 个原创资源，本阶段尚未复制。

## Connected city districts / 可进入的现代城市

The four 48×48 city templates are sparse NBT structures with a lower-level
service lift integrated into each district:

- Structure placement starts at world Y=63; the city street deck is at Y=82.
- A 9×9 maintenance vestibule replaces only its own small footprint in the maze.
- Two 3-wide corridors lead north and south from the vestibule and open at the
  district-template edges, so the lift does not end in a sealed chamber.
- A 4×4 ladder shaft passes through the host Backrooms level's ceiling.
- A landing and short bridge connect the ladder to the city street.
- Sparse NBT omits all ordinary air cells. Explicit air is written only inside
  the vestibule, the two short corridors and the shaft. The largest city template is 48×117×48; its lower 19-block access zone
  is sparse, so the bounding box does not erase the rest of the maze.
- The original lower maze remains intact outside that narrow access footprint.

四个现代街区都带有维护电梯入口：玩家可在后室六个楼层的迷宫中发现维护室，
穿过南北通道进入维护室，再沿梯井上行到 Y=82 的城市街道。
稀疏 NBT 不会把整个 48×48 的迷宫区域清空；只在入口、短通道和电梯井
范围内显式放置空气。

## Original and vanilla anomalies / 原创与原版异常结构

The 17 geological forms, five ground microstructures, two vanilla habitats,
15 GrandEcology habitats and two cave-wonder networks are available on all six
floors as chunk-local deterministic features. They use each floor's real
surface cell (Level 0 carpet at Y=64; Levels 1-5 air above the Y=63 floor),
preserve reserved routes, and avoid existing fluids and structures. Level 0
and Level 1 use one-quarter the normal feature-attempt rate, preserving their
horror and warehouse identities without removing any anomaly family.
Wetland pools are sealed and supported; cave passages cut only approved wall
blocks and preserve reserved routes.

Each of the six Backrooms dimensions has its own shared structure set, so
houses, city districts and vanilla fragments cannot independently choose the
same region within that floor. The six sets use distinct seed salts to avoid
repeating the same X/Z candidate grid across dimensions. Candidate spacing is
64 chunks, separation 24 chunks; this is a rare random-spread grid, not a
guaranteed distance between discoveries.

| Structure family | Weight | Placement |
|---|---:|---|
| 97 original RealScale buildings | 4 | Y=82 |
| 4 connected modern city districts | 2 | Starts at Y=63; city deck Y=82 |
| Vanilla plains village town-centre fragments | 1 | Y=82 |
| Vanilla ancient-city centre fragments | 1 | Y=82 |
| Vanilla ruined portals (10 normal + 3 giant templates) | 1 | Y=82 |
| Vanilla shipwreck fragments (11 templates) | 1 | Y=82 |

Vanilla content is reused through its existing Minecraft 1.21.1 template pools
and structure templates. The vanilla village and ancient-city entries are
intentionally size-zero fragments, not complete villages or complete ancient
cities. They do not replace or override vanilla structure definitions.

原版部分复用 Minecraft 1.21.1 自带的村庄广场、远古城市中心、废弃传送门
和沉船模板。村庄与远古城市采用单个中心片段，不冒充完整结构。
它们会作为上层空间中的异常景观出现；目前只有四个自制现代城市街区
拥有经过设计的迷宫入口和电梯通路。

## World safety / 世界安全

- The built-in data pack is selected by default for new worlds and remains user-disableable.
- It provides the same six structure families on all six Backrooms dimensions: Levels 0, 1, 2, 3, 4 and 5.
- Levels 0 and 1 use a wider structure candidate spacing (80 chunks rather than 64) so anomalies are less common, not unavailable.
- Each floor's anomaly families share one structure set, so a region chooses
  one family instead of independently placing overlapping city/building/vanilla
  sets. Each floor has a different placement salt.
- The maze feature runs before the anomaly structures; the structures use
  \`top_layer_modification\` so the small entrance can intentionally cut through
  the ceiling after the maze has been built.
- New worlds receive the anomaly pack by default; players may disable it in world data-pack settings.
- Existing worlds are not rewritten. If the pack is enabled for an existing world, structures affect only newly generated chunks.
- Back up important worlds before changing their data-pack selection.

## Water and salt / 水盐系统

- The original \`sea_salt + minecraft:water_bucket -> brine\` recipe is retained.
- An alternative \`sea_salt + boiled_water_bottle -> brine\` recipe is available.
  This route consumes the boiled-water bottle and returns one reusable empty flask as its crafting remainder.
- Level 1 supply loot includes a very rare empty bucket.
- Seven bespoke 16×16 sprites replace vanilla placeholders for water flasks,
  sampling buckets and Almond Water. Brine has a liquid-in-a-jar sprite.
- Level 0/1/3/4/5 have distinct water and underwater-fog tints; Level 2 keeps
  its existing tint.
- Nutrient IDs, 27 metric names, save keys and existing food profiles are
  unchanged.

## Test and build / 验收

\`\`\`sh
python3 qa/validate_water_anomaly_v4_4.py
gradle --no-daemon --stacktrace build
python3 qa/validate_anomaly_worldgen_v4_5.py
python3 qa/validate_water_anomaly_v4_4.py build/libs/gugugaga-4.0.0-alpha.1.jar
python3 qa/validate_anomaly_worldgen_v4_5.py build/libs/gugugaga-4.0.0-alpha.1.jar
\`\`\`

The CI server smoke test creates a fresh disposable world with the optional
anomaly data pack enabled and verifies that all anomaly structure registries
load alongside the six Backrooms dimensions. Static NBT checks verify the
lift ladder, doors, exit landing and strict lower-level air-carve boundary.

These checks do not replace visual client testing, exploration of several
random seeds, structure overlap inspection or a full survival playthrough.
The draft PR must remain unmerged until those results are reviewed.
