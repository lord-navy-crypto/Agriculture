# Nutrition v4.11 — balanced recovery tiers

The mod tracks 27 **abstract Minecraft nutrient indicators**; 25 use independent
daily intake ledgers and reserves, while water and sodium are delegated to the
existing WaterSaltV37 service. These units are not actual dietary quantities.

## Immediate reserve recovery (not delayed digestion)
Every completed food use updates reserves immediately. Each nutrient has its
own daily capped recovery. The contribution points satisfy today's intake
target, but are NOT added 1:1 to the reserve.

For nutrient i:
- T_i = daily food intake target (3 for most nutrients)
- R_i = recovery cap for current game day
- credit_i(I) = min(R_i, ceil(I*R_i/T_i))
- actual food gain = min(100, current + max(0,credit_i(newIntake)-credit_i(oldIntake))) - current

Already credited food is NEVER credited a second time at the next day boundary.
Missing daily intake still causes gradual depletion, with the old nutrient-
specific shortfall duration and losses. Existing saved scores are preserved.

## Gameplay cap tiers

| Tier | Daily reserve gain | Examples |
| --- | --- | --- |
| Slow | +4..+6 | A, B12, D, E, K, calcium, iodine, selenium, phosphorus |
| Medium | +7..+9 | protein, fat, fiber, B9, iron, magnesium, potassium, zinc |
| Fast | +10..+12 | carbohydrate, B1, B2, B3, B5, B6, B7, C |

Examples: vitamin B12 = +4/day, vitamin C = +12/day, B1 = +12/day.
Starting at 45, reaching the positive-effect threshold 65 requires 5 capped
B12 game days or 2 capped C game days. No instant reserve maximization.

The existing UI hover now reveals each day's consumed food points, target and
specific daily recovery cap. Original 27 indicator order, effect thresholds,
hydration/sodium ownership, data keys and network version remain unchanged.

## Test plan
- Verify 3/3 role mapping on N-key GUI after eating.
- Test repeated meals on one game day vs crossing a worldGameTime day.
- Test save upgrades (no reset), positive and negative effects.
- Validate independent hydration/sodium changes and creative exclusions.
- Compile the combined mod and exercise the NeoForge server/client.
