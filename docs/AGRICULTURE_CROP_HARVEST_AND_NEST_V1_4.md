# v1.4 Fruit crop harvest and wild bee habitat supports

## Fruit and berry production
Farmers can now empty-hand right-click mature tomato, cucumber, strawberry,
blueberry, bell pepper, eggplant, zucchini, raspberry, cranberry, okra,
chili pepper, green bean or pea crop blocks. Each harvest:
- yields one produce item, or **two** if a bee-dependent crop was successfully
  marked `pollinated` in its flowering window;
- keeps the plant in the world, resets its age to 4 and its saved `pollinated`
  property to false, then allows normal climate/farmland-controlled regrowth;
- returns PASS when immature, preventing duplicate harvests;
- leaves normal break-to-harvest seed/produce loot tables unchanged.

Repeated picking models multiple harvests from healthy plants, not perennial
plants surviving winters forever. The current crop art still uses vanilla
growth-stage placeholders, so leaves/vines and fruit stages are simplified.

## Wild bee nest supports
All four crafted nest habitat blocks now require support:
- the squash bee still requires exposed dirt, coarse dirt, sand or red sand;
- bumblebee cavities, mason bee hotels and leafcutter bee hotels require a
  solid upwards-facing block surface underneath.
Removing support causes the habitat block to break instead of leaving a
floating ground burrow or bee hotel. Local population-capped emergence still
applies and is not real brood storage; no wild nest produces honey.
Previous habitat block registry IDs remain unchanged.

## References
- University of Maryland Extension, repeated and single-harvest distinctions:
  https://www.extension.umd.edu/resource/when-plant-vegetables
- Missouri Extension, eggplant repeat harvesting:
  https://extension.missouri.edu/publications/g6369
- Clemson Extension, cucumber picking and fruit production:
  https://hgic.clemson.edu/factsheet/cucumber/
- Xerces Society, cavity and ground nest sites:
  https://xerces.org/pollinator-conservation/nesting-resources
