#!/usr/bin/env python3
"""Validate four-frame kitchen sprites and bounded ecology visual cues (v3.11).

Reads the actual built JAR, not just source path declarations. No third-party
Python packages are required.
"""
import json
import sys
import zipfile
from pathlib import Path

from validate_fusion_render_variants import png_pixels

ROOT = Path(__file__).resolve().parents[1]
BASE = ROOT / "src/main/resources/assets/gugugaga/models/block"
GENERATED = ROOT / "build/generated/resources/kitchenMotionV311/assets/gugugaga/textures/block/kitchen_motion"
REPORT = ROOT / "build/reports/agriculture/kitchen-motion-v311-contact-sheet.png"
JAVA = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
IDS = ("dough_bench", "skillet_station", "bamboo_steamer")

# These are proof-of-mechanism source checks, not a substitute for real client QA.
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

def main():
    assert len(sys.argv) == 2, "usage: validate_visual_animation_v311.py <jar>"
    jar_path = Path(sys.argv[1])
    assert jar_path.is_file(), jar_path
    generator = (ROOT / "tools/kitchen-motion-v311.gradle").read_text(encoding="utf-8")
    gradle = (ROOT / "build.gradle").read_text(encoding="utf-8")
    assert "apply from: 'tools/kitchen-motion-v311.gradle'" in gradle
    assert "dependsOn tasks.named('generateKitchenMotionV311')" in generator
    assert "outputs.dir(motionAssets)" in generator
    assert png_pixels(REPORT.read_bytes())[:2] == (256, 192), "invalid contact sheet"
    with zipfile.ZipFile(jar_path) as packaged:
        assert packaged.testzip() is None, "invalid packaged JAR"
        for id in IDS:
            path = f"assets/gugugaga/textures/block/kitchen_motion/{id}.png"
            source = GENERATED / (id + ".png")
            meta_path = source.with_suffix(".png.mcmeta")
            atlas = source.read_bytes()
            meta = meta_path.read_bytes()
            assert packaged.read(path) == atlas, id + " missing/corrupted JAR texture"
            assert packaged.read(path + ".mcmeta") == meta, id + " missing/corrupted JAR metadata"
            width, height, pixels = png_pixels(atlas)
            assert (width, height) == (16, 64), (id, width, height)
            frames = []
            for frame in range(4):
                start = frame * 16 * 16 * 4
                tile = pixels[start:start + 16 * 16 * 4]
                assert len(tile) == 1024 and all(tile[i] == 255 for i in range(3, len(tile), 4)), id
                frames.append(tile)
            assert len(set(frames)) == 4, id + ": animation has duplicate frames"
            data = read_json(meta_path)
            assert data == {"animation": {
                "frametime": 2, "interpolate": False, "frames": [0, 1, 2, 3]
            }}, id
            active = read_json(BASE / (id + "_active.json"))
            idle = read_json(BASE / (id + ".json"))
            assert active["textures"]["motion"] == f"gugugaga:block/kitchen_motion/{id}"
            assert any(element["faces"].get("up", {}).get("texture") == "#motion"
                       for element in active["elements"]), id + ": animation not visible on work surface"
            assert "motion" not in idle["textures"], id + ": idle station must remain static"
    for filename, markers in ECOLOGY.items():
        source = (JAVA / filename).read_text(encoding="utf-8")
        assert "public void animateTick(" in source, filename
        for marker in markers:
            assert marker in source, (filename, marker)
    print("VISUAL_ANIMATION_V311_PASS: 3 x 4 distinct animated sprite frames, "
          "metadata, visible work surfaces, exact JAR bytes, 5 bounded ecology "
          "visual cues, and a 256x192 review contact sheet.")

if __name__ == "__main__":
    main()
