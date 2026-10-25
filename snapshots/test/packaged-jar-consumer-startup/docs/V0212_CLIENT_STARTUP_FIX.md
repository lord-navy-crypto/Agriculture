# Gugu-Gaga v0.21.2 — client renderer event-bus regression

## Confirmed source bug

NeoForge 1.21.1 `EntityRenderersEvent.RegisterRenderers` and `RegisterLayerDefinitions` belong on **MOD** event bus. Previous `client/ClientEvents.java` had `@EventBusSubscriber(modid=..., value=Dist.CLIENT)` without specifying the bus. NeoForge 21.1.x defaults to **GAME** bus, hence those handlers were registered on the wrong bus and cannot process entity renderer registration. This is not caught by compiling or booting a dedicated server, which never loads client renderers.

v0.21.2 adds `bus=EventBusSubscriber.Bus.MOD` to the physical-client-only client event subscriber, leaving the correct `Dist.CLIENT` side restriction in place. This lets NeoForge register all three GeckoLib animated actors' renderers and the existing model layers. Default Sneak+V input uses the GAME event bus intentionally; only its key registration uses MOD.

## Live client validation

A new GitHub Actions `client-startup` job executes **real** `gradle runClient` under Linux `xvfb-run` with Mesa software OpenGL. Unlike compile-only checks, it requires three log markers: actual Gugu-Gaga mod construction, all three GeckoLib renderer registrations, and Minecraft's client tick. Failures preserve runtime console and client latest.log.

Previously verified dedicated-server boot compatibility is retained, with NeoForge **21.1.150+**, GeckoLib **4.8.2+ (4.x)**, MC 1.21.1, Java 21. The actual GeckoLib JAR's own version requirements still take priority. Preserve uppercase plain-chat `HELP`, Sneak+V listening, gaze-driven investigation and 5-stage automatically repeating pacing.

**Limitations:** Passing virtual Linux X11 client is not proof of Mac native rendering, every third-party shader/modpack, or full in-world gameplay. User should test a *fresh isolated profile*, remove old Gugu-Gaga jars, and send latest.log/crash report for any continued client failure.

Relevant official NeoForge 1.21.1 docs: https://docs.neoforged.net/docs/1.21.1/blockentities/ber/ (RegisterRenderers on mod event bus).
