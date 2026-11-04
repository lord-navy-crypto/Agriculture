# Threshold: Backrooms

An independent NeoForge 1.21.1 horror mod subproject in this repository.

- Mod ID: `threshold`
- Minecraft: `1.21.1`
- NeoForge: `21.1.259`
- Java: `21`
- Independent artifact: `threshold-0.1.0-alpha.1.jar`

Threshold has its own source tree, metadata, resource namespace, dimension registry data, and JAR. It does not import Agriculture classes or require the Agriculture mod.

## First dimensions

### Level 0 — Isolation
- Deterministic 32x32 room cells aligned to two chunks.
- Yellow office palette, carpet, pale ceiling and flush light panels.
- Shared-boundary doorway decisions keep adjacent chunks aligned.
- Rare column rooms, dim rooms, red rooms and interior dividers.

### Level 1 — The Vulnerable Shelter
- Deterministic 48x48 warehouse-room cells aligned to three chunks.
- Concrete halls, pillars, storage bays, decorative barrels and dim halls.
- Wide door openings are computed from shared room boundaries.
- Separate dimension type and ambient-light setting.

## Operator-only development commands

```text
/threshold travel level_0
/threshold travel level_1
/threshold travel overworld
/threshold whereami
```

These are test entry points. Physical transition structures will follow after the dimensions and generation are validated.

## Build

```sh
gradle --no-daemon --stacktrace :threshold:build
python3 qa/validate_threshold_dimensions.py threshold/build/libs/threshold-0.1.0-alpha.1.jar
```

Agriculture compatibility is intentionally optional and is not part of this foundation.
