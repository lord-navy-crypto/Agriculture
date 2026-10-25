# Gugu-Gaga Horror v0.12 — playable chases and triad

This Draft is based on the GeckoLib v0.11 animation branch. The game remains a five-act replayable encounter loop. No endings, final boss, or fourth-wall access.

## Character AI priorities
- Gugu-Gaga: nearby opted-in PLAYER first, Redactor second. A missing player lets the old observer notice the Redactor.
- Redactor (Lapis disguise): player and Gugu have EQUAL interest within comparable 22-block search radii, with nearest target chosen; either can reveal the true form. Redactor runs from REVEALED Yellow Smiler, not the disguised Yellow.
- Yellow Smiler: HELPS player, but Gugu is its primary adversary, and it does not fear the Redactor. Near an opted-in player during an active chase act, it approaches and drives Gugu into a short visible retreat, with one small in-world monster-to-monster hit on cooldown. Its fictional dialogue encourages the player to drive the penguin and Redactor away.

## Gameplay changes
- Penguin pursuit is line-of-sight initiated within 19 blocks, max 14 seconds, 42-second cooldown. Running 27 blocks away or breaking sight for 4 seconds ends it. Standard Minecraft navigation, no teleports/chunk forcing.
- Revealed Redactor can follow a visible close player using local navigation.
- Bounded surprise: existing GeckoLib animated penguin lunges once, subtle actionbar notice and **targeted** vanilla sound. Actual visible monster within 5 blocks, at least 55 seconds between cues per player.
- Player can toggle surprise cues with /gugugaga play scares off and /gugugaga play scares on. Leaving the session immediately stops this new targeting. Global settings survival_gameplay.horror_chases and survival_gameplay.horror_jumpscares default true **only in the already opt-in game mode**.
- This branch does not enable extra player damage; existing host-only hostility options remain independent. No screen flash, blocking camera, forced player commands or copied original audio.

## Research
- Alien: Isolation systemic sensory AI and threat pacing: https://www.gamedeveloper.com/design/the-perfect-organism-the-ai-of-alien-isolation
- Amnesia: The Bunker player choice under pressure: https://frictionalgames.com/2022-12-announcing-amnesia-the-bunker/
Mechanics are original implementations, not adaptations of protected assets/code.

## Before merging
- CI Python + Java 21 policies and full NeoForge Gradle build must pass.
- Test real Minecraft 1.21.1, NeoForge 21.1.259, Java21, GeckoLib 4.9.3 with single-player and dedicated multiplayer.
- In survival at night, OP /gugugaga play server on then player /gugugaga play join. Use /gugugaga investigate near a visible entity. Verify Witness and False Safety work.
- Verify line-of-sight start, escape by hiding, pursuit cooldown, Yellow driving off Gugu, Redactor nearer-target behavior and Yellow not harming players.
- Verify sound and lunge animation with two participants, opt-out controls, no unwanted sound to non-participants, session leave, server restart and save/reload.
- Run with no shaders or ambiance extras before testing optional sound mods.
