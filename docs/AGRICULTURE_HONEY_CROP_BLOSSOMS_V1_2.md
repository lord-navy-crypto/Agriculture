# Crop-blossom honey variants v1.2

The agriculture mod now has 30 floral-honey bottle variants: 29 specific forage sources and one wildflower blend. Seven new crop-blossom honeys make the v1.8 crop expansion matter to beekeeping as well as farming.

| Honey bottle | Source crop | Food points | Temporary effect |
|---|---|---:|---|
| Cassava blossom | Cassava | 6 | Speed I, 5 s |
| Taro blossom | Taro | 6 | Water Breathing I, 6 s |
| Yam blossom | Yam | 7 | Resistance I, 5 s |
| Watercress | Watercress | 5 | Regeneration I, 4 s |
| Endive | Endive | 5 | Haste I, 6 s |
| Kohlrabi | Kohlrabi | 6 | Absorption I, 5 s |
| Rutabaga | Rutabaga | 6 | Night Vision I, 8 s |

These seven effects are explicitly marked as custom fantasy gameplay effects in the data and translations. They are not claims about the medical properties of real honey. The 14 vanilla-flower honey variants continue to mirror their Java 1.21.1 suspicious-stew effect/duration contract, including adverse effects. Wildflower honey remains a no-bonus mixed-nectar item.

## Source selection

The existing harvest rules remain: a fully stocked vanilla beehive or bee nest, a clear lit-campfire smoke column, an empty bottle, at least six plants of one source, and a two-thirds dominant share. The source scan is bounded to the local area and never force-loads chunks. Mixed forage can still yield wildflower honey; wind/self-pollinated grain crops do not artificially dilute nectar counts.

## Asset and QA contract

Every new bottle has a dedicated item registry entry, bilingual item/effect translations, an item model, and a deterministic 16×16 source-colored sprite. CI validates that all 30 generated honey sprites are distinct and that source data, item effects, source routing, and language keys agree.
