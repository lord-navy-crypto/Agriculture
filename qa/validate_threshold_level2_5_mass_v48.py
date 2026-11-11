#!/usr/bin/env python3
"""Guard solid, chunk-local Backrooms shells on Levels 2-5."""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
WORLDGEN = ROOT / "threshold/src/main/java/io/github/lordnavycrypto/threshold/worldgen"
DATA = ROOT / "threshold/src/main/resources/data/threshold/dimension"

def main():
    feature = (WORLDGEN / "ThresholdRoomLayoutFeature.java").read_text()
    mass = (WORLDGEN / "ThresholdDeepLevelMassV48.java").read_text()
    for level in range(2, 6):
        assert (f"buildLevel{level}Chunk(world, chunkX, chunkZ);\n"
                f"            ThresholdDeepLevelMassV48.build(world, chunkX, chunkZ, {level});") in feature
        data = json.loads((DATA / f"level_{level}.json").read_text())
        assert data["generator"]["type"] == "minecraft:flat"
        assert data["generator"]["settings"]["features"] is True
        assert f"threshold:level_{level}" == data["generator"]["settings"]["biome"]
    assert "ThresholdDeepLevelMassV48.build(world, chunkX, chunkZ, 0)" not in feature
    assert "ThresholdDeepLevelMassV48.build(world, chunkX, chunkZ, 1)" not in feature
    assert "FOUNDATION_BOTTOM = 58" in mass and "FOUNDATION_TOP = 62" in mass
    assert "ROOF_BOTTOM = 72" in mass and "ROOF_TOP = 75" in mass
    assert "level == 4 ? 48 : 32" in mass
    assert "Math.floorDiv(x, roomSize)" in mass
    assert "Math.floorMod(z, roomSize)" in mass
    assert "world.ensureCanWrite(pos)" in mass
    assert "world.getBlockState(pos).isAir()" in mass
    assert "roomX != 0 || roomZ != 0" in mass
    assert "for (int y = FOUNDATION_BOTTOM; y <= FOUNDATION_TOP; y++)" in mass
    assert "for (int y = ROOF_BOTTOM; y <= ROOF_TOP; y++)" in mass
    assert "y == 72" in mass
    assert "Blocks.DARK_OAK_PLANKS" in mass
    assert "Blocks.POLISHED_DIORITE" in mass
    assert "Blocks.DEEPSLATE_BRICKS" in mass
    assert "Blocks.GRAY_CONCRETE" in mass
    print("THRESHOLD_LEVEL2_5_MASS_V48_PASS: four independent palettes, thick shells, chunk-local writes, original entries preserved")

if __name__ == "__main__":
    main()
