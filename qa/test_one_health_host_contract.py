"""Native host relation view distinguishes evidence association from transmission."""
import json
import unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
SRC=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga"
LANG=ROOT/"src/main/resources/assets/gugugaga/lang"
class HostEvidenceContract(unittest.TestCase):
    def test_native_host_graph(self):
        atlas=(SRC/"client/OneHealthScreen.java").read_text()
        screen=(SRC/"client/OneHealthHostScreen.java").read_text()
        catalog=(SRC/"microbes/OneHealthHostEvidenceCatalog.java").read_text()
        self.assertIn("new OneHealthHostScreen(",atlas)
        self.assertIn("OneHealthHostEvidenceCatalog.byProfile(",screen)
        self.assertIn("relation.sourceUrl()",screen)
        self.assertIn("new OneHealthHistoryScreen(",screen)
        self.assertIn("ANIMAL_DETECTION_REPORTED",catalog)
        self.assertIn("HUMAN_CASE_REPORTED",catalog)
        for forbidden in ["sendToServer","sendCommand","setHealth(","addEffect(",
                          "player.hurt(","infectionProbability","transmissionChance"]:
            self.assertNotIn(forbidden,screen+catalog)
    def test_bilingual_keys(self):
        for lang in ("en_us.json","zh_cn.json"):
            labels=json.loads((LANG/lang).read_text())
            for key in ("open","title","previous","next","virus","source",
                        "timeline","back","hostlabel","association","notspread"):
                self.assertTrue(labels["screen.gugugaga.onehealth.host."+key])
if __name__=="__main__":unittest.main()
