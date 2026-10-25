# v0.17 — Eye rig, visible last-sighting search, offline visual proof

**Stack:** Minecraft 1.21.1, NeoForge 21.1.259, Java 21, GeckoLib 4.9.3 on client/server. This PR is stacked on draft v0.16 and includes v0.10–v0.16. No extra third-party dependency or borrowed art.

## Game code changes

1. The penguin's original 6-cube head had two eye cubes and a beak cube molded into one head bone. Without moving those cubes or changing their UVs, we split them into 3 new GeckoLib bones: left_eye, right_eye and beak, each with parent=head. The head and feather details retain old animation bindings.
2. The idle and watch clips give the eyes minute synchronized movements. Gaze position offsets are bounded to 0.25 model units, with no full-screen/camera effect.
3. A **3-second looping search pose** makes the eyes, head and wings scan when the player breaks line of sight. GugugagaEntity syncs **only** one Boolean SEARCHING via standard NeoForge tracked data; it neither syncs, saves nor broadcasts the hidden player's coordinates. Existing chase/search time limits and per-player opt-in are unchanged. Searching is cleared after expiration, opt-out, Smiler intervention or reload.
4. No added particles, shaders, world edits or global server-player scans.

## Reproducible offline visual proof

The new qa/render_visual_review.py script uses standard Python 3 only. It decodes the **five generated 64×64 RGB PNGs actually bundled by Gradle**, checks CRC, and creates a 1412×390 nearest-neighbor 2D material contact sheet. A JSON manifest records per-texture SHA-256. The sheet clearly says **OFFLINE UV ATLAS — NOT GAME SCREENSHOT**; it is not a 3D render and cannot establish actual client appearance.

The new GitHub Actions artifact gugugaga-v017-visual-review contains:
- gugugaga-v017-material-review.png
- gugugaga-v017-material-manifest.json

The unchanged vanilla-style penguin observer.png is a pre-existing resource, not one of these five generated variants; this particular sheet therefore reviews five of the six actor skins.

## Verification gates

- CI: hierarchy of eye parent bones, animation keys, tracked searching boolean, absence of persisted hidden-player locations, repeatable PNG generation, contact-sheet SHA-256 and actual dimensions.
- Full NeoForge Gradle build, all prior Python/Java checks, upload JAR and material contact sheet.
- **Hands-on pending:** launch 1.21.1 client with GeckoLib, inspect nested pivots, eye alignment, actual search animation from different angles, 3D clipping, sounds, modded shaders, 2-player networking, FPS and ambience. A texture sheet is NOT a Minecraft screenshot.
- The MacBook Air remote agent was offline at development time; physical GUI checks cannot be claimed.

## Real-game playtest procedure

Duplicate a test instance and world, put v0.17 JAR and GeckoLib 4.9.3 in the correct client and server mods folders. Operator activates /gugugaga play server on; each player opts into /gugugaga play join at night in survival. Watch neutral penguin eye tracking. Get seen during Witness or False Safety at less than 19 blocks, move behind solid cover, and observe its three-second search animation as it goes to the actual last-sighted location. It must not track where you moved behind the wall. Then try /gugugaga play leave, creative mode, daylight, save and reload. Check that no search or private location persists. Inspect eye and beak UVs under daylight, caves and optional shaders.
