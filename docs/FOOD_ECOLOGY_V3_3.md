# Gugu Gaga Agriculture v3.3 — More Crops, Ocean Seafood, Non-Alcoholic Food Fermentation

Compatibility: Minecraft Java **1.21.1**, NeoForge **21.1.259**, Java **21**.

The v3.3 expansion adds gameplay content without renaming/removing the prior 100 plantable crops, original honey types, seafood, fungal cultures, 12 kitchen stations or optional embedded Fusion assets. The initial 100-crop generator and all previous kitchen sprites remain untouched.

## Eight additional real plantable crop lines (108 total)

| Crop | Produce | In-game seed starting recipe |
|---|---|---|
| Black Pepper 黑胡椒 | Black Pepper | Cocoa Beans + Wheat Seeds |
| Lemongrass 香茅 | Lemongrass | Sugar Cane + Wheat Seeds |
| Wasabi 山葵 | Wasabi | Sea Pickle + Wheat Seeds |
| Cumin 孜然 | Cumin | Beetroot Seeds + Wheat Seeds |
| Cardamom 小豆蔻 | Cardamom | Melon Seeds + Wheat Seeds |
| Fenugreek 葫芦巴 | Fenugreek | Pumpkin Seeds + Wheat Seeds |
| Sugar Beet 糖用甜菜 | Sugar Beet | Beetroot + Wheat Seeds |
| Daikon 白萝卜 | Daikon | Carrot + Wheat Seeds |

These are **fictional gameplay unlock recipes**, not botanical breeding recipes or biological claims. Each line has its own seed item, harvest item, climate band, standard eight-stage crop block, 16 blockstate variants (with/without pollination state) and mature harvesting loot. Existing CropClimate bands drive growth. The first version uses 16×16 custom crop-stage sprites; it does not yet add eight separate natural wild-seed worldgen plants.

## Expanded water products: eight inventory seafood catches

Anchovy 鳀鱼, Sardine 沙丁鱼, Herring 鲱鱼, Mackerel 鲭鱼, Sea Bass 海鲈鱼, Sea Bream 海鲷, Shrimp 虾, Crab 蟹.

Original vanilla Cod, Salmon, Pufferfish, Tropical Fish and all original fishing loot stay unchanged. **Ocean fishing only:** after a successful vanilla fish catch, the server makes one 1/5 chance roll to award **one additional** new seafood ingredient (random species). No new moving fish mobs or natural ocean schools are introduced in v3.3. Fish ingredients are not edible raw; process them at the existing smoke rack. Hold the existing reusable Kitchen Tongs in the offhand to obtain eight **Smoked Seafood** ingredients. The original real campfire smoke requirement still applies.

## Food fermentation vat: time-based production, not instant crafting

Craft and place the **Food Fermentation Vat**. With the vat empty, right-click a supported food ingredient in the main hand to insert **one** batch. A state-based progress bar advances through **three scheduled 80-tick stages**. When stage 3 is reached, empty-hand right-click to collect exactly one output. Break the vat before it finishes to recover the input; break it after maturation to recover the finished output. The vat cannot accept another ingredient while processing. There is no power, fuel inventory, passive duplication, network calls or forced chunk loading.

Ten distinct **non-alcoholic food culture** game outputs:
Fermented Soybeans 发酵黄豆, Cultured Cabbage 发酵卷心菜, Fermented Cucumber 发酵黄瓜, Fermented Daikon 发酵白萝卜, Aged Pepper Paste 发酵胡椒酱, Fermented Lemongrass 发酵香茅, Fermented Shiitake 发酵香菇, Cultured Beet Relish 发酵甜菜酱, Pickled Beetroot 腌渍甜菜根, Cultured Carrot 发酵胡萝卜.

**Important:** This is an abstract Minecraft game mechanic, not instructions for preparing fermented food in real life, preserving food safely, or producing beverages.

## Eight new condiments and eighteen new finished dishes

- Existing Spice Mortar with reusable Mortar and Pestle creates eight condiments from the new crops: cracked black pepper, lemongrass flakes, wasabi paste, ground cumin, ground cardamom, ground fenugreek, sugar beet pulp, daikon relish.
- Food recipes connect these to eight smoked seafood species, earlier Agriculture ingredients, original Minecraft bread/vegetables, fermented foods and twenty other dishes already present. New finished examples include Black Pepper Mackerel Rice, Lemongrass Sardine Soup, Wasabi Sea Bass Plate, Cumin Anchovy Toast, Cardamom Sea Bream Bowl, Pepper Crab Plate, Fermented Soybean Rice, Fermented Cucumber Salad and Fermented Shiitake Noodles.
- **Eighteen** new 3-ingredient crafting dishes, all requiring an actual fermented/seasoned/smoked intermediate.
- 68 unique inventory icons plus 64 crop-stage sprites from a completely independent generator. All translated to English and Simplified Chinese.

## Compatibility and quality gates

- No vanilla item ID or existing Agriculture registry ID overwritten.
- Actual NeoForge ocean-only fishing event supplements rewards instead of editing/deleting normal fishing loot.
- Ten ferment batches and three progress stages are represented by legal blockstates; breaking a loaded vat returns its ingredient or result via native loot conditions.
- CI verifies crops, seed recipes, harvest loot, 68 icons, 64 grow-stage textures, 8 seasoning conversions, 8 smoked seafood conversions, all 18 dishes and time-gated fermentation logic.
- Normal Java 21 compilation, embedded optional Fusion regression and a real NeoForge dedicated-server startup are required before merging.
- A successful server boot is not a claim of real client visual or manual interaction acceptance.

## Player test checklist

1. Back up a world, replace the previous Agriculture JAR with this one (only one version).
2. Craft Black Pepper Seeds using Cocoa Beans + Wheat Seeds; plant in farmland and check eight growth stages, harvest and additional seed drops.
3. Fish in an ocean until a normal vanilla fish catch and compare bonus catch events; original fish catches must remain present.
4. Use Kitchen Tongs on an existing Smoking Rack over a lit campfire to transform the new seafood.
5. Craft the Fermentation Vat and insert a supported vegetable. Check state progression and harvest only once after stage 3.
6. On a separate test vat, break a half-finished batch and verify the original food item is recovered.
7. Use Mortar and Pestle to grind new spice crops; craft seasoning and seafood dishes.
8. Compare old crops, honey, seaweed, fungal cultures and Fusion-enabled/disabled visuals.
