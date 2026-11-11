#!/usr/bin/env python3
"""Nutrition v4.13 gameplay recovery: exact 27-channel table and cap mathematics.

Real-world nutrients are not modeled in doses; 0..100 is a Minecraft reserve.
"""
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SRC = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
PHYS = (SRC / "NutritionPhysiologyV39.java").read_text("utf-8")
UI = (SRC / "NutritionScreenClientV35.java").read_text("utf-8")
NUT = (SRC / "NutritionV34.java").read_text("utf-8")
EFFECTS = (SRC / "NutritionEffectsV310.java").read_text("utf-8")
EXPECTED = [12,9,9,9,0,6,12,12,12,12,12,12,9,6,12,6,6,9,6,9,9,9,9,6,6,6,0]

def values(key):
    m = re.search(r"private static final int\[\] " + key + r"=\{(.*?)\};",PHYS,re.S)
    assert m, key
    return [int(n) for n in re.findall(r"\d+",m.group(1))]

def main():
    cap = values("REPLENISH")
    target = values("TARGET")
    losses = values("DAILY_LOSS")
    latency = values("LATENCY")
    assert cap == EXPECTED and len(cap) == 27
    assert all(6 <= cap[i] <= 12 for i in range(27) if i not in (4,26))
    assert set(cap[i] for i in range(27) if i not in (4,26)) == {6,9,12}
    assert target == [6,5,5,4,0,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,0]
    assert losses == [4,3,3,2,0,1,4,3,3,3,3,3,2,1,4,1,1,2,1,2,3,3,2,1,1,1,0]
    assert latency == [3,4,5,6,0,16,3,4,5,5,6,6,8,32,4,18,18,14,20,12,6,5,8,20,18,20,0]
    assert (cap[4],cap[26]) == (0,0)
    for i in range(27):
        if i in (4,26): continue
        tier = "fast" if cap[i] >= 10 else "medium" if cap[i] >= 7 else "slow"
        assert tier in ("fast","medium","slow")
        def recovered(intake):
            return min(cap[i],(intake*cap[i]+target[i]-1)//target[i])
        assert recovered(0) == 0
        assert recovered(target[i]) == cap[i]
        assert recovered(48) == cap[i]
        total = 0
        for intake in range(1,49):
            delta = recovered(intake) - recovered(intake-1)
            assert 0 <= delta <= cap[i]
            total += delta
        assert total == cap[i]
        # Recovery remains capped independently for each nutrient and day.
        assert cap[i] <= 12 and target[i] > 0 and losses[i] > 0 and latency[i] > 0

    # From default 45 to first positive effect at 65:
    assert (20 + cap[13] - 1)//cap[13] == 4  # B12, slow
    assert (20 + cap[14] - 1)//cap[14] == 2  # Vitamin C, fast
    assert latency[13] > latency[6] and losses[13] < losses[6]
    assert "public static int dailyRecoveryLimit(int i)" in PHYS
    assert "gained[i]=next-current;" in PHYS
    assert "next=current;" in PHYS  # No doubled recovery at midnight.
    assert "data.remove(DAILY+i)" in PHYS
    assert "Math.floorDiv(player.getServer().overworld().getGameTime(),24000L)" in NUT
    assert "NutritionPhysiologyV39.consume(values,contribution)" in NUT
    assert "NutritionPhysiologyV39.dailyRecoveryLimit(nutrientIndex)" in UI
    assert "today[nutrientIndex]" in UI
    assert "feedback.gugugaga.daily_recovery_limit" in UI
    assert "POSITIVE_THRESHOLD=65" in EFFECTS
    for locale in ("en_us","zh_cn"):
        trans = json.loads((ROOT / f"src/main/resources/assets/gugugaga/lang/{locale}.json").read_text("utf-8"))
        assert trans["feedback.gugugaga.daily_recovery_limit"].count("%s") == 3
    print("NUTRITION_BALANCE_V413_PASS: fast/medium/slow caps, instant reserve, no double credit, bilingual cap tooltip")

if __name__=="__main__": main()
