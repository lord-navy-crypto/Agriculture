"""Native, bilingual, read-only, sourced historical One Health GUI contract."""
import json
import unittest
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]
SRC=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga"
LANG=ROOT/"src/main/resources/assets/gugugaga/lang"
KEYS=["title","previous","next","virus","category","source","back",
      "empty","sourcelabel","sourcecaveat","filter.all","filter.rabies",
      "filter.avian","filter.animal","filter.human","filter.publichealth"]

class OneHealthHistoryContract(unittest.TestCase):
    def test_native_navigation_and_no_game_state_change(self):
        atlas=(SRC/"client/OneHealthScreen.java").read_text()
        timeline=(SRC/"client/OneHealthHistoryScreen.java").read_text()
        catalog=(SRC/"microbes/OneHealthHistoryCatalog.java").read_text()
        self.assertIn("new OneHealthHistoryScreen(",atlas)
        self.assertIn("OneHealthHistoryCatalog.filter(",timeline)
        self.assertIn("event.sourceUrl()",timeline)
        self.assertIn("entries.isEmpty()",timeline)
        self.assertIn("event.caveat(zh)",timeline)
        self.assertIn("2024-DON512",catalog)
        self.assertIn("20_8_97.html",catalog)
        for prohibited in ["sendToServer","sendCommand","setHealth(",
            "addEffect(","player.hurt(","infectionProbability","transmissionChance"]:
            self.assertNotIn(prohibited,timeline+catalog)

    def test_bilingual_complete(self):
        for language in ["en_us.json","zh_cn.json"]:
            data=json.loads((LANG/language).read_text())
            self.assertTrue(data["screen.gugugaga.onehealth.history"])
            for key in KEYS:
                self.assertTrue(data["screen.gugugaga.onehealth.history."+key])

if __name__=="__main__":
    unittest.main()
