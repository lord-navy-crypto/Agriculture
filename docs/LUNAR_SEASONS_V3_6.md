# Agriculture v3.6 — 32-Day Lunar Agricultural Calendar

Minecraft Java 1.21.1 / NeoForge 21.1.259 / Java 21.

## Four seasons from four existing eight-day moon cycles

The world year is exactly **32 Minecraft days**, derived from the normal Overworld day clock: `worldDay = floor(dayTime / 24000)`, and `yearDay = worldDay % 32`. Lunar phase uses `worldDay % 8` (0 = full moon; 4 = new moon). The calendar contains **four complete lunar cycles**.

| World year days | Season | Day count |
|---|---|---|
| 1–8 | Spring 春 | 8 days |
| 9–16 | Summer 夏 | 8 days |
| 17–24 | Autumn 秋 | 8 days |
| 25–32 | Winter 冬 | 8 days |

A year is 32 Minecraft days, or 640 minutes (10 hours 40 minutes) at the normal unpaused 20-minutes-per-day rate. The normal moon phase repeats exactly once per season; the **first day of each world season is the same full-moon phase**. No claim is made that physical lunar phases cause Earth's real seasons: this is an intentionally fast game-world *lunar agricultural calendar*. Seasons do not change the vanilla moon or time.

## Biomes: same world date, different local growing seasons

All biomes share the 32-day world year, so multiplayer farms never disagree about what year it is. Minecraft habitats can have different local season durations (always totaling 32) as an accessible game approximation, **not** a real meteorological prediction:

| Biome habitat | Spring | Summer | Autumn | Winter |
|---|---:|---:|---:|---:|
| Temperate plains/forest | 8 | 8 | 8 | 8 |
| Tropical jungle | 8 | 10 | 8 | 6 |
| Dry savanna/desert/badlands | 7 | 11 | 8 | 6 |
| Cold/snowy/taiga/grove/mountains | 7 | 6 | 8 | 11 |
| Wet swamp/mangrove | 9 | 9 | 8 | 6 |

Changing biomes does **not** reset days, the shared season or the moon. It only changes the *locally interpreted agricultural season*. Custom modded biomes without recognized name tags default to temperate.

## Crop behavior and shelter

All previous 100 core crops and the 8 later spice/vegetable crops have an **additive growth gate**. Each plant still uses original moisture, seed, climate-band, vanilla light, irrigation, pollination and harvest code. Only immature stages run seasonal checks; existing mature crops are not destroyed by winter.

Warm/wet crops strongly slow in ordinary winter; cool-season crops can grow more slowly. Cold-region winters are especially restrictive; tropical and arid habitat adaptations keep some growth. Summer can slow cool-weather crops in arid biomes.

Sheltered growth: a **glass canopy** within 2–4 blocks above a crop and a nearby heat source (campfire, furnace, magma, lava) can improve winter crop growth probabilities. Vanilla minimum crop light 9 still applies. These are small, bounded checks on crop random ticks: not a detailed thermodynamics or sealed-room simulator; torches supply light but do not automatically count as a major heating unit. The current simple heat model does not check furnace lit state or campfire flame state and needs balance testing in a Minecraft client.

## In-game status

Enter **`/agseason`** in chat, without operator permissions. The output contains the game year, world year-day (1–32), global calendar season, local biome season and local day in season, and an eight-phase moon description in English or Chinese.

The clock naturally follows vanilla `/time set` and player sleeping; there is no independent clock that can desync. This intentionally speeds the agricultural year, not the normal day/night loop. The new system is purely gameplay timekeeping and does not modify actual Minecraft weather, snow/ice worldgen, sky colors or biomes.

## Compatibility & tests

- Existing 108 plantable crops, 50 honey types, food processing, marine/fungal systems, nutrition N-key GUI and optional embedded Fusion resources remain unchanged.
- All 32 daily calendar states and eight lunar phases, five local calendars, greenhouse bounded checks, and both core/new crop seasonal wiring have source-level automated tests.
- NeoForge Java 21 compile, JAR packaging, actual dedicated-server boot and old v1–v3 regressions are mandatory CI gates before merge.
- Manual Minecraft client checks needed across season boundaries, alternate biome habitats, growth under roof heat, sleepers/time commands, and mod interoperability.
- Other installed seasons mods that independently modify crop growth may compound gameplay slowdowns. We do not currently integrate their APIs; avoid claiming automatic cross-mod coordination.

## Follow-on enhancements

A later version can use a fully data-driven list of 108 crop-specific tolerances (temperature, light, soil and dormancy), a genuine sealed-greenhouse and greenhouse thermostat mechanism, and calendar sync for a graphical HUD. Separately test new maritime/fungal biome habitat restrictions. This release implements the approved **32-day four-season calendar** first and does not falsely claim those larger systems are complete.
