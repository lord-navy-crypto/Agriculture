#!/usr/bin/env python3
"""Complete 27-indicator positive/negative effect contract for Agriculture v3.10."""
from pathlib import Path
import json,re,sys,zipfile
ROOT=Path(__file__).resolve().parents[1]
SRC=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
LANG=ROOT/"src/main/resources/assets/gugugaga/lang"
def src(name):return (SRC/(name+".java")).read_text("utf-8")
def main():
    matrix=src("NutritionEffectsV310")
    feedback=src("NutritionFeedbackV38")
    nutrition=src("NutritionV34")
    screen=src("NutritionScreenClientV35")
    profiles=src("NutritionProfilesV35")
    ids=re.findall(r'"([a-z0-9_]+)"',profiles.split("NUTRIENTS={",1)[1].split("};",1)[0])
    assert len(ids)==27 and len(set(ids))==27,ids
    pairs=re.findall(r'new Pair\(MobEffects\.([A-Z_]+),"([a-z_]+)",MobEffects\.([A-Z_]+),"([a-z_]+)"\)',matrix)
    assert len(pairs)==27, f"expected 27 pairs, got {len(pairs)}"
    assert all(a and b and c and d for a,b,c,d in pairs)
    assert "POSITIVE_LIMIT=5" in matrix and "NEGATIVE_LIMIT=4" in matrix
    assert "POSITIVE_THRESHOLD=65" in matrix
    assert "WATCH_DURATION=80" in matrix and "SUSTAINED_DURATION=220" in matrix
    assert "NutritionPhysiologyV39.stage(data,i)" in matrix
    assert "stage<0" in matrix and "stage==0 && score>=POSITIVE_THRESHOLD" in matrix
    assert "applyLimited(player,positive,POSITIVE_LIMIT)" in matrix
    assert "applyLimited(player,negative,NEGATIVE_LIMIT)" in matrix
    assert "if(water<=15 || sodium<=8)return" in matrix
    assert "else if(score>85)" in matrix
    assert "WaterSaltV37.water(player)" in matrix and "WaterSaltV37.salt(player)" in matrix
    assert "if(score>=35 && score<=75)" in matrix
    assert "NutritionEffectsV310.apply(player,tag)" in feedback
    assert "variedMeals" not in nutrition and "highlyVaried" not in nutrition
    assert "player.addEffect" not in nutrition, "legacy aggregate bonuses must be removed"
    assert "message.gugugaga.v310_effects_active" in nutrition
    assert "PAGES={{0,5},{5,12},{12,18},{18,23},{23,27},{0,0},{0,0},{0,27}}" in screen
    assert "page==7?27" in screen and "page==7?row" in screen
    assert "water<=8?-2:water<=15?-1:water>=65?1:0" in screen
    assert "salt<=8?-2:salt>85?-1:salt>=35 && salt<=75?1:0" in screen
    assert "NutritionEffectsV310.positiveKey" in screen
    assert "NutritionEffectsV310.negativeKey" in screen
    assert "feedback.gugugaga.effect_pair" in screen
    assert "feedback.gugugaga.effects_note" in screen
    for locale in ("en_us","zh_cn"):
        data=json.loads((LANG/(locale+".json")).read_text("utf-8"))
        required=[
            "message.gugugaga.v35_page_7","message.gugugaga.v310_effects_active",
            "feedback.gugugaga.effect_pair","feedback.gugugaga.nutrient_tooltip",
            "feedback.gugugaga.water_salt_tooltip","feedback.gugugaga.effects_note"
        ]
        for key in required:assert data.get(key),(locale,key)
        assert data["feedback.gugugaga.effect_pair"].count("%s")==2
        assert data["feedback.gugugaga.nutrient_tooltip"].count("%s")==7
        assert data["feedback.gugugaga.water_salt_tooltip"].count("%s")==3
    if len(sys.argv)>1:
        jar=Path(sys.argv[1])
        assert jar.is_file()
        with zipfile.ZipFile(jar) as z:
            assert z.testzip() is None
            name="io/github/lordnavycrypto/gugugaga/agriculture/NutritionEffectsV310.class"
            assert name in z.namelist()
            for locale in ("en_us","zh_cn"):
                lang=json.loads(z.read(f"assets/gugugaga/lang/{locale}.json"))
                assert "feedback.gugugaga.effects_note" in lang
    print("NUTRITION_EFFECTS_V310_OK: 27 positive/negative pairs, state thresholds, water/salt priority, 8-page effect matrix, bilingual tooltips")
if __name__=="__main__":main()
