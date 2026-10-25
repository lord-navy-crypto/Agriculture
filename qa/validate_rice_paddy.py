#!/usr/bin/env python3
"""Check the rice paddy growth gate against its documented design metadata."""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "src/main/resources"
RULE = json.loads((ASSETS / "data/gugugaga/agronomy/rice_paddy_rule_v0_4.json").read_text("utf8"))
SOURCE = (ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/agriculture/CropClimate.java").read_text("utf8")
DOC = (ROOT / "docs/REAL_AGRICULTURE_DATA_V0_2.md").read_text("utf8")

assert RULE["model"] == "irrigated_lowland_rice_gameplay_proxy"
assert RULE["requires_normal_farmland"] is True
assert RULE["growth_requires_freshwater"] is True
assert RULE["horizontal_radius_blocks"] == 3
assert RULE["vertical_offsets_from_crop"] == [-1, 0]
assert RULE["searches_only_loaded_chunks"] is True
assert RULE["not_a_universal_rice_rule"] is True
assert '"rice_seeds".equals(seedName) && !hasPaddyWater(level, pos)' in SOURCE
assert "private static boolean hasPaddyWater(ServerLevel level, BlockPos cropPos)" in SOURCE
assert "level.hasChunkAt(candidate)" in SOURCE
assert "FluidTags.WATER" in SOURCE
assert all(url.startswith("https://") for url in RULE["research_sources"])
assert "irri.org" in DOC and "not presented as universal rice physiology" in DOC
print("RICE_PADDY_RULE_OK: water radius, loaded-chunk guard and agronomic caveat match metadata")
