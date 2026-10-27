# Gugu Gaga Agriculture — Fusion Visual Overhaul v2.4

This stage adds a **real optional Fusion companion resource pack**, upgrades the Java2D material generators, and keeps the original NeoForge mod usable without Fusion.

## What is actually supported

- **Minecraft 1.21.1 / NeoForge 21.1.x**.
- Fusion's official *random textures* format and its `fusion:base` model loader, available in the NeoForge 1.21.1-compatible Fusion **1.3.16** release.
- Four already-registered, placeable bee habitat blocks: bumblebee, mason, leafcutter, squash. No fictional storage blocks have been added.
- Eight block model variants (normal + active) override only the original material textures; they preserve the original block geometry, particle fallback and activity blockstates.
- Eight freshly generated 32×32 PNG atlases, each containing four separate 16×16 texture choices. Their matching `.png.mcmeta` files use Fusion's `random` texture type.
- An illustrative generated contact sheet is deposited in `build/reports/agriculture/fusion-habitat-contact-sheet.png`.

## Install

1. Run `gradle build` to build the normal mod **and** the optional pack.
2. Install the ordinary Gugu Gaga Agriculture JAR in the `mods` folder.
3. If you want Fusion effects, install the official **Fusion (Connected Textures)** *NeoForge 1.21.1* client mod separately. Fusion is not bundled or redistributed with this project.
4. Copy `build/distributions/gugugaga-fusion-visuals-v2.4.zip` into the client's `resourcepacks` folder and activate it in Resource Packs.
5. Inspect all four bee habitats, especially active/idle visuals. Without Fusion, simply leave the optional pack disabled and use the regular mod.

The pack is intentionally **not** embedded in the default mod JAR because its Fusion JSON models would not load properly on clients without Fusion. Server installation does not require the companion pack.

## Development

- `gradle generateFusionVisualPack` — produces the pack folder and contact sheet.
- `gradle packageFusionVisualPack` — produces the optional installable resource pack ZIP.
- `gradle build` — assembles the ordinary mod + Fusion pack ZIP.
- `gradle runClient -PenableFusionDev=true` — opt into the official Fusion mod from Modrinth Maven in the dev runtime; no Fusion dependency is resolved in a normal build.

## Quality gates

- Source models must remain byte-for-byte unchanged; resource pack overrides only modify visuals.
- Random texture images must be 32×32 PNG with 2×2 16×16 tiles; model geometry and resource links must resolve.
- Crop, cuisine and honey generators retain their own independent art validation.
- NeoForge build and fresh real-server boot still gate release.
- A headless build does not prove every shader or Fusion client configuration; visual acceptance should use a real 1.21.1 client.

Fusion is by SuperMartijn642. Its external software and code are **not** distributed as part of this mod or the companion resource pack.

Official docs: https://github.com/SuperMartijn642/Fusion/wiki
