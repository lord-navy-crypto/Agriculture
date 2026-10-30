# Agriculture v3.8.0-rc.2 — Nutrition feedback contracts

This system is **fictional Minecraft game scoring**, not a nutritional model,
medical diagnosis, eating plan, or recommended dietary intake.

## Five disjoint groups (cover all 27 existing nutrient slots)

| Group | Score indices in v3.5 catalog | Status reward | Long-term low effect |
| --- | --- | --- | --- |
| Energy | 0, 2 | Speed I | Slowness I |
| Protein / maintenance | 1, 12, 13, 14, 19 | Haste I | Mining Fatigue I |
| B-family variety | 6–11 | Haste I | Mining Fatigue I |
| Plant-food variety | 3, 4, 5, 16, 17 | Night Vision I | Slowness I |
| Mineral support | 15, 18, 20–26 | Resistance I | Weakness I |

All 27 indices are present exactly once. They are grouped for game usability,
not because every nutrient in one group shares the same biological mechanism.
No changes to the actual 403-item nutrition catalog or v3.7 water/salt catalog.

## Decision thresholds

- Group score: **integer average** of the current 0–100 scores in the group.
- Low group: at least half of its metrics below 30 AND group average below 36.
- Improvement suggestion: low group with fewer than 3 consecutive elapsed game days, OR average below 42.
- Persistent strain: low group for at least 3 game days. Maximum **one** mild effect, duration 220 ticks refreshed each 200 ticks, never damage or death.
- Positive focus: group average at least 70 and half or more of that group's metrics at least 60. Maximum **one** short-lived extra positive effect; never stacked over the established food-diversity bonuses.
- Ordinary water/salt penalties take precedence; nutrition negative effects are suppressed during serious water or salt imbalance to avoid compounded impairment.
- All thresholds are game-balancing choices, **not real clinical cutoff values**.

## Storage and recovery

Existing player nutrition tag `gugugaga_nutrition_v35` remains unchanged.
Five new streak counters and last-observed game day are additional NBT keys.
New and upgraded worlds start at zero streak with a grace period. Day changes
increment at most three steps to avoid arbitrarily large jumps from time commands.
Scores and state persist across world save/load and player clone (death).
After eating, a group that no longer meets the low criterion clears its low streak.
Effects are short and expire quickly once no longer renewed; we do not forcibly
remove unrelated vanilla or other-mod potion effects.

## Client contract

The N-key Nutrition panel now has seven pages: five existing nutrient pages,
the unchanged sixth water/salt page, and a seventh group feedback page.
Server-authoritative snapshots carry 27 raw scores, hydration/salt, and five
group averages/status codes/day counters. The client never calculates or uploads
nutrition history. Hover a group row to read the full translated status and
streak. No real-world dietary recommendations are displayed.

## Manual tests

1. Upgrade a v3.8.0-rc.1 save: confirm raw scores are unchanged and new streaks start at 0.
2. In a test copy, simulate day progression for at least three days with a low
   group and confirm one mild penalty; low water/salt takes precedence.
3. Consume known varied foods and verify streak reset and brief effect expiry.
4. Check positive focus at high scores without duplicate effects over old
   multi-food bonuses.
5. Verify 7-page labels, full hover descriptions, narrow GUI scales, and
   localized zh_CN / en_US strings; multiplayer should receive server data.
