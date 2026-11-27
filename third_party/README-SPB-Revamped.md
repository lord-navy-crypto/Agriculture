# SPB Revamped — upstream source reference

This directory is intentionally represented as a Git submodule, not a copied source mirror.

- Upstream repository: https://github.com/SpacePotatoee/MinecraftFoundFootage
- Pinned upstream commit: `0c46c8301fc512c318ac93e23b669355b7d4b180`
- Pinned snapshot date: 2026-07-21
- Upstream default-branch build configuration: `mod_version=1.1.8`
- Target binary discussed in this compatibility project: SPB Revamped 1.20.1 `1.2.0`

## Important version boundary

The pinned public upstream source is **not confirmed to be the exact source for binary version 1.2.0**. Its checked-in `gradle.properties` identifies version 1.1.8. Do not label this snapshot as a complete 1.2.0 source release or assume it reproduces the 1.2.0 JAR.

## Clone with the upstream source

```bash
git clone --recurse-submodules https://github.com/lord-navy-crypto/Agriculture.git
cd Agriculture
git submodule status
```

If the repository was already cloned without submodules:

```bash
git submodule update --init --recursive
```

## macOS Alpha.4 compatibility work

The Alpha.4 compatibility overlay is a separate patch set. It must be applied and validated against the exact matching upstream source revision before it can be called a complete, reproducible project. A successful archive or static source inspection does not prove in-game rendering works.

## Licensing

The upstream repository carries its own license and notices. This repository links to the upstream source rather than copying or relicensing its files. Preserve the upstream license and attribution when working with the submodule.
