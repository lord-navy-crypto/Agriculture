#!/usr/bin/env python3
"""Structural contract checks for the Level 2-5 spatial-depth pass."""
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
WORLDGEN = ROOT / "threshold/src/main/java/io/github/lordnavycrypto/threshold/worldgen"
ENTRY = (WORLDGEN / "ThresholdRoomLayoutFeature.java").read_text("utf-8")
DEPTH = (WORLDGEN / "ThresholdDeepLevelDepthV413.java").read_text("utf-8")

def main():
    assert "public final class ThresholdDeepLevelDepthV413" in DEPTH
    assert "level < 2 || level > 5" in DEPTH
    assert "final int roomSize = level == 4 ? 48 : 32" in DEPTH
    assert "ThresholdRoomLayoutFeature.isReservedRoomPath" in DEPTH
    assert "private static boolean protectedGround" in DEPTH
    assert "roomX == 0 && roomZ == 0" in DEPTH
    # All four pre-authored landmarks remain owned by their existing generators.
    for text in ("x == 2 && z == 1", "x == 1 && z == 2",
                 "x == 1 && z == 1", "x == 2 && z == 0"):
        assert text in DEPTH, text

    # Actual room subdivision: five-block partitions, repeated openings,
    # L-shaped returns, structural piers and equipment bays.
    assert "for (int y = 64; y <= 68; y++)" in DEPTH
    assert "isPartitionX" in DEPTH and "isPartitionZ" in DEPTH
    assert "isReturnPartition" in DEPTH and "isDoorGap" in DEPTH
    assert "placePier" in DEPTH and "placeEquipmentAndFurniture" in DEPTH
    assert "partitionWindow(level)" in DEPTH

    # Vertical circulation is walkable and physically supported.
    assert "shouldHaveMezzanine" in DEPTH
    assert "findDeckPlan" in DEPTH and "canBuildDeck" in DEPTH
    assert "buildMezzanine" in DEPTH
    assert "Blocks.IRON_BARS" in DEPTH and "Blocks.WHITE_STAINED_GLASS_PANE" in DEPTH
    assert "Solid risers keep every stair physically supported." in DEPTH
    assert "Solid side curbs support rails" in DEPTH
    assert "for (int y = 64; y <= 66; y++)" in DEPTH

    # Chunk-local, air-only writes. Original floor, ceiling and transit paths
    # are never cleared or rewritten by this pass.
    assert "world.getBlockState(pos).isAir()" in DEPTH
    assert "world.setBlock(pos, state, 2)" in DEPTH
    assert "y <= FLOOR_Y || y >= CEILING_Y" in DEPTH
    assert "inChunk(x, z, minX, minZ)" in DEPTH
    assert "world.setBlock(pos, Blocks.AIR" not in DEPTH
    assert "ThresholdRoomLayoutFeature.isReservedRoomPath" in DEPTH

    # Integrated after the original room and established v4.8-v4.12 passes.
    for level in range(2, 6):
        block = re.search(
            rf"if \(serverLevel\.dimension\(\)\.equals\(ThresholdDimensions\.LEVEL_{level}\)\) \{{(.*?)return true;",
            ENTRY, re.S)
        assert block, f"Level {level} entry branch"
        body = block.group(1)
        assert f"buildLevel{level}Chunk(world, chunkX, chunkZ)" in body
        assert f"ThresholdDeepLevelDepthV413.build(world, chunkX, chunkZ, {level})" in body
        assert f"ThresholdDeepLevelDiscoveriesV412.build(world, chunkX, chunkZ, {level})" in body
    assert "ThresholdDeepLevelDepthV413.build(world, chunkX, chunkZ, 0)" not in ENTRY
    assert "ThresholdDeepLevelDepthV413.build(world, chunkX, chunkZ, 1)" not in ENTRY

    print("THRESHOLD_LEVEL2_5_DEPTH_V413_PASS: four levels, irregular room subdivisions, supported mezzanines, chunk-local air-only writes, reserved paths and landmark preservation")

if __name__ == "__main__":
    main()
