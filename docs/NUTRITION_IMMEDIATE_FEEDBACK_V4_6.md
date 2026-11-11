# Backroom Agriculture — Nutrition feedback repair

## What was broken / 原先问题
- Completed meals were written only into a hidden daily ledger. The visible 0–100 nutrient reserves did not update until the next Overworld day, creating the appearance that vegetables had no effect.
- The day counter used daylight time, which could stop advancing when the day-night cycle was disabled.
- Both raw eggplant and eggplant curry mistakenly inherited the egg nutrient profile due to the word "eggplant".

## New behavior / 新规则
- Each completed eating event immediately credits the 25 gameplay nutrient reserves (five broad categories include water; sodium and water are tracked by the separate water/salt system).
- A nutrient's daily gain is capped by its existing recovery limit, usually 2–5 game points. Its own daily intake target determines the gain. Repeated identical food cannot generate unlimited nutrition.
- Reaching the daily target prevents the next day's loss but **never credits the same meal twice**.
- The common Overworld game-time clock progresses even with daylight cycling off.
- Eating produces a localized action-bar summary of up to three gained reserves. When daily limits prevent further gains, it explains that explicitly. The N-key panel shows the new server-owned values when reopened.
- Eggplant uses only the vegetable group. Eggplant curry gains a small game fat/Vitamin-E override for its safflower oil ingredient.
- No world save keys, food item IDs, recipes, hydration/sodium ownership, or nutrition indicator indices changed.

## Test / 测试
1. Back up the world and install the updated JAR with its dependencies.
2. In Survival mode, press N and note the vitamin-A, fiber, vitamin-K, and vitamin-C values.
3. Eat one lettuce, cucumber, eggplant, or carrot (ensure you can complete the eating action). Reopen N: several relevant reserves should be higher immediately and an action-bar message should appear.
4. Drink water; verify the water meter updates through the separate water/salt model.
5. Repeat the same vegetable many times; the per-day gain eventually caps, with an explicit no-gain message.
6. Advance one ordinary game day; meeting a nutrient target does not grant a second bonus and never punishes unmet nutrients instantly.
7. With the daylight cycle disabled, the monotonic game-time maintenance still advances.
8. Verify preserved 27 indicator keys and existing saved player values.

This is an abstract fictional game-balance system. These scores are not real nutrient amounts or medical advice.
