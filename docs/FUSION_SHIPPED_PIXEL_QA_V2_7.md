# Fusion packaged-art integrity gate

The script qa/validate_fusion_render_variants.py operates on the exact distributable
Gugu Gaga Agriculture JAR and Fusion v2.7 ZIP, not the source PNG files.

It verifies all 1,200 crop, 24 fungal and 16 marine stages. For each 32x32
random atlas, every 16x16 tile must match the Java2D generator's deterministic
mirroring and brightness transform, including alpha and transparent pixels.

The test uses the Python standard library only (ZIP, zlib and PNG filter
decoding). It fails on corrupt PNG chunks, unexpected dimensions, missing
assets, incorrect mirrors, changed alpha shapes, or mismatched variants.

Run after compiling the mod and packaging Fusion:

    python3 qa/validate_fusion_render_variants.py build/libs/gugugaga-*.jar build/distributions/gugugaga-fusion-visuals-v2.7.zip

All base JAR art, gameplay and resource pack contents remain unchanged.
Actual in-client visual acceptance is still necessary before final release.


## Visual refinement v3.11

The Fusion crop, marine-plant and fungal texture atlases retain their documented 2×2 layout and position-based selection. Their four tiles now use deterministic, low-amplitude botanical treatments rather than brightness-only duplication: mirrored silhouettes, restrained channel shifts, and sparse leaf-vein highlight/shadow pixels. Transparent pixels and the original 16×16 alpha silhouette are preserved exactly. The packaged-art QA independently reproduces every output pixel from the base JAR source texture.

This is a client-side visual variation only. It does not alter crop profiles, growth stages, pollination, loot, nutrition, block models, or server behavior.
