#!/usr/bin/env python3
"""Contracts for common six-level Watchers, smarter Partygoers and Backrooms creative tabs."""
from pathlib import Path
import json

ROOT = Path(__file__).resolve().parents[1]
JAVA = ROOT / "threshold/src/main/java/io/github/lordnavycrypto/threshold"
RES = ROOT / "threshold/src/main/resources/assets/threshold"


def read(path):
    return (JAVA / path).read_text(encoding="utf8")


def main():
    director = read("entity/ThresholdEntityDirector.java")
    watcher = read("entity/ThresholdWatcherEntity.java")
    party = read("entity/ThresholdPartygoerEntity.java")
    attrs = read("entity/ThresholdEntityAttributes.java")
    items = read("item/ThresholdItems.java")
    blocks = read("block/ThresholdEnvironmentBlocks.java")
    tabs = read("ThresholdCreativeTabs.java")
    mod = read("ThresholdMod.java")

    assert "isThresholdDimension(dimension) && entityType == ThresholdEntities.WATCHER.get()" in director
    assert "WATCHER_SPAWN_INTERVAL_TICKS = 100" in director
    assert "MAX_LOCAL_WATCHERS = 3" in director
    for token in ("case 0 -> 2", "case 1 -> 3", "default -> 5"):
        assert token in director, f"common but capped Watcher spawning missing {token}"

    constructor = watcher.split("public ThresholdWatcherEntity(", 1)[1].split("@Override", 1)[0]
    assert "ThresholdDimensions.LEVEL_0" in constructor
    assert "setBehaviorMode(BehaviorMode.AGGRESSIVE)" in constructor
    assert "capturePlayer(player)" in watcher
    assert "destination, 4.5D, 65.0D, 4.5D" in watcher
    assert ".add(Attributes.MAX_HEALTH, 22.0D)" in attrs
    assert ".add(Attributes.MOVEMENT_SPEED, 0.34D)" in attrs

    for token in ("ACQUIRE_RANGE = 56.0D", "COMMIT_RANGE_SQR = 144.0D",
                  "sightMemoryTicks = 160", "lostSightTicks > 45",
                  "alertNearbyPack(player)", "private void alertNearbyPack",
                  "lastVisiblePlayerPosition", "private final double flankSign",
                  "partygoer.getNavigation().moveTo(flank.x, flank.y, flank.z, speed)"):
        assert token in party, f"Partygoer upgrade missing {token}"
    for token in ("MAX_HEALTH, 42.0D", "MOVEMENT_SPEED, 0.35D",
                  "ATTACK_DAMAGE, 7.0D", "FOLLOW_RANGE, ACQUIRE_RANGE"):
        assert token in party, f"Partygoer attribute upgrade missing {token}"

    for token in ("WATCHER_SPAWN_EGG", "HOUND_SPAWN_EGG", "PARTYGOER_SPAWN_EGG"):
        assert token in items, f"Spawn egg registration missing {token}"
    for token in ("ITEMS.registerSimpleBlockItem(LEVEL_1_TO_2)",
                  "ITEMS.registerSimpleBlockItem(LEVEL_2_FIRE_EXIT)",
                  "ITEMS.registerSimpleBlockItem(LEVEL_2_TO_3)",
                  "ITEMS.registerSimpleBlockItem(LEVEL_2_BRANCH_DOOR)",
                  "ITEMS.registerSimpleBlockItem(LEVEL_3_TO_2)",
                  "ITEMS.registerSimpleBlockItem(LEVEL_3_TO_4)",
                  "ITEMS.registerSimpleBlockItem(LEVEL_4_TO_3)",
                  "ITEMS.registerSimpleBlockItem(LEVEL_3_ELEVATOR)",
                  "ITEMS.registerSimpleBlockItem(LEVEL_4_TO_5)",
                  "ITEMS.registerSimpleBlockItem(LEVEL_5_TO_3)",
                  "ITEMS.registerSimpleBlockItem(LEVEL_5_TO_4)",
                  "ITEMS.registerSimpleBlockItem(LEVEL_5_ELEVATOR_PANEL)"):
        assert token in blocks, f"Creative BlockItem registration missing {token}"

    assert "BACKROOMS_BLOCKS" in tabs and "BACKROOMS_ENTITIES" in tabs
    assert "ThresholdEnvironmentBlocks.ITEMS.getEntries()" in tabs
    for token in ("WATCHER_SPAWN_EGG", "HOUND_SPAWN_EGG", "PARTYGOER_SPAWN_EGG",
                  "ALMOND_WATER", "FLASHLIGHT", "BATTERY"):
        assert token in tabs, f"Backrooms entities/supplies tab missing {token}"
    assert "ThresholdCreativeTabs.TABS.register(modBus)" in mod

    for locale in ("en_us.json", "zh_cn.json"):
        data = json.loads((RES / "lang" / locale).read_text(encoding="utf8"))
        for key in ("item.threshold.watcher_spawn_egg", "item.threshold.hound_spawn_egg",
                    "item.threshold.partygoer_spawn_egg", "itemGroup.threshold.backrooms_blocks",
                    "itemGroup.threshold.backrooms_entities"):
            assert data.get(key), f"{locale} missing {key}"

    model_names = (
        "watcher_spawn_egg", "hound_spawn_egg", "partygoer_spawn_egg",
        "level_1_to_2", "level_2_fire_exit", "level_2_to_3", "level_2_branch_door",
        "level_3_to_2", "level_3_to_4", "level_4_to_3", "level_3_elevator",
        "level_4_to_5", "level_5_to_3", "level_5_to_4", "level_5_elevator_panel",
    )
    for name in model_names:
        model = json.loads((RES / "models" / "item" / f"{name}.json").read_text(encoding="utf8"))
        assert model.get("parent"), f"item model missing parent: {name}"

    workflow = (ROOT / ".github/workflows/rewrite-build.yml").read_text(encoding="utf8")
    assert "qa/validate_threshold_entities_creative_v1.py" in workflow
    print("THRESHOLD_ENTITIES_CREATIVE_V1_PASS: six-level Watchers, Level 0 capture, pack Partygoers, spawn eggs and Backrooms tabs")


if __name__ == "__main__":
    main()
