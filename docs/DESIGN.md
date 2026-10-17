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
