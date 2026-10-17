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

## v0.2 operator/player trial commands
Use a disposable survival Overworld test world (do not overwrite an important save). Auto encounters are OFF by default. The operator can try `/gugugaga encounters on`, wait through the cooldown at night, or use the original manual command `/summon gugugaga:observer ~ ~ ~`. Any player can opt out with `/gugugaga quiet on`; this quiet flag lasts for their current session. An operator can turn the system back off with `/gugugaga encounters off`. The mod works with no Ollama installed. Build green is not the same as actual client/dedicated server gameplay acceptance.

## v0.3 atmospheric testing
For controlled testing, use `/summon gugugaga:observer ~ ~ ~` in a disposable world, set survival mode, then observe it from 8–25 blocks. Look directly at it, then look away. It should turn toward you, pause, quietly approach, and eventually retreat; its head and wing pose should differ. With a Nightwatch encounter enabled, a naturally placed observer will expire after 90 seconds; a manually summoned one persists. Use `/gugugaga quiet on` to stop it from selecting you as a target. Muting sounds or particles is possible in the server config (`encounters.haunting_audio`, `encounters.haunting_particles`). Original/Ollama-free AI remains functional.

### Immediate atmosphere toggle
`/gugugaga atmosphere status` reports the current audio/particle states. Operators can turn both OFF with `/gugugaga atmosphere off`, or back ON with `/gugugaga atmosphere on`. The vanilla Ambient sound slider remains available to players. This does not affect normal observer AI.

## v0.4 Witness Journal in-game test
1. In a NEW survival world, summon exactly one observer using `/summon gugugaga:observer ~ ~ ~`. Stand 8–24 blocks away. Look at its face for a few seconds, look away as it approaches, then stay close enough to witness it retreat.
2. Type `/gugugaga journal`. Up to three unique notes should unlock; repeats must not spam chat.
3. Test `/gugugaga journal off` and re-encounter: no new evidence may be stored. Turn back on with `journal on`. Test `journal clear` and verify all three notes are erased but opt-out preference is retained.
4. Rejoin the world and restart Minecraft to check player NBT persistence, then repeat on a dedicated multiplayer server. These interactive persistence checks are **not** replaced by compiling the mod.
5. The previous `quiet on`, `atmosphere off`, default-OFF night director and optional Ollama behavior must remain unchanged.

## v0.5 new suspense director smoke
Start a new world in survival mode. The director remains OFF by default. The operator can type `/gugugaga encounters on` and choose `/gugugaga director gentle|balanced|eerie`. After at least 8 minutes and when it is nighttime in the Overworld, an eligible player may hear a subtle vanilla chime or footstep, notice two particles, see an actual observer, or experience deliberate quiet. This is the intended variety, not a guaranteed new mob every time. To test observer behavior immediately, use `/summon gugugaga:observer ~ ~ ~`. Toggle `/gugugaga atmosphere off` to test muted events, and `/gugugaga quiet on` to exclude one player. Verify no unexpected chunk generation, damaged blocks or observer crowding. Use same Minecraft 1.21.1, Java 21 and NeoForge 21.1.259 as the previous releases. Keep old saves backed up.

## v0.7 hostility, lore and visual QA
Use a disposable new single-player test world with NeoForge 21.1.259, Java 21. Summon `/summon gugugaga:observer ~ ~ ~`. Its normal state should remain non-hostile, even after a hit, until an operator explicitly executes `/gugugaga hostility on`. Test `/gugugaga temper angry` and `/gugugaga temper corrupted` at safe distance: the bird should visibly change its asymmetric pose, give a quiet warning, approach only a survival target with line of sight and use a modest close-range attack no more often than every 75 ticks. Then run `/gugugaga hostility off` and `/gugugaga quiet on` to confirm it immediately stops attacks. Verify automatic recovery after 65/110 seconds respectively. Record three journal clues before trying `/gugugaga reassure`, then check `/gugugaga story`, the three trust choices, and lore questions sent through `/gugugaga talk`. Test NeoForge dedicated server and relog. This is not a substitute for full player-facing QA.


## v0.8 Three-Entity Manual Smoke

Use a disposable NeoForge 1.21.1 Java21 world. Manually summon each of: /summon gugugaga:observer ~ ~ ~; /summon gugugaga:redactor ~ ~ ~; /summon gugugaga:yellow_smiler ~ ~ ~. Redactor initially displays the blue Lapis mask; it reveals close to Gugu, corrupts temporarily if very close, and MUST flee Yellow when within 24 blocks. Yellow quietly observes Gugu. There is no natural new-entity spawning or completed quest/ending system in this alpha. Check fresh world, relog, dedicated server and performance before release.
