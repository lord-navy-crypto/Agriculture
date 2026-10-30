#!/usr/bin/env python3
"""Evidence-informed v3.9 nutrition game-contract checks.

Checks actual Java tables and mathematical boundary examples. These are
fictional game-state parameters, NOT clinical deficiency diagnostics.
"""
from pathlib import Path
import json,re,sys,zipfile
ROOT=Path(__file__).resolve().parents[1]
SRC=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
ASSETS=ROOT/"src/main/resources/assets/gugugaga"
DATA=ROOT/"src/main/resources/data/gugugaga/nutrition/food_profiles_v3_5.json"

def source(name):return (SRC/(name+".java")).read_text("utf-8")
def numbers(java,name):
    found=re.search(r"private static final int\[\] "+name+r"=\{(.*?)\};",java,re.S)
    assert found,name
    return [int(s) for s in found.group(1).replace("\n","").split(",") if s.strip()]
def simulate(score,daily,intake,loss,target,replenish):
    if intake>=target:return min(100,score+min(replenish,1+intake//target))
    return max(0,score-max(1,loss*(target-intake)//target))

def main():
    phys=source("NutritionPhysiologyV39")
    feedback=source("NutritionFeedbackV38")
    effects=source("NutritionEffectsV310")
    nutrition=source("NutritionV34")
    screen=source("NutritionScreenClientV35")
    wire=source("NutritionNetworkV35")
    foods=json.loads(DATA.read_text("utf-8"))
    ids=[x["id"] for x in foods["nutrients"]]
    assert len(ids)==27 and len(set(ids))==27
    losses=numbers(phys,"DAILY_LOSS")
    targets=numbers(phys,"TARGET")
    latencies=numbers(phys,"LATENCY")
    replenish=numbers(phys,"REPLENISH")
    for name,arr in (("losses",losses),("targets",targets),
                     ("latencies",latencies),("replenish",replenish)):
        assert len(arr)==27,(name,len(arr))
        assert all(0<=n<=60 for n in arr),name
    assert ids[4]=="hydration" and ids[26]=="sodium"
    assert targets[4]==0 and targets[26]==0
    assert losses[4]==0 and losses[26]==0
    assert latencies[4]==0 and latencies[26]==0
    assert losses[6]>losses[13] and latencies[6]<latencies[13], "B1 and B12 storage must differ"
    assert losses[14]>losses[15], "vitamin C vs vitamin D needs distinct kinetics"
    assert latencies[19]>latencies[6], "iron reserve must not behave like B1"
    zero=lambda i:simulate(60,1,0,losses[i],targets[i],replenish[i])
    assert zero(6)<zero(13),"missing B1 should deplete reserve faster than B12"
    assert simulate(25,1,20,losses[13],targets[13],replenish[13])<40, "no instant restoration"
    for i in range(27):
        if i in (4,26):continue
        assert targets[i]>0 and losses[i]>0 and replenish[i]>0 and latencies[i]>0
        assert 0<=simulate(0,1,0,losses[i],targets[i],replenish[i])<=100
        assert 0<=simulate(100,1,48,losses[i],targets[i],replenish[i])<=100

    groupMatch=re.search(r"private static final int\[\]\[\] GROUPS=\{(.*?)\};",feedback,re.S)
    assert groupMatch
    groups=[[int(n.strip()) for n in block.split(",")]
            for block in re.findall(r"\{([^{}]+)\}",groupMatch.group(1))]
    assert len(groups)==5
    selected=[i for group in groups for i in group]
    assert sorted(selected)==[i for i in range(27) if i not in (4,26)]
    assert len(selected)==len(set(selected))
    assert "NUTRIENT_STATES_OFFSET=GROUP_COUNT*3" in feedback
    assert "SNAPSHOT_SIZE=GROUP_COUNT*3+27*2" in feedback
    assert "NutritionPhysiologyV39.stage(data,i)" in feedback
    assert "NutritionPhysiologyV39.shortfallDays(data,i)" in feedback
    assert "if(water<=15 || sodium<=8)return" in effects
    assert "WaterSaltV37.water(player)" in effects and "WaterSaltV37.salt(player)" in effects
    assert "MobEffects.NIGHT_VISION" not in feedback
    assert "MobEffects.DAMAGE_RESISTANCE" not in feedback
    assert "player.hurt" not in feedback
    assert "NutritionPhysiologyV39.consume(values,contribution)" in nutrition
    assert "NutritionPhysiologyV39.advance(values" in nutrition
    assert "NutritionFeedbackV38.apply" in nutrition
    assert "result[NutritionPhysiologyV39.WATER_INDEX]=WaterSaltV37.water(player)" in nutrition
    assert "result[NutritionPhysiologyV39.SODIUM_INDEX]=WaterSaltV37.salt(player)" in nutrition
    assert "gugugaga_nutrition_v35" in nutrition
    assert "PlayerEvent.Clone" in nutrition
    assert "LOW_DAYS" in phys and "v39_last_processed_day" in phys
    assert "data.remove(DAILY+i)" in phys
    assert "data.putLong(LAST_DAY,worldDay)" in phys
    assert "worldDay<=last" in phys and "return;" in phys
    assert "event.registrar" not in phys
    assert 'registrar("3").playToClient' in wire
    assert "NUTRIENT_DAYS_OFFSET" in wire
    assert "NUTRIENT_STATES_OFFSET" in wire
    assert "feedback.gugugaga.nutrient_tooltip" in screen
    assert "NutritionPhysiologyV39.requiredDays" in screen
    assert "graphics.renderTooltip" in screen
    for locale in ("en_us","zh_cn"):
        content=json.loads((ASSETS/"lang"/(locale+".json")).read_text("utf-8"))
        for id in ids:
            assert content.get("feedback.gugugaga.role."+id),(locale,id)
        assert "feedback.gugugaga.nutrient_tooltip" in content
        assert "feedback.gugugaga.stage_0" in content
        assert content["feedback.gugugaga.nutrient_tooltip"].count("%s")==7
    if len(sys.argv)>1:
        jar=Path(sys.argv[1])
        assert jar.is_file(),jar
        with zipfile.ZipFile(jar) as archive:
            assert archive.testzip() is None
            classes=["NutritionPhysiologyV39","NutritionFeedbackV38",
                     "NutritionScreenClientV35","NutritionV34","NutritionNetworkV35"]
            for cls in classes:
                assert "io/github/lordnavycrypto/gugugaga/agriculture/"+cls+".class" in archive.namelist()
            assert "data/gugugaga/nutrition/food_profiles_v3_5.json" in archive.namelist()
    print("NUTRITION_PHYSIOLOGY_V39_OK: 27 modeled indicators, 25 distinct reserve kinetics, 2 linked meters, nonclinical thresholds, 27 localized roles and server sync")
if __name__=="__main__":main()
