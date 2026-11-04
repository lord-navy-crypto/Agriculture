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
    lights = (JAVA / "block/FlickeringPanelBlock.java").read_text(encoding="utf-8")
    vent = (JAVA / "worldgen/ThresholdIndustrialVentFeature.java").read_text(encoding="utf-8")
    mod = (JAVA / "ThresholdMod.java").read_text(encoding="utf-8")
    entities = (JAVA / "entity/ThresholdEntities.java").read_text(encoding="utf-8")
    watcher = (JAVA / "entity/ThresholdWatcherEntity.java").read_text(encoding="utf-8")
    hound = (JAVA / "entity/ThresholdHoundEntity.java").read_text(encoding="utf-8")
    director = (JAVA / "entity/ThresholdEntityDirector.java").read_text(encoding="utf-8")
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
    require(r"Each Level 1 room chooses one deterministic exit",
            layout, "Level 1 escape invariant remains documented")

    # The two lighting systems retain different failure and recovery profiles.
    require(r"int offChance\s*=\s*level0\s*\?\s*16\s*:\s*10\s*;", lights,
            "Level 0 and Level 1 retain distinct light-failure rates")
    require(r"int restoreChance\s*=\s*level0\s*\?\s*2\s*:\s*4\s*;", lights,
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
    ):
        if token not in watcher:
            raise AssertionError(f"Level 0 observer safety invariant missing: {label}")
    assert "MeleeAttackGoal" not in watcher
    assert "NearestAttackableTargetGoal" not in watcher
    assert "Attributes.ATTACK_DAMAGE" not in watcher

    # Level 1's Hound is a real but bounded hostile encounter.
    require(r"extends\s+Monster", hound, "Hound is a real hostile mob")
    require(r"new\s+MeleeAttackGoal", hound, "Hound has melee behavior")
    require(r"new\s+NearestAttackableTargetGoal<>\(this,\s*Player\.class", hound,
            "Hound can acquire players as targets")
    require(r"Attributes\.ATTACK_DAMAGE", hound, "Hound has explicit attack tuning")
    require(r"Attributes\.FOLLOW_RANGE", hound, "Hound has bounded follow range")

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
    require(r"level\.dimension\(\)\.equals\(ThresholdDimensions\.LEVEL_1\)\s+&&\s+event\.getEntity\(\)\s+instanceof\s+Mob\s+mob\s+&&\s+!\(mob\s+instanceof\s+ThresholdHoundEntity\)", director,
            "Level 1 admits only authored Threshold mobs in this first pass")
    require(r"spawnObservers\(level\)", director, "Level 0 observer spawner exists")
    require(r"spawnHounds\(level\)", director, "Level 1 Hound spawner exists")
    require(r"playerRoomX\s*==\s*0\s*&&\s*playerRoomZ\s*==\s*0", director,
            "Level 1 starter shelter is protected from Hound spawns")
    require(r"getMaxLocalRawBrightness\(pos\)\s*>\s*9", director,
            "Hounds prefer darker industrial areas")
    require(r"floor\.getCollisionShape\(level,\s*pos\.below\(\)\)\.isEmpty\(\)", director,
            "observer can stand on Level 0 carpet without requiring a full-cube floor")

    # GeckoLib geometry/animation and the compact dark pixel textures ship in
    # the independent mod JAR; no external model pack is required.
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
        png = texture_path.read_bytes()
        assert png[:8] == b"\x89PNG\r\n\x1a\n", f"{name} texture is not PNG"
        width, height = struct.unpack(">II", png[16:24])
        assert (width, height) == (64, 64), f"{name} texture dimensions are wrong"

    hound_loot = read_json(RESOURCES / "data/threshold/loot_table/entities/hound.json")
    assert hound_loot["type"] == "minecraft:entity"
    assert hound_loot["pools"] == [], "Hound should not drop unrelated survival resources"

    print("THRESHOLD_HORROR_IDENTITY_AND_ENTITY_RULES_PASS")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
