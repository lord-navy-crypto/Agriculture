#!/usr/bin/env python3
"""Nutrition Mode v4.14: server-authoritative toggle and N-screen re-entry."""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SRC = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
MODE = (SRC / "NutritionModeV414.java").read_text("utf-8")
NETWORK = (SRC / "NutritionModeNetworkV414.java").read_text("utf-8")
SCREEN = (SRC / "NutritionScreenClientV35.java").read_text("utf-8")
KEY = (SRC / "NutritionKeyTickV34.java").read_text("utf-8")
REQUEST = (SRC / "NutritionNetworkV34.java").read_text("utf-8")
PHYS = (SRC / "NutritionPhysiologyV39.java").read_text("utf-8")
NUTRITION = (SRC / "NutritionV34.java").read_text("utf-8")

def main():
    # The N-key dashboard itself owns the clickable control.
    assert "Button.builder(nutritionModeLabel()" in SCREEN
    assert "NutritionModeV414.requestClientToggle()" in SCREEN
    assert "syncNutritionModeButton()" in SCREEN
    assert "nutrition_mode_paused" in SCREEN
    assert "NutritionMode: ON" not in SCREEN  # localized labels, not hardcoded UI
    # It must remain reachable while OFF, otherwise users cannot turn it back on.
    assert "clientEnabled()" not in KEY
    assert "NutritionModeV414.enabled(player)" not in REQUEST
    # Toggle requests are client-originated but server-authoritative.
    assert "playToServer(Toggle.TYPE" in NETWORK
    assert "NutritionModeV414.setFromClient(player, payload.enabled())" in NETWORK
    assert "player.getServer().isSingleplayer() || player.hasPermissions(2)" in MODE
    assert "server.overworld().getGameRules().getRule(ENABLED).set(enabled, server)" in MODE
    assert "new NutritionModeNetworkV414.State(enabled, canToggle)" in MODE
    assert "setClientState(" in NETWORK
    # Pausing updates the clock without accumulating offline depletion.
    assert "NutritionPhysiologyV39.pause(record(player)" in NUTRITION
    assert "data.putLong(LAST_DAY,worldDay)" in PHYS
    assert "data.remove(DAILY+i)" in PHYS
    for locale in ("en_us", "zh_cn"):
        trans = json.loads((ROOT / f"src/main/resources/assets/gugugaga/lang/{locale}.json").read_text("utf-8"))
        for key in (
            "button.gugugaga.nutrition_mode_on",
            "button.gugugaga.nutrition_mode_off",
            "button.gugugaga.nutrition_mode_locked",
            "button.gugugaga.nutrition_mode_pending",
            "message.gugugaga.nutrition_mode_paused",
        ):
            assert key in trans, (locale, key)
    print("NUTRITION_MODE_V414_PASS: N-screen toggle, OFF re-entry, server permissions, pause clock, bilingual labels")

if __name__ == "__main__":
    main()
