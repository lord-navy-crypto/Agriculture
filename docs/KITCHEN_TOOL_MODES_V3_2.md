# Kitchen v3.2 — 5 Specialist Utensil Modes with No Extra Workstation Blocks

Minecraft Java 1.21.1, NeoForge 21.1.259, Java 21. This release extends Gugu Gaga Agriculture's original Kitchen v3.1 and keeps **all 12 existing workstation blocks**, 100 crops, 50 honey types, original 68 cuisine items and optional embedded Fusion resources.

## What's different?

Five existing tools, previously craftable but without real processing uses, now change **what an existing workstation does**. The player places no new machine. The main hand holds **one input**; the offhand holds the relevant reusable tool.

| Existing station | Offhand tool | Operation | Six / five input examples |
|---|---|---|---|
| Chopping Board 切配台 | Chef Knife 厨刀 | Precision slicing | vanilla beef / chicken / cod / salmon / potato / apple |
| Chopping Board 切配台 | Box Grater 刨丝器 | Fine grating | vanilla carrot / potato / beetroot / pumpkin / apple; mod cucumber |
| Smoking Rack 熏制架 | Kitchen Tongs 厨房夹 | Specialist smoking | prepared beef / chicken / cod / salmon strips, sliced mushroom |
| Mixing Basin 搅拌盆 | Measuring Cup 量杯 | Measured preparation | existing apple/grape/carrot juices, cocoa mix and pumpkin puree |
| Stock Pot 炖煮汤锅 | Skimmer Spoon 漏勺 | Skim / clarify | mushroom/cod/onion stock and pumpkin/potato soup bases |

**Legacy behavior is unchanged**: without a specialized offhand tool, the Chopping Board and Smoking Rack still use their old raw-ingredient recipes; a Wooden Spoon still works for old mixing recipes and a Soup Ladle still works for old stock recipes. The new optional mode is selected only when the held ingredient and offhand utensil match a registered specialty transformation. The modes consume exactly one main-hand ingredient in Survival and yield one prepared item; tools are reusable. For smoking and stock clarification, the original lit-campfire smoke requirement still applies.

Twenty-seven new processing outputs are now registered, including raw meat strips, fish fillets, vegetable shreds, prepared smoked portions, measured drink/sauce bases, and clarified soup stocks. These are **intermediates, not immediately edible foods**. The Food Properties are on the 20 completed recipes, which include smoked-beef wraps, smoked-chicken salads, salmon-apple plates, hash browns, pumpkin fritters, three measured fruit drinks, clear mushroom and cod soups, and silky pumpkin broth.

## What is really implemented

- Actual server-side main/offhand variant routing in KitchenProcessingBlock through KitchenV32Modes.resolve.
- Both main-hand and offhand recipe selectors are explicit and tested. All 27 new tool routes appear in a data manifest; the same 27 routes are declared in Java for deterministic behavior.
- Special-mode effects are short server-sent particles; for stock-pot processes the existing 10-tick ACTIVE model animation is reused. No persistent machine data, extra chunks, or client-only server calls.
- 47 new independent 16×16 sprites in their own generated resource folder; the old cuisine and previous kitchen art are not overwritten.
- 20 real 3-ingredient crafting recipes consume at least one of these new processing outputs and also use original Minecraft items and existing Agriculture produce.
- All new items appear in the existing Cuisine creative tab with English and Chinese display names.

## Testing and boundaries

CI checks old v2.8, v2.9, v3.0 and v3.1 contracts, exact per-mode input→output Java maps, absence of registry collisions, all 20 recipes, bilingual strings and all 47 generated sprites. It also builds a NeoForge Java 21 JAR and starts a real NeoForge dedicated server.

- Manual Minecraft client acceptance **still required** for animation polish, particles, cooking UX and game balance.
- "Measured" is a game abstraction (one input to one output), not a multi-slot multi-liquid simulator.
- Serving plates and bowls used in final crafting are not automatically returned. That requires a separately tested remaining-items implementation.
- No new full-time processing/fermentation or cooking timer systems are claimed.

## Suggested in-game test

1. On a backup world, replace the previous Agriculture JAR; do not put two Agriculture JARs in the mods folder.
2. Chopping Board + raw vanilla beef + Chef Knife in offhand -> Raw Beef Strips. Without the knife, unchanged legacy inputs should continue to work.
3. Chopping Board + vanilla potato + Box Grater -> Shredded Potato. Try without the grater: legacy Diced Potato should still result.
4. Smoking Rack above a lit campfire, main-hand Raw Beef Strips + Kitchen Tongs -> Smoked Beef Strips. Remove heat: conversion must refuse and never consume input.
5. Mixing Basin with Measuring Cup in offhand + Apple Juice -> Measured Apple Drink Base. With Wooden Spoon instead -> the old Apple Syrup recipe.
6. Stock Pot with Skimmer Spoon in offhand + Mushroom Stock -> Clear Mushroom Stock. With Soup Ladle instead, all original stock-pot recipes continue.
7. Craft one of the 20 new finished dishes and verify its new preparation ingredient is required; check Fusion installed and uninstalled.
