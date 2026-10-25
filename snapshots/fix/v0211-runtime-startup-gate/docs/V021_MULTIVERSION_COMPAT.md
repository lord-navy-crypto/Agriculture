# Gugu-Gaga v0.21.1: runtime compatibility correction

## Verified incident and root cause

The v0.21.0 mod required NeoForge >=21.1.100 and GeckoLib >=4.8.2. Compilation passed, but REAL dedicated server boot at NeoForge 21.1.100 + GeckoLib 4.8.2 fails before mod construction, because **GeckoLib itself requires NeoForge 21.1.150 or newer**. The exact failure is:

> Mod geckolib requires neoforge 21.1.150 or above. Currently, neoforge is 21.1.100.

Never present compile-only Gradle builds as proof of runtime compatibility again. That previous release candidate's claimed lower NeoForge bound was invalid.

## Corrected candidates and requirements

- Minecraft Java 1.21.1 only; Java 21.
- NeoForge **21.1.150 or newer** in Minecraft 1.21.1 line. Individual newer GeckoLib builds MAY impose a stricter minimum; users must honor the installed GeckoLib JAR's own version requirement.
- GeckoLib NeoForge Minecraft 1.21.1 edition, version **4.8.2 or newer 4.x**, required on BOTH client and dedicated server. Forge/Fabric builds are not interchangeable.
- Recommended first working combo **NeoForge 21.1.259 + GeckoLib 4.9.3**: confirmed booting a real dedicated server in earlier CI.
- Lowest candidate **NeoForge 21.1.150 + GeckoLib 4.8.2** requires current live server boot checks to pass before claiming support.

The shipped candidate is compiled against NeoForge 21.1.150 + GeckoLib 4.8.2, with extra compile matrix coverage and real dedicated server boot checks on representative pairs. GitHub Actions requires BOTH a real mod constructor startup marker and the Minecraft server-ready log marker. All failed startup logs are retained as Actions artifacts.

## Install and manual client QA

1. Create a separate Minecraft 1.21.1 NeoForge test profile, using Java 21 and a backed-up test world.
2. Remove ALL earlier Gugu-Gaga versions from the profile's mods folder (one Gugu-Gaga JAR only). Extract the Actions artifact ZIP; place only the included v0.21.1 JAR in mods.
3. Install a matching GeckoLib **NeoForge for Minecraft 1.21.1** 4.x JAR. Start with NeoForge 21.1.259 + GeckoLib 4.9.3 when possible to isolate the lowest-version problem.
4. Read the crash screen and latest.log (or newest crash-reports file) if Minecraft still fails. The reporter must provide Minecraft, NeoForge, GeckoLib and Java versions; no claim about their particular machine can be made from GitHub CI alone.
5. If it launches, verify uppercase HELP rescue, crouch+V listening, real four-second gaze investigation, three actors and repeating five-act cycle.

## What we have NOT verified

Automated dedicated server startup is not a Mac GUI client launch or an interactive in-world graphics test. It does NOT certify audio/animations/shader stacks, game balance, safety of existing save worlds or client mod conflicts. Keep this branch as an unmerged draft until actual user playtests succeed.
