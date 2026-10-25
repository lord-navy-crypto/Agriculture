import json
import unittest
from pathlib import Path
BASE=Path(__file__).resolve().parents[1]
JAVA=BASE/"src/main/java/io/github/lordnavycrypto/gugugaga"
ASSET=BASE/"src/main/resources/assets/gugugaga"
DATA=BASE/"src/main/resources/data/gugugaga"
IDS=("rabies_wildlife_case","avian_influenza_case","field_clinic")
class PathogenSurvivalAssets(unittest.TestCase):
 def test_native_registration_and_player_state(self):
  mod=(JAVA/"GuguGagaMod.java").read_text()
  source=(JAVA/"microbes/PathogenScenarioBlock.java").read_text()
  runtime=(JAVA/"microbes/PathogenGameplayRuntime.java").read_text()
  for key in ("PathogenWorldContent.BLOCKS.register(modBus)","PathogenWorldContent.ITEMS.register(modBus)","PathogenGameplayRuntime::onTick"):self.assertIn(key,mod)
  for key in ("PathogenGameplayRuntime.contact(","PathogenGameplayRuntime.clinic(","PathogenGameplayRuntime.simulateIllness("):self.assertIn(key,source)
  self.assertIn("getPersistentData()",runtime)
  self.assertIn("MobEffects.MOVEMENT_SLOWDOWN",runtime)
  for forbidden in ("forceChunk(","getAllEntities(","getChunk(","setHealth(","infectNearby("):self.assertNotIn(forbidden,runtime+source)
 def test_survival_crafting_models_loot_and_language(self):
  for id in IDS:
   block=json.loads((ASSET/"blockstates"/(id+".json")).read_text())
   self.assertEqual(block["variants"][""]["model"],"gugugaga:block/"+id)
   self.assertTrue((ASSET/"models/block"/(id+".json")).exists())
   self.assertTrue((ASSET/"models/item"/(id+".json")).exists())
   loot=json.loads((DATA/"loot_table/blocks"/(id+".json")).read_text())
   self.assertEqual(loot["pools"][0]["entries"][0]["name"],"gugugaga:"+id)
   recipe=json.loads((DATA/"recipe"/(id+".json")).read_text())
   self.assertEqual(recipe["type"],"minecraft:crafting_shaped")
   self.assertEqual(recipe["result"]["id"],"gugugaga:"+id)
  for language in ("en_us","zh_cn"):
   lang=json.loads((ASSET/"lang"/(language+".json")).read_text())
   for id in IDS:self.assertIn("block.gugugaga."+id,lang)
   for key in ("contact","illness","early_care","supportive_care"):self.assertIn("message.gugugaga.pathogen."+key,lang)
if __name__=="__main__":unittest.main()
