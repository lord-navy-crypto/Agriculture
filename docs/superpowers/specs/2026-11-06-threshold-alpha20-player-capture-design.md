# Threshold Alpha 20 — Player Capture and Watcher Modes

## Intent
Make Partygoer deaths a controlled Backrooms transition without inventory loss, and make Watcher behavior explicitly testable in passive and aggressive modes. The purpose is to make Level 0 encounters understandable and repeatable while keeping the existing six-dimension world and routes intact.

## Accepted behavior
- A death caused by a Threshold Partygoer preserves the player's full carried inventory (main inventory, armor, and offhand), then respawns the player at the established Level 0 entrance: X=4.5, Y=65.0, Z=4.5.
- Only Partygoer-caused deaths receive this treatment. Other death sources keep normal Minecraft behavior. The global keepInventory gamerule is never changed.
- The inventory snapshot is captured before vanilla death cleanup, Partygoer-caused item drops are suppressed, and the snapshot is restored once on PlayerEvent.Clone. A one-shot marker routes the new player on PlayerRespawnEvent.
- Natural Watchers default to PASSIVE: observe, stalk, search, peek, retreat, and never damage or capture players.
- AGGRESSIVE Watchers pursue using ordinary navigation and truthful observation memory. On close, visible contact they capture (teleport) the player to the same Level 0 entrance; they do not kill the player.
- Operator test summons are stable and remain available while the player moves out of range. Test Watchers do not expire due to short lifetime, stale observation memory, or failed fake-retreat navigation.
- Provide separate operator summons for passive and aggressive Watchers; retain the existing harmless model-preview summon and the existing watcher_hunt command as a compatibility alias.
- Watcher status reports its mode. Existing local encounter cap, flashlight interruption, line-of-sight limits, GeckoLib clips, and six-dimension route graph remain intact.

## Approaches considered
- Partygoer: changing the global keepInventory gamerule was rejected because it changes every death in every dimension. A per-death inventory snapshot and respawn redirect is selected because it scopes behavior to the Partygoer damage source.
- Watcher: merely extending the current lifetime was rejected because it leaves the disappearance and mode ambiguity unresolved. An explicit behavior mode plus persistent operator test mode is selected.
- Aggressive Watcher: ordinary lethal damage was rejected because the accepted behavior is a special capture and return to Level 0. A server-authoritative teleport on close, visible contact is selected.

## Constraints
- Minecraft 1.21.1 / NeoForge 21.1.x; keep existing GeckoLib dependency and Java package conventions.
- Keep the six registered dimensions, established Level 0 entry at (4.5, 65.0, 4.5), Level 0/1 room dimensions, protected shelters, and all existing route coordinates unchanged.
- Do not grant Watchers omniscient tracking through walls. Continue using visible observations and bounded last-known-position memory.
- Do not alter natural-spawn allowlists or increase the natural Watcher encounter cap.
- Do not change the global keepInventory gamerule or the behavior of deaths caused by other entities.

## Acceptance checks
- QA proves Partygoer-specific damage detection, pre-death inventory snapshot, drop suppression, one-time clone restoration, and Level 0 respawn routing.
- QA proves passive mode cannot damage/capture, aggressive mode captures to the established entrance, and test summons do not despawn on distance or navigation failure.
- NeoForge compilation, packaged-JAR validation, and dedicated-server startup/worldgen smoke checks pass.
- In-game validation remains required for actual Partygoer death/respawn, Watcher mode behavior, and GeckoLib animation presentation.
