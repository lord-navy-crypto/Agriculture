# Suspicious-stew parity for floral honey (Java 1.21.1)

**Rule:** A vanilla flower's honey now confers **exactly the same effect and
duration as that flower's vanilla Java 1.21.1 suspicious stew**. This also
includes adverse effects (blindness, poison, weakness, wither); no
beneficial-only or arbitrary substitutions.

The existing four mod-original honey variants from clover, lavender,
buckwheat and sunflower have no native corresponding suspicious stew;
their pre-existing fantasy effects are retained, distinctly marked as
*custom*. No overwriting of native Minecraft suspicious stew is performed.

Flower | Honey effect | Duration
---|---|---
Dandelion (`minecraft:dandelion`) | Saturation I | 0.35 s
Poppy (`minecraft:poppy`) | Night Vision I | 5 s
Allium (`minecraft:allium`) | Fire Resistance I | 4 s
Cornflower (`minecraft:cornflower`) | Jump Boost I | 6 s
Azure Bluet (`minecraft:azure_bluet`) | Blindness I | 8 s
Blue Orchid (`minecraft:blue_orchid`) | Saturation I | 0.35 s
Lily of the Valley (`minecraft:lily_of_the_valley`) | Poison I | 12 s
Oxeye Daisy (`minecraft:oxeye_daisy`) | Regeneration I | 8 s
Torchflower (`minecraft:torchflower`) | Night Vision I | 5 s
Red Tulip (`minecraft:red_tulip`) | Weakness I | 9 s
Orange Tulip (`minecraft:orange_tulip`) | Weakness I | 9 s
White Tulip (`minecraft:white_tulip`) | Weakness I | 9 s
Pink Tulip (`minecraft:pink_tulip`) | Weakness I | 9 s
Wither Rose (`minecraft:wither_rose`) | Wither I | 8 s

**Important mechanical case:** vanilla `HoneyBottleItem.finishUsingItem`
removes Poison, which would negate lily-of-the-valley parity if implemented
only as a food effect. The lily variant now subclasses HoneyBottleItem and
applies poison **after** native honey consumption. All other variants reuse
native HoneyBottleItem, native bottles and native drinking behavior.

**Harvesting unchanged:** full vanilla honey-level-5 hive, empty glass
bottle, lit campfire smoke, local plant scan (at least six, >=2/3 dominant),
reset native honey level; 6 wild-bee entities cannot manufacture these bottles.
This floral-source estimate is a Minecraft abstraction, not bee flight history.

**Version caution:** 1.21.4 changed some suspicious stew durations and
introduced eyeblossoms; this mod targets **1.21.1**, so these 1.21.4+
details are deliberately excluded.

**Sources:**
- Minecraft Wiki, Suspicious Stew, including 1.21.4 history:
  https://minecraft.wiki/w/Suspicious_stew
- Original Java-edition flower durations (pre-1.21.4):
  https://minecraft.wiki/w/Suspicious_Stew/Effects
- NeoForge 1.21.1 food effects API:
  https://docs.neoforged.net/docs/1.21.1/items/
