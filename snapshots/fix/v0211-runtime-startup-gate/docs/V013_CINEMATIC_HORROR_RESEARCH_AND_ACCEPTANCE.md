# v0.13 — Cinematic Horror Without Scripted Endings

**Scope:** Minecraft 1.21.1 / NeoForge 21.1.259 / Java21 / GeckoLib 4.9.3. This PR inherits five-act v0.10, GeckoLib v0.11, and triad chase v0.12. It deliberately excludes endings A–E, final Boss, cross-game lore, world manipulation, real-world popups, forced perspective, and third-party movie audio/art.

## Research translated into mechanics

1. Alfred Hitchcock's 1972 AFI seminar: an anticipated threat sustains suspense longer than an unprepared surprise. Source: https://www.hitchcockwiki.com/wiki/Interview%3A_Alfred_Hitchcock_at_the_AFI_Seminar_roundtable_%2818/Aug/1972%29 . **Our application:** the player hears limited hints before meaningful danger, not a flash on each timer.
2. John Carpenter's own Halloween (1978) soundtrack notes: audio radically alters the feel of otherwise unscary images. Source: https://theofficialjohncarpenter.com/halloween-soundtrack/ . **Our application:** low-volume recurring motifs and differing pitch; no music or copyrighted soundtrack reproduced.
3. Motion Picture Association interview of A Quiet Place's sound designers: removing sonic layers creates powerful negative space. Source: https://www.motionpictures.org/2019/02/oscar-watch-a-quiet-places-sound-designers-on-2/ . **Our application:** deliberate silence, false steps, and quiet clues instead of blanket loud ambience.
4. AI of Alien: Isolation developer analysis (Game Developer) and peer-reviewed-style game studies analysis: separate director pacing from monster perception, player must be able to escape. Sources: https://www.gamedeveloper.com/design/the-perfect-organism-the-ai-of-alien-isolation and https://gamestudies.org/2002/articles/jaroslav_svelch . **Our application:** cinematic pacing policy only schedules low-volume cues; actual monster never gets new wall-penetrating target updates.

These are design inspirations, not copies of dialogue, effects, copyrighted models or camera techniques.

## Implemented
- Original side-effect-free `CinematicSuspensePolicy` creates pseudo-random, **deterministic** ambient cue mixes per player, act and repeat count at no more than one roll every 28 in-game seconds. Whispers uses misleading footsteps, soft taps and silence. Witness uses distant tones and pauses. False Safety adds periodic misleading noises. Stillness and Respite are always silent under this NEW director.
- Cinematic cues use `ServerPlayer.playNotifySound`, not nearby `ServerLevel.playSound` broadcasting to unjoined players. The existing legacy observer cues are separate. New cues honor the audio config and cap at 0.18 volume.
- `LastSeenSearchPolicy`: a pursuing observer updates target coordinates **only while visible**, then navigates to the remembered coordinate for at most 4 seconds unseen, subsequently checks that area for at most 3 more seconds. Pursuit is never transferred mid-chase to a newly closer user. Memory is cleared when search ends and on save/reload.
- A successful scare remains line-of-sight + radius + cooldown gated, with a contextual Redactor phrase and optional audio. No flashing, seizure-prone overlays, forced commands, jumpscare fullscreen or real-life references.
- Original three-entity priority and opt-in multiplayer eligibility are preserved.

## Playtest required before main merge
- A) GitHub Actions Python regression + 2 new pure Java smoke tests + NeoForge build.
- B) Real 1.21.1 client with GeckoLib 4.9.3: view all three geo models, textures, new one-shot lunge and walk/reveal poses. Visually confirm no clipping.
- C) In Witness/False Safety, get spotted by Gugu, break line of sight behind blocks, move to a different hiding location: Gugu must navigate to **last seen** place rather than the hidden player's current location. Test daylight, leaving, creative, and pause transitions.
- D) Two clients close together, only one joined: scripted cinematic sound must go to participating player, not unjoined bystander. Verify sound OFF config. Test per-player scare OFF and session leave.
- E) Run sessions twice to check cadence/variety and that respite isn't interrupted by v0.13 cinematic cues. Existing older ambient Nightwatch system may still create its own positional sound in opted-in encounters; audit that separately.
- F) Check dedicated server logs and save/reload. There must be no client-only code on server, forced chunk loads or player inventory/world edits.

Never claim completion of real client or multiplayer acceptance from Gradle CI alone.
