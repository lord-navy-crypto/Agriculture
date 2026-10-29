# Agriculture v2.8 — Functional kitchen and ingredient processing

The v2.8 milestone builds a **real gameplay loop** on top of the existing 100-crop and 68-dish catalog. It preserves all existing crop IDs, honey production, vanilla-compatible textures, and the embedded optional Fusion pack.

## Four player-operated workstations

| Placeable block | Operation | Example inputs → output |
|---|---|---|
| Chopping Board / 切配台 | Cut vegetables | tomato → diced tomato; onion → chopped onion; cucumber → sliced cucumber; cabbage → shredded cabbage |
| Hand Mill / 手摇研磨台 | Grind grains and seeds | rice → rice flour; buckwheat → buckwheat flour; wheat → wheat flour; sesame → ground sesame |
| Fruit Press / 水果压榨机 | Press fruit/vegetables | grape → grape juice; strawberry → strawberry juice; pineapple → pineapple juice; tomato → tomato juice |
| Mixing Basin / 搅拌盆 | Mix prepared ingredients | diced tomato → tomato purée; sliced cucumber → cucumber relish; strawberry juice → compote base; wheat flour → flatbread dough |

The mixer requires a **Wooden Mixing Spoon** in the offhand (the spoon is reusable and never consumed). Other stations accept one raw/processed item in the main hand.

The server consumes **one ingredient** and gives **exactly one processed item**, placing it in the player's inventory or dropping it in front of the station if inventory is full. No waiting, fuel, machine tick, infinite duplication, block entity, or forced chunk loading. In Creative mode, material consumption follows normal creative behavior.

**Use:** Place the crafted station, hold an eligible ingredient in the main hand, and right-click. Right-clicking empty-handed shows a station hint. A mixing basin without a spoon explains the missing requirement.

## New dishes (12) and dining ware (2)

1. Tomato Basil Toast — bread, diced tomato, basil
2. Cucumber Onion Salad — sliced cucumber, chopped onion, ceramic plate
3. Cabbage Rice Bowl — shredded cabbage, rice, carrot
4. Buckwheat Flatbread — buckwheat flour, wheat flour, sugar
5. Sesame Vegetable Noodles — ground sesame, rice, carrot
6. Berry Breakfast Bowl — strawberry juice, oats, apple
7. Pineapple Glazed Rice — pineapple juice, rice, soybean
8. Grape Fruit Cup — grape juice, grapes, apple
9. Tomato Vegetable Soup — tomato purée, chopped onion, carrot
10. Cucumber Relish Plate — cucumber relish, lettuce, ceramic plate
11. Forest Berry Pancakes — compote base, buckwheat flour, sugar
12. Rustic Herb Flatbread — flatbread dough, diced tomato, basil

Two new crafted dining items are the **Wooden Mixing Spoon** and **Ceramic Plate**. The spoon is a reusable mixing utensil. Plates are ingredients for plated dishes; they are consumed when assembling the final serving in this version.

The 16 processed outputs are **ingredients, not food items**; use them in the vanilla crafting grid to produce full meals. None of the processing operations claims to simulate industrial milling or cooking safety; this is lightweight Minecraft gameplay.

## Art and packaging

- Four distinct 3D workstation block models with low-profile collision shapes.
- 30 individually generated 16×16 inventory graphics, with a 6×5 contact sheet.
- The original 68-item cuisine icon generator is untouched; no existing resources are overwritten.
- The generated assets, station blocks, translations, and meals all ship inside the same Agriculture JAR. Fusion continues to be optional.
- On CI, the new QA checks processing map parity, registrations, all 18 new craft recipes, block drops, source consistency, language labels, and image pixel integrity.

## Deliberately deferred

Real multi-slot inventories, slow cooking timers, storage containers, temperature, fermentation, spoilage, and any extra crop lines are **not** implied by these four manually operated stations. Those are future milestones, after client gameplay acceptance. The 100 existing crop varieties already provide a broad foundation for processed meals.
