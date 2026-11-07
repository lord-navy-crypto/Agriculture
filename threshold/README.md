# Backroom Agriculture（后室农业）— Backrooms subsystem

The former THRESHOLD project is now an integrated subsystem of **Backroom Agriculture**.

- One NeoForge 1.21.1 mod, one installation, one distributable JAR.
- Existing `threshold:` dimension, block, item, sound, loot, and asset namespaces remain unchanged for world compatibility.
- Existing Agriculture content remains under `gugugaga:`. There is no separate `threshold` Mod ID or dependency on a second Agriculture JAR.
- Level 0–5 dimensions, entity director, transit, loot, and world generation register through the single Backroom Agriculture mod entry point.
- GeckoLib 4.9.3 and Lithostitched 1.7.11 are required runtime mods; neither library is bundled into the JAR.
- Level 1's greenhouse uses Agriculture crop blocks, the 27-indicator food-nutrition system, water handling, and the composter loop.

Build from the repository root:

```sh
gradle --no-daemon --stacktrace build
python3 qa/validate_threshold_dimensions.py build/libs/gugugaga-4.0.0-alpha.1.jar
python3 qa/smoke_threshold_server.py
```

The historical `threshold/` source and resource directory is retained as an internal package boundary, not as a Gradle subproject.
