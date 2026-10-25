"""Game QA is mandatory and requires REAL world/actor/chat/key milestones."""
import pathlib,unittest
R=pathlib.Path(__file__).resolve().parents[1]
class Harness(unittest.TestCase):
 def test_real_game_flow_uses_physical_gui(self):
  s=(R/"qa/gameplay_e2e.py").read_text()
  for x in ("runClient","xdotool","WORLD_PLAYER_READY","/gugugaga play test triad",
            'click(830,280)','"LISTEN_V_KEY"','"STAGE_FALSE_SAFETY"',
            '"STAGE_RESPITE"','"LOOP_TO_STILLNESS"',
            "RuntimeError(\"ACTUAL GAMEPLAY TEST FAILED","results.json"):
   self.assertIn(x,s)
 def test_explicit_qa_gating(self):
  src=R/"src/main/java/io/github/lordnavycrypto/gugugaga"
  policy=(src/"ai/E2EGameplayProof.java").read_text()
  self.assertIn('Boolean.getBoolean("gugugaga.e2e")',policy)
  key=(src/"client/InWorldGameProof.java").read_text()
  self.assertIn("mc.player!=null && mc.level!=null",key)
  workflow=(R/".github/workflows/build.yml").read_text()
  self.assertIn("real-gameplay:",workflow)
  self.assertIn("gugugaga-actual-gameplay-e2e",workflow)
if __name__=="__main__":unittest.main()
