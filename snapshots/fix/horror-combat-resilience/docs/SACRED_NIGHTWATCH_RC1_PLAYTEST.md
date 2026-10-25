# Gugu-Gaga Sacred Nightwatch — RC1 integration & user acceptance

**Version:** 0.19.0-rc.1 · Minecraft 1.21.1 · NeoForge 21.1.259 · Java 21 · GeckoLib 4.9.3 (**separate mandatory dependency for BOTH client and dedicated server**).

**Status:** FIRST PLAYTEST CANDIDATE. The CI must succeed before distributing this candidate. CI success does not prove the Minecraft client actually renders animations, that all scenes feel scary, or that LAN/dedicated multiplayer is stable. Preserve previous main branch and all stacked draft PRs until playtests.

## Single source of truth

Use the Actions run of the RC1 branch and download **gugugaga-sacred-nightwatch-rc1**. This zip contains:
- exactly **gugugaga-0.19.0-rc.1.jar**
- **SHA256SUMS.txt**: confirms the compiled file was not changed
- **RC1_MANIFEST.json**: lists actual class/assets checked inside JAR
- **PLAYTEST_INSTRUCTIONS.txt**

IMPORTANT: Unzip the GitHub artifact, copy **only** the JAR into the NeoForge instance's `mods` directory. Do not copy the zip itself into mods. **Remove any older Gugu-Gaga JAR** from that directory. Do not confuse this branch with main or v0.18's historical test artifact. Add the matching GeckoLib 4.9.3 JAR to the same mods folder. If testing on a remote dedicated server, install matching mod and GeckoLib on both server and connecting client.

The separate `gugugaga-v017-visual-review` artifact contains a 2D texture atlas ONLY, not an installable mod or actual in-game screenshot.

## Setup — singleplayer, backup test world

1. Create or copy a throwaway Minecraft 1.21.1 world and a separate NeoForge **21.1.259** profile with Java 21.
2. Place RC1 and GeckoLib in `mods`. Remove all older Gugu-Gaga versions. Start Minecraft. If launch fails, keep `logs/latest.log`.
3. Enter test world with cheats/operators enabled. Enter **`/time set night`** and **`/gamemode survival`**. Select an outdoor zone with 25+ blocks of walkable loaded terrain, and a cave/building nearby. Real safe collision/loaded chunk checks can legitimately reject spawning on cliffs or in very narrow tunnels.
4. Type `/gugugaga play server on`, then `/gugugaga play join`.
5. **Fast-track scene:** `/gugugaga play test triad` (operator only, needs above survival/night/opt-in). Read its real actor success flags. If any fail, move to flatter open terrain and repeat; the normal act retries missing actors twice. Inspect real mob models and their interactions.
6. **Test actual progression:** `/gugugaga play test witness`. Approach the penguin and **look directly at it within 12 blocks**, then `/gugugaga investigate`. Stage should become **FALSE_SAFETY** and Lapis/Yellow appear or retry shortly. Look directly at Lapis or Yellow within 12 blocks and `/gugugaga investigate` again. It should enter **RESPITE**, then return to STILLNESS.
7. At WITNESS or FALSE_SAFETY, try `/gugugaga play listen` while holding the sneak key; it should return a coarse hint at most every 12 seconds when a real entity is in 20 blocks. Try listening with no entities nearby (silence). Try `/gugugaga play help` when a visible Yellow and a Gugu are nearby.
8. Let the bird actually start a chase. Break sight behind cover: **sprint** to expose the last audible position, then compare with **crouching** or stopping. The bird must eventually give up; it cannot follow a hidden silently walking player. For no pursuit, verify `/gugugaga play status` and the config `horror_chases`.
9. Repeat in a cave: only a real nearby penguin or Redactor produces the v0.18 low-volume proximity pulse, gated by `horror_cave_threat_audio` and `haunting_audio`. Outdoor or empty caves should have no **real-monster pulse**, although unrelated atmospheric cues may still occur.
10. Try `/gugugaga play aid off` / `aid on` and `/gugugaga play scares off`. Try `/gugugaga play leave`; the old observer ambient option may be independently enabled but Nightwatch play-mode should no longer target a nonparticipant. Operator global stop: `/gugugaga play server off`.

### Independent direct spawn visual test

Operators can create **persistent manual** test entities with vanilla commands:
```mcfunction
/summon gugugaga:observer ~ ~ ~
/summon gugugaga:redactor ~ ~ ~
/summon gugugaga:yellow_smiler ~ ~ ~
```
Manually summoned mobs are not subject to the 90s scripted penguin lifetime; remove test copies before real survival balancing. Separate off-by-default hostility toggles are for development only.

### Timing and limitations

- Ordinary ambience can be delayed by an **8-minute minimum** director cooldown. A **WITNESS staged sighting** is attempted at act entry, independently of that director cooldown.
- Five act order: STILLNESS (35 s) → WHISPERS (90 s) → WITNESS (requires actual penguin evidence) → FALSE_SAFETY (requires actual Lapis/Yellow evidence) → RESPITE (70 s) → STILLNESS. These are active-night seconds; daylight suspends progress.
- If stage spawning fails due to blocked terrain or no loaded site, RC1 tries bounded attempts and reports the results in the test scene; no chunk forcing or world alteration.
- Chat-command interactions remain a functional alpha UX, not a finished journal/device HUD. The code deliberately has no ending or final boss yet.

## Acceptance checklist (keep observations separate from assumptions)

| Area | Real manual acceptance target |
|---|---|
| Boot | Correct mod/version and GeckoLib; no missing class/model warnings |
| Appearance | 3 actors render, no missing textures, eye/beak bone aligned |
| Scripted stage | Test triad places real actors; investigate transitions twice |
| Rivalry | Redactor reacts to Smiler, Yellow can ward off Gugu |
| Stealth | Sprint behind wall vs quiet crouch differs; chase stops |
| Listening | Real coarse direction and cooldown; none when no actors |
| Cave | Threat-dependent quiet pulse; no globally loud noise |
| Multiplayer | Different players join/leave/aid settings independent |
| Save | Reload clears chase memory and transient session test states |
| Performance | No unintended chunk load, stable FPS and no growth in entities |
| Enjoyment | Is first sighting tense? After 20–30 min is there variation? |

Record PASS / FAIL / NOT TESTED against each category, real screenshots, whether shaders were enabled, and `logs/latest.log` on failure. **Don't label stable or 'really scary' until players have actually tried it.**

## What automated CI certifies

All inherited v0.1–v0.18 policy regressions, multi-actor source integration checks, Gradle NeoForge/GeckoLib jar compilation, exact version marker, six required PNG skins, 4 GeckoLib geometry files, 3 animation sets, required entity classes, generated SHA256 checksum and readable user instructions. It does not run a real graphics client.
