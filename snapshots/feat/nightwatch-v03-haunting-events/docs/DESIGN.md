# Gugu Gaga Nightwatch — design v0.1

## Player promise
A harmless-looking penguin entity becomes unsettling through intelligent observation, incomplete information, and long intervals of normal survival.

## MVP: the first observer
- A single server-authoritative entity `gugugaga:observer` with original temporary low-poly penguin art.
- No natural spawning in the first MVP. A tester summons one deliberately with `/summon gugugaga:observer`.
- Four phases: **DORMANT**, **OBSERVE**, **APPROACH**, **RETREAT**.
- At most one AI evaluation each 10 server ticks per entity.
- Within 32 blocks, the observer tracks the nearest survival player. At safe distances it only approaches when not being watched; when close or watched, it withdraws.
- No damage, no inventory modification, no chunk forcing, no sound copying, no player data transmission.

## Pacing roadmap
1. v0.1: valid NeoForge registration, original placeholder renderer, manual test world, reliable AI state machine.
2. v0.2: server-side `HorrorDirector` with per-player cooldowns, player-configurable spawn eligibility, strict entity budgets.
3. v0.3: native directional audio using newly recorded / licensed recordings (not downloaded meme audio).
4. v0.4: investigation clues, consistent entity memory, environmental events that preserve builds.
5. v0.5: multiplayer, accessibility, save migration, compatibility and profiling.

## Player agency and accessibility
- Players must retain predictable ways to avoid and dismiss threats.
- No forced flashing, extreme volume spikes, misleading operating-system warnings, or surprise global world edits.
- Make encounter density adjustable and spawn disabled by default until tuned.

## Inspiration boundaries
Influences: Minecraft environmental horror, stealth creature AI, restrained uncanny-space fiction, and analog-horror clue structure. Do not copy their proprietary assets, specific sound files, creature models or scripts.


## v0.2 Nightwatch Director (implemented behind a default-OFF server option)
- Real NeoForge server tick: scans only once per 200 ticks and only when server opted in.
- Night-only, overworld-only, survival-only; never targets creative or spectator.
- At least 8 minutes cooldown per player, 10 minutes by default; newly joined
  players also wait before their first automatic encounter.
- Maximum 1 observer near a player (48 blocks), maximum 8 observers across all
  loaded dimensions; no additional natural hostile mobs.
- Try four deterministic positions 16 blocks away, with solid floors and empty
  headspace. Only inspect chunks that are already fully loaded.
- No chunk tickets, no chunk generation requests, no block edits, no damage.
  An unsafe/unloaded spawn attempt simply fails and consumes its cooldown.
- Any player can use `/gugugaga quiet on` to opt out for the current session,
  and `/gugugaga quiet off` to rejoin.
- Operators explicitly enable with `/gugugaga encounters on`, or disable
  with `/gugugaga encounters off`. Off does not delete hand-summoned entities.
- Ollama remains optional, local-only, disabled by default and advice-only;
  neither the director nor the observer makes HTTP calls per server tick.
- Until client and dedicated-server acceptance pass, this is an
  **experimental prototype**, not a production release.

## v0.3 sensory pacing and stealth (new experimental implementation)
- Head, eyes and beak pivot as one while watching (original placeholder low-poly art), with subtle phase-based wing motions.
- Three bounded location-based cues: a distant chime while being watched, a faint step while following unseen, and a soft pulse after retreat. Every cue is volume-capped to <=0.26 and rate-limited at 8–15 seconds.
- Existing observers ignore survival players in quiet mode; creative and spectator are still excluded. No world/network content is transmitted.
- Path updates once per 30 ticks rather than every 10 ticks, while the 10-tick state machine remains deterministic.
- Auto-generated sightings last 90 active seconds to avoid filling explored terrain with immortal entities; manually spawned observers are unaffected. State/lifetime are saved to NBT.
- Atmosphere uses existing vanilla game sound registry, no third-party unlicensed audio. A complete original recorded soundtrack remains a separate future milestone.
- Server config sound and particles can be disabled independently; no mandatory flashing or sudden volume spikes.
