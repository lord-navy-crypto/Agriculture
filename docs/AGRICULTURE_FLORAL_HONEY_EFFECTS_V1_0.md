# v1.0 Flower-sourced honey and food effects

This update uses **Minecraft suspicious stew** as its gameplay inspiration,
not as a claim that eating real monofloral honey has potion-like effects.

Honey bottle | Food points | Saturation modifier | Temporary fantasy effect
---|---:|---:|---
Clover | 6 | 0.20 | Jump Boost I, 10s
Lavender | 5 | 0.35 | Night Vision, 15s
Buckwheat | 7 | 0.15 | Resistance I, 10s
Sunflower | 6 | 0.25 | Speed I, 12s
Dandelion (vanilla flower) | 6 | 0.20 | Haste I, 12s
Poppy (vanilla flower) | 5 | 0.30 | Slow Falling, 10s
Allium (vanilla flower) | 6 | 0.20 | Regeneration I, 5s
Cornflower (vanilla flower) | 6 | 0.20 | Water Breathing, 12s

**Vanilla protections remain**: level-5 managed beehive, lit campfire smoke,
a consumed empty bottle, honey level resets; bee habitats don't produce honey.
At harvest the selector counts mature nearby flowering crops, specific native
Minecraft flowers and other mixed blossoms. At least six plants of one source
and >=2/3 of counted forage needed to earn a floral bottle. Otherwise produce
ordinary vanilla honey. Flower types are not recorded in individual bee NBT
or native hive inventories, so this is a low-cost **local-habitat proxy** and
not a claim of accurate nectar-flight history or real botanical certification.

## References
- Minecraft official 1.21.4 release notes: floral identity controls suspicious
  stew effects: https://feedback.minecraft.net/hc/en-us/articles/32385811139085-Minecraft-Java-Edition-1-21-4-The-Garden-Awakens
- NeoForge 1.21.1 food system:
  https://docs.neoforged.net/docs/1.21.1/items/
- NeoForge 1.21.1 status effects:
  https://docs.neoforged.net/docs/1.21.1/items/mobeffects/
- USDA ARS honey technical review: real floral sources affect aroma and flavor,
  **not** miraculous in-game effects:
  https://www.ars.usda.gov/research/publications/publication/?seqNo115=126568
