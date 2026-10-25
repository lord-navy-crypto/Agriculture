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
