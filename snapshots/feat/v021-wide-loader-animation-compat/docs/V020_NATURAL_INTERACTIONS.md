# v0.20 — naturally playing Gugu-Gaga (chat rescue + listening key + gaze)

Target branch based on Sacred Nightwatch RC1; no main merge until client playtest. Build version 0.20.1-alpha.1, Minecraft 1.21.1 / NeoForge 21.1.259 / Java 21 / GeckoLib 4.9.3.

## New player interactions

- **Help by chat:** When server owner enabled Nightwatch, chat the exact phrase **`HELP`** (without leading slash). The word must be UPPERCASE; leading/trailing spaces are ignored, but lowercase, mixed case, sentences and former `HELP Yellow Face` aliases are not intercepted. This exact message is canceled so it is not publicly broadcast. Server calls the **existing, bounded** requestSmilerHelp function. Nearby existing Yellow must be visible, a Gugu must be within intervention distance, player must have opted into active chase act, and there is an aid cooldown. **Does not summon/teleport Yellow; will explain failed help attempts**. Ordinary chat (including lowercase `help`) remains unchanged.
- **Dedicated listen key:** Default **V** while holding Sneak, remappable at Options > Controls > Key Binds > Gugu-Gaga Nightwatch. Client registers a proper physical-client-only NeoForge RegisterKeyMappingsEvent mapping, checks ClientTickEvent.Post and uses an ordinary server-validated command for compatibility with dedicated server. A 12-second cooldown and true loaded monster limits still apply. Server returns coarse hints via Action Bar with an accessible chat history copy. This is a *real Minecraft key mapping* but the HUD is deliberately minimalist and has no custom textures/shaders.
- **Automatic observation:** In WITNESS, look directly at a real Gugu within 12 blocks with uninterrupted LOS for ~4 seconds; you automatically investigate it and reach FALSE_SAFETY. Then focus your view on a real Lapis or Yellow for ~4 seconds to reach RESPITE. Partial focus resets upon looking away, and no unseen/impostor location reveals occur. Player receives two small "Observing..." action-bar signals (no overlays/flashes), and the existing investigation command remains an optional fallback.
- **Fifth phase:** RESPITE remains **70s temporary quiet** followed by STILLNESS, not a final boss, quest chain, compulsory plot dialogue or story ending. Cycles replay without requiring commands. This preserves the original player goal.
- Existing world-owner `/gugugaga play server on` and player `/gugugaga play join` remain as **deliberate consent** for an experimental horror mode. Admin test mode, chase toggles, quiet leave, entity caps and all gameplay AI remain intact.

## How to test the v0.20 bundle

Extract the GitHub Actions **gugugaga-natural-playtest-v020** artifact. Install ONLY the included `gugugaga-0.20.1-alpha.1.jar` under mods alongside the separately downloaded **GeckoLib 4.9.3 NeoForge 1.21.1**; delete the old 0.19 RC JAR. Use a backup world and matching mods on any dedicated server.

```mcfunction
/time set night
/gamemode survival
/gugugaga play server on
/gugugaga play join
/gugugaga play test witness
```

Look directly at the penguin for 4 seconds, then find the spawned Yellow or Lapis and look for 4 seconds. Stand near Gugu and visible Yellow, type **HELP** into *ordinary chat*, confirm Smiler moves to actually ward Gugu if allowed. Hold Sneak + V to hear nearby true monster evidence; press V without sneaking or with a chat window open and verify **no action**. Observe cooldown and action-bar prompt; remap key to another key to verify Controls registration. Wait out RESPITE and check automatic STILLNESS reset; no final story scene should launch.

If a static stage refuses to spawn, relocate to suitable loaded ground and repeat OP `/gugugaga play test triad`, check collision and latest.log. In multiplayer verify the help chat is not broadcast, independent actor aid permissions/cooldowns and no cross-participant clues. Confirm vanilla/other mod chat still works normally. Check dedicated server boots without client class loading errors.

## Explicit remaining acceptance limits

A passing Gradle build does not equal successful real Minecraft client or server playtest. Verify actual rendering and controls, English and Chinese labels, access to chat, true line-of-sight for gaze and creature assistance; record bugs/screenshots and latest.log. Current player opt-in command is a safety choice, not yet a settings GUI. No false claim of a finished retail horror game.

## Sources for the exact APIs

- [NeoForge 1.21.1 Key Mappings](https://microcosm1535.github.io/NeoForgeDocs-CN/docs/1.21.1/misc/keymappings/)
- [NeoForge 1.21.1 ServerChatEvent](https://aldak0.ru/javadoc/1.21.1-21.1.x/net/neoforged/neoforge/event/ServerChatEvent.html)
- [NeoForge 1.21.1 event buses](https://microcosm1535.github.io/NeoForgeDocs-CN/docs/1.21.1/concepts/events/)
