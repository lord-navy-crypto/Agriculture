# Gugu-Gaga Horror v0.15 — deception and earnable trust

**Gameplay feature**, not a plot, final battle, betrayal ending or boss. Inherits the previous five acts and prior side-effect-free policy/asset tests. Java 21 / NeoForge 21.1.259 / Minecraft 1.21.1 / GeckoLib 4.9.3 (client + server). Draft PR only pending real gameplay acceptance.

## Disguised Redactor / Lapis: fair tell and stalking
- In a real WITNESS/FALSE_SAFETY act with visible player and when masked, Lapis is curious about the player as much as about Gugu.
- If watched directly by a participating player at up to 15 blocks, the disguised mob stops, looks at the player and occasionally plays an original **0.9-second uneven twitch** (`animation.redactor.mask_tell`), a low-volume signal and local actionbar clue. 42-second minimum tell cooldown.
- When ignored and 8–20 blocks away, Lapis can move toward the player with normal Minecraft pathfinding no faster than a new request every 40 ticks. Never teleports or digs; staring prevents this creeping.
- Creeping and player-interest reveal need actual line of sight; no seeing hidden players through blocks. At closer distances the pre-existing reveal and optional hostile behavior can occur.
- New clue is not guaranteed every time and does not forcibly break the disguise or reveal the name of the secret creature.

## Yellow Smiler: persuasion through concrete action
- In WITNESS or FALSE_SAFETY, a participant can explicitly `/gugugaga play help` if an existing Smiler within 12 blocks is visible and an existing penguin is within 22 blocks. A 15-second per-player call cooldown prevents spam; each Smiler still must find a penguin inside its own locally loaded 22-block radius. It travels toward the penguin using navigation and may ward it off at very close range, with sight.
- Original skeleton adds new non-looping `animation.smiler.ward`; the gesture accompanies an actual intervention. Yellow does not attack the player and is not afraid of Redactor.
- Gift and genuine repel are voluntary-context "favors" increasing a **0–5 fictional per-session Smiler influence meter**, at most once every 65 seconds. It changes a short line of persuasive dialogue and the status readout; it NEVER makes the player attack other mobs or changes player controls, NPC allegiances, quests, real-world behavior, or final outcomes.
- `/gugugaga play aid off` disables that participant's unsolicited Smiler assistance, persuasion and help calls. `aid on` restores it. `play leave` clears the in-memory favor state.
- The existing right-click 2-torch gift cooldown remains unchanged. No inventory confiscation, no invasive HUD or outbound connections.

## Acceptance
1. Full Python tests + original Java21 tests + new 120k policy smoke + NeoForge Gradle build + jar artifact pass.
2. Observe Redactor Lapis in-game: staring (within 15) causes rare tell; turn away at 8–20 and it walks on valid paths; at close visible range the mask can fall; wall-separated players do not trigger surprise/reveal.
3. In multiplayer, commands `/gugugaga play help`, `/gugugaga play aid off`, `aid on`, `play status`; check one Smiler cannot rescue multiple people across unloaded chunks, favors capped and only from actual help, and aid opt-out immediate.
4. Check GeckoLib bones, all textures and both one-shot gestures with a client and dedicated server; verify soft sound levels and other mod compatibility.
5. Retain five-act progression, no new endings, no Boss. Keep Draft until real client and dedicated-server acceptance.
