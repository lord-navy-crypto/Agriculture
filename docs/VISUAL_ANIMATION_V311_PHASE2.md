# Gugu Gaga Agriculture — Visual & Animation Refinement v3.11

**Scope:** Minecraft 1.21.1 / NeoForge 21.1.259 / Java 21.
**Version:** 3.11.0-alpha.2, continuing visual work on the existing Draft PR.

## 1. Four-frame animated surfaces

Ten deterministic four-frame PNG atlases are now generated at build time:

| Existing system | Animated surface | State behavior |
|---|---|---|
| Dough Bench | Moving rolling ridge / flour texture | Only ACTIVE model |
| Skillet Station | Sizzle and browning glints | Only ACTIVE model |
| Bamboo Steamer | Condensation and bamboo highlights | Only ACTIVE model |
| Fermentation Vat | Bubbles and surface rings | Stage 1, 2 and 3 each use a distinct palette |
| Salt Evaporation Pan | Brine ripples, concentrated brine shimmer, salt crystal glints | Stage 1, 2 and 3; empty stage remains static |
| Wild bee habitats | Pollen-colored motes inside the entrance | Only ACTIVITY=3 models for all four habitat species |

- Gradle task: `generateKitchenMotionV311`.
- Each atlas is 16×64 pixels: four 16×16 frames, two game ticks per frame.
- All nine atlases are packaged into the normal mod JAR; no runtime dependency is added.
- Existing block states continue to control which model is visible. The animation does not change processing time, recipe results, water consumption, salt output, or crop growth.
- The kitchen ACTIVE window remains server-authoritative; fermentation and salt pan progress remain stored in their existing block states.
- Review sheet: `build/reports/agriculture/agriculture-motion-v311-contact-sheet.png`.

These are animated texture surfaces, not bone-driven geometry. No Block Entity, renderer registration, server animation loop, entity scan, or chunk-loading behavior is added.

## 2. Client-side ecology cues

The existing client display-tick feedback remains state-gated:

| System | Gate | Cue |
|---|---|---|
| Pollination-eligible crops | Flowering ages 4–7 | Floral fleck / saved-pollinated marker |
| Wild bee habitats | Existing ACTIVITY 1–3 | Rare nectar fleck |
| Fermentation vat | Non-empty batch | Stage-dependent bubbles / completed marker |
| Marine crops | Waterlogged, age 2–3 | Rare underwater bubble |
| Fungal cultures | Age 2–3 | Occasional spore mote |

These visuals do not mutate gameplay state. Each display callback can emit at most one particle; inactive states return early.

## 3. Swaying Garden interoperability

Two block tags are included for optional client-side foliage animation:

- `#gugugaga:wind_swaying_plants`: 212 crop, wild-plant and marine-plant blocks.
- `#gugugaga:wind_swaying_fungi`: 6 fungal culture blocks.

The mod does not depend on Swaying Garden. See `docs/CLIENT_VISUAL_COMPATIBILITY_V311.md` for client configuration.

## 4. Validation and acceptance

- `qa/validate_visual_animation_v311.py`: validates all 40 unique animation frames, metadata, model bindings, exact JAR bytes, static idle variants, four bee-habitat bindings, and five ecology display hooks.
- `qa/validate_swaying_garden_compat_v311.py`: cross-checks both compatibility tags against registered block IDs.
- Existing Agriculture / Kitchen / Nutrition / Fusion regression checks and real dedicated-server boot remain in CI.
- A real client review is still required for perceived motion quality, shader compatibility and FPS. Headless CI cannot establish visual smoothness.
