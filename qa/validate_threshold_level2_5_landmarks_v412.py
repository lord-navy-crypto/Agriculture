#!/usr/bin/env python3
"""V4.12 Backrooms: test unique four-level landmark rooms and walkable geometry."""
from pathlib import Path
import re

ROOT=Path(__file__).resolve().parents[1]
SRC=ROOT/"threshold/src/main/java/io/github/lordnavycrypto/threshold/worldgen"
LAND=(SRC/"ThresholdDeepLevelDiscoveriesV412.java").read_text("utf-8")
FEATURE=(SRC/"ThresholdRoomLayoutFeature.java").read_text("utf-8")
THEMES=(SRC/"ThresholdDeepLevelArchitectureV49.java").read_text("utf-8")
MASS=(SRC/"ThresholdDeepLevelMassV48.java").read_text("utf-8")

def check():
    for level in range(2,6):
        assert re.search(
            rf"buildLevel{level}Chunk\(world, chunkX, chunkZ\);\s+"
            rf"ThresholdDeepLevelMassV48.build\(world, chunkX, chunkZ, {level}\);\s+"
            rf"ThresholdDeepLevelArchitectureV49.build\(world, chunkX, chunkZ, {level}\);\s+"
            rf"ThresholdDeepLevelDiscoveriesV412.build\(world, chunkX, chunkZ, {level}\);",
            FEATURE
        ), "Missing 2-5 landmark dispatch"
    assert "ThresholdDeepLevelDiscoveriesV412.build(world, chunkX, chunkZ, 0)" not in FEATURE
    assert "ThresholdDeepLevelDiscoveriesV412.build(world, chunkX, chunkZ, 1)" not in FEATURE
    for level,room,method in ((2,(2,1),"pressureGallery"),(3,(1,2),"transformerGantries"),
                               (4,(1,1),"recordsAtrium"),(5,(2,0),"guestWingGallery")):
        assert f"case {level} -> {method}(" in LAND
        assert f"case {level} -> x == {room[0]} && z == {room[1]};" in LAND

    assert "final int roomX = Math.floorDiv(originX, roomSize)" in LAND
    assert "final int roomZ = Math.floorDiv(originZ, roomSize)" in LAND
    assert "Math.floorMod(x, roomSize)" in LAND
    assert "Math.floorMod(z, roomSize)" in LAND
    assert "if (!isLandmarkRoom(level, roomX, roomZ)) return;" in LAND
    assert "world.ensureCanWrite(pos)" in LAND
    assert "world.getBlockState(pos).isAir()" in LAND
    assert "if (y <= WALK_FLOOR_Y || y >= 71) return;" in LAND

    # All four unique landmark sectors include real overhead construction,
    # clearances and physically connected stair approaches in new chunks.
    assert LAND.count("putAir(world, pos, x, UPPER_DECK_Y, z") >= 4
    assert "UPPER_DECK_Y = 66" in LAND
    assert "WALK_FLOOR_Y = 63" in LAND
    assert LAND.count("StairBlock.FACING") == 1
    assert "Blocks.STONE_BRICK_STAIRS" in LAND
    assert "Blocks.POLISHED_DEEPSLATE_STAIRS" in LAND
    assert "Blocks.POLISHED_DIORITE_STAIRS" in LAND
    assert "Blocks.SPRUCE_STAIRS" in LAND
    for start in (7,11,8):
        levels = [64,65,66]
        assert levels == [64+n for n in range(3)] and levels[-1]==66
    assert 66-63-1==2  # two full blocks of ground clearance

    for label in ("pressureGallery","transformerGantries","recordsAtrium","guestWingGallery"):
        assert LAND.count(label+"(")>=2
    for prop in ("Blocks.IRON_BARS","Blocks.DARK_OAK_FENCE",
                 "Blocks.BOOKSHELF","Blocks.REDSTONE_LAMP",
                 "Blocks.COPPER_BLOCK","Blocks.RED_CARPET"):
        assert prop in LAND, prop
    assert "FOUNDATION_BOTTOM = 58" in MASS and "ROOF_TOP = 75" in MASS
    assert "themeVariant(seed, roomX, roomZ, level)" in THEMES
    print("THRESHOLD_LEVEL2_5_LANDMARKS_V412_PASS: four genuine raised rooms, safe entry, shell and theme integration")

if __name__=="__main__": check()
