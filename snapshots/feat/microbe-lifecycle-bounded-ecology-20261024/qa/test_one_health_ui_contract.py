"""Real One Health user interface is native, bilingual and strictly read-only."""
from pathlib import Path
import json
import unittest

ROOT=Path(__file__).resolve().parents[1]
SRC=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga"

class OneHealthUiContract(unittest.TestCase):
    def test_h_menu_has_native_readonly_atlas_button(self):
        h=(SRC/"client/NightwatchScreen.java").read_text()
        atlas=(SRC/"client/OneHealthScreen.java").read_text()
        self.assertIn('new OneHealthScreen(this)',h)
        self.assertIn('screen.gugugaga.onehealth.open',h)
        self.assertIn('OneHealthEvidenceCatalog.all()',atlas)
        self.assertIn('profile.humanImpact()',atlas)
        self.assertIn('profile.animalImpact()',atlas)
        self.assertIn('profile.uncertainty()',atlas)
        self.assertIn('profile.ncbiTaxonId()',atlas)
        self.assertIn('Component.translatable("screen.gugugaga.onehealth.title")',
                      atlas)
        self.assertIn('Minecraft.getInstance().setScreen(parent)',atlas)
        for prohibited in ('sendToServer','sendCommand','player.hurt(',
                           'setHealth(','addEffect(','spawn(','getEntities('):
            self.assertNotIn(prohibited,atlas)

    def test_bilingual_english_and_chinese_keys_match(self):
        lang=ROOT/"src/main/resources/assets/gugugaga/lang"
        en=json.loads((lang/"en_us.json").read_text())
        zh=json.loads((lang/"zh_cn.json").read_text())
        keys=[
            'open','title','previous','next','details','back',
            'noautomatic','sources'
        ]
        for key in keys:
            full='screen.gugugaga.onehealth.'+key
            self.assertIn(full,en)
            self.assertIn(full,zh)
            self.assertTrue(en[full].strip())
            self.assertTrue(zh[full].strip())

    def test_no_fictional_epidemiological_constants(self):
        source=(SRC/"microbes/OneHealthEvidenceCatalog.java").read_text()
        policy=(SRC/"microbes/OneHealthObservationPolicy.java").read_text()
        self.assertIn('11292',source)
        self.assertIn('11320',source)
        self.assertIn('https://www.cdc.gov/rabies/about/',source)
        self.assertIn('https://www.who.int/news-room/fact-sheets/',source)
        for forbidden in ('infectionProbability','transmissionChance',
                          'growthRate','replicationRate','incubationTicks',
                          'infectPlayer','addInfection','mutateStrain'):
            self.assertNotIn(forbidden,source+policy)
        self.assertIn('EXTERNAL_RECORD_REQUIRES_INDEPENDENT_VERIFICATION',
                      policy)
        self.assertIn('HUMAN_ILLNESS_NOT_DIAGNOSED',policy)

if __name__=="__main__":
    unittest.main()
