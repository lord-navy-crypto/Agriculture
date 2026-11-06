# Threshold Alpha 20 — Player Capture and Watcher Modes Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement Partygoer-specific inventory-preserving Level 0 respawn and explicit, reliable passive/aggressive Watcher modes.

**Architecture:** Register a dedicated server-side player event handler. Snapshot the player's inventory in persistent data before death cleanup, cancel only Partygoer-caused drops, restore the snapshot on death cloning, and route the respawned player to Level 0. Give Watcher an explicit persisted mode, a close-contact capture path for aggressive mode, and a non-expiring operator test mode.

**Tech Stack:** Java 21, Minecraft 1.21.1, NeoForge 21.1.x, GeckoLib, Python source-contract QA, GitHub Actions.

**Spec:** `docs/superpowers/specs/2026-11-06-threshold-alpha20-player-capture-design.md`

## Global Constraints
- Minecraft 1.21.1 / NeoForge 21.1.x; retain existing GeckoLib dependency.
- Preserve six dimensions and all established Level 0/1 room dimensions, shelters, entry coordinates, and route coordinates.
- Level 0 entrance is X=4.5, Y=65.0, Z=4.5.
- Partygoer inventory preservation is scoped to Partygoer-caused deaths; never change global keepInventory.
- Natural Watchers default to passive; aggressive capture uses ordinary navigation and visible/last-known observations only.
- Natural Watcher cap and dimension-specific natural-spawn allowlists remain unchanged.
- Do not merge the existing draft PR or main.

## Review Focus
- A player killed by another mob or fall must not receive the Partygoer inventory/respawn treatment.
- A Partygoer-caused death with a full inventory, armor, and offhand must restore every slot exactly once.
- A non-death player clone (for example, dimension travel) must not copy the Partygoer snapshot.
- An aggressive Watcher must not capture through walls or from a stale observation.
- An operator test Watcher must remain inspectable when no player is inside its sensing radius or a fake-retreat path fails.

---

### Task 1: Add failing Partygoer and Watcher behavior contracts

**Files:**
- Modify: `qa/validate_threshold_horror_preservation.py`

**Interfaces:**
- Consumes: existing source paths under `threshold/src/main/java/io/github/lordnavycrypto/threshold`.
- Produces: executable source-contract checks for the player event handler, Watcher modes, and operator commands.

- [ ] **Step 1: Add assertions for Partygoer-only inventory snapshot, drop suppression, clone restoration, and respawn routing.**
- [ ] **Step 2: Add assertions for explicit Watcher modes, passive non-damage, aggressive capture destination, stable test mode, and mode-specific commands.**
- [ ] **Step 3: Run `python3 qa/validate_threshold_horror_preservation.py`.**
  Expected: FAIL on the missing player event handler and Watcher mode contracts.

### Task 2: Implement Partygoer-specific inventory preservation and respawn routing

**Files:**
- Create: `threshold/src/main/java/io/github/lordnavycrypto/threshold/entity/ThresholdPlayerEvents.java`
- Modify: `threshold/src/main/java/io/github/lordnavycrypto/threshold/ThresholdMod.java`

**Interfaces:**
- Produces: `ThresholdPlayerEvents.onPartygoerPlayerDeath(LivingDeathEvent)`, `onPartygoerDrops(LivingDropsEvent)`, `onPlayerClone(PlayerEvent.Clone)`, and `onPlayerRespawn(PlayerEvent.PlayerRespawnEvent)`.

- [ ] **Step 1: Implement Partygoer damage-source detection using both causing and direct entities.**
- [ ] **Step 2: Snapshot Inventory.save(ListTag) and selected hotbar slot in player persistent data during LivingDeathEvent.**
- [ ] **Step 3: Cancel drops only for the marked Partygoer-caused player death.**
- [ ] **Step 4: Restore the snapshot only when PlayerEvent.Clone.isWasDeath() is true; transfer a one-shot respawn marker and remove the original snapshot.**
- [ ] **Step 5: On PlayerRespawnEvent, teleport the ServerPlayer to ThresholdDimensions.LEVEL_0 at (4.5, 65.0, 4.5), then clear the marker.**
- [ ] **Step 6: Register the handler on NeoForge.EVENT_BUS and run the targeted QA script.**
  Expected: all Partygoer event contracts pass; no global gamerule mutation exists.

