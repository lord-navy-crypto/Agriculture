# Gugu-Gaga v0.21: practical version compatibility

v0.20.1 required NeoForge >=21.1.259 and GeckoLib >=4.9.3. Those floors were tied to the newest local development environment instead of an actual oldest-API check.

The candidate v0.21 targets Minecraft Java **1.21.1** and Java **21**, and proposes NeoForge **21.1.100 or later in the 21.1 series**, plus GeckoLib **4.8.2 or later but below 5.0** using the **NeoForge 1.21.1** variant. Both the client and dedicated server need the matching GeckoLib and mod JARs. GeckoLib remains mandatory: all three original animated actors extend its classes. Forge and Fabric GeckoLib files are *not* substitutes.

The *release JAR is compiled against the earliest proposed versions* (NeoForge 21.1.100 + GeckoLib 4.8.2). The CI matrix also compiles against NeoForge 21.1.100 + GeckoLib 4.9.3, NeoForge 21.1.200 + GeckoLib 4.8.4, NeoForge 21.1.259 + GeckoLib 4.8.2, and NeoForge 21.1.259 + GeckoLib 4.9.3. The build dependencies and declared minimum versions are independent variables, so building against a new library cannot accidentally raise the runtime floor. Review actual GitHub Actions results and do not publish this range if baseline or matrix builds fail.

Only one Gugu-Gaga JAR belongs in mods; remove older copies. Do not install the downloaded artifact ZIP directly. The player HELP-only chat, crouch+V listening, gaze investigation, all five stages and the automatic fifth-to-first loop are unchanged.

**Not asserted**: compatibility with Minecraft 1.20.1 or 1.21.4, Forge, Fabric, GeckoLib 5.x, Java 17 or all future versions. CI compilation is not full 3D game, dedicated server, modpack conflict or animation rendering validation. For failed launches provide latest.log / crash report with Minecraft, NeoForge, GeckoLib and Java versions.
