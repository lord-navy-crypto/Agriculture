"""V21 validation: 15 real disease-agent identities, sources, and truthful host proxy labels."""
import unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
JAVA=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga"
class V21Catalog(unittest.TestCase):
    def test_15_agents_reuse_original_catalog_and_disclose_proxies(self):
        roster=(JAVA/"pathogens/PathogenRoster15.java").read_text()
        facts=(JAVA/"pathogens/ScientificTaxonProfiles15.java").read_text()
        host=(JAVA/"pathogens/VanillaHostMapping15.java").read_text()
        self.assertEqual(roster.count('new Entry('),15)
        self.assertEqual(facts.count('new Profile('),15)
        self.assertIn("PathogenRoster15.byId(taxonId)",facts)
        self.assertIn("VanillaHostMapping15.byId(taxonId).visualProxy()",facts)
        self.assertIn("No agent-specific calibrated timeseries imported",facts)
        self.assertIn('if(ALL.size()!=15||BY_ID.size()!=15',facts)
        self.assertIn('foot_and_mouth_disease_virus',facts)
        self.assertIn('phytophthora_infestans',facts)
        self.assertIn('xanthomonas_campestris',facts)
        self.assertIn('erwinia_amylovora',facts)
        self.assertIn('Set.of("minecraft:beetroots"),true',host)
        self.assertIn("never a pathogen measurement",facts.lower())
    def test_read_only_player_research_commands(self):
        command=(JAVA/"command/PathogenSpatialFieldCommands.java").read_text()
        self.assertIn('Commands.literal("research")',command)
        self.assertIn('Commands.literal("list")',command)
        self.assertIn("ScientificTaxonProfiles15.byId(taxonId)",command)
        self.assertIn("ScientificTaxonProfiles15.ALL",command)
        self.assertIn("NOT proof of host infection",command)
        workflow=(ROOT/".github/workflows/build.yml").read_text()
        self.assertIn("ScientificTaxonProfiles15Smoke.java",workflow)
        profiles=(JAVA/"pathogens/ScientificTaxonProfiles15.java").read_text()
        for disallowed in ("infect(", "setHealth(", "addEffect(", "setBlock(",
                           "transmissionProbability(", "forceChunk("):
            self.assertNotIn(disallowed,profiles)
if __name__=="__main__":unittest.main()
