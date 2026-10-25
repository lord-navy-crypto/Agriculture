# Agriculture and pollinator improvement — v0.4

## Scope

Keep the existing 36 plantable crops. This milestone improves their pollination payoff and adds a safer honey-collection option; it does not add new crop species.

## Crop behavior

- The new data file `data/gugugaga/agronomy/crop_profiles_v0_4.json` now records reviewed profiles for all 36 existing crops: harvested organ, gameplay climate band, qualitative water class, real-world pollination notes, game pollination group, and cited maturity ranges where a suitable extension source provides them.
- CropClimate explicitly classifies all 36 registered seed types and fails fast if a future crop is added without a climate classification.
- The current crop-growth climate bands remain a gameplay abstraction, not a claim that Minecraft biome temperatures equal real soil temperatures.
- Crops in the existing NONE pollination group remain independent of bee presence.
- Pollinator-compatible crops can be pollinated during their reproductive/flowering growth stages (age 4–6).
- Pollination no longer accelerates crop age. A compatible bee marks the crop as pollinated, and a mature pollinated crop yields one additional produce item at harvest.
- The pollination check is performed on the server during the crop's existing random tick and stops once pollinated, limiting entity scans and avoiding persistent background tasks.

## Bee ecology and nests

The four primary independent pollinator entities remain bumblebee, mason bee, squash bee, and leafcutter bee. They are not biologically interchangeable:

- Bumblebees form social colonies and commonly nest in sheltered cavities or abandoned small-mammal nests. They can support buzz-pollinated crops.
- Mason bees are solitary cavity nesters and are useful early-season pollinators for crops such as berries.
- Squash bees are solitary ground nesters closely associated with cucurbit flowers; retain undisturbed bare-soil nesting habitat in the gameplay model.
- Leafcutter bees are solitary cavity nesters that use leaf pieces to line brood cells. Their key commercial pollination use is alfalfa seed, which is not among the current 36 crops.

**Honey accuracy rule:** managed honey bees make the familiar harvestable surplus honey. Bumblebees and the solitary mason, squash, and leafcutter bees should not be turned into four interchangeable honey factories. Their primary gameplay reward is crop pollination; their nesting sites and species-specific behavior are a separate follow-on milestone.

## Honey collection

- Honey Harvester works with vanilla beehives and bee nests, so it does not replace or corrupt vanilla hive storage.
- It requires honey level 5, a clear column to a lit campfire or soul campfire within five blocks below the hive, and an empty glass bottle in the player's inventory (except in Creative mode).
- It consumes one bottle, outputs one honey bottle, resets honey level to 0, and does not deliberately anger the colony.
- It never creates honey from an empty hive and never force-loads a neighboring chunk.

## Authoritative ecology references

- University of Minnesota Extension, cucurbit pollination and squash bee nesting: https://extension.umn.edu/agriculture/specialty-crops/commercial-fruit-production/vining-vegetable-growing-guide
- University of Minnesota Extension, native and managed pollinator nesting differences: https://extension.umn.edu/agriculture/specialty-crops/pollination/minnesota-pollinators
- Xerces Society, nesting resources (ground-nesting vs cavity-nesting native bees): https://www.xerces.org/pollinator-conservation/nesting-resources
- USDA Agricultural Research Service, insect pollination handbook: https://www.ars.usda.gov/arsuserfiles/20220500/onlinepollinationhandbook.pdf
- International Rice Research Institute, alternate wetting and drying in rice systems: https://ghgmitigation.irri.org/mitigation-technologies/alternate-wetting-and-drying

## Next implementation milestone

1. Add species-specific nest-site blocks and spawn/habitat checks for the four primary pollinators.
2. Replace shared vanilla-bee nesting assumptions only after the custom entity and hive APIs are verified against Minecraft 1.21.1 / NeoForge 21.1.259.
3. Add dedicated in-game integration tests for harvest output, campfire safety, and crop-yield changes.
4. Build species-specific nesting-site blocks and lifecycle behavior without pretending solitary bees make harvestable honey.

## v0.5: four source-backed bee nesting-site prototypes

The original 36 crops remain unchanged in count. Four habitat blocks are now registered with individual block IDs, recipes, loot tables and activity values (0–3), unlocked via the creative tools tab:

- `gugugaga:bumblebee_habitat`: sheltered cavity, accepts nearby bumblebees plus flowering forage.
- `gugugaga:mason_bee_habitat`: wooden cavity/stem proxy for mason bees.
- `gugugaga:squash_bee_habitat`: a bare-soil habitat that must be placed above dirt/grass/sand-like substrates. Activity requires squash bees and zucchini crops at reproductive age.
- `gugugaga:leafcutter_bee_habitat`: wooden nesting tube proxy for solitary leafcutter bees.

The four new blocks are **nesting-site activity indicators**, not new honey-producing beehives. They gain activity levels from nearby corresponding bees and suitable flowers or crops. They **do not yet store bees, simulate brood, force bees to return home, or generate honey**. All bee AI currently still inherits vanilla Bee behavior; species-specific nesting goals remain a later, more invasive change.

This is biologically motivated by Xerces Society guidance that bumble bees occupy sheltered cavities, mason/leafcutter bees use cavities, and squash bees dig nests in soil near cucurbits. UMN Extension confirms squash bee/cucurbit affinity; USDA ARS documents cavity nesting and brood cells for alfalfa leafcutting bees. No numeric nest-time parameter was converted from real data.

Sources:
- https://xerces.org/pollinator-conservation/nesting-resources
- https://extension.umn.edu/agriculture/specialty-crops/commercial-fruit-production/vining-vegetable-growing-guide
- https://www.ars.usda.gov/pacific-west-area/logan-ut/pollinating-insect-biology-management-systematics-research/docs/alfalfa-leafcutting-bee-alcb/

## v0.5 pollinator specificity correction

A verified University of Minnesota Extension guide describes squash bees as closely associated with *Cucurbita* squash and pumpkins. Our 36-crop selection includes **zucchini** (*Cucurbita pepo*), but cucumbers belong to *Cucumis*. The squash-bee bonus and squash-bee soil habitat therefore require zucchini in this prototype, not cucumber. Other bee species can still assist cucumbers. Tomatoes and eggplants are self-fertile; the optional buzz-pollination yield bonus now recognizes bumblebees specifically without preventing normal no-bee crops from maturing.

Sources:
- https://extension.umn.edu/agriculture/specialty-crops/commercial-fruit-production/vining-vegetable-growing-guide
- https://extension.umn.edu/agriculture/specialty-crops/pollination/requirements
- https://extension.umn.edu/agriculture/specialty-crops/pollination/minnesota-pollinators

### Activity lifecycle correction

Nest-site activity is no longer permanently locked at level 3 after a single bee visit. Once nearby matching bees or suitable flowering forage disappear, activity decays gradually. This is only an observational gameplay indicator, not colony death or a count of living brood. The process is bounded by random block ticks without background jobs, autonomous spawning, or force-loaded chunks.
