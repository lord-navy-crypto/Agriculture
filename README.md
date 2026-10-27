# Gugu Gaga Agriculture & Apiaries

A farming, food, beekeeping, marine-plant, and fungi expansion for **Minecraft 1.21.1 / NeoForge 21.1.259+ / Java 21**.

## Current content (v2.7 release candidate)

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

A separate optional Fusion resource pack adds random texture variations to crop-field growth stages, bee habitats, marine plants, and fungi. Its 1,240 stage-model overrides use the Fusion base loader while preserving the original crop/plant model geometry. The regular mod JAR keeps its vanilla-compatible models. It requires [Fusion (Connected Textures)](https://www.curseforge.com/minecraft/mc-mods/fusion-connected-textures) for NeoForge 1.21.1 on the client.

1. Install the regular Gugu Gaga Agriculture JAR in `mods`.
2. Optionally install the compatible Fusion mod in the client's `mods` folder.
3. Optionally put the generated `gugugaga-fusion-visuals-v2.7.zip` in the client's `resourcepacks` folder and enable it.

The optional Fusion ZIP is generated under `build/distributions/`. Fusion's own mod JAR is never redistributed by this repository.

## Build and validation

Run with Java 21:

```sh
gradle --no-daemon --stacktrace build
python3 qa/validate_release_metadata_v2_7.py build/libs
```

GitHub Actions additionally checks agricultural resources, crop/seed visual audits, Fusion model and atlas consistency, and real NeoForge dedicated-server startup. The visual contact sheets are uploaded as CI artifacts.

**Development status:** v2.7 is an automated-test-verified release candidate, not proof of exhaustive manual Minecraft client rendering checks. Test the base and optional Fusion configurations before public distribution.

## References

- [Changelog](CHANGELOG.md)
- [Fusion visual overhaul](docs/FUSION_VISUAL_OVERHAUL_V2_4.md)
- [Crop and seed icon visual audit](docs/AGRICULTURE_CROP_ICON_DEPTH_V2_5.md)
- [Chinese player guide](docs/AGRICULTURE_PLAYER_GUIDE_ZH_CN.md)
