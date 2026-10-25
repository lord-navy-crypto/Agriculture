# Gugu-Gaga Horror v0.7 — The Bird and the Redactor
Status: Narrative canon and partial experimental implementation. The Redactor
is **not yet a spawned NPC, boss or dimension**; do not represent it as a
fully modelled villain in release notes.

## Who is the villain?

- Gugu-Gaga is an unusual, cold-loving guardian called the **Lost Watcher**.
  Its funny-looking, familiar penguin-like body is deliberately retained.
  It can speak, observe, become wary or angry, and temporarily turn corrupted.
  Being angry does NOT automatically make it evil.
- The **Redactor / 遗忘校对者** is the actual antagonist: a mysterious
  narrator that swaps the meaning of observations, omits essential facts and
  spreads claims that Gugu-Gaga is evil. The apparent "evil Gugu" may be
  under the Redactor's influence rather than choosing malice.
- **Witnesses** are players investigating. They can trust the Guardian,
  trust the Redactor, or reserve judgment. Side selection changes story
  interpretation, not inventory, save ownership, access rights or damage.

## Four chapters (story design, not all implemented)

### I — Footprints on the Snow
The penguin is encountered in a cold biome. Its cartoon beak looks ordinary
until it stops turning its head while the rest of the body rotates.
First evidence: the creature was already watching the player.

### II — The Footsteps That Didn't Match
Different footsteps continue for a moment after the bird becomes still.
The journal reveals that not all footsteps belonged to Gugu-Gaga.
Player may speak with the bird and observe its anger if disturbed.

### III — The Redactor's Version
A later journal entry suggests that someone altered the witness records.
The named antagonist is revealed only after collecting three independent
clues. Players may choose GUARDIAN, REDACTOR or UNDECIDED using story commands.
**Future gameplay:** staged forged notes and a harmless "impossible wall"
render illusion confined to the Minecraft scene, without editing builds.

### IV — Quiet or Corruption
The Redactor attempts to turn the Lost Watcher into a hostile entity.
Players can avoid a warning-based attack, investigate, or use
`/gugugaga reassure` with all three journal clues to calm the bird.
**Future gameplay:** further antagonist entity, final confrontation with
peaceful alternative, fully localized dialogue branches and credits.

## Runtime AI: what v0.7 actually implements

CALM = existing four-phase observation/approach/retreat.
ANGRY = can be triggered by hitting a bird, or operator `temper angry`.
CORRUPTED = operator-created test state; the permanent Redactor encounter
that causes it is a future chapter. Both ANGRY and CORRUPTED expire
automatically into CALM after bounded duration.

Only when an operator runs `/gugugaga hostility on` may anger or corruption
perform a small close-range game attack after at least 30 ticks of audible/
visual warning, with 75-tick cooldown and a distance/visibility check.
Hostility is OFF by default. Quiet-mode, creative/spectator exclusion and
loaded-only local AI are never bypassed. No teleport, griefing, block changes,
infinite respawning, forced chunks, or aggressive audio.

## Visual direction: make strange material uncanny, NOT a comedy jumpscare

1. Keep the canonical **A penguin silhouette** and the original beak. Its
   absurdity becomes frightening through context, not through transforming
   it into a generic red-eye humanoid.
2. Silence and restraint: a creature should sometimes remain completely
   motionless for several seconds, then turn its head very slowly.
3. Half-familiar facial motion: its eyes and beak move with the head; a
   corrupted pose gains a subtle 9-degree asymmetry, not wild stretching.
4. Lighting and values: when original authorized A texture art is available,
   preserve black/white markings but reduce overly shiny highlights. Add
   restrained cool-shadow accents and muted blue-gray undertones. Do NOT
   replace with a random unrelated downloaded penguin texture.
5. Audio: quiet directionally placed cues, long silent gaps, deliberate
   pauses. No sudden full-volume memes, fake operating-system prompts,
   strobe filters, invasive system effects or copied third-party sound clips.
6. Transitions: the player should see the same body in CALM, ANGRY and
   CORRUPTED states, making the changed posture feel wrong rather than
   unveiling an obviously different boss.
7. Future asset pipeline: A-reference authorization -> palette audit ->
   daytime/snow/cave/render screenshots -> shader and accessibility tests ->
   original model/texture update with proper rights and recorded provenance.
   The code currently adjusts poses; v0.7 does **not** include a newly
   approved faithful A texture redraw.

## Experiment commands

```text
/summon gugugaga:observer ~ ~ ~
/gugugaga hostility status
/gugugaga hostility on
/gugugaga temper calm
/gugugaga temper angry
/gugugaga temper corrupted
/gugugaga hostility off
/gugugaga story
/gugugaga trust guardian
/gugugaga trust redactor
/gugugaga trust neutral
/gugugaga journal
/gugugaga reassure
```

Only operators can switch global hostile mode or entity mood. Any player can
choose/clear their story stance and use `/gugugaga quiet on` to opt out.
Reassurance requires an observer within ten blocks, line of sight and three
actual journal clues. Reassuring never grants power to the antagonist.
Never test alpha-only behavior in the sole copy of an important world.

## Release blockers

- CI Java 21/NeoForge build and pure test suite.
- Single-player, save/relog and dedicated-server multiplayer verification.
- Threat entity targeting, line of sight, gentle damage, angry recovery.
- Quiet mode, hostile mode OFF and mob cap tests under lag conditions.
- Faithful A material texture is still pending authorized source and visual
  validation. The Redactor itself is story-only, not a finished NPC.
