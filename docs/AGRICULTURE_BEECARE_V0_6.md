## v0.6 pollination and honey harvesting (2026-10-25)

This milestone refines the existing 36-crop / six-registered-bee content without adding species.

- **Separate reproductive pollination from vegetative growth:** CropClimate rolls control whether a plant grows this random tick, but no longer block nearby bee visits from marking flowers pollinated. Pollinated mature crops still yield the existing small gameplay bonus.
- **Two smoke-protected harvest outputs:** The existing honey harvester still requires a honey-level-5 vanilla nest or beehive and an unobstructed lit campfire below it. Holding shears in the offhand now selects **three honeycombs**, consuming one shears durability; otherwise the tool uses one glass bottle from offhand/inventory to collect **one honey bottle**. Each collection resets the vanilla honey level to zero. Standard vanilla interactions are preserved.
- **Species boundary:** Managed vanilla honey bees generate honeycomb/honey bottles. The custom bumblebee, mason, squash and leafcutter habitat blocks remain non-honey-producing observation structures. Xerces distinguishes cavity and ground nesters; no evidence supports treating solitary bees as honey-production colonies.
- No new crop IDs or bee IDs. No free honey, infinite honeycomb duplication or forced chunk loading.
- The current wild-bee AI still reuses vanilla Bee goals and does not simulate full solitary nesting and reproduction; habitat activity is a gameplay indicator, not real brood lifecycle.

### References

- UMN Extension, pollinator necessity depends on harvested plant organ: https://extension.umn.edu/agriculture/specialty-crops/pollination/requirements
- UMN Extension, squash bees and bumblebees forage on cucurbit flowers: https://extension.umn.edu/agriculture/specialty-crops/commercial-fruit-production/vining-vegetable-growing-guide
- Xerces Society, nesting resources for cavity- and ground-nesting bees: https://www.xerces.org/pollinator-conservation/nesting-resources
- NeoForge 1.21.1 docs, interaction pipeline: https://docs.neoforged.net/docs/1.21.1/items/interactionpipeline/

## v0.7 field ecology and performance correction

- **Reproductive visits:** A live bee must carry nectar for a late-stage crop to receive the bee-assisted harvest marker. This is a lightweight proxy for recent forage visitation, not actual pollen-species tracking. Mature crops are never scanned, reducing unnecessary random-tick work in mature fields.
- **Ground nesting:** The squash-bee habitat must be placed on bare dirt, coarse dirt, sand, or red sand. Grass, mycelium, rooted dirt and podzol no longer count as bare nesting substrate. The habitat loses support when soil below is removed. Source: Xerces Society, Nesting Resources, https://www.xerces.org/pollinator-conservation/nesting-resources .
- **Floral associations:** Bumblebee habitats treat mature tomato and eggplant crops as forage sources. Squash-bee habitat remains restricted to zucchini (Cucurbita pepo), not cucumber (Cucumis sativus). UMN Extension: https://extension.umn.edu/agriculture/specialty-crops/commercial-fruit-production/vining-vegetable-growing-guide and https://extension.umn.edu/agriculture/specialty-crops/pollination/requirements .
- **Rice performance:** The same seven-by-seven, two-height irrigation check now reuses a MutableBlockPos, avoiding up to 98 temporary coordinate allocations per random tick; the loaded-chunk guard and the freshwater requirement are preserved.
- **Honey harvesting:** Campfire smoke uses Minecraft's own CampfireBlock.isSmokeyPos implementation after checking loaded chunks. This aligns the harvester with vanilla hive smoke behavior rather than imposing an unrelated air-only rule. NeoForge mappings for CampfireBlock (1.21.1): https://aldak.netlify.app/javadoc/1.21.1-21.1.x/net/minecraft/world/level/block/campfireblock .

These are design and resource optimizations; no benchmark-based speedup percentage is claimed.


## v0.8 wild bee entity behavior: hive separation and lineage preservation

The six **existing** native-bee IDs now instantiate `WildPollinatorBee`, still extending
the native `Bee` class. No registry ID changed, and no crop was added.

- Wild bees cannot claim, travel to, or enter vanilla `beehive` / `bee_nest` blocks
  through normal vanilla hive goals. `hasHive()` remains false, hive assignments
  are ignored, and `getHivePos()` is null even for older entities with saved hive data.
  Consequently they should not inflate honey production as managed honeybee workers.
- Flower-assisted animal breeding (a **gameplay abstraction**, not accurate solitary-bee
  reproduction) now requires parents of the same registered species and returns that
  same entity type, never a vanilla honeybee.
- The four separate habitat blocks remain observational nest-site structures, not
  physically occupied colonies. Vanilla Bee flight, model, nectar-gathering and other
  behavior are **still reused**. Do not claim full solitary-bee seasonal nesting or
  brood provisioning, which is outside the scope of this change.

### Biological and code references

- Xerces Society, nesting resources (ground/cavity/bumblebee distinction):
  https://xerces.org/pollinator-conservation/nesting-resources
- USDA ARS, squash bees are solitary ground nesters and Cucurbita specialists:
  https://www.ars.usda.gov/pacific-west-area/logan-ut/pollinating-insect-biology-management-systematics-research/docs/squash-pollination/
- NeoForge 1.21.1 Bee API (hive and reproduction virtual methods):
  https://aldak.netlify.app/javadoc/1.21.1-21.1.x/net/minecraft/world/entity/animal/bee
