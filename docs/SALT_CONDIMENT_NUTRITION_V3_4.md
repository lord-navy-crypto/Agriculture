# Agriculture v3.4 — Salt, Condiment Fermentation, Wild Farmland, Nutrition Diversity

Minecraft Java 1.21.1 / NeoForge 21.1.259 / Java 21. This is an **additive** release candidate; all v3.3 blocks, 108 crops, honey, older kitchens, marine food and optional Fusion assets remain.

## 1. Salt resources and salt bricks

- **Salt Deposit 盐沉积** is a mineable overworld block. It drops 2–4 **Sea Salt 食盐** items; exact yield is game balance, not a claim about real geological concentration.
- Salt Rock Veins are relatively rare in deserts and badlands and replace stone ore host rock; Salt Beach Crust generates around beaches or sand-rich dry areas using sand replacement.
- **Salt Block 食盐方块** uses a reversible 9-salt storage recipe. These features generate in **newly generated chunks** only; old terrain is never retroactively overwritten.
- Existing Minecraft salt-free cooking remains available; no old recipes are made uncraftable.
- Salt also participates in herb salt, brine and new prepared-food recipes. "Brine" is a Minecraft crafting item, not a real-world conservation recipe.

## 2. Ten aging crock food recipes

The new **Condiment Aging Crock 调味品发酵缸** complements (does not replace) the earlier v3.3 Food Fermentation Vat.

The player holds one eligible ingredient in the main hand and one matching game catalyst in the offhand: **Sea Salt** for salt-pickled vegetables or **Food Culture Starter** for other sauces. It consumes both in Survival, enters a persisted batch state, and takes **3 sequential 100-tick stages** to mature. Empty-hand right click collects a single final result after maturation. Breaking the block before completion drops the original ingredient; breaking a mature batch drops the output, in addition to the crock item. No item duplication and no client-side recipe authorization.

The supported **gameplay products** are Soy Sauce 酱油, Rice Vinegar 米醋, Apple Vinegar 苹果醋, Mushroom Savory Sauce 菌菇鲜味酱, Salt-Pickled Cucumber 盐渍黄瓜, Salt-Pickled Daikon 盐渍白萝卜, Salt-Pickled Cabbage 盐渍卷心菜, Salt-Pickled Beetroot 盐渍甜菜根, Cultured Chili Sauce 发酵辣椒酱 and Seasoned Fish Sauce 风味鱼露.

These are **abstract, non-intoxicating in-game items**, not microbiology simulations or instructions for making/handling actual foods. The in-game starter and durations do not predict real taste, safety or production conditions.

Twelve new dishes consume these outputs and various vanilla and Agriculture ingredients. This release makes no claim of implementation of real-world chemical flavor chemistry or continuous liquid storage.

## 3. Geography and biomes

Natural-style farmed patches of the eight v3.3 crops use a new paired farmland + growing crop feature without changing the older 100-crop worldgen tables. Placement is rare (1 in 18 chunk placement attempts per feature) and restricted by biome:

| Crop | Minecraft proxy biomes |
|---|---|
| Black pepper | Jungle, Sparse Jungle, Bamboo Jungle |
| Lemongrass | Jungle, Swamp, Mangrove Swamp |
| Wasabi | River, Old Growth Pine Taiga, Old Growth Spruce Taiga |
| Cumin | Savanna, Savanna Plateau, Badlands |
| Cardamom | Jungle, Bamboo Jungle, Sparse Jungle |
| Fenugreek | Savanna, Plains, Badlands |
| Sugar beet | Plains, Meadow, Taiga |
| Daikon | Plains, Meadow, Forest |

These are *broad habitat/game-balance categories*, not proof that a crop can grow in every real-world site resembling a Minecraft biome. Old wild plants still spawn on native soil, and the game-specific farmland pockets do not represent untouched natural ecosystems. Newly generated farmland may need irrigation from existing water to survive, as normal vanilla farmland does.

## 4. Player nutrition diversity (first playable version)

Press **N** in the game world; the client sends a request to the server for a six-line nutrition panel displayed in chat:
- Grains & energy
- Protein-rich foods
- Seeds & fats
- Fruit & vegetable variety
- Mineral-rich variety
- Whole foods & fiber

Stats are stored in the player entity's persistent NBT and updated only after consuming **edible game items**, including vanilla and modded foods. The system categorizes foods by their registered item ID into broad game groups; it is deliberately *not* a complete nutrient database. Players with four or more sufficiently diverse categories get a mild **Mining Haste I** bonus. Low values do not impose any penalty, health loss or body-image score. Scores cannot diagnose nutrient deficits, recommend intake, replace healthcare advice, or represent every vitamin or mineral.

This is only the first step towards a richer system, not a complete model of real human biochemistry. A polished graphical panel, detailed B-complex, A/C/D/E/K vitamin mapping and documented food metadata for all items are deferred rather than falsely claimed complete.

## Verification & client review

CI must pass crop, kitchen, honey and Fusion regressions; dedicated-salt, aging and nutrition contracts; 26 v3.4 pixel PNGs; Java 21 NeoForge compilation; and real dedicated server startup.

Manual client acceptance still needed for worldgen distribution, salt rarity, stable crop farmland, N-key chat panel, positive haste behavior and aging-crock interaction. Suggested testing uses a backup test world and checks that only newly explored chunks contain new salt deposits and farmland pockets.

Reference:
- NeoForge 1.21.1 biome modifiers: https://docs.neoforged.net/docs/1.21.1/worldgen/biomemodifier/
- NeoForge 1.21.1 key mapping: https://docs.neoforged.net/docs/1.21.1/misc/keymappings/
- NeoForge 1.21.1 network payload: https://docs.neoforged.net/docs/1.21.1/networking/payload/
