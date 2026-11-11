#!/usr/bin/env python3
"""Guard Level 2-5 themed architecture against transit and save regressions."""
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
WORLDGEN = ROOT / "threshold/src/main/java/io/github/lordnavycrypto/threshold/worldgen"
FEATURE = (WORLDGEN / "ThresholdRoomLayoutFeature.java").read_text()
THEMES = (WORLDGEN / "ThresholdDeepLevelArchitectureV49.java").read_text()
MASS = (WORLDGEN / "ThresholdDeepLevelMassV48.java").read_text()

def main():
    for level in range(2, 6):
        assert re.search(
            rf"buildLevel{level}Chunk\(world, chunkX, chunkZ\);\s+"
            rf"ThresholdDeepLevelMassV48.build\(world, chunkX, chunkZ, {level}\);\s+"
            rf"ThresholdDeepLevelArchitectureV49.build\(world, chunkX, chunkZ, {level}\);",
            FEATURE
        ), level

    assert "ThresholdDeepLevelArchitectureV49.build(world, chunkX, chunkZ, 0)" not in FEATURE
    assert "ThresholdDeepLevelArchitectureV49.build(world, chunkX, chunkZ, 1)" not in FEATURE
    for m in ("utilityHalls(", "electricalStation(", "officeComplex(", "terrorHotel("):
        assert THEMES.count(m) >= 2, m
    for m in ("case 2 -> utilityHalls", "case 3 -> electricalStation",
              "case 4 -> officeComplex", "case 5 -> terrorHotel"):
        assert m in THEMES, m

    # Unmodified floors / ceiling / dimension paths and old nutrition code.
    assert "world.getBlockState(pos).isAir()" in THEMES
    assert "world.ensureCanWrite(pos)" in THEMES
    assert "Math.floorDiv(x, size)" in THEMES and "Math.floorMod(z, size)" in THEMES
    assert "Math.abs(lx - size / 2) <= 4" in THEMES
    assert "if (entry) return" in THEMES
    assert "int landing = lz <= 10 ? 64 + lz - 6 : 68" in THEMES
    assert "Service catwalk spans the central cross at Y=68" in THEMES
    assert "The Beverly balcony is deliberately continuous across the center" in THEMES
    assert "FOUNDATION_BOTTOM = 58" in MASS and "ROOF_TOP = 75" in MASS
    assert "net.minecraft.world.level.block.Blocks" in THEMES
    assert "world.setBlock(pos, state, 2)" in THEMES
    # New themes must not replace existing base layer or ceiling with overlays.
    assert not re.search(r"\bair\(world,\s*pos,\s*x,\s*(?:63|71),", THEMES)
    assert all(keyword.lower() in THEMES.lower() for keyword in
        ("steel arches", "transformer", "cubicle", "ballroom balcony",
         "boiler", "catwalk", "ceiling grid"))
    print("THRESHOLD_LEVEL2_5_THEME_V49_PASS: four distinct room grammars, central passage buffers, entry protection")

if __name__ == "__main__":
    main()
