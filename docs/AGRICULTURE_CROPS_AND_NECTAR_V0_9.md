# Agriculture & Apiaries v0.9 — eight crop lines and four nectar-sourced honeys

**New crops:** lavender, clover, basil, mint, mustard, canola, celery, leek.
The existing **36** crops remain unchanged: 44 registered crops in total.
Every addition has a plantable seed, biome-climate band, eight age stages, seed
return, maturity-locked harvest item, English/Chinese labels and a crafting recipe.

**Botanical notes:** Lavender, basil and mint are aromatic herbs; celery and
leek supply edible vegetative organs; mustard and canola are modeled as seed/oilseed
crops; clover is modeled as a floral forage and cover crop. Herbs often support
multiple leaf harvests, and clovers/lavender can be perennial. The shared 0–7
Minecraft crop lifecycle is **intentionally simplified**, not an accurate seasonal
plant phenology or stalk/bulb cultivation protocol. Canola and mustard may gain
optional bee-assisted seed yield, but leafy herbs do not need bee visits to
provide harvested leaves.

**Honey types:** clover, lavender, buckwheat and sunflower bottles are produced
through the existing smoke-safe honey harvesting tool. When bottling from a full
vanilla managed hive, a one-time bounded scan (radius 5 blocks horizontally)
identifies the predominant available flowering resource:
- >=6 late-stage plants of one qualifying crop;
- that crop comprises >= two thirds of the locally counted nectar plants and flowers;
- otherwise the output is ordinary Minecraft honey (mixed or unidentified forage).
- Shears mode still yields three vanilla honeycombs; never creates typed honeycombs.
- Each collection still requires one glass bottle except Creative mode, a full
  native hive (honey level 5), clear native campfire smoke and resets hive level.
- The new bottles inherit vanilla HoneyBottleItem food/drink and container behavior.
  Initial assets reuse vanilla honey bottle textures and crop models, pending art.

**The floral scan is a gameplay abstraction, not monofloral certification**:
real honey is made from nectar gathered over time by foraging honeybees across
much larger landscapes. No inference of exact nectar history or bee flight is
claimed. Wild bee nests never produce bottled honey.

## Sources

- University of Minnesota Extension, growing herbs (heat, soil and botanical
  families): https://extension.umn.edu/garden-and-home/yard-and-garden/gardening-in-minnesota/growing-herbs
- University of Minnesota Extension, planting vegetable gardens: https://extension.umn.edu/garden-and-home/yard-and-garden/gardening-in-minnesota/planting-the-vegetable-garden
- University of Minnesota Extension, seed-starting leeks:
  https://extension.umn.edu/garden-and-home/yard-and-garden/gardening-in-minnesota/starting-seeds-indoors
- University of Arizona Cooperative Extension, monofloral honeys:
  https://extension.arizona.edu/publication/monofloral-honeys
- University of Arizona Cooperative Extension, apiary floral abundance:
  https://extension.arizona.edu/publication/arizona-specialty-honeys
- FAO, floral calendars and nectar sources:
  https://www.fao.org/4/x0083e/X0083E04.htm
- USDA AMS, clover and buckwheat honey floras:
  https://www.ams.usda.gov/sites/default/files/media/Comb_Honey_Inspection_Instructions%5B1%5D.pdf

## v0.9.1 iterative herb harvest

Mature **basil and mint** crops can be harvested by empty-hand right-click,
producing one leaf item while retaining the plant and returning it to age 4.
Both plants can grow again normally. Ordinary plant breaking still uses
the original seed/produce loot tables, so this is an alternative harvesting
route, not duplicate simultaneous output. Other crops retain original behavior.

UMN Extension documents successive harvests of culinary herb foliage:
https://extension.umn.edu/garden-and-home/yard-and-garden/gardening-in-minnesota/growing-herbs
