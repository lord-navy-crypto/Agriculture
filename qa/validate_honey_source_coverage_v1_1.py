#!/usr/bin/env python3
"""Ensure crop expansion does not silently break mixed-flower honey classification."""
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "src/main/resources"
SOURCE = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/agriculture/NectarHoneySource.java"

PROFILE_FILES = (
    "crop_profiles_v0_4.json",
    "crop_additions_v0_9.json",
    "crop_additions_v1_0.json",
    "crop_additions_v1_3.json",
    "crop_additions_v1_7.json",
    "crop_additions_v1_8.json", "crop_additions_v1_9.json", "crop_additions_v2_0.json", "crop_additions_v2_1.json",
)

def main():
    crop_names = []
    for name in PROFILE_FILES:
        data = json.loads((RES / "data/gugugaga/agronomy" / name).read_text(encoding="utf-8"))
        crop_names.extend(row["crop"] for row in data["crops"])
    assert len(crop_names) == 100 and len(set(crop_names)) == 100
    crop_blocks = {name + "_crop" for name in crop_names}

    source = SOURCE.read_text(encoding="utf-8")
    sources_block = source.split("private static final String[] SOURCES = {", 1)[1].split("};", 1)[0]
    specific_sources = set(re.findall(r'"(?:gugugaga:)?([a-z0-9_]+_crop)"', sources_block))
    exclusions_block = source.split("private static final Set<String> NON_NECTAR_CROPS = Set.of(", 1)[1].split(");", 1)[0]
    non_nectar = set(re.findall(r'"([a-z0-9_]+_crop)"', exclusions_block))

    assert specific_sources <= crop_blocks
    assert non_nectar <= crop_blocks
    assert not (specific_sources & non_nectar)
    assert "SOURCE_INDEX.get(cropResource.toString())" in source
    assert "else if (isOtherNectarCrop(cropId))" in source
    assert "return !NON_NECTAR_CROPS.contains(cropId);" in source

    # The seven new insect-pollinated crop lines now have dedicated honey variants.
    expected_specialty = {
        "cassava_crop", "taro_crop", "yam_crop", "watercress_crop",
        "endive_crop", "kohlrabi_crop", "rutabaga_crop",
        "jicama_crop", "adzuki_bean_crop", "mung_bean_crop", "cowpea_crop",
        "safflower_crop", "chicory_crop", "mustard_greens_crop", "water_spinach_crop",
        "celeriac_crop", "purslane_crop", "chayote_crop", "grape_crop",
        "tea_crop", "coffee_crop", "rapini_crop", "napa_cabbage_crop",
        "melon_crop", "watermelon_crop", "fava_bean_crop", "jute_mallow_crop",
    }
    assert expected_specialty <= specific_sources
    assert "teff_crop" in non_nectar

    generic = crop_blocks - specific_sources - non_nectar
    assert len(specific_sources) == 35
    assert len(non_nectar) == 11
    assert len(generic) == 54
    data = json.loads((RES / "data/gugugaga/agronomy/honey_sources_v1_2.json").read_text(encoding="utf-8"))
    assert "Mature mod crops count as potential nectar forage by default" in data["description"]
    print("HONEY_FORAGE_COVERAGE_V1_2_OK: 100 crops; 35 specific source crops; 11 explicit non-nectar crops; 54 generic nectar/mixed-forage crops")

if __name__ == "__main__":
    main()
