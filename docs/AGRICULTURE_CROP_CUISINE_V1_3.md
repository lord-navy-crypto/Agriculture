# Crop cuisine v1.3 — alternative recipes and category checks

Added 20 alternative ways to cook existing registered dishes, without creating more crop or food items.

Variants reuse grains (quinoa, teff, sorghum, amaranth, millet), underused vegetables (kohlrabi, jicama, bok choy, rapini, okra, jute mallow), existing mushrooms and marine ingredients, and distinct floral honey bottles.

Alternative recipes for bread, cookies and pumpkin pie produce their original Minecraft items rather than duplicate custom replacements. The single source manifest lives at `data/gugugaga/agronomy/cuisine_recipe_variants_v1_3.json`.

Minecraft Java 1.21.1 requires crafting recipe `category` to be `building`, `equipment`, `redstone` or `misc`. The preceding v1.2 bug fix corrected 15 recipes that mistakenly used `food`. This release adds a regression guard across all cooking and crafting JSON files.

The CI source check validates the manifest, quantities, outputs, ingredients and recipe categories. NeoForge compile and actual server boot checks remain mandatory before claiming release readiness.
