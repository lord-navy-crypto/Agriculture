# Gugu Gaga Agriculture — Visual & Animation Refinement v3.11, second pass

**Scope:** Minecraft 1.21.1 / NeoForge 21.1.259 / Java 21.
**Version:** 3.11.0-alpha.2. Built on alpha.1's Fusion botanical detail and short workstation particles.

## 1. Real animated workstation surfaces

Three already-registered workstations now carry **four real PNG animation frames** in their ACTIVE block models:

| Existing station | Animated part | 4-frame motion | Additional FX |
|---|---|---|---|
| Dough Bench | Top work surface | Moving flour / rolling ridge | Client-only flour puffs |
| Skillet Station | Cooking surface | Alternating browning/sizzle glints | Smoke and occasional flame |
| Bamboo Steamer | Lid top | Shifting weave highlights and condensation | Gentle steam |

- Textures are generated deterministically by the Gradle task generateKitchenMotionV311.
- Each source atlas is 16×64 pixels (four 16×16 frames), with companion PNG metadata declaring a two-tick frame duration.
- Only the existing _active models refer to these animations. Idle models remain static; the existing 10-game-tick ACTIVE lifetime remains server-authoritative.
- The work surfaces animate **by pixels**, not a physically moving mesh or GeckoLib skeleton.
- No block registry ID, collision shape, inventory storage, recipe, or server-side animation ticker has changed.
- Preview: build/reports/agriculture/kitchen-motion-v311-contact-sheet.png.

## 2. Ecology display-tick feedback

These only affect **client-side ambient display-tick visuals**. They do not update crop states, simulate disease, represent food safety, or run client-side AI.

| Existing block | State gate | Visual cue |
|---|---|---|
| Pollination-eligible crops | Flowering ages 4–7 | Sparse floral fleck, or distinct saved-pollinated marker |
| Wild bee habitats | Existing ACTIVITY 1–3 | Rare nectar fleck, proportional to gauge; squash bee morning window respected |
| Fermentation vat | Non-empty batch | Sparse working-stage bubble; distinct completed-batch mote |
| Marine crops | Waterlogged and age 2–3 | Rare underwater bubble |
| Fungal culture | Age 2–3 | Very occasional spore mote |

Particles are sampled probabilistically by vanilla's random display callback. No block entities, background scans, or chunk-loading calls are added. Per callback, at most one particle may be emitted, with early returns for inactive blocks.

Farm crop wind **does not** yet have bone deformation or geometry sway; do not characterize these effects as such.

## 3. Validation

- Run Gradle build with Java 21.
- Run the dedicated v3.11 QA against the built mod JAR: qa/validate_visual_animation_v311.py.
- Check all 12 animation frames, metadata, model bindings, packaged texture bytes, review-sheet dimensions, and five stage-bound ecology hooks.
- Full Agriculture / Kitchen / Nutrition / Fusion regression checks and real dedicated server boot remain in CI.
- Fusion is still optional; the standard JAR works without it.

## 4. In-client review

Place all three stations and operate them with their existing utensils. Check idle frames are static, work surfaces animate during the short ACTIVE window, and particle cues stop afterwards. Inspect a flower-stage pollination crop, a bee habitat with activity, in-progress fermentation vat, mature fungus, and waterlogged sea crops.

Finally inspect a dense farm at normal settings and with particles set to Minimal; compare FPS and particle clutter. Headless tests do **not** prove rendered animation quality; review in a real client.
