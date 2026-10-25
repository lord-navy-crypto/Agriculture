# Gugu-Gaga Horror — Dependency Matrix (NeoForge 1.21.1, verified 2026-10-18)

Read FIRST: Gugu-Gaga v0.10's real entity models currently use **native Minecraft ModelPart / MobRenderer** animation. The five-act survival loop uses vanilla entity AI / audio and server-owned state. Therefore, there are **NO required third-party animation JARs in v0.10**. Do not claim GeckoLib is integrated; a future phase must port entities to GeoEntity / GeoModel / animation controllers and pass an actual build before adding a hard required mod dependency.

## Required NOW (v0.10)
- **Minecraft Java 1.21.1**
- **NeoForge 21.1.259**
- **Java 21**
- Our own `gugugaga-0.10.0-alpha.1.jar` once the CI artifact passes.
- None of GeckoLib, PlayerAnimator, Iris, CreativeCore or AmbientSounds is required for the mod to launch.

## Third-party animation APIs (correct purpose / exact MC & loader)

| Package | Official version | Use | Status |
|---|---|---|---|
| GeckoLib | **NeoForge 1.21.1 4.9.3**, `geckolib-neoforge-1.21.1-4.9.3.jar` | Best candidate for custom animated mob skeletons, smooth stalking, false-friendly Lapis shell, revealing Redactor, staged penguin corruption and Yellow's abnormal smile. | **Future development integration**, do not install as a dependency until we actually add Geo code and NeoForge mod metadata. |
| playerAnimator | `player-animation-lib-forge-2.0.4+1.21.1.jar`, marked **NeoForge** on CurseForge | Animate the **PLAYER** avatar in optional cinematics. | Not needed for our three mob skeletons. Avoid until there is a tested, opt-in player animation feature. |

Official download pages:
- GeckoLib (NeoForge 1.21.1 4.9.3): https://www.curseforge.com/minecraft/mc-mods/geckolib/files/8893490
- playerAnimator (Minecraft 1.21.1 NeoForge): https://www.curseforge.com/minecraft/mc-mods/playeranimator/files/all?page=1&version=1.21.1

**Critical**: GeckoLib's `geckolib-forge-` and `geckolib-fabric-` downloads are the WRONG loader, even though the Minecraft version matches. When eventually integrated as mandatory, include **on server AND client**. When the user has modpack duplicates, use one matching GeckoLib version, not two.

## Recommended OPTIONAL *client-side* ambience/testing addons (not gameplay dependencies)

1. **Sound Physics Remastered 1.21.1-1.4.10 (NeoForge)**: more believable in-game echo/occlusion, especially cave sound. Official files: https://www.curseforge.com/minecraft/mc-mods/sound-physics-remastered/files/all?page=1&pageSize=20&version=1.21.1. Consider first for sound mood; check whether our faint chimes remain audible after occlusion.
2. **AmbientSounds 6 v6.3.10 for MC1.21.1 NeoForge**: richer natural background ambience. Exact file `AmbientSounds_NEOFORGE_v6.3.10_mc1.21.1.jar`. This mod is **client side**, and the publisher says it **requires CreativeCore**. Download matching **CreativeCore_NEOFORGE_v2.13.51_mc1.21.1.jar** as well. Only for players who want the extra mix; it is about 81 MB, and overlapping atmosphere audio may obscure suspense cues.
   - https://www.curseforge.com/minecraft/mc-mods/ambientsounds/files/all?page=1&pageSize=20&version=1.21.1
   - https://www.curseforge.com/minecraft/mc-mods/creativecore/files/all
3. **Embeddium 1.0.15 for 1.21.1 NeoForge**: optimization, useful for evaluating mod FPS/frametimes.
   - https://www.curseforge.com/minecraft/mc-mods/embeddium/files/all
4. **Iris Shaders 1.8.12 for NeoForge 1.21.1**: optional shader rendering. Install only after testing with no shader; shader pack choice impacts performance/visibility, and some render/sodium-class combinations can conflict. Avoid the beta until intentionally tested.
   - https://www.curseforge.com/minecraft/mc-mods/irisshaders/files/all?gameVersionTypeId=6&page=1&version=1.21.1

## Suggested installation tiers for a normal user

- **Tier A — Minimum & debugging**: NeoForge, Java21, our Gugu-Gaga JAR. **Zero extra dependencies.**
- **Tier B — Atmospheric horror**: Tier A + Sound Physics Remastered. Optionally add AmbientSounds6 **and** CreativeCore, reduce AmbientSounds global volume if narrative cues are drowned out.
- **Tier C — Visual lab**: Tier B + Embeddium and (only after separate checks) Iris plus compatible shader pack. No resource packs or third-party horror character art copied.
- **Tier D — Animator development**: stay on Tier A until official GeckoLib migration gets a passing CI with exact 1.21.1 NeoForge version and required mod metadata. Then distribute one tested new package with matching GeckoLib. `PlayerAnimator` only if the game deliberately animates players in a future optional cutscene.

## Why this order?
- Runtime dependency explosion is a common cause of Minecraft startup failures.
- All three creatures currently have functional custom Java head/wing/pose animations WITHOUT GeckoLib. Complex timeline/keyframe animation deserves a deliberate **separate PR**, not a pretend soft dependency.
- NeoForge 1.21.1 mods are not interchangeable with Forge 1.20.1 or Fabric 1.21.1.
- Sound/ambience/performance mods should not be hard-required or shipped by us without permission.
- All additional downloaded JARs come from official owner pages. Check filename, game version, mod loader, Java compatibility and any listed required dependencies.

## v0.10 survival horror gameplay (implemented on branch; client QA pending)
- Server owner opts in: `/gugugaga play server on`.
- Each player explicitly enters with `/gugugaga play join`; quit with `/gugugaga play leave`.
- Night only, Overworld and Survival only; `/gugugaga quiet on` still works.
- Five repeating game acts: Stillness -> Whispers -> Witness -> False Safety -> Respite. A voluntary `/gugugaga investigate` near visible, actual creatures advances important transitions. Intro ambience takes time; spawning the first observer follows existing rare eight-minute minimum.
- The second and third character can appear as **bounded six-minute scripted visitors** only after progressing in-game investigations; these guests have no forced chunks or block edits.
- No five ending scripts, portals, fake admin permissions, camera hijacking, world data corruption or high-volume forced sounds.
- On multiplayer, the opt-in player might still hear other nearby players' vanilla-positioned sounds; full per-client audio routing is a future acceptance item.
