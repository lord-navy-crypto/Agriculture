#!/usr/bin/env python3
"""Validate shipped four-frame work-surface animations and ecology cues (v3.11).

Reads actual built JAR bytes. No third-party Python packages are required.
"""
import json
import sys
import zipfile
from pathlib import Path

from validate_fusion_render_variants import png_pixels

ROOT = Path(__file__).resolve().parents[1]
BASE = ROOT / "src/main/resources/assets/gugugaga/models/block"
GENERATED = ROOT / "build/generated/resources/kitchenMotionV311/assets/gugugaga/textures/block/kitchen_motion"
REPORT = ROOT / "build/reports/agriculture/agriculture-motion-v311-contact-sheet.png"
JAVA = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"

# texture ID -> model file -> which surface binding must be animated
ANIMATIONS = {
    "dough_bench": ("dough_bench_active.json", "element_up"),
    "skillet_station": ("skillet_station_active.json", "element_up"),
    "bamboo_steamer": ("bamboo_steamer_active.json", "element_up"),
    "fermentation_vat_stage1": ("fermentation_vat_stage1.json", "cube_top"),
    "fermentation_vat_stage2": ("fermentation_vat_stage2.json", "cube_top"),
    "fermentation_vat_stage3": ("fermentation_vat_stage3.json", "cube_top"),
    "salt_pan_brine": ("salt_evaporation_pan_stage_1.json", "element_up"),
    "salt_pan_concentrated": ("salt_evaporation_pan_stage_2.json", "element_up"),
    "salt_pan_crystals": ("salt_evaporation_pan_stage_3.json", "element_up"),
}

ECOLOGY = {
    "AgricultureCropBlock.java": (
        "pollinationGroup == PollinationGroup.NONE",
        "state.getValue(POLLINATED)",
        "ParticleTypes.FALLING_NECTAR",
        "random.nextInt(72)",
    ),
    "BeeNestHabitatBlock.java": (
        "state.getValue(ACTIVITY)",
        "ParticleTypes.FALLING_NECTAR",
        "random.nextInt(94 - activity * 16)",
    ),
    "FoodFermentationVatBlock.java": (
        "state.getValue(BATCH) == 0",
        "ParticleTypes.BUBBLE_POP",
        "ParticleTypes.COMPOSTER",
    ),
    "MarinePlantCropBlock.java": (
        "state.getValue(WATERLOGGED)",
        "ParticleTypes.BUBBLE",
        "random.nextInt(85)",
    ),
    "FungalCultureBlock.java": (
        "state.getValue(AGE) < 2",
        "ParticleTypes.SPORE_BLOSSOM_AIR",
        "random.nextInt(95)",
    ),
}

def read_json(path):
    return json.loads(path.read_text(encoding="utf-8"))

def has_animated_surface(model, surface_kind):
    if surface_kind == "cube_top":
        return model.get("textures", {}).get("top") == "#motion"
    return any(element.get("faces", {}).get("up", {}).get("texture") == "#motion"
               for element in model.get("elements", []))

def main():
    assert len(sys.argv) == 2, "usage: validate_visual_animation_v311.py <jar>"
    jar_path = Path(sys.argv[1])
    assert jar_path.is_file(), jar_path
    generator = (ROOT / "tools/kitchen-motion-v311.gradle").read_text(encoding="utf-8")
    gradle = (ROOT / "build.gradle").read_text(encoding="utf-8")
    assert "apply from: 'tools/kitchen-motion-v311.gradle'" in gradle
    assert "dependsOn tasks.named('generateKitchenMotionV311')" in generator
    assert "outputs.dir(motionAssets)" in generator
    assert png_pixels(REPORT.read_bytes())[:2] == (256, 576), "invalid agriculture motion contact sheet"

    with zipfile.ZipFile(jar_path) as packaged:
        assert packaged.testzip() is None, "invalid packaged JAR"
        for texture_id, (model_file, surface_kind) in ANIMATIONS.items():
            texture_path = f"assets/gugugaga/textures/block/kitchen_motion/{texture_id}.png"
            source = GENERATED / (texture_id + ".png")
            meta_path = source.with_suffix(".png.mcmeta")
            atlas, meta = source.read_bytes(), meta_path.read_bytes()
            assert packaged.read(texture_path) == atlas, texture_id + " missing/corrupted JAR texture"
            assert packaged.read(texture_path + ".mcmeta") == meta, texture_id + " missing/corrupted JAR metadata"

            width, height, pixels = png_pixels(atlas)
            assert (width, height) == (16, 64), (texture_id, width, height)
            frames = []
            for frame in range(4):
                start = frame * 16 * 16 * 4
                tile = pixels[start:start + 16 * 16 * 4]
                assert len(tile) == 1024 and all(tile[i] == 255 for i in range(3, len(tile), 4)), texture_id
                frames.append(tile)
            assert len(set(frames)) == 4, texture_id + ": animation has duplicate frames"
            data = read_json(meta_path)
            assert data == {"animation": {
                "frametime": 2, "interpolate": False, "frames": [0, 1, 2, 3]
            }}, texture_id

            model = read_json(BASE / model_file)
            assert model.get("textures", {}).get("motion") == f"gugugaga:block/kitchen_motion/{texture_id}"
            assert has_animated_surface(model, surface_kind), texture_id + ": no visible animated surface"

        # The unoccupied/empty variants intentionally remain static.
        for path in ("fermentation_vat_stage0.json", "salt_evaporation_pan_stage_0.json",
                     "dough_bench.json", "skillet_station.json", "bamboo_steamer.json"):
            model = read_json(BASE / path)
            assert "motion" not in model.get("textures", {}), path + ": idle model unexpectedly animated"

    for filename, markers in ECOLOGY.items():
        source = (JAVA / filename).read_text(encoding="utf-8")
        assert "public void animateTick(" in source, filename
        for marker in markers:
            assert marker in source, (filename, marker)

    print(
        "VISUAL_ANIMATION_V311_PASS: 9 x 4 distinct animated atlases, exact JAR bytes, "
        "surface bindings, static idle states, five bounded ecology visual hooks, "
        "and a 256x576 contact sheet."
    )

if __name__ == "__main__":
    main()
