# Client visual compatibility — Swaying Garden and GeckoLib

## Swaying Garden (first client-side foliage integration)

Gugu-Gaga Agriculture publishes two ordinary Minecraft block tags:

- `#gugugaga:wind_swaying_plants` — 212 blocks: 100 main crops, 100 wild crop plants, 8 additional spice/root crops, and 4 marine plants.
- `#gugugaga:wind_swaying_fungi` — 6 fungal culture blocks.

These tags add no mod dependency and do not change server gameplay. They let a compatible client-side Swaying Garden installation discover Gugu-Gaga blocks by tag instead of requiring 212 individual block IDs.

For Minecraft 1.21.1 / NeoForge, use the matching Swaying Garden 1.21.1 NeoForge build. On the client, open `config/swaying_garden-client.toml` and add the tag strings to the existing target list:

```toml
"#gugugaga:wind_swaying_plants"
"#gugugaga:wind_swaying_fungi"
```

Keep them inside the list already used by your installed Swaying Garden version; do not replace the rest of that file. The effect is client-side shader foliage movement and requires a compatible shader loader and shader pack. It is optional: the mod remains fully playable without Swaying Garden or shaders.

## GeckoLib (next-stage 3D animation)

GeckoLib is a different tool: it provides keyframed, bone-based 3D animation for entities, items and block entities. It is appropriate for articulated kitchen mechanisms, bee wing/body animation and other rigs. For GeckoLib-animated blocks, the architecture generally needs a Block Entity and a dedicated renderer; adding the library alone does not animate existing vanilla baked block models.

The core Gugu-Gaga JAR remains standalone. Do not make GeckoLib a mandatory dependency just to improve crop sway. A separate, optional integration should be prototyped first, beginning with one low-risk workstation and a measured client/server test before expanding to all stations or entities.

## Other render extensions

Shader loaders (Iris-family projects) affect the client's shader pipeline; they are not replacements for GeckoLib and are not required for the mod's base animations. Keep shader-loader compatibility optional and validate the exact NeoForge 1.21.1 build before recommending a client pack.
