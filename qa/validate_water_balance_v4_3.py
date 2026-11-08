#!/usr/bin/env python3
"""Deterministic abstract-game hydration balance and source-contract regression."""
from pathlib import Path
import json
ROOT=Path(__file__).resolve().parents[1]
JAVA=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
SRC=(JAVA/"WaterSaltV37.java").read_text("utf8")
F=ROOT/"src/main/resources/data/gugugaga/nutrition"

def day(sprint=False,nether=False,high_salt=False):
    water=100
    sodium=60
    wp=sp=drained=0
    for tick in range(100,24001,100):
        # 100 activity points per sample, plus sprint/environment factors.
        wp+=100+(100 if sprint else 0)+(100 if nether else 0)+(50 if high_salt else 0)
        drained+=wp//400
        water=max(0,water-wp//400)
        wp%=400
        sp+=100
        sodium=max(0,sodium-sp//4800)
        sp%=4800
    return water,sodium,wp,sp,drained

def main():
    for contract in (
        "SAMPLE_TICKS=100","WATER_DRAIN_UNITS=400","SALT_DECAY_TICKS=4800",
        "v42_water_progress","v42_salt_progress",
        "public static int activityUnits(boolean sprinting,boolean nether,boolean highSalt)",
        "player.level().dimension()==Level.NETHER",
        "player.tickCount%200!=0",
        "PlayerEvent.Clone", "LivingEntityUseItemEvent.Finish",
    ):
        assert contract in SRC,contract
    # Evaluate total demand from an initial full bar. The old 120/day idle
    # drain was mathematically impossible to sustain without constant drinking.
    assert day()==(40,55,0,0,60),day()
    assert day(sprint=True)==(0,55,0,0,120)
    assert day(nether=True)==(0,55,0,0,120)
    assert day(sprint=True,nether=True)==(0,55,0,0,180)
    assert day(high_salt=True)==(10,55,0,0,90)
    water=json.loads((F/"water_salt_profiles_v3_7.json").read_text("utf8"))["foods"]
    foods=json.loads((F/"food_profiles_v3_5.json").read_text("utf8"))["foods"]
    assert len(foods)==404 and set(foods).issubset(water)
    # Two safe drinks + edible meals can cover the ordinary game's daily
    # water demand; varied ordinary meals replace salt at least as fast as rest uses it.
    assert 2*water["gugugaga:boiled_water_bottle"]["water"]>=60
    assert sum(water[x]["sodium"] for x in (
        "minecraft:bread","minecraft:carrot","minecraft:cooked_beef",
        "minecraft:apple","minecraft:cooked_salmon"))>=5
    print("BACKROOM_WATER_BALANCE_V4_3_PASS: idle 60/day, active demand higher, salt 5/day, 404 food profiles")

if __name__=="__main__":
    main()
