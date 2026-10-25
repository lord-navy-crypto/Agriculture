# v0.14 – Horror game/mod/film/fiction research translated into original gameplay

**Scope:** Minecraft 1.21.1 + NeoForge 21.1.259 + Java 21 + GeckoLib 4.9.3. The v0.10 five-act cycle, v0.11 animation, v0.12 rivalries and v0.13 stealth/suspense remain. Narrative endings and boss content are deferred.

## Verified creative references (concepts, not assets)
- From The Fog (CurseForge): distant Herobrine sightings, occasional environment cues, config. https://www.curseforge.com/minecraft/mc-mods/from-the-fog
- The Man From The Fog (CurseForge): watched-versus-unwatched stalking; the creature may disappear or escalate when the player looks at it. https://www.curseforge.com/minecraft/mc-mods/the-man-from-the-fog
- Cave Dweller by Gargin (CurseForge): cave ambience intensifies with proximity, multi-mode animations. https://www.curseforge.com/minecraft/mc-mods/cave-dweller
- Phasmophobia (Kinetic Games via Steam): active equipment/evidence identification; ghost behavior varies by type. https://store.steampowered.com/app/739630/Phasmophobia/
- Lethal Company (Zeekerss via Steam): cooperative scavenging, unknown threat and communication. https://store.steampowered.com/app/1966720/Lethal_Company/
- Amnesia: The Bunker (Frictional Games): adaptive stalking and limited light as pressure. https://frictionalgames.com/2022-12-announcing-amnesia-the-bunker/
- The Thing (1982), director interview: uncertainty about imitation and identity. https://www.theguardian.com/culture/2024/mar/25/john-carpenter-kurt-russell-the-thing-horror-classic
- House of Leaves (publisher): physically impossible haunted architecture and unreliable exploration. https://www.penguinrandomhouse.com/books/36526/house-of-leaves-by-mark-z-danielewski/readers-guide
- The Haunting of Hill House (publisher catalog): setting contributes to haunting. https://global.penguinrandomhouse.com/announcements/theres-a-book-for-that-haunted-houses/

## Implemented as playable v0.14 code
1. New `HorrorEvidencePolicy` supplies distinct real-observation clues when a participant uses the existing `/gugugaga investigate` **while seeing an actual nearby loaded entity**. Penguin observing vs pursuing, Redactor still disguised vs revealed, Yellow smiling vs exposed. The masked Redactor is **not automatically spoiled**.
2. The old five-act evidence condition still determines progression. The nearest visible entity, not a non-visible or unloaded entity, provides that evidence. Each player gets at most one extra flavor report every 3 seconds, gated by the optional `survival_gameplay.horror_evidence_clues` setting.
3. New `DepthSuspensePolicy`: if the player is under cover in a cave/interior, remixes the previously scheduled v0.13 cinematic cue: distant chimes can become hollow taps and some footsteps give way to silence. It NEVER creates additional cue events, triggers mobs, alters chunks, or exceeds the 0.18 loudness ceiling.
4. Does not install other creators' mod files or copy art, text, music, audio samples, unique models or code.

## Deferred ideas, not claimed shipped
- Natural-world silhouettes behind far-away trees or windows with player-defined frequency.
- Stronger cave cave-noise gradients linked to **real loaded monsters**, not just roof cover.
- More distinct Redactor hiding/imitating behaviors and Smiler's misleading hints.
- Two-player evidence sharing with opt-in chat and no persistent tracking.
- Game-space illusions resembling impossible corridors: visual-only, no block rewriting.

## Actual game acceptance
1. CI Java 21 v0.14 policy smoke, existing regressions and Gradle package must pass.
2. With GeckoLib 4.9.3 installed on client/server, verify five-act progress, exact entity proximity, line of sight and disguised/revealed evidence messages.
3. Run /gugugaga play server on, /gugugaga play join; survival at night, use /gugugaga investigate near the 3 entities. Evidence clue shouldn't claim the player sees hidden entity.
4. In cave vs open sky, compare same 28-second v0.13 cue intervals; no extra events, no distortion or global sound blast. Set horror_evidence_clues=false and confirm old stage progression continues without descriptive clue.
5. Two-player tests: only voluntarily joined players receive clue messages; verify save/reload, physics/shader compatibility, no forced chunk loads.
6. Keep as Draft PR and do not merge until visual, audio, gameplay and dedicated server acceptance pass.
