# v3.7 Hydration and Sodium Balance: Minecraft Survival Gameplay

Minecraft Java 1.21.1, NeoForge 21.1.259, Java 21. All 108 crops, 32-day seasons, previous kitchen recipes, 27 nutrients, fungi, seafood, beekeeping, and embedded optional Fusion visuals are preserved.

## Two new meters, not medical diagnostics
- **Hydration 0–100:** begins at 80. Every 200 game ticks (10 seconds of world time), water declines by 1. Sprinting and Nether gameplay add one point of demand each. Extremely low scores cause brief Mining Fatigue and Slowness effects; no direct damage or death. Drink or eat to restore.
- **Salt balance 0–100:** begins at 60 and very slowly normalizes. Eating salted dishes and seawater foods helps maintain it. An excessively high balance increases in-game hydration needs. It is not a blood sodium measurement. No intake in milligrams, fixed human intake targets, age/gender/body weight restrictions or instructions for real eating.
- 407 explicit food and drink records in `data/gugugaga/nutrition/water_salt_profiles_v3_7.json`. Values are **fictional per-item score contributions**, never a real-water content, fluid volume or nutritional laboratory report. Honey bottles only add 2 water points; fruit, soups and normal drinks add more. Old 27 nutrients remain stored separately.
- Players retain hydration and salt states across death and world reloads. All food/drink consumption and clock-based depletion are evaluated on the server.

## Fixed nutrition access
- A fixed **Nutrition** button appears to the right of the normal player inventory; selecting it requests the server-owned view.
- The default **N** key still opens the same interface; the HUD includes a compact reminder.
- The sixth page contains hydration and salt balance with progress bars and game-status descriptions. The first five pages still show 27 macro/vitamin/mineral category indicators in EN/ZH.
- No UI scores are trusted from a connected client.

## Collecting, boiling and drinking
1. Craft an Empty Water Flask with a Glass Bottle and an Iron Nugget. Right-click a freshwater source block with the empty flask; natural ocean biomes are rejected (saltwater is not ordinary fresh drinking water).
2. The collected item is **Untreated Freshwater**. It restores water when consumed but has a 1-in-5 abstract gameplay chance of a very brief nausea effect. Natural water is not universally poisonous in real life; contaminant risk depends on source.
3. Put a raw water bottle over a campfire or in a furnace using normal Minecraft cooking mechanics to create **Boiled Drinking Water**. Boiling is a simplified Minecraft microbial safety step, not chemical filtration.
4. Combine a **boiled** bottle with charcoal and paper to produce a filtered-themed item. The input is already boiled, since charcoal and paper alone should never be represented as guaranteed pathogen-free drinking water.
5. Drinking returns the reusable empty flask. Old vanilla water/potion interactions are not overwritten, and regular food, soups, juice and honey can add some game hydration.
6. Recipes are in `data/gugugaga/recipe/`. New water item models reference included vanilla inventory textures; dedicated bespoke sprites may follow.

## Safety, balance and limitations
Scientific context: National Academies state total water comes from drinking water, other beverages and food; sodium is an essential electrolyte. WHO and CDC stress microbial and chemical hazards differ. Boiling may reduce microbial contamination but **does not remove toxic chemicals**. None of the in-game water transformations constitute real-water testing or preparation advice.

These survival levels deliberately make game resource planning more challenging without imposing real-world calorie targets, diet restrictions, or health diagnoses. A survival config UI and per-biome water contamination sources are future expansions, not implemented in v3.7.

Acceptance: CI static contracts; actual Java21 compile; dedicated NeoForge server startup. A Minecraft client must still test Inventory button, HUD, bottle refill and return, and recipes on a backup world.
