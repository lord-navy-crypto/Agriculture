# Kitchen v3.1 — Specialist tools, real stations and another food processing layer

Minecraft Java Edition **1.21.1**, NeoForge **21.1.259**, Java **21**.

This is an **additive** update to Agriculture v3.0. No original Minecraft item, custom crop, kitchen item, dish or honey ID is replaced. The core mod still ships as a single JAR, with the Fusion enhancement resource pack embedded and enabled only when Fusion itself is installed.

## Four specialist stations

| Placeable machine | Main-hand item examples | Required reusable offhand tool | Heat | Work feedback |
|---|---|---|---|---|
| Whisking Station 打发料理台 | vanilla egg, cocoa mix, glow berry juice, apple juice, pumpkin puree, berry compote base | Wire Whisk 打蛋器 | none | brief active 3D model frame + particles |
| Spice Mortar Station 研钵调味台 | vanilla cocoa beans, sesame meal, pumpkin seed meal, dried kelp, chopped onion and sweet berries | Mortar and Pestle 研钵与研杵 | none | brief active frame + particles |
| Straining Station 食材过滤台 | apple/grape/tomato/beetroot juices, kelp paste, rice flour | Kitchen Strainer 滤网 | none | brief active frame + particles |
| Stock Pot 炖煮汤锅 | steamed pumpkin, sautéed mushrooms, caramelized onion, smoked cod, steamed beetroot or potato | Soup Ladle 汤勺 | **lit smoky campfire** | brief active frame + smoke particles |

A valid right-click checks the main-hand ingredient, correct offhand utensil and, for Stock Pot, vanilla campfire smoke. It consumes exactly one input in Survival and outputs exactly one registered result in the inventory or as a nearby dropped item. The tool is **not** consumed. No uncontrolled ticker, infinite passive machine, forced chunk loading or industrial recipe system is added.

The animation uses the already-proven KitchenV3AnimatedBlock's state: the server switches the block to an alternate baked model, emits particles and schedules a state reset after **10 game ticks**. This is a lightweight animation frame, not a continuous rotating model or a persistent, inventory-storing machine. In Creative mode, input item consumption follows vanilla Creative rules.

## Twenty-four new prepared ingredients

- **Whisking:** beaten egg, whipped cocoa, glow berry froth, aerated apple mix, whipped pumpkin, whipped berry topping.
- **Mortar:** crushed cocoa nibs, sesame paste, pumpkin seed paste, seaweed seasoning, onion paste, berry mash.
- **Straining:** clarified apple juice, clarified grape juice, strained tomato juice, clarified beetroot juice, kelp broth, sifted rice flour.
- **Stock Pot:** pumpkin soup base, mushroom stock, onion stock, cod stock, beetroot soup base, potato soup base.

These results are intermediate materials. No fictional nutrient effects or healing claims are assigned to them. They also feed **23 new finished food dishes**, such as egg rice breakfast, chocolate cream biscuit, sesame rice balls, kelp-spiced cod, clarified apple jelly, garden tomato broth, mushroom rice soup, beetroot winter stew and cod noodle broth.

The new dish recipes consume actual intermediate items and additional vanilla or Agriculture ingredients. **Existing v3.0 utensils become functional without requiring players to replace them.** The other decorative hand tools still retain their prior scope.

## Regression coverage and known boundaries

- All earlier **100 crops**, **50 honey types**, **68 original cuisine items**, v2.8/v2.9 kitchen items, and v3.0 tools, tableware and stations remain intact.
- The original 5 stations and v3.0's 3 stations retain their existing interaction requirements.
- 4 new stations have distinct **idle/active 3D models**, crafting recipes, drops and translations.
- 47 new food/item icons generated in a separate folder, with a **6 × 8** contact sheet.
- CI includes v2.8/v2.9/v3.0 regression suites, new v3.1 station/recipe mapping tests, actual generated PNG tests, NeoForge Java 21 build and real dedicated server smoke test.
- **In-game client acceptance remains outstanding** (models, rendering and full gameplay interactions).
- Intermediates currently have **instant single-input transformation**, not multi-ingredient simmering times or actual liquid simulation.
- Bowls, plates and tumblers used in ordinary crafting are still consumed; reusable container-return mechanics are not claimed.