### Task 3: Implement explicit Watcher modes and reliable encounter lifetime

**Files:**
- Modify: `threshold/src/main/java/io/github/lordnavycrypto/threshold/entity/ThresholdWatcherEntity.java`

**Interfaces:**
- Produces: public `BehaviorMode { PASSIVE, AGGRESSIVE }`, `setBehaviorMode(BehaviorMode)`, `getBehaviorModeName()`, `enablePassiveTestMode()`, and `enableAggressiveTestMode()`.

- [ ] **Step 1: Add failing QA assertions for PASSIVE default, no passive player damage, aggressive capture, and stable test mode.**
- [ ] **Step 2: Add and persist BehaviorMode; natural Watchers default to PASSIVE.**
- [ ] **Step 3: Keep passive stalking/search/peek behavior but prevent its strike from damaging or capturing the player.**
- [ ] **Step 4: Add aggressive pursuit and capture only when the player is visible and within close-contact range; route to the Level 0 entrance.**
- [ ] **Step 5: Extend natural sensing/memory moderately and stop failed fake-retreat paths from discarding the Watcher.**
- [ ] **Step 6: Make operator test modes persistent until explicitly removed; when the player leaves range or memory expires, remain visible and wait rather than despawn.**
- [ ] **Step 7: Run targeted QA and inspect the resulting state transitions.**
  Expected: passive cannot damage/capture; aggressive captures only after visible close contact; operator test entities survive out-of-range and failed path cases.

### Task 4: Add explicit operator controls and server probes

**Files:**
- Modify: `threshold/src/main/java/io/github/lordnavycrypto/threshold/command/ThresholdCommands.java`
- Modify: `threshold/src/main/java/io/github/lordnavycrypto/threshold/worldgen/ThresholdRuntimeChecks.java`
- Modify: `qa/smoke_threshold_server.py`
- Modify: `qa/validate_threshold_horror_preservation.py`

**Interfaces:**
- Consumes: Task 3 `ThresholdWatcherEntity.BehaviorMode`, `setBehaviorMode`, and test-mode methods.
- Produces: `/threshold summon watcher_passive`, `/threshold summon watcher_aggressive`, compatibility alias `/threshold summon watcher_hunt`, and `/threshold watcher mode passive|aggressive`.

- [ ] **Step 1: Add QA assertions for the new command paths and status output.**
- [ ] **Step 2: Implement separate stable passive/aggressive summons while preserving `/threshold summon watcher` as the harmless preview.**
- [ ] **Step 3: Add nearest-Watcher mode switching and include mode in `/threshold watcher status`.**
- [ ] **Step 4: Add server-start runtime markers for Partygoer event registration, Watcher mode readiness, and Level 0 capture destination.**
- [ ] **Step 5: Require the new markers in the dedicated-server smoke test and run targeted QA.**
  Expected: all new command and server-readiness markers are present.

### Task 5: Version, package, and verify Alpha 20

**Files:**
- Modify: `threshold/build.gradle`
- Modify: `.github/workflows/rewrite-build.yml`
- Modify: `threshold/README.md`
- Modify: `docs/THRESHOLD_LEVEL0_LEVEL1_DESIGN.md`

- [ ] **Step 1: Bump the independent Threshold artifact to `0.1.0-alpha.20` and update artifact naming/path.**
- [ ] **Step 2: Document the new Partygoer and Watcher behavior and exact operator commands.**
- [ ] **Step 3: Run `python3 qa/validate_threshold_horror_preservation.py`.**
  Expected: PASS.
- [ ] **Step 4: Run `gradle --no-daemon --stacktrace :threshold:build`.**
  Expected: exit code 0 and Alpha 20 JAR exists.
- [ ] **Step 5: Run `python3 qa/validate_threshold_dimensions.py threshold/build/libs/threshold-0.1.0-alpha.20.jar`.**
  Expected: packaged dimensions, routes, and resources validate.
- [ ] **Step 6: Run `python3 qa/smoke_threshold_server.py`.**
  Expected: dedicated NeoForge server reaches ready state with all six dimensions and all required markers.
- [ ] **Step 7: Commit the verified changes and open a Draft PR targeting `feature/threshold-dimensions-level0-level1`; do not merge it.**

