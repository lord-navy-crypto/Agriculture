#!/usr/bin/env python3
"""Validate that floral-honey data, item effects, and forage routing stay aligned."""
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "src/main/resources"
SRC = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"


def read_resource(path):
    return json.loads((RES / path).read_text(encoding="utf-8"))


def main():
    data = read_resource("data/gugugaga/agronomy/honey_sources_v0_9.json")
    bee = (SRC / "BeeContent.java").read_text(encoding="utf-8")
    source_java = (SRC / "NectarHoneySource.java").read_text(encoding="utf-8")
    poison_java = (SRC / "PoisonFlowerHoneyBottleItem.java").read_text(encoding="utf-8")
    en = read_resource("assets/gugugaga/lang/en_us.json")
    zh = read_resource("assets/gugugaga/lang/zh_cn.json")

    variants = data["honey_variants"]
    items = [row["item"] for row in variants]
    assert len(items) == 30 and len(set(items)) == 30

    switch_body = bee.split('private static DeferredItem<HoneyBottleItem> floralHoney(String name)', 1)[1]
    switch_body = switch_body.split("return ITEMS.register(name", 1)[0]
    cases = {
        match.group(1): match.group(2)
        for match in re.finditer(
            r'case\s+"([a-z0-9_]+_honey_bottle)"\s*->(.*?)(?=\n\s*case\s+"|\n\s*default\s*->)',
            switch_body,
            re.S,
        )
    }
    assert set(cases) == set(items)

    for row in variants:
        item = row["item"]
        body = cases[item]
        nutrition = re.search(r"\.nutrition\((\d+)\)", body)
        saturation = re.search(r"\.saturationModifier\(([0-9.]+)F\)", body)
        assert nutrition and int(nutrition.group(1)) == row["food_nutrition"], item
        assert saturation and abs(float(saturation.group(1)) - row["saturation_modifier"]) < 1e-6, item

        effect = re.search(
            r"new MobEffectInstance\(MobEffects\.([A-Z_]+),\s*(\d+),\s*0\)",
            body,
        )
        if item == "lily_of_the_valley_honey_bottle":
            effect = re.search(
                r"new MobEffectInstance\(MobEffects\.([A-Z_]+),\s*(\d+),\s*0\)",
                poison_java,
            )
        if row["gameplay_effect"] == "NONE":
            assert effect is None and row["effect_duration_ticks"] == 0, item
        else:
            assert effect, item
            assert effect.group(1) == row["gameplay_effect"], item
            assert int(effect.group(2)) == row["effect_duration_ticks"], item

        assert f'floralHoney("{item}")' in bee
        assert f"item.gugugaga.{item}" in en and f"item.gugugaga.{item}" in zh

    # Source identifiers in data must match the exact identifiers scanned at harvest.
    source_block = source_java.split("private static final String[] SOURCES = {", 1)[1].split("};", 1)[0]
    routed_sources = set(re.findall(r'"([^"]+)"', source_block))
    expected_sources = {
        row["source_crop"] for row in variants
        if row["source_crop"] != "mixed_flower_forage"
    }
    assert routed_sources == expected_sources
    assert len(routed_sources) == 29

    # Every item needs an explicit route from the dominant source or mixed-forage branch.
    for item in items:
        constant = item.removesuffix("_honey_bottle").upper() + "_HONEY_BOTTLE"
        assert f"BeeContent.{constant}.get()" in source_java, item

    vanilla_flower_items = {
        "dandelion_honey_bottle", "poppy_honey_bottle", "allium_honey_bottle",
        "cornflower_honey_bottle", "azure_bluet_honey_bottle", "blue_orchid_honey_bottle",
        "lily_of_the_valley_honey_bottle", "oxeye_daisy_honey_bottle", "torchflower_honey_bottle",
        "red_tulip_honey_bottle", "orange_tulip_honey_bottle", "white_tulip_honey_bottle",
        "pink_tulip_honey_bottle", "wither_rose_honey_bottle",
    }
    vanilla_stew_items = {
        row["item"] for row in variants
        if row["source_of_effect"] == "minecraft_java_1_21_1_suspicious_stew"
    }
    assert vanilla_stew_items == vanilla_flower_items
    print("HONEY_SOURCE_CONTRACT_OK: 30 variants; food metadata/effects match code; 29 forage routes; 14 vanilla flower stew effects preserved")


if __name__ == "__main__":
    main()
