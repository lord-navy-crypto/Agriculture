# Development setup — Minecraft 1.21.1 / NeoForge

Required: **JDK 21**. The project uses NeoForge ModDevGradle `2.0.148`, NeoForge `21.1.259` and Gradle `9.2.1`.

## Initial checkout
```bash
git clone https://github.com/lord-navy-crypto/Gugu-Gaga-horror.git
cd Gugu-Gaga-horror
```

The initial repository intentionally does not include a binary Gradle wrapper JAR. Until one is generated and committed from a trusted Gradle distribution, install Gradle 9.2.1 and use:

```bash
gradle build
gradle runClient
```

For a wrapper on your machine, run `gradle wrapper --gradle-version 9.2.1` and commit `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`, and `gradle/wrapper/gradle-wrapper.properties` from trusted official Gradle tooling.

## Testing in-game
1. Start a new creative test world with cheats (not an important existing save).
2. Switch to survival for AI interaction (`/gamemode survival`).
3. Summon one prototype using `/summon gugugaga:observer ~ ~ ~`.
4. Observe from afar, look directly at the entity, walk closer, turn away.
5. Confirm it doesn't cause damage, force-load chunks, or duplicate endlessly.
6. Repeat on a dedicated server before enabling ambient encounters.

No CurseForge package is considered release-ready until the `build` workflow passes and provenance is reviewed.
