# GeckoLib integration — Gugu-Gaga Agriculture 3.11

## Runtime requirement

The current v3.11 GeckoLib prototype uses **GeckoLib 4.9.3 for Minecraft 1.21.1 NeoForge**.

Install the matching GeckoLib 4.9.3 NeoForge mod on both the client and dedicated server. The Agriculture JAR references GeckoLib as a runtime mod dependency; it does not shade or bundle GeckoLib classes.

The Gradle dependency uses the official GeckoLib Cloudsmith Maven repository and the documented artifact pattern:

`software.bernie.geckolib:geckolib-neoforge-1.21.1:4.9.3`

## First converted workstation: Dough Bench

The existing `gugugaga:dough_bench` registry ID, recipe map, rolling-pin requirement, collision shape, item ID, loot table and finite ACTIVE blockstate are retained.

Only the in-world renderer is changed:

- `GeckoKneadingBenchBlock` implements `EntityBlock` and selects `ENTITYBLOCK_ANIMATED`.
- `GeckoKneadingBenchBlockEntity` implements `GeoBlockEntity`.
- The existing server-owned `KitchenV3AnimatedBlock.ACTIVE` state selects the GeckoLib work animation. Vanilla blockstate synchronization remains authoritative.
- The idle animation gently moves the roller; the work animation rolls it across the dough and slightly compresses the dough bone.
- A client-only `GeoBlockRenderer` is registered through `EntityRenderersEvent.RegisterRenderers`. Dedicated-server code does not load client renderer classes.
- The item inventory model remains the existing vanilla block model.

## Asset layout

- `assets/gugugaga/geo/kneading_bench.geo.json`: root, dough and roller bones.
- `assets/gugugaga/animations/kneading_bench.animation.json`: idle and work animation clips.
- `assets/gugugaga/textures/entity/kneading_bench.png`: generated 64×64 four-material atlas.

The PNG is generated deterministically during `processResources`; no runtime image generation is used.

## Acceptance tests

CI runs `qa/validate_geckolib_kneading_v311.py` against the built JAR. It checks the exact GeckoLib dependency, block-entity and client renderer registration, model bones, animation clips, generated texture dimensions and byte-for-byte JAR asset parity.

Headless CI cannot prove perceived animation quality. In a real 1.21.1 NeoForge client, place the Dough Bench, use a rolling pin in the offhand and process valid dough ingredients. Confirm the roller animation follows the 10-tick ACTIVE state, then returns to idle. Also confirm the static item icon, block placement, recipe output and dedicated-server startup.
