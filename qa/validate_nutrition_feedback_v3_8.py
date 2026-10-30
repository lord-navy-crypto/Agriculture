#!/usr/bin/env python3
"""v3.8.0-rc.2 feedback data contract, layout and packaged-JAR checks.
Static assertions plus boundary-model tests; gameplay/client needs manual QA.
"""
from pathlib import Path
import re,json,sys,zipfile

ROOT=Path(__file__).resolve().parents[1]
SRC=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
ASSET=ROOT/"src/main/resources/assets/gugugaga"
read=lambda name:(SRC/(name+".java")).read_text("utf-8")
def main():
    logic=read("NutritionV34")
    group=read("NutritionFeedbackV38")
    screen=read("NutritionScreenClientV35")
    packet=read("NutritionNetworkV35")
    request=read("NutritionNetworkV34")
    # Parse the actual Java data table, not a hand-maintained duplicate.
    match=re.search(r"private static final int\[\]\[\] GROUPS=\{(.*?)\};",group,re.S)
    assert match,"GROUPS table not found"
    sections=re.findall(r"\{([^{}]+)\}",match.group(1))
    assert len(sections)==5
    indices=[[int(x.strip()) for x in section.split(",")] for section in sections]
    flat=[i for section in indices for i in section]
    assert sorted(flat)==list(range(27)),f"Groups must partition the 27 metrics: {flat}"
    assert len(flat)==len(set(flat))
    # Check the game classification decision boundaries:
    def score(nums,part):return sum(nums[i] for i in part)//len(part)
    def low(nums,part):
        return 2*sum(nums[i]<30 for i in part)>=len(part) and score(nums,part)<36
    def high(nums,part):
        return 2*sum(nums[i]>=60 for i in part)>=len(part) and score(nums,part)>=70
    normal=[45]*27; lacking=[15]*27; supplied=[90]*27
    for part in indices:
        assert score(normal,part)==45 and not low(normal,part) and not high(normal,part)
        assert low(lacking,part) and high(supplied,part)
        assert not low(supplied,part) and not high(lacking,part)
        # Just one nutrient below 30 should not penalize groups of 5+.
        if len(part)>2:
            isolated=normal[:]; isolated[part[0]]=15
            assert not low(isolated,part)
    assert 'private static final String LAST_DAY="v38_feedback_day"' in group
    assert 'private static final String STREAK_PREFIX="v38_feedback_low_"' in group
    assert "Math.min(3L,day-old)" in group
    assert "if(!nutrition.contains(LAST_DAY))" in group
    assert "recoverAfterEating" in group
    assert "stage(vals,i,days(nutrition,i))" in group
    assert "WaterSaltV37.water(player)<=15" in group
    assert "WaterSaltV37.salt(player)<=8" in group
    assert "selectedNegative<0 && !legacyVaried" in group
    assert "MobEffects.DAMAGE_RESISTANCE" in group
    assert "MobEffects.WEAKNESS" in group
    assert "MobEffects.DIG_SLOWDOWN" in group
    assert "MobEffects.NIGHT_VISION" in group
    assert "MobEffects.MOVEMENT_SLOWDOWN" in group
    assert "player.hurt(" not in group and "player.kill(" not in group
    assert "NutritionFeedbackV38.recoverAfterEating(values)" in logic
    assert "NutritionFeedbackV38.advance(values" in logic
    assert "NutritionFeedbackV38.apply(player,values" in logic
    assert "NutritionFeedbackV38.snapshot(record(player))" in logic
    assert '"gugugaga_nutrition_v35"' in logic
    assert "PlayerEvent.Clone" in logic
    assert "V38_FLOOR=15" in logic
    assert "new NutritionNetworkV35.State(NutritionV34.snapshot(player)" in request
    assert "NutritionV34.feedbackSnapshot(player)" in request
    assert 'registrar("2").playToServer' in request
    assert 'registrar("2").playToClient' in packet
    assert "int[] scores,int water,int salt,int[] feedback" in packet
    assert "new State(values,water,salt,feedback)" in packet
    assert "value+2" in packet and "value-2" in packet
    assert "NutritionFeedbackV38.SNAPSHOT_SIZE" in packet
    assert "payload.feedback()" in packet
    assert "int page=5" in screen
    assert "page==6?NutritionFeedbackV38.GROUP_COUNT" in screen
    assert "feedback.gugugaga.group_" in screen
    assert "feedback.gugugaga.tooltip" in screen
    assert "feedback.gugugaga.stage_" in screen
    assert "graphics.renderTooltip" in screen
    assert "setScroll" in screen and "PAGES.length" in screen
    assert "return page==6?NutritionFeedbackV38.GROUP_COUNT" in screen
    for locale in ("en_us","zh_cn"):
        keys=json.loads((ASSET/"lang"/(locale+".json")).read_text("utf-8"))
        required=["message.gugugaga.v35_page_6",
                  "feedback.gugugaga.note","feedback.gugugaga.tooltip"]
        required += [f"feedback.gugugaga.group_{n}" for n in range(5)]
        required += [f"feedback.gugugaga.stage_{n}" for n in range(4)]
        for k in required:assert keys.get(k),(locale,k)
        assert keys["feedback.gugugaga.tooltip"].count("%s")==4
    if len(sys.argv)>1:
        jar=Path(sys.argv[1]);assert jar.is_file()
        with zipfile.ZipFile(jar) as archive:
            assert archive.testzip() is None
            names=set(archive.namelist())
            for cls in ("NutritionFeedbackV38","NutritionV34",
                        "NutritionNetworkV34","NutritionNetworkV35",
                        "NutritionScreenClientV35"):
                name=f"io/github/lordnavycrypto/gugugaga/agriculture/{cls}.class"
                assert name in names,name
            assert b"feedback.gugugaga.group_0" in archive.read(
                    "assets/gugugaga/lang/en_us.json")
    print("NUTRITION_FEEDBACK_V38_OK: 27 unique metrics, 5 groups, " +
          "grace/streak/restore, bounded effects, synchronized GUI and locales")
if __name__=="__main__":
    main()
