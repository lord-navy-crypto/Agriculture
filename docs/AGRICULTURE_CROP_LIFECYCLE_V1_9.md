# Agriculture crop lifecycle v1.9

## New crop lines

v1.9 adds eight crops to the validated catalog, bringing the total to 88: jicama, adzuki bean, mung bean, cowpea, safflower, chicory, mustard greens and water spinach.

Each line includes a registered crop block and planting item, harvested produce, climate-band mapping, eight growth ages, pollination-state blockstate coverage, seed recipe, mature-only produce loot, bilingual names, compost data, dedicated seed art and crop-specific stage/produce art. Mustard greens and water spinach use repeat-cut leaf harvest. The climate bands are gameplay proxies, not a measured climate simulation.

## Visual and resource contract

The generator validates 88 unique crop profiles, 1,056 stage/pollination models and textures, and 176 unique crop/seed inventory icons. Unknown harvested-organ mappings fail the build rather than silently receiving a generic-looking icon. CI publishes review sheets; a graphical client pass is still required to judge visual readability in-world.
