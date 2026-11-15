#!/usr/bin/env python3
"""Validate v4.15 universal third-party food compatibility and v4.14 master toggle."""
import json
import re
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]
SRC=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
DB=ROOT/"src/main/resources/data/gugugaga/nutrition/food_profiles_v3_5.json"
PROFILES=(SRC/"NutritionProfilesV35.java").read_text(encoding="utf-8")
COMPAT=(SRC/"NutritionFoodCompatibilityV415.java").read_text(encoding="utf-8")
EAT=(SRC/"NutritionV34.java").read_text(encoding="utf-8")
TOOLTIP=(SRC/"NutritionTooltipV40.java").read_text(encoding="utf-8")
MODE=(SRC/"NutritionModeV414.java").read_text(encoding="utf-8")
NETWORK=(SRC/"NutritionModeNetworkV414.java").read_text(encoding="utf-8")

def main():
    catalog=json.loads(DB.read_text(encoding="utf-8"))
    categories=set(catalog["profiles"])
    nutrients=[n["id"] for n in catalog["nutrients"]]
    assert len(nutrients)==27 and len(set(nutrients))==27
    assert len(catalog["foods"])>=403

    mapped=set(re.findall(r't\("([a-z_]+)",\s*"(?:c|forge)"',COMPAT))
    assert mapped, "No cross-mod category tags found"
    assert mapped <= categories, (mapped-categories)
    for required in ("vegetable","leafy","fruit","berries","grain","root","legume",
                     "seed","meat","seafood","egg","dairy","mushroom","seaweed",
                     "honey","herb","sweet","beverage"):
        assert required in mapped, required

    # Exact Agriculture profiles must win before tag/path inference.
    assert "int[] exact=VALUES.get(itemId);" in PROFILES
    assert PROFILES.index("int[] exact=VALUES.get(itemId);") < PROFILES.index("NutritionFoodCompatibilityV415.infer(stack)")
    assert "public static int[] contribution(ItemStack stack)" in PROFILES
    assert "public static int[] categoryContribution(String category)" in PROFILES
    assert "public static boolean hasResolvedProfile(ItemStack stack)" in PROFILES

    # Third-party tags plus a conservative registry-path fallback.
    assert "TagKey.create(Registries.ITEM" in COMPAT
    assert "stack.is(candidate.tag())" in COMPAT
    assert "inferFromPath(path, categories);" in COMPAT
    assert "if (categories.isEmpty()) return null;" in COMPAT
    assert "NutritionPhysiologyV39.linked(i)" in COMPAT
    assert "Math.min(10, result[i] + profile[i])" in COMPAT

    # Both actual consumption and player-facing tooltip use the same resolver.
    assert "NutritionProfilesV35.contribution(food)" in EAT
    assert "NutritionProfilesV35.contribution(stack)" in TOOLTIP
    assert "NutritionProfilesV35.hasResolvedProfile(stack)" in TOOLTIP
    assert "!NutritionModeV414.enabled(player)" in EAT
    assert "!NutritionModeV414.clientEnabled()" in TOOLTIP

    # Existing v4.14 world-persistent master toggle remains server-authoritative.
    assert 'GameRules.register("gugugagaNutrition"' in MODE
    assert "GameRules.BooleanValue.create(true)" in MODE
    assert "server.overworld().getGameRules().getRule(ENABLED).set(enabled, server)" in MODE
    assert "player.hasPermissions(2)" in MODE
    assert "playToServer(Toggle.TYPE" in NETWORK
    assert "NutritionModeV414.setFromClient(player, payload.enabled())" in NETWORK

    for locale in ("en_us","zh_cn"):
        lang=json.loads((ROOT/f"src/main/resources/assets/gugugaga/lang/{locale}.json").read_text(encoding="utf-8"))
        for key in ("button.gugugaga.nutrition_mode_on","button.gugugaga.nutrition_mode_off",
                    "button.gugugaga.nutrition_mode_locked","message.gugugaga.nutrition_mode_paused"):
            assert lang.get(key), (locale,key)

    print("NUTRITION_UNIVERSAL_FOOD_V415_PASS: 27 channels, exact-profile priority, common/forge tags, ID inference, tooltip/consumption parity, hydration/sodium separation, v4.14 toggle contracts")

if __name__=="__main__":
    main()
