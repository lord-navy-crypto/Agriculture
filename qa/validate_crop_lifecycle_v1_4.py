#!/usr/bin/env python3
"""Whole-catalog crop-cycle contract for all 64 crop lines."""
import json
from pathlib import Path
ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "src/main/resources"
SRC = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
def load(path):
    return json.loads((RES / path).read_text(encoding="utf8"))
def main():
    profiles = [
        load("data/gugugaga/agronomy/crop_profiles_v0_4.json")["crops"],
        load("data/gugugaga/agronomy/crop_additions_v0_9.json")["crops"],
        load("data/gugugaga/agronomy/crop_additions_v1_0.json")["crops"],
        load("data/gugugaga/agronomy/crop_additions_v1_3.json")["crops"],
    ]
    crops = [row["crop"] for group in profiles for row in group]
    assert len(crops) == 64 and len(set(crops)) == 64
    en, zh = load("assets/gugugaga/lang/en_us.json"), load("assets/gugugaga/lang/zh_cn.json")
    reg = (SRC / "AgricultureContent.java").read_text("utf8")
    climate = (SRC / "CropClimate.java").read_text("utf8")
    crop_java = (SRC / "AgricultureCropBlock.java").read_text("utf8")
    generator = (ROOT / "tools/crop-textures.gradle").read_text("utf8")
    compost = load("data/neoforge/data_maps/item/compostables.json")["values"]
    for crop in crops:
        states = load(f"assets/gugugaga/blockstates/{crop}_crop.json")["variants"]
        assert set(states) == {f"age={age},pollinated={p}" for age in range(8) for p in ("false", "true")}, crop
        for age in range(8):
            base = f"gugugaga:block/crops/{crop}_stage{age}"
            assert states[f"age={age},pollinated=false"]["model"] == base, (crop, age)
            expected = base + ("_pollinated" if age >= 4 else "")
            assert states[f"age={age},pollinated=true"]["model"] == expected, (crop, age)
        for item in (crop, crop + "_seeds"):
            model = load(f"assets/gugugaga/models/item/{item}.json")
            assert model["parent"] == "minecraft:item/generated"
            assert model["textures"]["layer0"] == f"gugugaga:item/crops/{item}", item
            assert f"item.gugugaga.{item}" in en and f"item.gugugaga.{item}" in zh, item
            assert f"gugugaga:{item}" in compost, item
        assert f'crop("{crop}_crop", "{crop}_seeds"' in reg, crop
        assert f'"{crop}_seeds"' in climate, crop
        assert load(f"data/gugugaga/recipe/{crop}_seeds.json")["result"]["id"] == f"gugugaga:{crop}_seeds"
        loot = load(f"data/gugugaga/loot_table/blocks/{crop}_crop.json")["pools"]
        assert len(loot) == 2 and loot[0]["entries"][0]["name"] == f"gugugaga:{crop}_seeds"
        assert loot[1]["entries"][0]["name"] == f"gugugaga:{crop}"
        assert f"block.gugugaga.{crop}_crop" in en and f"block.gugugaga.{crop}_crop" in zh
    assert "palettes = [" in generator and "categoryGroups = [" in generator
    assert "cropIds.size() != 64" in generator
    assert "thisCropStageHashes.size() != 8" in generator
    assert "matureTextureHashes.size() != 64" in generator
    assert "allStageHashes.size() != 768" in generator
    assert "tasks.named('processResources')" in generator
    assert "cropSpecificStageDetail.call(image, crop, stage, top, accent, leaf, leafDark, leafLight)" in generator
    detail = generator.split("def cropSpecificStageDetail = {", 1)[1].split("def stageTexture = {", 1)[0]
    # Each of the 64 crops must have both a seedling-specific mark and a mature-form
    # detail branch. Palette-only variation is not accepted as crop-specific artwork.
    for crop in crops:
        assert detail.count(f"case '{crop}':") >= 2, (crop, "missing seedling or mature morphology")
    for name in "tomato cucumber strawberry blueberry bell_pepper eggplant zucchini raspberry cranberry okra chili_pepper green_bean pea broccoli brussels_sprouts cotton".split():
        assert f'"{name}_seeds"' in crop_java, name
    for name in "basil mint arugula swiss_chard parsley collard_greens dill cilantro bok_choy lettuce spinach kale celery".split():
        assert f'"{name}_seeds"' in crop_java, name
    for name in "lavender clover artichoke".split():
        assert f'"{name}_seeds"' in crop_java, name
    assert "level.setBlock(pos, state.setValue(AGE, 4).setValue(POLLINATED, false), 3)" in crop_java
    print("CROP_LIFECYCLE_V1_5_OK: 64 crops; 64 explicit seedling/mature morphology pairs; 8 stage models each; pollen variants; 128 custom item icons; seeds/loot/recipes/translations/compost validated")
if __name__ == "__main__":
    main()
