# v0.16 — Original model and cinema-quality *restraint* pass

**Mode:** opt-in Nightwatch, 5 repeatable acts, 3 independent actors; no final ending, boss or rewritten story. Target: Minecraft 1.21.1, NeoForge 21.1.259, Java 21, GeckoLib 4.9.3 on client/server.

## Shipped files

- **Four pre-existing GeckoLib geometry files** augmented with details parented to the existing animated bones: feather tips/tuft and chest seam on Gugu; crystal facets on masked Lapis; cracked outer trim and sleeve projections on revealed Redactor; brow, collar, sleeve cuffs and boot details on Yellow. Geometry is original and stays 64x64 texture UV safe.
- **Five procedurally generated original 64x64 sprite variants** gain richer material language without a shader dependency: beveled blue mineral, uneven dark archival mask, softly shaded friendly-yellow mascot, asymmetrically weathered revealed mascot, and cold rim-lit corrupted penguin. Original neutral penguin PNG remains unchanged to preserve player recognition.
- **Three GeckoLib animation JSON files** gain secondary body movement at controlled speed, keeping the existing eight observer clips, three Redactor clips and four Smiler clips. No camera shakes, flashes, extreme rotation, external art or sound.
- **Three action-linked VFX** (actual Lapis tell, Smiler ward, and penguin retreat) play only with real line-of-sight and `encounters.haunting_particles=true`, with 2–4 vanilla particles each action. The old creature logic and stage state remain authoritative; cosmetic effects cannot spawn creatures or edit the world.

## Notes: intentional compatibility
- No new external dependencies, shaders, APIs, models loaded by reflection or worldgen features.
- Texture generation uses **only Python 3 standard library**, generated into Gradle's build folder. Nothing from online mods is bundled.
- Transparent/animated material rendering is not forced, preserving modest GPUs and third-party shaders.
- Texture detail is authored against current blocky UV space, *not* official imported skins. We have not run eyes-on GUI acceptance yet.

## Verification
1. Python QA: geometry bones, finite box sizes, 64x64 UV bounds, animation timeline frames, procedural PNG dimensions and size.
2. Original regression, v0.16 Java particle-budget smoke, full Gradle build and JAR upload.
3. Actual 1.21.1 client tests: inspect seams UV wrapping, feather wing intersections, Lapis plaque/front readability, Redactor true mesh shape, Yellow brows/cuffs.
4. Verify animation body offsets and new `mask_tell` / `ward` one-shots with GeckoLib in varied light conditions, caves and rain.
5. Dedicated two-client server: audio+particle ON/OFF, small action VFX, low FPS effect, no involuntary encounter on opt-out.
6. Keep Draft until hands-on testing; never claim actual FPS improvements without benchmarks.
