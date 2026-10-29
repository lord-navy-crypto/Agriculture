# v2.9 Vanilla Pantry & Kitchen: Minecraft Base-Game Food Integration

The expansion **adds to** Gugu Gaga Agriculture v2.8. It does not rename, remove, or rebalance the existing 100 crop types, 50 floral honey bottles, 68 original cuisine items, 4 existing kitchen stations, or the embedded optional Fusion graphics.

## Why

The existing kitchen system mostly processed custom crops. Base-game vegetables, fruit, cocoa, seaweed, eggs, milk, fish, meat and honey deserve equally useful roles in prepared meals.

## Five usable stations

| Station | Source → product examples | Requirement |
|---|---|---|
| Chopping Board / 切配台 | carrot → carrot ribbons, potato → diced potato, beetroot → grated beetroot, pumpkin → pumpkin chunks, mushrooms → mushroom slices | right-click with one item |
| Hand Mill / 手摇研磨台 | cocoa beans → cocoa powder, pumpkin seeds → ground seeds, dried kelp → kelp powder | right-click with one item |
| Fruit Press / 水果压榨机 | apple → apple juice, sweet berries → berry juice, glow berries → glow berry juice, melon → melon juice | right-click with one item |
| Mixing Basin / 搅拌盆 | apple juice → apple syrup, pumpkin chunks → pumpkin purée, carrot ribbons → slaw, cocoa powder → cocoa mix | **wooden spoon in offhand** |
| Campfire Smoking Rack / 篝火熏制架 | raw cod/salmon/beef/chicken → corresponding edible smoked product | **lit campfire with clear smoke underneath** |

Each workstation converts 1 eligible input to 1 item on a server-authoritative click, no inventory GUI and no continuous background computation. A player inventory overflow drops the output beside the workstation. The four fish/meat smoking routes need smoke; without smoke the input is not consumed. **Smoking is instant once its environmental precondition is met** (a gameplay abstraction, not realistic time-based cooking).

## Expanded ingredients

**25 additional intermediates:** cut/grated vanilla vegetables, mushroom slices, powdered cocoa or seeds, kelp powder, fruit/vegetable juices, and mixtures derived from them. Four additional smoked products are edible directly. Of the **29 new transformations**, 23 begin with vanilla Minecraft ingredients and 6 begin with earlier processed materials. Combined with the 16 v2.8 transformations, this gives **45 processing routes**.

The 23 raw vanilla conversions cover base-game carrots, potatoes, beetroot, pumpkins, brown/red mushrooms, melon slices, sweet berries, cocoa beans, pumpkin seeds, melon seeds, dried kelp, wheat seeds, apples, glow berries, raw cod, raw salmon, raw beef and raw chicken. The existing wheat → wheat flour processing continues as before.

## Twenty additional dishes

| Dish | Main prepared/vanilla inputs |
|---|---|
| Apple Oat Crumble 苹果燕麦酥 | apple syrup + oats + vanilla cookies |
| Pumpkin Beef Stew 南瓜牛肉炖菜 | pumpkin purée + vanilla cooked beef + potato |
| Apple-Glazed Pork 苹果蜜汁猪排 | apple syrup + vanilla cooked porkchop + carrot |
| Mushroom Egg Toast 蘑菇鸡蛋吐司 | sliced brown mushroom + vanilla egg + bread |
| Seaweed Salmon Rice 海带鲑鱼饭 | kelp paste + vanilla cooked salmon + rice |
| Golden Carrot Salad 金胡萝卜沙拉 | carrot slaw + vanilla golden carrot + lettuce |
| Beetroot Potato Soup 甜菜根马铃薯汤 | grated beetroot + vanilla potato + onion |
| Sweet Berry Jam Toast 甜浆果果酱面包 | sweet berry compote + vanilla bread + glow berries |
| Cocoa Oat Bites 可可燕麦小饼 | cocoa mix + oats + vanilla sugar |
| Pumpkin Seed Crackers 南瓜籽脆饼 | pumpkin seed meal + wheat flour + sugar |
| Melon Apple Salad 西瓜苹果沙拉 | melon cubes + vanilla apple + sweet berries |
| Carrot Chicken Bowl 胡萝卜鸡肉饭 | carrot juice + vanilla cooked chicken + rice |
| Glow Berry Custard 发光浆果布丁 | glow berry juice + vanilla egg + milk bucket |
| Honey Salmon Plate 蜂蜜鲑鱼拼盘 | carrot ribbons + vanilla cooked salmon + honey bottle |
| Red Mushroom Tomato Stew 红蘑菇番茄汤 | sliced red mushroom + tomato purée + onion |
| Melon Seed Bread 西瓜籽面包 | melon seed meal + flatbread dough + vanilla sugar |
| Smoked Salmon Rice 熏鲑鱼饭 | smoked salmon + rice + kelp paste |
| Smoked Beef Hash 熏牛肉马铃薯炒 | smoked beef + diced potato + onion |
| Smoked Chicken Carrot Bowl 熏鸡胡萝卜饭 | smoked chicken + carrot slaw + rice |
| Smoked Cod Pie 熏鳕鱼馅饼 | smoked cod + wheat flour + potato |

In addition, original Minecraft bread, cookies, egg, milk bucket, honey bottle, cooked meat, potatoes and carrots appear directly in recipes and are **not replaced or re-registered**; they remain official Minecraft item IDs. Vanilla bucket/bottle recipe remainder behavior is left to Minecraft.

## Art & build validation

- 79 independent kitchen icons, including the old 30.
- 5 station blocks each have an independent 3D model, collision shape, crafting recipe and mining drop.
- Old catalog, 68 legacy cuisine sprites, 50 honey variants and optional built-in Fusion assets remain unchanged.
- Automated QA covers all 45 exact input→output station mappings, the smoke and spoon safety gates, all bilingual labels, the 20 recipes and collisions with old shapeless recipes, new icons, and dedicated-server boot.
- Real Minecraft **client** testing of these interactions and visuals is not yet claimed.

## Boundaries for the next release

The smoke rack uses a smoke requirement rather than a fuel inventory or delayed processing. Mixing and pressing remain one-input steps; no invented refrigerator, spoilage, persistent multi-slot machine, or complex temperature simulation is claimed in v2.9. The priorities for follow-up are more than one ingredient per device, time/heat-controlled baking, preservation, realistic utensil reuse in recipes, and better custom machines.
