# Nutrition v4.13 — 6 / 9 / 12 reserve recovery balance

The mod tracks 27 **abstract Minecraft nutrient indicators**. Twenty-five
indicators use independent daily intake ledgers and reserves; hydration and
sodium remain exclusively owned by WaterSaltV37. These are game points, not
real dietary quantities, doses, RDAs, or clinical measurements.

## New recovery caps

| Tier | Daily reserve recovery | Nutrient indicators |
|---|---:|---|
| Slow | +6 | Vitamin A, B12, D, E, calcium, iodine, selenium, phosphorus |
| Medium | +9 | Protein, fat, fiber, B9, K, iron, magnesium, potassium, zinc |
| Fast | +12 | Carbohydrate, B1, B2, B3, B5, B6, B7, C |

Hydration and sodium use their separate water/salt model and are not included
in these 25 reserve caps.

## What remains unchanged
- 27 nutrient indicator order and all persistent NBT keys.
- Daily food intake targets (most micros: 3 points; macros retain their own).
- Daily shortage/depletion losses and B1/B12-specific persistence windows.
- 0–100 reserve bounds, 65 positive-effect threshold and warning thresholds.
- Immediate per-meal feedback, per-day intake ledger, and no double credit
  at the next day rollover.
- Existing saved nutrient reserves are migrated unchanged.

## Calculation
For a nutrient with daily intake target T and daily reserve cap R:

`credit(I) = min(R, ceil(I × R / T))`

A meal awards only the difference between the new and previous cumulative
credit. Meeting the daily target earns the full tier cap; additional food
cannot exceed that cap. Reaching a reserve score of 65 from 45 with full
daily intake takes 2 capped days for a +12 nutrient, 3 days for +9, and
4 days for +6, subject to current reserve clamping and no shortfall losses.

## Validation
- Exact 27-slot cap table, including zero-owned hydration and sodium slots.
- Every active reserve is in the 6–12 range and all three tiers are present.
- Daily targets, depletion loss and latency arrays remain unchanged.
- Intake credit is monotonic, bounded, and reaches the cap exactly at target.
- Midnight does not duplicate meal credit.
- N-key tooltip still displays today's intake, target and the updated cap.
- English and Chinese tooltip placeholders remain valid.
