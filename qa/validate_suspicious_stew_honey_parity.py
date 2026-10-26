#!/usr/bin/env python3
"""Verify exactly matching flower, effect and duration for Java 1.21.1 stews."""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SRC = ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
DATA = ROOT/"src/main/resources/data/gugugaga/agronomy/honey_sources_v0_9.json"

# Java Edition 1.21.1, BEFORE the 1.21.4 duration rebalance.
VANILLA = {"dandelion":["SATURATION",7],"poppy":["NIGHT_VISION",100],"allium":["FIRE_RESISTANCE",80],"cornflower":["JUMP",120],"azure_bluet":["BLINDNESS",160],"blue_orchid":["SATURATION",7],"lily_of_the_valley":["POISON",240],"oxeye_daisy":["REGENERATION",160],"torchflower":["NIGHT_VISION",100],"red_tulip":["WEAKNESS",180],"orange_tulip":["WEAKNESS",180],"white_tulip":["WEAKNESS",180],"pink_tulip":["WEAKNESS",180],"wither_rose":["WITHER",160]}

def main():
    meta = json.loads(DATA.read_text("utf8"))
    variants = {row["source_crop"].removeprefix("minecraft:"): row
                for row in meta["honey_variants"]
                if row["source_crop"].startswith("minecraft:")}
    assert set(variants) == set(VANILLA)
    bee = (SRC/"BeeContent.java").read_text("utf8")
    nectar = (SRC/"NectarHoneySource.java").read_text("utf8")
    agriculture = (SRC/"AgricultureContent.java").read_text("utf8")
    poison = (SRC/"PoisonFlowerHoneyBottleItem.java").read_text("utf8")
    for name, (effect, ticks) in VANILLA.items():
        entry = variants[name]
        assert entry["gameplay_effect"] == effect, name
        assert entry["effect_duration_ticks"] == ticks, name
        assert entry["source_of_effect"] == "minecraft_java_1_21_1_suspicious_stew"
        assert entry["item"] == name+"_honey_bottle"
        assert f'"minecraft:{name}"' in nectar
        assert f'floralHoney("{name}_honey_bottle")' in bee
        assert f'event.accept(BeeContent.{name.upper()}_HONEY_BOTTLE.get())' in agriculture
        if name == "lily_of_the_valley":
            assert "PoisonFlowerHoneyBottleItem" in bee
            assert "super.finishUsingItem(stack, level, consumer)" in poison
            assert "consumer.addEffect(new MobEffectInstance(MobEffects.POISON, 240, 0))" in poison
        else:
            assert (f'new MobEffectInstance(MobEffects.{effect}, {ticks}, 0)' in bee), name
    assert "SOURCE_INDEX.get(identifier)" in nectar
    assert "otherFlowers++" in nectar
    assert "otherFlowerKinds.add(identifier)" in nectar
    assert "dominantCount < 6" in nectar
    print(f"STEW_HONEY_PARITY_OK: {len(VANILLA)} exact vanilla flowers, correct effect durations and poison handling")

if __name__ == "__main__":
    main()
