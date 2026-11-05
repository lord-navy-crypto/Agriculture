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
    require(r"if \(roll < 30\) return 0", layout,
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

    # Level 0's observer is deliberately harmless and transient.
    for token, label in (
        ("setNoAi(true)", "observer has no AI"),
        ("setNoGravity(true)", "observer does not fall"),
        ("setInvulnerable(true)", "observer cannot be damaged"),
        ("noPhysics = true", "observer has no physical collision"),
        ("public boolean isPickable()", "observer cannot be selected or hit"),
        ("public boolean isPushable()", "observer cannot push or be pushed"),
        ("public boolean isAttackable()", "observer is not attackable"),
        ("directlyNoticed", "observer disappears when directly noticed"),
        ("Mth.rotLerp", "observer turns toward the player without pathfinding"),
        ("setInvisible(true)", "observer has a rare visual dropout"),
        ("nextFlickerTick", "observer flicker interval is bounded"),
    ):
        if token not in watcher:
            raise AssertionError(f"Level 0 observer safety invariant missing: {label}")
    assert "MeleeAttackGoal" not in watcher
    assert "NearestAttackableTargetGoal" not in watcher
    for token, source, label in (
        ("MAX_LIFETIME_TICKS = 180", watcher, "Watcher lifetime is hard-bounded to nine seconds"),
        ("PERCEPTION_INTERVAL_TICKS = 2", watcher, "Watcher perception runs at a bounded 10 Hz cadence"),
        ("if (tickCount % PERCEPTION_INTERVAL_TICKS != 0) return;", watcher,
         "Watcher skips alternate expensive perception ticks"),
        ("updateFlicker()", watcher, "Watcher flicker is updated independently of perception"),
        ("updateInterval(4)", entities, "Watcher transform synchronization is reduced to five updates per second"),
    ):
        if token not in source:
            raise AssertionError(f"THRESHOLD Watcher performance contract missing: {label}")
    watcher_tick = watcher[watcher.index("public void tick()"):watcher.index("public boolean isPickable()")]
    assert "setNoAi(true)" not in watcher_tick, "Watcher must not reassert static AI flags every tick"
    assert "setNoGravity(true)" not in watcher_tick, "Watcher must not reassert static gravity flags every tick"
    assert "noPhysics = true" not in watcher_tick, "Watcher must not reassert static physics flags every tick"
    require(r"DIRECT_NOTICE_GRACE_CHECKS = 3", watcher,
            "Watcher vanishes within 0.3 seconds after being directly noticed")
    require(r"directlyObservedTicks >= DIRECT_NOTICE_GRACE_CHECKS", watcher,
            "Watcher only vanishes after a sustained direct sighting")
    assert "if (directlyNoticed) discard();" not in watcher, "Watcher must not vanish on the first look frame"
    require(r"SHOWCASE_LIFETIME_TICKS = 600", watcher,
            "Operator preview has a 30-second inspection window")
    require(r"enableShowcaseMode\(\)", commands,
            "Summon command enables a stable visual preview")
    require(r"findWatcherShowcasePosition\(level, player\)", commands,
            "Summon command places the preview in front of the player")
    require(r"player\.getLookAngle\(\)\.dot\(toSpawn\) > 0\.35D", director,
            "Natural Watcher spawns outside the player's forward view")
    require(r"LightTexture\.FULL_BRIGHT", watcher_renderer,
            "Watcher silhouette remains visible in dark rooms")
    assert "Attributes.ATTACK_DAMAGE" not in watcher

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
    require(r"new InvestigateLastKnownPositionGoal\(this\)", hound,
            "Hound investigates the last known position after losing sight of a player")
    require(r"getTarget\(\) instanceof Player target && hasLineOfSight\(target\)", hound,
            "Hound updates its search memory only while it can see the player")
    require(r"lastKnownPlayerPosition", hound,
            "Hound retains a short-lived last-known player position")
    require(r"searchTicks", hound, "Hound search memory expires after a bounded interval")
    require(r"lastKnown\.x \+ Math\.cos\(angle\) \* radius", hound,
            "Hound searches around rather than standing still at the last known position")
    require(r"new StalkPlayerGoal\(this\)", hound,
            "Hound stalks quiet players before committing to an attack")
    require(r"horizontalDistanceSqr\(\) > 0\.025D", hound,
            "Hound detects fast player movement as a noise cue")
    require(r"candidate\.isSprinting\(\)", hound,
            "Sprinting escalates the Hound from stalking to pursuit")
    require(r"hound\.getNavigation\(\)\.moveTo\(player, 0\.68D\)", hound,
            "Hound approaches quietly at a slower stalking speed")
    require(r"FlashlightItem\.isActive\(candidate\)", hound,
            "Hound does not acquire a player actively using the flashlight")
    require(r"FlashlightItem\.isActive\(illuminated\)", hound,
            "Hound retreats from an actively used flashlight")
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

    # Dimension guardrails: Level 0 admits only the harmless watcher and players; Hounds
    # are rejected outside Level 1 and are not spawned in the entry shelter.
    require(r"event\.getEntity\(\)\s+instanceof\s+LivingEntity\s+living", director,
            "Level 0 rejects every non-player living creature")
    require(r"!\(living\s+instanceof\s+Player\)", director,
            "Level 0 still allows players to traverse")
    require(r"!\(living\s+instanceof\s+ThresholdWatcherEntity\)", director,
            "Level 0 allows only its observer creature")
    require(r"event\.getEntity\(\)\s+instanceof\s+Projectile", director,
            "Level 0 rejects hostile projectiles")
    require(r"event\.getEntity\(\)\s+instanceof\s+ThresholdHoundEntity", director,
            "Hound dimension is explicitly restricted")
    require(r"level\.dimension\(\)\.equals\(ThresholdDimensions\.LEVEL_1\)\s+&&\s+event\.getEntity\(\)\s+instanceof\s+LivingEntity\s+living\s+&&\s+!\(living\s+instanceof\s+Player\)\s+&&\s+!\(living\s+instanceof\s+ThresholdHoundEntity\)", director,
            "Level 1 admits only players and authored Threshold creatures in this first pass")
    require(r"spawnObservers\(level\)", director, "Level 0 observer spawner exists")
    require(r"spawnHounds\(level\)", director, "Level 1 Hound spawner exists")
    require(r"quiet stalking state; noise and proximity trigger pursuit", director,
            "Hound spawns into its stalking state rather than receiving an instant target")
    for token, source, label in (
        ("WATCHER_SPAWN_INTERVAL_TICKS = 300", director, "Observer spawn cadence remains 15 seconds"),
        ("WATCHER_NEARBY_RADIUS = 42.0D", director, "Observer sightings retain local encounter spacing"),
        ("gameTime % WATCHER_SPAWN_INTERVAL_TICKS == 0L", director, "Observer sightings use the bounded cadence"),
        ("hasNearbyWatcher(level, player)", director, "Natural spawns cannot stack Watchers around one player"),
        ("hasNearbyWatcher(level, player)", commands, "Operator summons share the same local Watcher cap"),
        ("if (!level.addFreshEntity(watcher))", commands, "Operator summon reports rejected duplicates rather than false success"),
    ):
        if token not in source:
            raise AssertionError(f"THRESHOLD Watcher budget contract missing: {label}")
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
                         ("case 12 ->", "Level 0 connected sub-floor annex")):
        if token not in l0_architecture:
            raise AssertionError(f"THRESHOLD Level 0 architecture missing: {label}")
    for token, label in (("case 11 ->", "Level 1 freight bays"),
                         ("case 12 ->", "Level 1 parking circulation"),
                         ("case 13 ->", "Level 1 concrete garden"),
                         ("case 14 ->", "Level 1 electrical switchgear"),
                         ("case 15 ->", "Level 1 warehouse micro-maze"),
                         ("case 16 ->", "Level 1 sunken maintenance trench")):
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
            },
            "hound": {
                "animation.threshold_hound.idle",
                "animation.threshold_hound.walk",
                "animation.threshold_hound.run",
                "animation.threshold_hound.sniff",
                "animation.threshold_hound.alert",
                "animation.threshold_hound.stalk",
                "animation.threshold_hound.retreat",
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

    hound_loot = read_json(RESOURCES / "data/threshold/loot_table/entities/hound.json")
    assert hound_loot["type"] == "minecraft:entity"
    assert hound_loot["pools"] == [], "Hound should not drop unrelated survival resources"

    # Model the natural spawn schedule over five minutes. Each spawn pass
    # returns immediately after one successful Watcher, and its lifetime is
    # shorter than the next pass, so natural active count cannot exceed one.
    spawn_interval = 300
    lifetime = 180
    active_expiries: list[int] = []
    max_active = 0
    for game_time in range(0, 6000):
        active_expiries = [expiry for expiry in active_expiries if expiry > game_time]
        if game_time % spawn_interval == 0:
            active_expiries.append(game_time + lifetime)
        max_active = max(max_active, len(active_expiries))
    assert max_active == 1, f"Natural Watcher budget exceeded: {max_active}"
    assert lifetime < spawn_interval

    print("THRESHOLD_HORROR_IDENTITY_AND_ENTITY_RULES_PASS watcher_budget=1 lifetime=180 spawn_interval=300 perception_hz=10")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
