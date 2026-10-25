import json
import unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
A=ROOT/"src/main/resources/assets/gugugaga"
D=ROOT/"src/main/resources/data/gugugaga"
J=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga"
IDS=("soil_bacillus_patch","plant_lactiplantibacillus_patch",
     "ecoli_microhabitat_display","t4_phage_microhabitat_display")
class NativeWorldContent(unittest.TestCase):
 def test_native_registration(self):
  mod=(J/"GuguGagaMod.java").read_text()
  code=(J/"microbes/MicrobeWorldPatchBlock.java").read_text()
  reg=(J/"microbes/MicrobeWorldContent.java").read_text()
  self.assertIn("MicrobeWorldContent.BLOCKS.register(modBus)",mod)
  self.assertIn("MicrobeWorldContent.ITEMS.register(modBus)",mod)
  self.assertIn("MicrobeWorldContent::onCreativeTab",mod)
  self.assertIn("CreativeModeTabs.NATURAL_BLOCKS",reg)
  self.assertIn("world.setBlock(pos,state.setValue(VIEW,",code)
  self.assertIn("public void randomTick(",code)
  self.assertIn("MicrobeLifecyclePolicy.advance(",code)
  self.assertIn("world.hasChunkAt(other)",code)
  self.assertIn("setValue(SATELLITE,true)",code)
  self.assertIn(".randomTicks()",reg)
  self.assertIn("NaturalMicrobeCatalog.documentedHostRelation(",code)
  self.assertIn("ParticleTypes.ENCHANT",code)
  self.assertIn("world.hasChunkAt(nearby)",code)
  for key in IDS: self.assertIn('"'+key+'"',reg)
  for bad in ("forceChunk(","getChunk(","setHealth(","addEffect(","infectPlayer("):
   self.assertNotIn(bad,reg+code)
 def test_block_assets_and_bilingual(self):
  langs=[json.loads((A/"lang"/(k+".json")).read_text()) for k in ("en_us","zh_cn")]
  for id in IDS:
   states=json.loads((A/"blockstates"/(id+".json")).read_text())["variants"]
   self.assertEqual(set(states),{f"view={v},satellite={str(s).lower()}" for v in range(3) for s in (False,True)})
   for n in range(3):
    p=states[f"view={n},satellite=false"]["model"].split(":")[1].split("/")[1]
    m=json.loads((A/"models/block"/(p+".json")).read_text())
    self.assertEqual(m["parent"],"minecraft:block/cross")
   item=json.loads((A/"models/item"/(id+".json")).read_text())
   self.assertEqual(item["parent"],"minecraft:item/generated")
   loot=json.loads((D/"loot_table/blocks"/(id+".json")).read_text())
   self.assertEqual(loot["pools"][0]["entries"][0]["name"],"gugugaga:"+id)
   for l in langs:
    self.assertIn("block.gugugaga."+id,l)
    self.assertIn("message.gugugaga.microbeworld."+id,l)
 def test_wild_generation_only_for_soil_and_leaf(self):
  for id in IDS[:2]:
   c=json.loads((D/"worldgen/configured_feature"/(id+".json")).read_text())
   p=json.loads((D/"worldgen/placed_feature"/(id+".json")).read_text())
   b=json.loads((D/"neoforge/biome_modifier"/(id+".json")).read_text())
   self.assertEqual(c["type"],"minecraft:simple_block")
   self.assertEqual(c["config"]["to_place"]["state"]["Name"],"gugugaga:"+id)
   self.assertEqual(p["feature"],"gugugaga:"+id)
   self.assertEqual(b["type"],"neoforge:add_features")
   self.assertEqual(b["features"],"gugugaga:"+id)
   self.assertEqual(b["step"],"vegetal_decoration")
  for id in IDS[2:]:
   self.assertFalse((D/"worldgen/placed_feature"/(id+".json")).exists())
if __name__=="__main__": unittest.main()
