# Agriculture crop-specific art precision v1.5

## Scope

This is a crop-only visual-quality pass on the existing 64 crop catalog. Bee species, habitats, honey behavior and pollination mechanics are not expanded in this change. Existing registry IDs, crop ages, loot, recipes, composting and save-compatible block names remain unchanged.

## Texture corrections

The previous generator already emitted custom 16×16 PNGs, but much of the plant silhouette was shared by broad crop categories. A different palette alone is not enough to call a crop texture precise.

The v1.5 pass adds explicit crop-ID-specific details to both seedling and reproductive/mature stages for all 64 crops. The general plant silhouette also expands with age: wider foliage and progressively added side growth make ages 4–7 read as continued development rather than eight hash-distinct but visually similar sprites. A validated leaf-architecture map now gives every crop one of eight growth forms—blade, rosette, bulb/rhizome, vine, bush, fern, flowering stem or ribbed stalk—with different height caps and leaf geometry. The mapping follows growth habit rather than harvested-organ category; for example, strawberries use a low rosette, sweet potatoes use a trailing vine, and peanut/chili/okra plants use branching bush silhouettes. These details distinguish, for example:

- Grain forms: corn ears and kernels, rice panicles, barley awns, oat panicles, rye spikes, sorghum heads, millet clusters, buckwheat grains, quinoa clusters, sesame capsules and sunflower heads.
- Fruit forms: tomato calyxes, strawberry seed marks, cucumber/zucchini stripes, blueberry and cranberry berries, pepper lobes, eggplant calyxes, raspberry drupelets, ribbed okra pods and curved chili peppers.
- Roots and bulbs: onion/garlic/shallot bulbs, radish and turnip shoulders, sweet-potato/parsnip roots, peanut pods, and segmented ginger/turmeric rhizomes.
- Leafy crops: cabbage heads, loose lettuce rosettes, pointed spinach, broccoli and cauliflower heads, kale edges, celery ribs, leek blades, Swiss-chard stems, Brussels-sprout buds and bok-choy stalks.
- Pods, flowers, herbs and fiber: visible peas/beans in pods, chickpea/lentil forms, lavender spikes, clover flowers, mustard/canola blossoms, artichoke buds, distinct herb leaf patterns, cotton bolls and flax flowers.

The sprites remain compact Minecraft pixel art, not photographs or a claim that one cultivar has the same appearance in every growing region.

## Generated assets and safeguards

The Gradle generator still creates assets deterministically under `build/generated/resources/cropAssets` and packages them through `processResources`:

- 512 normal crop-stage textures: 64 crops × 8 ages.
- 256 pollinated-stage textures: 64 crops × ages 4–7.
- 768 matching crop models.
- 128 custom inventory icons: one produce icon and one seed icon for each crop.

QA now checks that every crop ID has explicit seedling and mature morphology branches, in addition to the existing checks for 64 crop registrations/profiles, eight distinct stages per crop, 64 unique mature sprites, 768 unique stage/pollination textures, bilingual labels, seed recipes, harvest loot and composting entries.
The Gradle build also emits `build/reports/agriculture/crop-stage-contact-sheet.png`: each crop appears on one labeled row, with ages 0–7 followed by pollinated ages 4–7. CI uploads this sheet as a separate `crop-stage-contact-sheet` artifact so the actual generated sprites can be visually reviewed instead of trusting file counts alone.

## Acceptance boundary

Source and structural checks cannot confirm the final in-game appearance by themselves. Before calling this release-ready, launch a fresh Minecraft 1.21.1 / NeoForge client and inspect all 64 crops at ages 0–7, the pollinated variants, mature drops, and the repeated-harvest regrowth cycle. The existing dedicated-server boot test is not a substitute for this graphical review.
