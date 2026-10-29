# Kitchen v3.0 — Utensils, Tableware and Short-Frame Work Animations

**Compatibility:** Minecraft 1.21.1 / NeoForge 21.1.259 / Java 21. This is an additive release candidate; all v2.9 item/block IDs, old food recipes, original 100 crops and the in-JAR optional Fusion visuals remain present.

## What's actually interactive

| Station | Main-hand input | Offhand reusable utensil | Environment | Example output |
|---|---|---|---|---|
| Dough Bench / 面案台 | wheat/rice/buckwheat flours, existing flatbread dough, cocoa mix | Rolling Pin / 擀面杖 | No external heat | Bread Dough, Rice Dumpling Dough, Pizza Base, Cocoa Cookie Dough |
| Skillet Station / 煎炒台 | diced potato, chopped onion, mushroom slices, carrot ribbons, smoked beef and more | Frying Spatula / 锅铲 | Lit campfire smoke below the station | Crispy Potato Hash, Caramelized Onions, Sautéed Mushrooms, Glazed Carrots |
| Bamboo Steamer / 竹蒸笼台 | raw rice, prepared pumpkin, cabbage, potato, vanilla carrot/beetroot, mushroom | Steamer Lid / 蒸笼盖 | Lit campfire smoke below the station | Steamed Rice, Steamed Pumpkin, Steamed Cabbage, Steamed Beetroot |

Each station takes exactly **one** valid main-hand input per click, checks the offhand utensil and heat/smoke if required, and returns one registered prepared item. The tool stays in the offhand and is never consumed or damaged. There is no hidden per-tick food production, chunk loading or block inventory. A no-tool or no-heat click refuses without eating input.

When processing succeeds on the server:
- The player receives the output in the inventory, or as a dropped item if full.
- A modest set of kitchen/steam particles and one sound play.
- The station changes from the idle model to the working model. A scheduled tick returns it to idle after **10 game ticks** (~0.5 seconds).
- Players can watch a noticeable low-cost operation animation without a custom renderer or looping block entity.

## Twelve tool items

Rolling Pin, Frying Spatula, Steamer Lid, Chef Knife, Wire Whisk, Soup Ladle, Kitchen Tongs, Kitchen Strainer, Box Grater, Mortar and Pestle, Measuring Cup, Skimmer Spoon.

All twelve are registered and craftable with their own 16×16 icons. The **first three are functional required inputs** for the corresponding workstations. The others are currently inventory/roleplay utensils, not yet required by processing; their individual specialized operations are deliberately deferred rather than falsely claimed.

## Twelve tableware items

Wooden Bowl, Ceramic Bowl, Soup Bowl, Serving Platter, Bread Basket, Glazed Tea Cup, Glass Tumbler, Dessert Plate, Lunch Box, Sauce Dish, Baking Tray, Bamboo Steamer Basket.

All have shapely crafting recipes, bilingual inventory strings and independent icons. Several appear in final plated dish recipes. **Crafting currently consumes tableware ingredients**; reusable plated servings/returnable bowl mechanics need a separate tested crafting remainder implementation.

## Twenty-four intermediates

Eight on each workbench:
- Knead: Bread Dough, Rice Dumpling Dough, Buckwheat Noodle Dough, Sesame Bun Dough, Pizza Base, Cocoa Cookie Dough, Bran Cracker Dough, Pumpkin Pastry Dough.
- Fry: Crispy Potato Hash, Caramelized Onions, Sautéed Mushrooms, Seared Red Mushrooms, Glazed Carrots, Pan-Roasted Tomato, Seared Smoked Beef, Toasted Seaweed.
- Steam: Steamed Rice, Steamed Pumpkin, Steamed Cabbage, Steamed Potato, Steamed Carrot, Steamed Beetroot, Steamed Onion, Steamed Mushrooms.

## Eighteen dishes

Tomato Flatbread Pizza, Handmade Rice Dumplings, Buckwheat Mushroom Noodles, Sesame Steamed Buns, Pumpkin Pastry, Cocoa Crunch Cookies, Hearty Potato Plate, Creamy Mushroom Bowl, Warm Carrot Salad, Vegetable Steamed Rice, Beetroot Garden Soup, Smoked Beef Panini, Toasted Seaweed Rice, Sweet Berry Bran Biscuits, Red Mushroom Warm Soup, Onion Vegetable Pie, Country Bread Basket, Garden Lunch Tray.

All eighteen dishes require at least one of the new prepared items. Other ingredients come from the existing mod crops or original Minecraft foods.

## Scope and verification

- New blocks: 3 animated station models × 2 model states each.
- New items: 12 tools + 12 tableware + 24 intermediates + 18 foods = **66** additional items.
- Original kitchen: 79 items and five pre-existing stations remain independent. Original cuisine items remain unchanged.
- Generated art: dedicated 66-item contact sheet (288 × 528 px, six columns) and independent PNG output path. Does not modify v2.9 or crop-cuisine icon generators.
- CI includes v2.8 and v2.9 regression tests, v3 resource and pixel tests, Java 21 NeoForge compilation, and dedicated server startup.
- **Client-game visual and item interaction testing is still needed**; an automated build is not a claim of rendered frames verified on a real client.

## User test checklist

1. In a backup game world, install the new single mod JAR, not simultaneously with an older Agriculture version.
2. Place Dough Bench; put Rolling Pin in the offhand and Wheat Flour in the main hand. The result is Bread Dough; look for a brief model state change.
3. Try without Rolling Pin: no ingredient should be consumed.
4. Place Skillet Station above a lit campfire with unobstructed smoke. Offhand Frying Spatula + main-hand Chopped Onion → Caramelized Onions. Try without smoke: no conversion.
5. Place Bamboo Steamer above a campfire. Offhand Steamer Lid + main-hand Rice → Steamed Rice.
6. Craft one of the 18 new meals and verify the correct prepared ingredients are required.
7. Check the old five processing stations, old recipes, honey bottles, crop models and the optional Fusion visual pack in both Fusion/no-Fusion clients.
