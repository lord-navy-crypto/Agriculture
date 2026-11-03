# GeckoLib integration — Gugu-Gaga Agriculture 3.11

## Runtime requirement

The current v3.11 GeckoLib prototype uses **GeckoLib 4.9.3 for Minecraft 1.21.1 NeoForge**.

Install the matching GeckoLib 4.9.3 NeoForge mod on both the client and dedicated server. The Agriculture JAR references GeckoLib as a runtime mod dependency; it does not shade or bundle GeckoLib classes.

The Gradle dependency uses the official GeckoLib Cloudsmith Maven repository and the documented artifact pattern:

`software.bernie.geckolib:geckolib-neoforge-1.21.1:4.9.3`

## Shared 3D animation framework and converted workstations

The existing `gugugaga:dough_bench` registry ID, recipe map, rolling-pin requirement, collision shape, item ID, loot table and finite ACTIVE blockstate are retained.

Only the in-world renderer is changed:

- `GeckoKitchenStationBlock` implements `EntityBlock` and selects `ENTITYBLOCK_ANIMATED` for all three stations.
- `GeckoKitchenStationBlockEntity` is a shared `GeoBlockEntity` with an immutable profile for kneading, skillet or steaming.
- The existing server-owned `KitchenV3AnimatedBlock.ACTIVE` state selects each station's work animation. Vanilla blockstate synchronization remains authoritative.
- A single client-only `GeoBlockRenderer` resolves the correct geometry, texture and animation file from the profile.
- Renderer registration is client-only. Dedicated-server code does not load client renderer classes.
- Existing item IDs, crafting recipes, utensils, ingredient transformations, collision shapes and finite 10-tick work window remain unchanged.
- Static inventory item models remain the existing vanilla models.

## Asset layout

- `kneading_bench`: root, dough and roller bones; roller movement and dough compression.
- `skillet_station`: root, pan, handle, spatula and food bones; spatula movement and food bounce.
- `bamboo_steamer`: root, basket and lid bones; lid lift and settling motion. Existing vanilla smoke particles provide steam.
- Each station has its own `geo/*.geo.json`, `animations/*.animation.json` and generated 64×64 four-material texture atlas.

The PNG is generated deterministically during `processResources`; no runtime image generation is used.

## Acceptance tests

CI runs `qa/validate_geckolib_kitchen_v311.py` against the built JAR. It checks the exact GeckoLib dependency, block-entity and client renderer registration, model bones, animation clips, generated texture dimensions and byte-for-byte JAR asset parity.

Headless CI cannot prove perceived animation quality. In a real 1.21.1 NeoForge client, place each station, use its matching reusable utensil in the offhand, and process a valid ingredient: rolling pin for dough, frying spatula for the skillet, and steamer lid for the bamboo steamer. Confirm each rig switches to its work animation for the existing 10-tick ACTIVE window and returns to idle. Also confirm item icons, block placement, recipe outputs and dedicated-server startup.
