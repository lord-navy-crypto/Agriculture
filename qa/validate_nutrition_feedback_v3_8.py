#!/usr/bin/env python3
"""Compatibility contract: v3.8 interface remains while v3.9 models 27 nutrients separately."""
from pathlib import Path
import json,sys,zipfile
ROOT=Path(__file__).resolve().parents[1]
SRC=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
ART=ROOT/"src/main/resources/assets/gugugaga"
def java(n):return (SRC/(n+".java")).read_text("utf-8")
def main():
    feedback=java("NutritionFeedbackV38")
    effects=java("NutritionEffectsV310")
    physiology=java("NutritionPhysiologyV39")
    screen=java("NutritionScreenClientV35")
    nutrition=java("NutritionV34")
    req=java("NutritionNetworkV34")
    net=java("NutritionNetworkV35")
    assert "GROUP_COUNT=5" in feedback and "SNAPSHOT_SIZE=GROUP_COUNT*3+27*2" in feedback
    assert "NUTRIENT_STATES_OFFSET" in feedback
    assert "NUTRIENT_DAYS_OFFSET" in feedback
    assert "NutritionPhysiologyV39.stage(data,i)" in feedback
    assert "NutritionPhysiologyV39.shortfallDays(data,i)" in feedback
    assert "if(water<=15 || sodium<=8)return" in effects
    assert "WaterSaltV37.water(player)" in effects and "WaterSaltV37.salt(player)" in effects
    assert "MobEffects.NIGHT_VISION" not in feedback
    assert "MobEffects.DAMAGE_RESISTANCE" not in feedback
    assert "NutritionPhysiologyV39.advance" in nutrition
    assert "NutritionPhysiologyV39.consume" in nutrition
    assert "NutritionFeedbackV38.apply" in nutrition
    assert "NutritionFeedbackV38.snapshot" in nutrition
    assert '"gugugaga_nutrition_v35"' in nutrition
    assert "PlayerEvent.Clone" in nutrition
    assert "int page=5" in screen and "PAGES.length" in screen
    assert "page==6?NutritionFeedbackV38.GROUP_COUNT" in screen
    assert "feedback.gugugaga.nutrient_tooltip" in screen
    assert "feedback.gugugaga.role." in screen
    assert "graphics.renderTooltip" in screen
    assert 'registrar("3").playToServer' in req
    assert 'registrar("3").playToClient' in net
    assert "payload.feedback()" in net
    assert "SNAPSHOT_SIZE" in net
    assert "NUTRIENT_DAYS_OFFSET" in net
    assert "int[] scores,int water,int salt,int[] feedback" in net
    assert "NutritionV34.feedbackSnapshot(player)" in req
    for lang in ("en_us","zh_cn"):
        d=json.loads((ART/"lang"/(lang+".json")).read_text("utf-8"))
        assert all(d.get("feedback.gugugaga.group_"+str(i)) for i in range(5))
        assert all(d.get("feedback.gugugaga.stage_"+str(i)) for i in range(4))
        assert d.get("feedback.gugugaga.nutrient_tooltip")
    if len(sys.argv)>1:
        with zipfile.ZipFile(sys.argv[1]) as jar:
            assert jar.testzip() is None
            for name in ("NutritionFeedbackV38","NutritionPhysiologyV39",
                    "NutritionNetworkV35","NutritionV34","NutritionScreenClientV35"):
                assert "io/github/lordnavycrypto/gugugaga/agriculture/"+name+".class" in jar.namelist()
    print("NUTRITION_FEEDBACK_V38_COMPAT_PASS: established 7-page GUI and packet, 27 individual statuses")
if __name__=="__main__":main()
