# Nutrition Mode switch (v4.14)

## In-game operation

Agriculture now provides a **world-persistent gamerule**, default **ON**:

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
- Nutrition HUD, inventory button, N-key action and food nutrition tooltips
  disappear after the server sync (typically immediately or within one second).
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

1. Enter a fresh world with default gamerule true. Confirm N HUD/button and
   eating feedback work.
2. Run `/gamerule gugugagaNutrition false`; within a second HUD/button and
   nutrition food tooltips should disappear. Eating should still satisfy normal
   hunger and trigger normal Agriculture recipes.
3. Check after multiple Minecraft days that stored reserves and water/salt are
   unchanged while OFF and nutrition status effects are not refreshed.
4. Run `/gamerule gugugagaNutrition true`; verify UI and new meal gains resume,
   existing reserves are preserved, and no backlog depletion occurs.
5. Restart single-player and dedicated servers, reconnect, and confirm that
   the mode persists and the correct UI is shown.
6. Test with Farmer's Delight and other food mods. Their vanilla food/cooking
   functionality must not depend on Nutrition Mode.

Note: This feature is implemented in source; a built JAR and actual Minecraft
runtime test are still required before updating the modpack.
