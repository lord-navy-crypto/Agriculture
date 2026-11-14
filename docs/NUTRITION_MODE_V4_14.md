# Nutrition Mode switch (v4.14)

## In-game operation

Open the nutrition dashboard with **N**. The top of the panel now contains a clickable **Nutrition Mode: ON/OFF** control. It is available even while the system is paused, so the same panel can turn nutrition back on. In single-player, the world owner can use it without enabling cheats; on a dedicated server, only an operator can change the world-wide setting. Other players see the current state but cannot change it.

Agriculture also provides the equivalent **world-persistent gamerule**, default **ON**:

- `/gamerule gugugagaNutrition false` disables optional nutrition.
- `/gamerule gugugagaNutrition true` reenables it.
- `/gamerule gugugagaNutrition` reports current state.

Changing gamerules requires world/administrator permissions. Gamerules persist in
the world save, work for single-player and dedicated servers, and are exposed in
Minecraft's game-rule editor when the world is configured.

## Behavior

When **OFF**:
- Completed meals no longer add 27-channel nutrition scores.
- Nutrition day-end depletion and effect refresh stop.
- Hydration/sodium gains, decay and penalties stop.
- The small HUD hint, inventory shortcut and food nutrition tooltips disappear after server sync. The **N key remains available** so the dashboard can be reopened and Nutrition Mode can be turned back on.
- Existing nutrition reserves and water/salt values are retained. Turning ON
  resumes from those values, without retroactive offline depletion.
- Previously granted short-duration potion effects expire naturally in up to
  240 game ticks rather than being removed blindly (other mods may legitimately
  supply the same vanilla effects).
- **Not affected:** ordinary Minecraft hunger, food use, farms, crops, kitchen
  machines, food recipes, vanilla food attributes, and other Agriculture content.

### Multiplay

The gamerule is **server-authoritative**. All players in the same world share
the same mode; a normal client cannot override the server setting. The server
sends a small status packet when players connect and when the value changes.

### Testing checklist

1. Enter a fresh world with default gamerule true. Press N and confirm the
   dashboard's ON control, HUD hint, inventory shortcut and eating feedback work.
2. Press N and click the OFF control. Confirm the dashboard stays open and
   shows a paused notice; the HUD hint, inventory shortcut and nutrition food
   tooltips disappear. N must still reopen the dashboard. Eating should still
   satisfy normal hunger and trigger normal Agriculture recipes.
3. Check after multiple Minecraft days that stored reserves and water/salt are
   unchanged while OFF and nutrition status effects are not refreshed.
4. Press N and click the ON control (or use the gamerule command); verify UI
   and new meal gains resume, existing reserves are preserved, and no backlog
   depletion occurs.
5. Restart single-player and dedicated servers, reconnect, and confirm that
   the mode persists and the correct UI is shown.
6. Test with Farmer's Delight and other food mods. Their vanilla food/cooking
   functionality must not depend on Nutrition Mode.

Note: This feature is implemented in source; a built JAR and actual Minecraft
runtime test are still required before updating the modpack.
