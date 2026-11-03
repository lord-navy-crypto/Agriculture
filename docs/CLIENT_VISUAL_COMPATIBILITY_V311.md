# Client visual compatibility — Swaying Garden, GeckoLib and shader loaders

## Swaying Garden — first foliage integration

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

## GeckoLib — active 3D animation integration

The current Gugu-Gaga v3.11 development branch now uses **GeckoLib 4.9.3 for Minecraft 1.21.1 NeoForge** as a required runtime dependency. Install the matching GeckoLib mod on both client and dedicated server.

The existing Dough Bench is the first converted workstation. Its registry ID, recipes, rolling-pin requirement and server-owned ACTIVE state are preserved; only its in-world renderer uses a GeckoLib Block Entity and GeoBlockRenderer. See `docs/GECKOLIB_INTEGRATION_V311.md` for installation and acceptance checks.

GeckoLib-animated blocks require a Block Entity and a dedicated renderer; adding the library alone does not animate existing vanilla baked block models. Further conversions will be staged one workstation or entity at a time, with dedicated-server and real-client checks before expansion.

## Oculus / Iris-family shader loaders

If “OLAP” meant Oculus: shader loaders are not 3D animation libraries. They affect the client's shader pipeline. The original Oculus project is not the correct version target for this 1.21.1 NeoForge project; use a loader explicitly built for 1.21.1, such as the compatible Iris/NeoForge line or a matching NeOculus fork, and test the exact shader pack. Do not bundle a shader loader into the Agriculture JAR.

Keep all shader-loader compatibility optional. Swaying Garden, GeckoLib and shader loaders solve different problems and should not be treated as interchangeable dependencies.
