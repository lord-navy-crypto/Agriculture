# Gugu Gaga Agriculture & Apiaries

A farming, food, beekeeping, marine-plant, and fungi expansion for **Minecraft 1.21.1 / NeoForge 21.1.259+ / Java 21**.

## Current content (v2.7.0-rc.2 single-JAR release candidate)

- **100 plantable crops**, each with dedicated seed and harvested produce items, crop-specific silhouettes, growth textures, and recipes.
- Eight growth-stage textures per crop plus four pollen-marked textures for reproductive stages.
- **100 wild seed sources** that spawn naturally in compatible Overworld biomes.
- **50 honey varieties** with distinct floral sources and custom bottle graphics.
- **68 crop-cuisine items** with recipes and tailored, reproducible icons.
- Marine farming, edible sea plants, and fungal cultures with dedicated textures and growth-stage visuals.
- Distinct pollinator and beekeeping content, including four placeable bee habitat designs.
- Four organized Creative Mode tabs: Agriculture, Beekeeping, Cuisine, and Marine & Fungi.
- English and Chinese item names.

## Optional Fusion visual enhancement

The base mod uses standard Minecraft PNG and JSON resources and **does not require Fusion**.

The **single Agriculture JAR** now embeds its entire Fusion visual pack as `resourcepacks/gugugaga_fusion/`, including all 1,240 stage-model overrides. Those models preserve the original crop/plant geometry and textures and are **only activated when compatible Fusion is installed** on the client. Without Fusion, the standard Minecraft models are used.

1. Install `gugugaga-2.7.0-rc.2.jar` in the client's `mods` folder (and on the server if applicable).
2. Optional: install compatible [Fusion (Connected Textures)](https://www.curseforge.com/minecraft/mc-mods/fusion-connected-textures) for NeoForge 1.21.1 in the client's `mods` folder. The bundled graphics automatically activate.
3. **No extra resource pack ZIP or manual selection is needed.**

The developer build still exports `build/distributions/gugugaga-fusion-visuals-v2.7.zip` for independent pack testing; players do not install it. Fusion itself is never redistributed in our JAR, and remains an optional client-only dependency.

## Build and validation

Run with Java 21:

```sh
gradle --no-daemon --stacktrace build
python3 qa/validate_release_metadata_v2_7.py build/libs
```

GitHub Actions additionally checks agricultural resources, crop/seed visual audits, exact embedded Fusion pack/JAR content, Fusion model and atlas consistency, and real NeoForge dedicated-server startup. The visual contact sheets are uploaded as CI artifacts.

**Development status:** v2.7 is an automated-test-verified release candidate, not proof of exhaustive manual Minecraft client rendering checks. Test the base and optional Fusion configurations before public distribution.

## References

- [Changelog](CHANGELOG.md)
- [Fusion visual overhaul](docs/FUSION_VISUAL_OVERHAUL_V2_4.md)
- [Crop and seed icon visual audit](docs/AGRICULTURE_CROP_ICON_DEPTH_V2_5.md)
- [Chinese player guide](docs/AGRICULTURE_PLAYER_GUIDE_ZH_CN.md)
