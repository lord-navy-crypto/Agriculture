#!/usr/bin/env python3
"""Protect THRESHOLD's original horror identity and enforce dimension-specific entity rules."""
from __future__ import annotations

import json
import re
import struct
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
JAVA = ROOT / "threshold/src/main/java/io/github/lordnavycrypto/threshold"
RESOURCES = ROOT / "threshold/src/main/resources"


def require(pattern: str, source: str, label: str) -> None:
    if not re.search(pattern, source, re.MULTILINE | re.DOTALL):
        raise AssertionError(f"THRESHOLD contract changed: {label}")


def read_json(path: Path) -> dict:
    return json.loads(path.read_text(encoding="utf-8"))


def main() -> int:
    layout = (JAVA / "worldgen/ThresholdRoomLayoutFeature.java").read_text(encoding="utf-8")
    runtime_checks = (JAVA / "worldgen/ThresholdRuntimeChecks.java").read_text(encoding="utf-8")
    lights = (JAVA / "block/FlickeringPanelBlock.java").read_text(encoding="utf-8")
    vent = (JAVA / "worldgen/ThresholdIndustrialVentFeature.java").read_text(encoding="utf-8")
    mod = (JAVA / "ThresholdMod.java").read_text(encoding="utf-8")
    entities = (JAVA / "entity/ThresholdEntities.java").read_text(encoding="utf-8")
    watcher = (JAVA / "entity/ThresholdWatcherEntity.java").read_text(encoding="utf-8")
    watcher_renderer = (JAVA / "client/ThresholdWatcherRenderer.java").read_text(encoding="utf-8")
    hound = (JAVA / "entity/ThresholdHoundEntity.java").read_text(encoding="utf-8")
    partygoer = (JAVA / "entity/ThresholdPartygoerEntity.java").read_text(encoding="utf-8")
    flashlight = (JAVA / "item/FlashlightItem.java").read_text(encoding="utf-8")
    battery = (JAVA / "item/BatteryItem.java").read_text(encoding="utf-8")
    partygoer_model = (JAVA / "client/ThresholdPartygoerModel.java").read_text(encoding="utf-8")
    client_events = (JAVA / "client/ThresholdClientEvents.java").read_text(encoding="utf-8")
    director = (JAVA / "entity/ThresholdEntityDirector.java").read_text(encoding="utf-8")
    commands = (JAVA / "command/ThresholdCommands.java").read_text(encoding="utf-8")
    attributes = (JAVA / "entity/ThresholdEntityAttributes.java").read_text(encoding="utf-8")
    renderers = (JAVA / "client/ThresholdClientRenderers.java").read_text(encoding="utf-8")
    level0 = read_json(RESOURCES / "data/threshold/worldgen/biome/level_0.json")
    level1 = read_json(RESOURCES / "data/threshold/worldgen/biome/level_1.json")

    # Freeze established room dimensions and vertical profiles.
    constants = {
        "FLOOR_Y": 63,
        "LEVEL_0_ROOM_SIZE": 32,
        "LEVEL_0_CARPET_Y": 64,
        "LEVEL_0_WALL_BOTTOM": 65,
        "LEVEL_0_WALL_TOP": 71,
        "LEVEL_0_CEILING_Y": 72,
        "LEVEL_1_ROOM_SIZE": 48,
        "LEVEL_1_WALL_BOTTOM": 64,
        "LEVEL_1_WALL_TOP": 73,
        "LEVEL_1_CEILING_Y": 74,
    }
    for name, value in constants.items():
        require(rf"\b{name}\s*=\s*{value}\s*;", layout, f"{name} must remain {value}")

    # Entry-room identities and original architectural vocabulary.
    require(
        r"if\s*\(roomX\s*==\s*0\s*&&\s*roomZ\s*==\s*0\)\s*return\s+0\s*;",
        layout, "Level 0 entry remains the yellow-office baseline")
    require(
        r"if\s*\(roomX\s*==\s*0\s*&&\s*roomZ\s*==\s*0\)\s*return\s+1\s*;",
        layout, "Level 1 entry remains the stocked industrial shelter")
    for token, label in (
        ("Blocks.YELLOW_CARPET", "Level 0 yellow carpet"),
        ("Blocks.BROWN_CARPET", "Level 0 worn carpet"),
        ("Blocks.GRAY_CARPET", "Level 0 alternate carpet"),
        ("Blocks.RED_CARPET", "Level 0 rare red-room carpet"),
        ("ThresholdEnvironmentBlocks.FADED_WALLPAPER", "Level 0 faded wallpaper"),
        ("ThresholdEnvironmentBlocks.STAINED_WALLPAPER", "Level 0 stained partitions"),
        ("ThresholdEnvironmentBlocks.FLICKERING_PANEL", "Level 0 animated fluorescent panels"),
        ("ThresholdEnvironmentBlocks.WAREHOUSE_WALL", "Level 1 warehouse walls"),
        ("Blocks.SMOOTH_STONE", "Level 1 industrial floor"),
        ("Blocks.IRON_BARS", "Level 1 overhead industrial fittings"),
    ):
        if token not in layout:
            raise AssertionError(f"THRESHOLD horror-preservation contract changed: {label}")

    require("ThresholdEnvironmentBlocks.CEILING_VENT", vent,
            "Level 1 retains its authored animated ventilation unit")
    require(r"new BlockPos\(\(chunkX << 4\) \+ 12, 73, \(chunkZ << 4\) \+ 12\)",
            vent, "Level 1 vent remains at its established overhead placement")

    # Existing door and return passage coordinates must not drift.
    require(
        r"return\s+localX\s*==\s*0\s*&&\s*\(localZ\s*==\s*15\s*\|\|\s*localZ\s*==\s*16\)",
        layout, "Level 0 entry transition remains at its established wall")
    require(
        r"return\s+localX\s*==\s*0\s*&&\s*\(localZ\s*==\s*23\s*\|\|\s*localZ\s*==\s*24\)",
        layout, "Level 1 return passage remains at its established wall")
    require(r"Level 1 and Level 2 rooms choose deterministic exits",
            layout, "Level 1 / Level 2 escape invariants remain documented")

    # Complex room archetypes add detail without changing the validated room
    # grid, entry shelters, transition coordinates or floor-level circulation.
    require(r"buildLevel0ArchitecturalDetails\(world, chunkX, chunkZ, seed, pos\)", layout,
            "Level 0 receives deterministic architectural overlays")
    require(r"buildLevel1ArchitecturalDetails\(world, chunkX, chunkZ, seed, pos\)", layout,
            "Level 1 receives deterministic architectural overlays")
    require(r"buildLevel0EnvironmentalDressing\(world, chunkX, chunkZ, seed, pos\)", layout,
            "Level 0 receives restrained carpet wear, vents and light details")
    require(r"buildLevel1EnvironmentalDressing\(world, chunkX, chunkZ, seed, pos\)", layout,
            "Level 1 receives industrial ventilation, cable and warning-lane details")
    require(r"private static int level0Architecture\(", layout,
            "Level 0 has a deterministic room-archetype selector")
    require(r"private static int level1Architecture\(", layout,
            "Level 1 has a deterministic room-archetype selector")
    require(r"if \(roll < 26\) return 0", layout,
            "Level 0 keeps its classic empty-office archetype as the largest single room variant")
    require(r"if \(roll < 97\) return 11", layout,
            "Level 0 includes a regular micro-maze share")
    require(r"if \(roll < 97\) return 15", layout,
            "Level 1 includes a regular warehouse micro-maze share")
    require(r"case 9 ->", layout, "Level 1 includes parking-deck bays")
    require(r"case 10 ->", layout, "Level 1 includes narrow maintenance spines")
    require(r"case 8 ->", layout, "Level 0 includes a pale arch-gallery landmark")
    require(r"case 9 ->", layout, "Level 0 includes a suspended false-ceiling anomaly")
    require(r"case 11 ->", layout, "Level 1 includes freight loading bays")
    require(r"case 12 ->", layout, "Level 1 includes multi-storey parking circulation")
    require(r"case 13 ->", layout, "Level 1 includes a rare artificial-light concrete garden")
    require(r"Blocks\.SMOOTH_SANDSTONE", layout, "Level 0 arch galleries use a distinct pale stone frame")
    require(r"Blocks\.MOSS_BLOCK", layout, "Level 1 concrete gardens use restrained overgrowth")
    require(r"Blocks\.AZALEA_LEAVES", layout, "Level 1 concrete gardens have layered foliage")
    require(r"private static boolean isReservedRoomPath\(", layout,
            "architectural overlays reserve a route from each actual doorway to the room hub")
    require(r"doorwayCenterAlongZ", layout,
            "doorway reservation uses the same deterministic edge center as room generation")
    require(r"doorwayCenterAlongX", layout,
            "doorway reservation uses the same deterministic edge center as room generation")
    require(r"if \(roomX == 0 && roomZ == 0\) continue", layout,
            "architectural overlays preserve both entry rooms")
    for token, label in (
        ("Blocks.WHITE_STAINED_GLASS_PANE", "Level 0 office cubicles"),
        ("Blocks.GRAY_STAINED_GLASS_PANE", "Level 0 mirrored office partitions"),
        ("Blocks.STAINED_WALLPAPER", "Level 0 staggered partitions"),
        ("Blocks.COPPER_BLOCK", "Level 1 industrial pipework"),
        ("Blocks.IRON_BARS", "Level 1 rack frames and gantry rails"),
        ("Blocks.SMOOTH_STONE_SLAB", "Level 1 loading platforms"),
        ("isLevel1MachineIsland", "Level 1 machine-bay structures"),
        ("for (int y = 64; y <= 69; y++)", "Level 1 rack frames rest on the warehouse floor"),
        ("for (int y = 64; y <= 68; y++)", "Level 1 gantry supports rest on the warehouse floor"),
        ("!protectedTile && (localX == 8 || localX == 40)", "gantry supports respect protected circulation/resource tiles"),
    ):
        if token not in layout:
            raise AssertionError(f"THRESHOLD architecture contract missing: {label}")

    # The two lighting systems retain different failure and recovery profiles.
    require(r"int offChance\s*=\s*level0\s*\?\s*18\s*:\s*8\s*;", lights,
            "Level 0 and Level 1 retain distinct light-failure rates")
    require(r"int restoreChance\s*=\s*level0\s*\?\s*2\s*:\s*8\s*;", lights,
            "Level 1 retains its longer dark recovery interval")

    # Both biomes remain free of generic natural spawner tables. The entity
    # director owns controlled, dimension-specific sightings and encounters.
    effects0 = level0["effects"]
    effects1 = level1["effects"]
    assert effects0["mood_sound"]["sound"] == "minecraft:ambient.cave"
    assert effects1["mood_sound"]["sound"] == "minecraft:block.vault.ambient"
    assert effects0["mood_sound"]["sound"] != effects1["mood_sound"]["sound"]
    assert effects0["fog_color"] != effects1["fog_color"]
    assert effects0["sky_color"] != effects1["sky_color"]
    for name, biome in (("Level 0", level0), ("Level 1", level1)):
        assert biome["spawners"] == {}, f"{name} must use the controlled entity director"
        assert biome["spawn_costs"] == {}, f"{name} must not acquire generic spawn costs"

    # Registry, attributes, mod-bus and client renderer wiring.
    require(r"MobCategory\.MISC", entities, "observer is not a hostile natural-spawn category")
    require(r"MobCategory\.MONSTER", entities, "Hound is registered as a monster")
    require(r"ThresholdEntities\.ENTITY_TYPES\.register\(modBus\)", mod,
            "custom entity types are registered")
    require(r"ThresholdEntityAttributes::registerAttributes", mod,
            "custom entity attributes are registered")
    require(r"NeoForge\.EVENT_BUS\.register\(ThresholdEntityDirector\.class\)", mod,
            "dimension-specific entity director is registered")
    require(r"registerEntityRenderer\(ThresholdEntities\.WATCHER\.get\(\)",
            renderers, "observer renderer is registered")
    require(r"registerEntityRenderer\(ThresholdEntities\.HOUND\.get\(\)",
            renderers, "Hound renderer is registered")
    require(r"registerEntityRenderer\(ThresholdEntities\.PARTYGOER\.get\(\)",
            renderers, "Partygoer renderer is registered")
    assert renderers.count("registerEntityRenderer(ThresholdEntities.WATCHER.get()") == 1
    assert renderers.count("registerEntityRenderer(ThresholdEntities.HOUND.get()") == 1
    assert renderers.count("registerEntityRenderer(ThresholdEntities.PARTYGOER.get()") == 1
    assert "@EventBusSubscriber" not in client_events, "second renderer subscriber must stay disabled"
    require(r"ThresholdEntities\.WATCHER\.get\(\)", attributes,
            "observer attributes are registered")
    require(r"ThresholdEntities\.HOUND\.get\(\)", attributes,
            "Hound attributes are registered")

    # Level 0's Watcher is a bounded, damageable stalker with honest local perception.
    for token, label in (
        ("EncounterState", "Watcher has explicit encounter states"),
        ("ObservationStyle", "Watcher uses distinct observation styles"),
        ("ObservationSnapshot", "Watcher stores immutable last-known observations"),
        ("confidence(long now)", "Watcher memory confidence decays with age"),
        ("cornerPeekWaypoint", "Watcher sweeps around the last sighting instead of walking straight to it"),
        ("searchWaypointIndex", "Watcher search uses a bounded three-point sweep"),
        ("searchPauseTicks", "Watcher pauses to peek around corners"),
        ("EncounterState.REPOSITION", "Watcher can perform a fake retreat reposition"),
        ("encounterState != EncounterState.REPOSITION", "fake-retreat invisibility is protected from ambient flicker"),
        ("beginFakeReposition", "fake retreat uses ordinary navigation rather than teleporting"),
        ("MEMORY_TICKS = 120", "Watcher memory has a bounded lifetime"),
        ("relayLocalObservation", "nearby Watchers relay local observations"),
        ("peer.observation.observedAt() > observation.observedAt()", "relays preserve the original observation timestamp"),
        ("isPlayerNoisy", "Watcher distinguishes loud and quiet player movement"),
        ("isBeingWatchedBy", "direct eye contact changes Watcher behavior"),
        ("FlashlightItem.isActive(player)", "a held flashlight can interrupt a scare"),
        ("beginRush", "Watcher can initiate a short charge"),
        ("nearMiss = server.getRandom().nextInt(4) == 0", "some charges become near-miss scares"),
        ("performStrike", "Watcher has a single-swipe attack state"),
        ("getAttributeValue(Attributes.ATTACK_DAMAGE)", "Watcher swipe uses its registered attack-damage attribute"),
        ("player.knockback(0.22D", "Watcher hit has bounded knockback"),
        ("RUSH_COOLDOWN_TICKS = 240", "jump scares have a long cooldown"),
        ("beginRetreat", "Watcher withdraws after a scare"),
        ("setFlankerRole", "a second local Watcher receives a flanker role"),
        ("PEER_RELAY_RADIUS = 48.0D", "peer communication is local"),
        ("NATURAL_LIFETIME_TICKS = 600", "natural Watcher lifetime is bounded"),
        ("PERCEPTION_INTERVAL_TICKS = 4", "Watcher perception runs at five updates per second"),
        ("updateFlicker()", "Watcher flicker is updated independently of perception"),
        ("updateInterval(4)", "Watcher transform synchronization is bounded"),
        ("animation.threshold_watcher.lunge", "Watcher has a lunge animation"),
        ("animation.threshold_watcher.strike", "Watcher has a strike animation"),
        ("animation.threshold_watcher.peek", "Watcher has a corner-peek animation"),
    ):
        if token not in watcher and token not in entities:
            raise AssertionError(f"Level 0 Watcher behavior contract missing: {label}")

    watcher_constructor = watcher[
        watcher.index("public ThresholdWatcherEntity"):watcher.index("/** A harmless model/animation preview")
    ]
    assert "setNoAi(true)" not in watcher_constructor, "Natural Watcher must retain AI"
    assert "setNoGravity(true)" not in watcher_constructor, "Natural Watcher must use normal movement physics"
    assert "setInvulnerable(true)" not in watcher_constructor, "Natural Watcher must be damageable"
    assert "noPhysics = true" not in watcher_constructor, "Natural Watcher must use collision and path navigation"
    assert "public boolean isPickable()" not in watcher, "Natural Watcher must remain selectable"
    assert "public boolean isAttackable()" not in watcher, "Natural Watcher must remain attackable"
    assert "MeleeAttackGoal" not in watcher, "Watcher uses its bounded encounter state machine, not a generic chase loop"
    assert "setNoAi(true)" in watcher[watcher.index("public void enableShowcaseMode"):watcher.index("public void enableTestEncounterMode")], "only the harmless preview disables AI"
    require("SHOWCASE_LIFETIME_TICKS = 600", watcher, "operator preview has a 30-second inspection window")
    require("TEST_ENCOUNTER_LIFETIME_TICKS = 1200", watcher, "active test encounter has a bounded 60-second lifetime")
    require("enableTestEncounterMode", commands, "operator can summon an active Watcher encounter")
    assert "getEncounterStateName" in watcher and "getObservationConfidence" in watcher and "getObservationAgeTicks" in watcher
    assert "watcherStatus" in commands, "local Watcher state and memory telemetry are inspectable in game"
    assert "ObservationStyle[] soloStyles" in watcher, "only the second member is assigned the FLANKER style"
    assert watcher.index("if (encounterState == EncounterState.RETREAT)") < watcher.index("Player player = findNearestValidPlayer"), "retreat timer continues even when no player is in range"
    assert watcher.index("if (visible && FlashlightItem.isActive(player)") < watcher.index("if (encounterState == EncounterState.RUSH)"), "flashlight can interrupt an active rush"
    assert "if (!peerAlreadyRushing" in watcher, "nearby Watchers do not rush the same player simultaneously"
    assert "attackConsumed" in watcher, "a scare cannot chain multiple swipes"
    assert "fakeRetreat && !testEncounterMode" in watcher, "fake retreats are rare and disabled for operator test encounters"
    assert "distanceToSqr(fakeRetreatDestination) <= 4.0D" in watcher, "fake retreat reappears only after path navigation reaches its destination"
    assert "if (stateTicks >= 60) discard()" in watcher, "failed fake-retreat paths do not teleport through walls"
    assert "if (encounterState == EncounterState.REPOSITION)" in watcher and "getNavigation().stop();\n                discard();" in watcher, "a successful player hit cancels a hidden fake retreat"
    assert "public boolean hurt(DamageSource source, float amount)" in watcher and "beginRetreat(attacker)" in watcher
    require("watcher_hunt", commands, "active Watcher summon has a dedicated command")
    assert "findShowcasePosition(level, player, 8, 16, 2)" in commands, "active test encounter uses a safe loaded floor position"
    assert "player.getLookAngle().dot(toSpawn) > 0.35D" in director, "natural Watcher spawns outside the player's forward view"
    require("LightTexture.FULL_BRIGHT", watcher_renderer,
            "Watcher silhouette remains visible in dark rooms")
    require("Attributes.ATTACK_DAMAGE", attributes, "Watcher has a registered attack attribute")
    require("Attributes.MOVEMENT_SPEED", attributes, "Watcher has a registered movement attribute")
    require("Attributes.MAX_HEALTH", attributes, "Watcher has a bounded health pool")

    # Level 1's Hound is a real but bounded hostile encounter.
    require(r"extends\s+Monster", hound, "Hound is a real hostile mob")
    require(r"new\s+MeleeAttackGoal", hound, "Hound has melee behavior")
    require(r"new\s+NearestAttackableTargetGoal<>\(this,\s*Player\.class", hound,
            "Hound can acquire players as targets")
    require(r"Attributes\.ATTACK_DAMAGE", hound, "Hound has explicit attack tuning")
    require(r"Attributes\.FOLLOW_RANGE", hound, "Hound has bounded follow range")
    require(r"animation\.threshold_hound\.run", hound, "Hound has a distinct pursuit animation")
    require(r"animation\.threshold_hound\.sniff", hound, "Hound has a dedicated sniffing animation")
    require(r"animation\.threshold_hound\.alert", hound, "Hound has a dedicated alert animation")
    require(r"animation\.threshold_hound\.stalk", hound, "Hound has a low stalking gait")
    require(r"animation\.threshold_hound\.retreat", hound, "Hound has a fast retreat animation")
    require(r"animation\.threshold_hound\.bite", hound, "Hound has a dedicated bite attack animation")
    require(r"getAttackAnim\(0\.0F\) > 0\.12F", hound, "Hound bite animation follows the real attack swing")
    require(r"new InvestigateLastKnownPositionGoal\(this\)", hound,
            "Hound investigates the last known position after losing sight of a player")
    require(r"getTarget\(\) instanceof Player target && hasLineOfSight\(target\)", hound,
            "Hound updates its search memory only while it can see the player")
    require(r"lastKnownPlayerPosition", hound,
            "Hound retains a short-lived last-known player position")
    require(r"searchTicks", hound, "Hound search memory expires after a bounded interval")
    require(r"double angle = step \* 2\.399963229728653D", hound,
            "Hound searches with a widening golden-angle pattern")
    require(r"searchPatternIndex", hound, "Hound advances through bounded search waypoints")
    require(r"new StalkPlayerGoal\(this\)", hound,
            "Hound stalks quiet players before committing to an attack")
    require(r"horizontalDistanceSqr\(\) > 0\.025D", hound,
            "Hound detects fast player movement as a noise cue")
    require(r"noiseListenCooldown", hound, "Hound listening checks are rate-limited")
    require(r"findNearestNoisyPlayer\(18\.0D\)", hound,
            "Hound chooses the nearest noisy player rather than the nearest player overall")
    require(r"noiseDetectionRange\(candidate, range\)", hound,
            "Hound hearing uses a movement-sensitive acoustic radius")
    require(r"player\.hasLineOfSight\(this\)", hound,
            "Hound hearing is attenuated when walls obstruct the sound path")
    require(r"range \*= 0\.55D", hound,
            "solid obstruction substantially reduces hearing distance without making walls soundproof")
    require(r"player\.isSprinting\(\) \|\| player\.isFallFlying\(\)", hound,
            "sprinting and fall-flying use the longest hearing radius")
    require(r"maximumRange \* 0\.78D", hound,
            "swimming creates a shorter hearing radius than sprinting")
    require(r"maximumRange \* 0\.72D", hound,
            "jumping creates a shorter hearing radius than sprinting")
    require(r"maximumRange \* 0\.62D", hound,
            "ordinary movement has a shorter hearing radius than sprinting")
    require(r"noisy\.position\(\)\.add\(uncertainty\)", hound,
            "Hound hears an uncertain sound location instead of gaining live wall tracking")
    require(r"searchTicks = 80", hound, "sound investigation has a short bounded memory")
    require(r"SoundEvents\.WOLF_GROWL", hound, "Hound gives a restrained audio response to heard noise")
    require(r"candidate\.isSprinting\(\)", hound,
            "Sprinting escalates the Hound from stalking to pursuit")
    require(r"hound\.getNavigation\(\)\.moveTo\(player, 0\.68D\)", hound,
            "Hound approaches quietly at a slower stalking speed")
    require(r"isIlluminatedByFlashlight\(candidate\)", hound,
            "Hound only avoids players whose active flashlight is aimed at it")
    require(r"player\.getLookAngle\(\)\.dot\(toHound\) > 0\.58D", hound,
            "Hound flashlight defense checks the beam direction")
    assert hound.index("findIlluminatingPlayer()") < hound.index("super.tick()"), "aimed flashlight interrupts before Hound target/attack AI runs"
    assert "ThresholdHoundEntity.this.lightRepelledTicks > 0" in hound and "hound.lightRepelledTicks > 0" in hound and "hound.lightRepelledTicks == 0" in hound, "Hound remains in its retreat window after flashlight contact"
    melee_goal = hound[hound.index("new MeleeAttackGoal"):hound.index("new InvestigateLastKnownPositionGoal")]
    assert "lightRepelledTicks == 0" in melee_goal and "isIlluminatedByFlashlight(target)" in melee_goal, "Hound melee cannot override active flashlight defense"
    require(r"lightRepelledTicks", hound, "Hound remains repelled for a short, bounded interval")
    require(r"getNavigation\(\)\.moveTo", hound, "Hound withdraws rather than despawning")

    # Partygoer is now a hostile predator with a stalk-to-hunt escalation.
    assert "extends Monster" in partygoer, "Partygoer must be a hostile mob"
    assert "new MeleeAttackGoal" in partygoer, "Partygoer must have real melee attacks"
    assert "Attributes.ATTACK_DAMAGE" in partygoer, "Partygoer must have tuned attack damage"
    assert "Attributes.FOLLOW_RANGE" in partygoer, "Partygoer must have a bounded pursuit range"
    assert "new StalkPlayerGoal(this)" in partygoer, "Partygoer must stalk quiet players"
    assert "new NearestAttackableTargetGoal<>(this, Player.class, true)" in partygoer
    assert "isBeingWatchedBy(candidate)" in partygoer, "Direct eye contact must trigger a hunt"
    assert "candidate.isSprinting()" in partygoer, "Sprinting must trigger pursuit"
    assert "SoundEvents.ENDERMAN_SCREAM" in partygoer, "Hunt escalation must have an audio sting"
    assert "CAPTURE_TICKS" not in partygoer, "Old passive capture mechanic must be removed"
    assert "player.teleportTo" not in partygoer, "Partygoer must attack rather than teleport players"
    for clip in ("idle", "stalk", "run", "alert", "attack"):
        assert f"animation.threshold_partygoer.{clip}" in partygoer, f"Partygoer missing {clip} controller state"

    # Dimension guardrails are based on natural-spawn reason, not blanket entity-join rejection.
    assert "event.getEntity() instanceof Projectile" in director, "Threshold entity-join guard is narrowly scoped to projectiles"
    assert "instanceof LivingEntity" not in director, "ordinary living entities must not be blanket-rejected"
    assert "return entityType == ThresholdEntities.WATCHER.get();" in director, "Level 0 natural-spawn whitelist is Watcher only"
    assert "return entityType == ThresholdEntities.HOUND.get();" in director, "Level 1 natural-spawn whitelist is Hound only"
    require("shouldBlockNaturalSpawn", director, "natural-spawn reason policy is explicit")
    require("MobSpawnEvent.SpawnPlacementCheck.Result.FAIL", director,
            "unapproved natural spawns are rejected before placement")
    assert "spawnObservers(level)" in director, "Level 0 Watcher spawner exists"
    assert "spawnHounds(level)" in director, "Level 1 Hound spawner exists"
    require("quiet stalking state; noise and proximity trigger pursuit", director,
            "Hound spawns into its stalking state rather than receiving an instant target")
    assert "hasNearbyWatcher(level, player)" in director, "natural spawns respect the two-Watcher cap"
    assert "hasNearbyWatcher(level, player)" in commands, "operator summons respect the same cap"
    assert "if (!level.addFreshEntity(watcher))" in commands, "operator summon reports rejected spawns rather than false success"
    for token, source, label in (
        ("WATCHER_SPAWN_INTERVAL_TICKS = 300", director, "Watcher spawn cadence remains bounded"),
        ("WATCHER_NEARBY_RADIUS = 42.0D", director, "Watcher encounters use a local spacing radius"),
        ("MAX_LOCAL_WATCHERS = 2", director, "a local encounter is capped at two Watchers"),
        ("countNearbyWatchers", director, "local Watcher count is explicit"),
        ("nearbyWatchers == 0 ? 4 : 8", director, "second Watcher spawn is rarer than the first"),
        ("12 + level.getRandom().nextInt(9)", director, "second Watcher enters closer for local coordination"),
        ("watcher.setFlankerRole(nearbyWatchers > 0)", director, "second Watcher is assigned as a flanker"),
    ):
        if token not in source:
            raise AssertionError(f"THRESHOLD Watcher coordination budget missing: {label}")
    require(r"gameTime % 480L == 0L", director, "Hound encounters use a slower cadence")
    require(r"if \(sight\.getType\(\) == HitResult\.Type\.MISS\) continue", director,
            "Hounds do not materialize directly in the player's view")
    require(r"playerRoomX\s*==\s*0\s*&&\s*playerRoomZ\s*==\s*0", director,
            "Level 1 starter shelter is protected from Hound spawns")
    require(r"getMaxLocalRawBrightness\(pos\)\s*>\s*9", director,
            "Hounds prefer darker industrial areas")
    require(r"floor\.getCollisionShape\(level,\s*pos\.below\(\)\)\.isEmpty\(\)", director,
            "observer can stand on Level 0 carpet without requiring a full-cube floor")

    # Check each dimension's new room variants in its own generator method.
    l0_architecture = layout[
        layout.index("private static void buildLevel0ArchitecturalDetails"):
        layout.index("private static void buildLevel1ArchitecturalDetails")
    ]
    l1_architecture = layout[
        layout.index("private static void buildLevel1ArchitecturalDetails"):
        layout.index("private static int level0Architecture")
    ]
    for token, label in (("case 8 ->", "Level 0 arch gallery"), ("case 9 ->", "Level 0 false ceiling"),
                         ("case 10 ->", "Level 0 crimson room fragment"),
                         ("case 11 ->", "Level 0 micro-maze partitions"),
                         ("case 12 ->", "Level 0 connected sub-floor annex"),
                         ("case 13 ->", "Level 0 records archive aisles"),
                         ("case 14 ->", "Level 0 offset false-corridor walls"),
                         ("case 15 ->", "Level 0 broken ceiling grid"),
                         ("case 16 ->", "Level 0 raised office mezzanine"),
                         ("case 17 ->", "Level 0 nested loop maze"),
                         ("case 18 ->", "Level 0 sunken conference pit"),
                         ("case 19 ->", "Level 0 irregular pillar forest"),
                         ("case 20 ->", "Level 0 multi-pocket floor fold"),
                         ("case 21 ->", "Level 0 accessible stair tower"),
                         ("case 22 ->", "Level 0 dynamic blackout corridor")):
        if token not in l0_architecture:
            raise AssertionError(f"THRESHOLD Level 0 architecture missing: {label}")
    for token, label in (("case 11 ->", "Level 1 freight bays"),
                         ("case 12 ->", "Level 1 parking circulation"),
                         ("case 13 ->", "Level 1 concrete garden"),
                         ("case 14 ->", "Level 1 electrical switchgear"),
                         ("case 15 ->", "Level 1 warehouse micro-maze"),
                         ("case 16 ->", "Level 1 sunken maintenance trench"),
                         ("case 17 ->", "Level 1 overhead freight gantry"),
                         ("case 18 ->", "Level 1 compressor hall"),
                         ("case 19 ->", "Level 1 sorting conveyors"),
                         ("case 20 ->", "Level 1 raised loading-office mezzanine"),
                         ("case 21 ->", "Level 1 accessible pump sump"),
                         ("case 22 ->", "Level 1 stacked pallet-rack canyon"),
                         ("case 23 ->", "Level 1 Gothic concrete arcade"),
                         ("case 24 ->", "Level 1 overhead cross-dock crane hall"),
                         ("case 25 ->", "Level 1 ventilation exchange hall"),
                         ("case 26 ->", "Level 1 multi-engine generator plant")):
        if token not in l1_architecture:
            raise AssertionError(f"THRESHOLD Level 1 architecture missing: {label}")

    for token, label in (
        ("buildLevel0VerticalNetwork", "Level 0 connected upper galleries"),
        ("buildLevel1VerticalNetwork", "Level 1 connected elevated catwalks"),
        ("hasLevel0UpperNeighbour", "Level 0 shared-edge vertical bridge decisions"),
        ("hasLevel1UpperNeighbour", "Level 1 shared-edge vertical bridge decisions"),
        ("StairBlock.FACING", "walkable stair connections between elevations"),
        ("set(world, pos, x, 58, z", "real lower-level floors below the base plane"),
    ):
        if token not in layout:
            raise AssertionError(f"THRESHOLD vertical architecture contract missing: {label}")

    if "Blocks.RED_CONCRETE" not in l0_architecture:
        raise AssertionError("THRESHOLD Level 0 crimson fragment must use its red-room palette")
    if "Blocks.LIGHTNING_ROD" not in l1_architecture:
        raise AssertionError("THRESHOLD Level 1 switchgear must include transformer details")

    for token, label, source in (
        ("Blocks.BOOKSHELF", "Level 0 archive shelf banks", l0_architecture),
        ("Blocks.LECTERN", "Level 0 archive catalog stations", l0_architecture),
        ("0xA14A0L", "Level 0 seed-stable false-corridor gaps", l0_architecture),
        ("upperWalkway", "Level 0 ceiling details respect upper-gallery clearance", l0_architecture),
        ("Blocks.CHAIN", "Level 1 gantry suspension", l1_architecture),
        ("Blocks.LIGHTNING_ROD", "Level 1 compressor assemblies", l1_architecture),
        ("Blocks.SMOOTH_STONE_SLAB", "Level 1 walkable conveyor belts", l1_architecture),
        ("Blocks.OAK_PLANKS", "Level 0 raised mezzanine flooring", l0_architecture),
        ("0xA17A0L", "Level 0 nested maze seed-stable door gaps", l0_architecture),
        ("Blocks.SMOOTH_STONE", "Level 0 sunken conference floor", l0_architecture),
        ("Blocks.IRON_BLOCK", "Level 1 raised mezzanine flooring", l1_architecture),
        ("Blocks.LIGHTNING_ROD", "Level 1 sump pump equipment", l1_architecture),
        ("rackProtected", "Level 1 rack canyon protects machinery footprints", l1_architecture),
        ("boolean sideStair", "alternate mezzanine access stair", l0_architecture),
        ("Direction.EAST", "side stair direction and pit access", l0_architecture),
        ("localX == 28", "Level 0 pit has an alternate stair opening", l0_architecture),
        ("localX == 12", "Level 1 sump has an alternate stair opening", l1_architecture),
        ("localX >= 13 && localX <= 17", "Level 1 sump alternate stair flight", l1_architecture),
        ("!protectedTile && !upperWalkway", "Level 1 lower room preserves routes and catwalks", l1_architecture),
        ("0xA19A0L", "Level 0 seed-stable pillar variation", l0_architecture),
        ("Blocks.SMOOTH_STONE", "Level 0 recessed floor pockets", l0_architecture),
        ("case 21 ->", "Level 0 stair tower and raised landing", l0_architecture),
        ("Blocks.POLISHED_ANDESITE", "Level 1 Gothic arcade piers", l1_architecture),
        ("Blocks.IRON_BLOCK", "Level 1 overhead crane tracks", l1_architecture),
        ("Blocks.CUT_COPPER", "Level 1 ventilation exchange ducts", l1_architecture),
        ("FlickeringPanelBlock.LIT, false", "Level 0 blackout panels start dark", l0_architecture),
        ("Blocks.BLACK_CONCRETE", "Level 0 blackout ceiling absorbs light", l0_architecture),
        ("Blocks.FURNACE", "Level 1 generator engine cores", l1_architecture),
        ("Blocks.REDSTONE_LAMP", "Level 1 breaker-panel indicator lamps", l1_architecture),
        ("blackoutPocket ? 12 : 2", "blackout panels recover more slowly", lights),
        ("blackoutPocket ? 8 : 18", "blackout panels fail more often", lights),
        ("level0Architecture(seed, roomX, roomZ) >= 19", "Level 0 landmark rooms own their vertical clearance", layout),
        ("level1Architecture(seed, roomX, roomZ) >= 23", "Level 1 landmark rooms own their overhead clearance", layout),
    ):
        if token not in source:
            raise AssertionError(f"THRESHOLD architecture detail missing: {label}")

    # Stairs must reach the actual walking surfaces, not stop one block short.
    for pattern, label in (
        (r"localZ >= 12 && localZ <= 16;", "Level 0 upper stair reaches the gallery landing"),
        (r"localZ >= 11 && localZ <= 16", "Level 0 lower stair reaches the annex floor"),
        (r"localZ >= 18 && localZ <= 24;", "Level 1 upper stair reaches the catwalk landing"),
        (r"localZ >= 21 && localZ <= 26", "Level 1 lower stair reaches the maintenance floor"),
        (r"set\(world, pos, x, 70, z, Blocks\.WHITE_STAINED_GLASS_PANE", "Level 0 railing sits above the carpet"),
        (r"set\(world, pos, x, 72, z, Blocks\.IRON_BARS", "Level 1 railing sits above the catwalk surface"),
    ):
        require(pattern, layout, label)

    for token, label in (
        ("THRESHOLD_LEVEL0_WATCHER_ADVANCED_AI_READY_OK", "Level 0 Watcher attributes and navigation runtime probe"),
        ("THRESHOLD_LEVEL1_HOUND_ADVANCED_AI_READY_OK", "Level 1 Hound attributes and navigation runtime probe"),
        ("new BlockPos(48, 68, 16)).is(Blocks.OAK_STAIRS)", "Level 0 upper stair landing"),
        ("new BlockPos(55, 58, 11)).is(Blocks.OAK_STAIRS)", "Level 0 lower stair landing"),
        ("new BlockPos(72, 70, 72)).is(Blocks.STONE_BRICK_STAIRS)", "Level 1 upper stair landing"),
        ("new BlockPos(81, 58, 69)).is(Blocks.STONE_BRICK_STAIRS)", "Level 1 lower stair landing"),
    ):
        if token not in runtime_checks:
            raise AssertionError(f"THRESHOLD runtime vertical probe missing: {label}")

    # Stairs must reach the actual walking surfaces, not stop one block short.
    for pattern, label in (
        (r"localZ >= 12 && localZ <= 16;", "Level 0 upper stair reaches the gallery landing"),
        (r"localZ >= 11 && localZ <= 16", "Level 0 lower stair reaches the annex floor"),
        (r"localZ >= 18 && localZ <= 24;", "Level 1 upper stair reaches the catwalk landing"),
        (r"localZ >= 21 && localZ <= 26", "Level 1 lower stair reaches the maintenance floor"),
        (r"set\(world, pos, x, 70, z, Blocks\.WHITE_STAINED_GLASS_PANE", "Level 0 railing sits above the carpet"),
        (r"set\(world, pos, x, 72, z, Blocks\.IRON_BARS", "Level 1 railing sits above the catwalk surface"),
    ):
        require(pattern, layout, label)

    for token, label in (
        ("new BlockPos(48, 68, 16)).is(Blocks.OAK_STAIRS)", "Level 0 upper stair landing"),
        ("new BlockPos(55, 58, 11)).is(Blocks.OAK_STAIRS)", "Level 0 lower stair landing"),
        ("new BlockPos(72, 70, 72)).is(Blocks.STONE_BRICK_STAIRS)", "Level 1 upper stair landing"),
        ("new BlockPos(81, 58, 69)).is(Blocks.STONE_BRICK_STAIRS)", "Level 1 lower stair landing"),
    ):
        if token not in runtime_checks:
            raise AssertionError(f"THRESHOLD runtime vertical probe missing: {label}")

    # GeckoLib geometry/animation and the compact dark pixel textures ship in
    # the independent mod JAR; every animated bone must exist in the matching model.
    for name in ("watcher", "hound"):
        geometry_path = RESOURCES / f"assets/threshold/geo/{name}.geo.json"
        animation_path = RESOURCES / f"assets/threshold/animations/{name}.animation.json"
        texture_path = RESOURCES / f"assets/threshold/textures/entity/{name}.png"
        geometry = read_json(geometry_path)["minecraft:geometry"][0]
        animation = read_json(animation_path)["animations"]
        assert geometry["description"]["texture_width"] == 64
        assert geometry["description"]["texture_height"] == 64
        assert geometry["bones"], f"{name} geometry has no bones"
        assert animation, f"{name} has no GeckoLib animations"
        required_clips = {
            "watcher": {
                "animation.threshold_watcher.still",
                "animation.threshold_watcher.head_track",
                "animation.threshold_watcher.stutter",
                "animation.threshold_watcher.fold",
                "animation.threshold_watcher.shroud_drift",
                "animation.threshold_watcher.lunge",
                "animation.threshold_watcher.strike",
                "animation.threshold_watcher.peek",
            },
            "hound": {
                "animation.threshold_hound.idle",
                "animation.threshold_hound.walk",
                "animation.threshold_hound.run",
                "animation.threshold_hound.sniff",
                "animation.threshold_hound.alert",
                "animation.threshold_hound.stalk",
                "animation.threshold_hound.retreat",
                "animation.threshold_hound.bite",
            },
        }[name]
        assert required_clips.issubset(animation.keys()), f"{name} is missing a required animation state"
        bone_names = {bone["name"] for bone in geometry["bones"]}
        for animation_name, clip in animation.items():
            for animated_bone in clip.get("bones", {}):
                assert animated_bone in bone_names, (
                    f"{name} animation {animation_name} references missing bone {animated_bone}"
                )
        png = texture_path.read_bytes()
        assert png[:8] == b"\x89PNG\r\n\x1a\n", f"{name} texture is not PNG"
        width, height = struct.unpack(">II", png[16:24])
        assert (width, height) == (64, 64), f"{name} texture dimensions are wrong"
        if name == "watcher":
            assert len(png) > 1024, "Watcher texture must contain visible detail, not a near-black placeholder"

    partygoer_geometry = read_json(RESOURCES / "assets/threshold/geo/partygoer.geo.json")["minecraft:geometry"][0]
    partygoer_animation = read_json(RESOURCES / "assets/threshold/animations/partygoer.animation.json")["animations"]
    assert partygoer_geometry["description"]["identifier"] == "geometry.threshold.partygoer"
    assert partygoer_geometry["description"]["texture_width"] == 64
    assert partygoer_geometry["description"]["texture_height"] == 64
    assert partygoer_geometry["bones"] and partygoer_animation
    required_partygoer_clips = {
        "animation.threshold_partygoer.idle",
        "animation.threshold_partygoer.stalk",
        "animation.threshold_partygoer.run",
        "animation.threshold_partygoer.alert",
        "animation.threshold_partygoer.attack",
    }
    assert required_partygoer_clips.issubset(partygoer_animation.keys()), "Partygoer animation states are incomplete"
    partygoer_bones = {bone["name"] for bone in partygoer_geometry["bones"]}
    for animation_name, clip in partygoer_animation.items():
        for animated_bone in clip.get("bones", {}):
            assert animated_bone in partygoer_bones, (
                f"Partygoer animation {animation_name} references missing bone {animated_bone}"
            )
    require(r"geo/partygoer\.geo\.json", partygoer_model, "Partygoer model uses dedicated geometry")
    require(r"animations/partygoer\.animation\.json", partygoer_model, "Partygoer model uses dedicated animation")
    require(r"durability\(MAX_CHARGE\)", flashlight, "Flashlight charge persists as item durability")
    require(r"MAX_CHARGE\s*=\s*480", flashlight, "Flashlight has eight-minute charge capacity")
    require(r"onUseTick", flashlight, "Flashlight consumes charge while active")
    require(r"MobEffects\.NIGHT_VISION", flashlight, "Flashlight provides its short beam pulse")
    require(r"getBarWidth", flashlight, "Flashlight shows remaining charge")
    require(r"FlashlightItem\.RECHARGE_AMOUNT", battery, "Battery restores flashlight charge")
    require(r"case 7 ->", layout, "Level 0 includes the blackout-annex archetype")
    require(r"case 8 ->", layout, "Level 1 includes the conveyor/service-cable archetype")

    # Alpha 20 Partygoer-specific death/respawn contract. This intentionally
    # fails until the server-side player event handler is implemented.
    player_events_path = JAVA / "entity/ThresholdPlayerEvents.java"
    player_events = player_events_path.read_text(encoding="utf-8") if player_events_path.exists() else ""
    for token, label in (
        ("LivingDeathEvent", "Partygoer inventory is captured before death cleanup"),
        ("LivingDropsEvent", "Partygoer-caused player drops are intercepted"),
        ("PlayerEvent.Clone", "inventory restoration is bound to player cloning"),
        ("isWasDeath()", "non-death clones are excluded"),
        ("PlayerEvent.PlayerRespawnEvent", "respawn routing runs after player respawn"),
        ("ThresholdPartygoerEntity", "the special rule is scoped to Partygoer damage"),
        ("getPersistentData()", "inventory snapshot and one-shot marker survive entity cloning"),
        ("getInventory().save(new ListTag())", "main inventory, armor and offhand are snapshotted"),
        ("getInventory().load", "the inventory snapshot is restored"),
        ("getInventory().selected", "the selected hotbar slot is preserved"),
        ("event.setCanceled(true)", "Partygoer-caused inventory drops are suppressed"),
        ("ThresholdDimensions.LEVEL_0", "Partygoer victims return to Level 0"),
        ("4.5D, 65.0D, 4.5D", "Partygoer victims return to the original Level 0 entry"),
    ):
        if token not in player_events:
            raise AssertionError(f"THRESHOLD Partygoer respawn contract missing: {label}")
    assert "keepInventory" not in player_events, "Partygoer preservation must not change the global keepInventory gamerule"
    require(r"NeoForge\\.EVENT_BUS\\.register\\(ThresholdPlayerEvents\\.class\\)", mod,
            "Partygoer player event handlers are registered on the game event bus")

    hound_loot = read_json(RESOURCES / "data/threshold/loot_table/entities/hound.json")
    assert hound_loot["type"] == "minecraft:entity"
    assert hound_loot["pools"] == [], "Hound should not drop unrelated survival resources"

    # Model the bounded two-member encounter. One spawn attempt is possible
    # per interval; a 30-second lifetime permits at most two overlapping members.
    spawn_interval = 300
    lifetime = 600
    local_cap = 2
    active_expiries: list[int] = []
    max_active = 0
    for game_time in range(0, 6000):
        active_expiries = [expiry for expiry in active_expiries if expiry > game_time]
        if game_time % spawn_interval == 0 and len(active_expiries) < local_cap:
            active_expiries.append(game_time + lifetime)
        max_active = max(max_active, len(active_expiries))
    assert max_active == local_cap, f"Natural Watcher budget mismatch: {max_active}"
    assert lifetime == spawn_interval * 2
    assert local_cap == 2

    print("THRESHOLD_HORROR_IDENTITY_AND_ENTITY_RULES_PASS watcher_budget=2 lifetime=600 spawn_interval=300 perception_hz=5 memory=120")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
