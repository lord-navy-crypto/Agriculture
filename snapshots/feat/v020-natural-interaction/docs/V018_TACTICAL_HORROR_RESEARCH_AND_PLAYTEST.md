# v0.18: Gugu-Gaga turns from a haunting demo into tactical horror

Branch targets prior v0.17 draft PR #19, NOT main. Actual work implemented below; client and multiplayer playtests remain required before release. Still no endings, final boss or story spoilers.

## Verified competitive mechanics (official author/project sources)

1. [The Man From The Fog — CurseForge](https://www.curseforge.com/minecraft/mc-mods/the-man-from-the-fog): appears every night or two, initially watches, advances when overlooked, may vanish or turn aggressive when looked at. The [v1.3 notes](https://www.curseforge.com/minecraft/mc-mods/the-man-from-the-fog/files/5361339) explicitly describe search on lost sight, fog and chasing/pathing; earlier builds describe improving stalking, swimming and doors. We do **not** copy their code, textures, creature or destructive player-home mechanics.
2. [The Knocker — CurseForge](https://www.curseforge.com/minecraft/mc-mods/the-knocker): same creators, many stalking events including appearance while player sleeps. [Version 1.5.1 notes](https://www.curseforge.com/minecraft/mc-mods/the-knocker/files/8221815) specifically add chance of disguising as a player; other updates add react-to-being-watched and swimming. We retain our *original* Redactor/Lapis character and do not impersonate real players.
3. [Cave Dweller — Gargin](https://www.curseforge.com/minecraft/mc-mods/cave-dweller): cave ambient sounds intensify when real monster approaches, and nightmare variant increases spawn frequency for *testing*. Previous v0.14 only changed ambience because roof was present. v0.18 connects restrained additional cave pulse to actual nearest Gugu/Redactor.
4. [From The Fog — CurseForge original FAQ](https://blog.curseforge.com/from-the-fog-frequently-asked-questions/): long-distance sightings, tree/window watching, changing environmental cues and configurable pacing; no guaranteed chase in every encounter. v0.18 continues intentional long quiet periods and no block destruction.
5. [Amnesia: The Bunker — developer listing](https://store.steampowered.com/app/1944430/Amnesia_The_Bunker/): adaptive monster hears player actions and noises, light/tools determine options. We focus on real tactical noisy running versus silent crouch.
6. [Phasmophobia — studio listing](https://store.steampowered.com/app/739630/Phasmophobia/): ghost-specific evidence and distinct gear, investigation under threat, player teamwork. We add a player-requested, short-range, coarse sound clue rather than a free all-seeing creature compass.
7. [Lethal Company — store listing](https://store.steampowered.com/app/1966720/Lethal_Company/): crew navigation and cooperation, radar guidance and communication. Co-op investigation and playtesting are the next higher-priority improvements after validating current single-player feel.

## Actually coded in v0.18

**Gugu hearing**: During a REAL active chase, its actual target can only be found through a wall when moving fast enough AND sprinting, within 18 blocks horizontal/3D squared, 7-block height difference, with valid opted-in player context and noise enabled. Sneaking or holding still prevents fresh noise tracking. Gugu navigates to that last ACTUALLY HEARD position; after the sprint ceases it follows finite remembered evidence, then gives up. Existing 14-second max chase, 4-second lost-sight rule, 3-second search, 42-second chase cooldown all remain. No target teleport, hidden-path spying, game map edits or new permanent player-position persistence. Server-only transient coordinates remain private.

**Crouched listen**: An opted-in survival player crouches and enters `/gugugaga play listen`. A 12s per-session cooldown applies even on empty findings. Actual *loaded* penguin, Redactor or Yellow within a 20-block radius yields one nearest coarse directional whisper (ahead, behind, left or right; near or distant). Each entity's sound has a distinct audible metaphor without saying its name. No omniscient clue when there's no real mob or player not eligible. This is an optional player-requested command, not an intrusive HUD or speech listener.

**Cave threat sounds**: In covered cave/interior during WITNESS/FALSE_SAFETY, only when a real Gugu or Redactor is within 18 blocks, at a maximum cadence of every 12 seconds, a per-player extremely quiet low tone varies with threat range (5, 10, 18 blocks). No fake nearby threat audio. Master sound config OFF mutes these cues; independent server flags `horror_noise_hunt` and `horror_cave_threat_audio` default TRUE *inside the already OFF-by-default playable loop*. No new dependencies or borrowed samples.

**Long-session fix**: Increase per-player act tick counter saturation to near `Integer.MAX_VALUE` rather than freezing at 24,000 (an exact cue multiple could otherwise lead to repeat cue spam once per second).

## Verdict, acceptance and tests

**Technical functionality**: build jar, Python integration checks, Java21 policy executable smoke, NeoForge Gradle build, artifact upload are necessary but not sufficient.

**Release checklist**: real Minecraft 1.21.1 NeoForge+GeckoLib clients, two-person dedicated server and 30+ minutes solo for pacing.
- Test 14-second chase in open sky and cave, sprint behind wall vs shift crouch and stationary after LOS; path must go only to visible/heard position, not follow hidden walking.
- Show no response to spectator/creative/quiet/opt-out player; toggle independent hearing config; run save/reload with no target memory retained.
- Shift and `/gugugaga play listen` while entity is 5/15/21 blocks away, separated by solid wall, and during cooldown; verify direction and species-distinct sounds only with actual mobs and per-player results.
- Listen while none are nearby should correctly report silence and consume 12s cooldown.
- Cave and roofed indoor vs open terrain: 12-second proximity cues only if genuine actor within range; confirm low volume ceiling at 0.16, no extra fake threats.
- Shader/GeckoLib 3D animation/texture/FPS checks; simultaneous multiple participants who opt in/out independently.
- If content remains repetitive after 20–30 minutes, do **not** label it a finished horror game. Add procedural encounter variation, player-relevant tools and more meaningful multiplayer before release.

**Known limits**: the remote MacBook Air game client was offline at development time. CI cannot measure fear, player enjoyment, 3D audio position, subjective pacing or actual FPS. Current listening interaction is chat-command based, not a finished in-world handheld device. This is an **alpha vertical slice** until evaluated by human playtesters.
