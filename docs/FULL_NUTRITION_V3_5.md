# Agriculture v3.5 — Comprehensive Gameplay Nutrition System

**Minecraft Java 1.21.1 / NeoForge 21.1.259 / Java 21**

The previous six-name v3.4 nutrition sketch is upgraded into a persistent, food-specific **27-metric gameplay system**. This does **not** determine human dietary adequacy, body condition, caloric intake, deficiencies, laboratory values or real vitamin requirements. It does not suggest diets or restrict eating.

## Complete metric catalog

The 27 score channels are split into three sections:
- **Five broad gameplay groups:** carbohydrate-rich foods, protein-rich foods, fat-rich foods, fiber, and hydrating foods.
- **All 13 vitamins:** A, B1 (thiamin), B2 (riboflavin), B3 (niacin), B5 (pantothenic acid), B6, B7 (biotin), B9 (folate), B12, C, D, E and K.
- **Nine mineral-source indicators:** calcium, iron, magnesium, potassium, zinc, iodine, selenium, phosphorus and sodium.

Each has a Chinese and English translated display name and independent bounded state in player persistent NBT. A food grants one or more of the 27 metrics using its entry in the shipped `food_profiles_v3_5.json` file. Each profile contributes **0–8 abstract gameplay points** per consumed food, summed to at most 10 per channel. Values are **not** real grams, milligrams, micrograms, calories or dietary reference intakes.

**Explicit catalog coverage:** 403 resource identifiers, including all registered Agriculture edible crops, cooked cuisine, kitchen meals, edible marine/fungi foods, v3.3/v3.4 dishes, 50 original honey bottles and over 40 original Minecraft foods. Foods are mapped to food-category templates (fruit, leafy greens, roots, legumes, grains, seafood, egg, dairy, fungi, seaweed, seeds, etc.). All IDs remain unchanged; new items do not need remapping in worlds. Unknown edible items from other mods get neutral game contributions only, not fabricated vitamin values.

## Playing and viewing

Press **N** in the game world. The client sends a lightweight request; the server returns all 27 score values using a bounded clientbound packet. This opens a **full read-only graphical panel** with translated labels, progress bars and Previous/Next/Done controls. A paged chat report remains as a backward-compatible text option:

1. Macronutrient / fiber / hydration categories — 5 entries
2. Vitamins A through B7 — 7 entries
3. Vitamins B9 through K — 6 entries
4. Minerals: calcium, iron, magnesium, potassium, zinc — 5 entries
5. Minerals: iodine, selenium, phosphorus, sodium — 4 entries

The graphical panel has its own five-page controls (independent from the optional chat report): each row shows a label, visual progress bar and number from **0 to 100**. The GUI is read-only; it receives all values from the server, never computes nutrients from client-side inventory. The chat-report page cursor and nutrient values persist in the world save.

## State and balance

- The server changes the values **only after a food-use completion event**. Merely holding food or aborting eating does not award points. The normal Minecraft hunger/saturation system remains entirely vanilla.
- Scores start at a gameplay baseline of 45 and gently normalize toward 40 over multiple in-game days. No missing nutrients cause damage, debuffs, hunger changes or negative attributes.
- **Varied-food milestone** (at least three macro categories, four vitamin categories and two mineral categories above 55): a small Haste I boost.
- **Highly varied milestone** (at least four macros, six vitamins and four minerals above 70): adds Speed I. No negative consequences when either milestone is absent.
- Death/player clone preserves progress. On upgrading from v3.4, the system transfers earlier broad category scores into corresponding new groups, retaining old NBT rather than deleting it.
- Using many different foods makes it easier to raise different meters, but this is purely optional game progression and **not a recommendation for food consumption in real life**.

## Test and compatibility

The repository QA checks:
- Every registered Agriculture food is in the new catalog, including all 50 honey types, plus common original Minecraft foods.
- 27 unique metric definitions and exactly 13 vitamins.
- Each profile refers only to known nutrient IDs and uses bounded nonnegative contributions.
- Minecraft and mod foods have explicit namespaced resource IDs.
- No old item/block changes and no other food or kitchen registration modifications.
- Server-only food consumption, proper persistence/migration, N-key client request and five-page server-owned report.
- English and Chinese labels for each metric, and presence of the catalog and five nutrition classes inside the compiled single JAR.
- Full previous v2/v3 farm, kitchen, marine, food, beekeeping, fermentation, and embedded optional Fusion regressions, NeoForge Java build, real dedicated-server boot.

**Manual client acceptance is still needed**, especially after death and on multiplayer servers. The UI is a new read-only graphical panel with the older paged chat output retained for compatibility. Real client rendering and packet handling require manual in-game acceptance. A real laboratory nutrition database or precise medical simulator would require independently sourced per-food nutrient amounts, serving sizes, preparation losses, player-specific physiology and nutritional risk review; none of those features are claimed here.
