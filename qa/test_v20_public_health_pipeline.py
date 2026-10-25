"""V20 regression tests for provenance-preserving WHO FluNet imports."""
import importlib.util
import json
import tempfile
import unittest
from pathlib import Path

MODULE = Path(__file__).resolve().parents[1] / "tools/publichealth/import_who_flunet.py"
spec = importlib.util.spec_from_file_location("import_who_flunet", MODULE)
assert spec and spec.loader
loader = importlib.util.module_from_spec(spec)
spec.loader.exec_module(loader)


class PublicHealthImportTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.raw = self.root / "WHO.csv"
        self.out = self.root / "observed.csv"
        self.header = ("COUNTRY_AREA_TERRITORY,ISO_WEEKSTARTDATE,"
                       "SPEC_PROCESSED_NB,INF_ALL\n")
    def write(self, body):
        self.raw.write_text(self.header+body, encoding="utf-8")

    def test_real_data_contract_preserves_counts_and_sources(self):
        self.write(
            "Exampleland,2026-09-28,100,12\n"
            "Exampleland,2026-10-05,120,18\n"
            "Exampleland,2026-10-12,90,9\n"
            "Exampleland,2026-10-19,100,15\n"
            "Otherland,2026-10-19,500,100\n"
        )
        meta=loader.import_country(self.raw, "Exampleland", "2026-10-25", self.out)
        lines=self.out.read_text().splitlines()
        self.assertEqual(len(lines),5)
        self.assertIn("2026-10-25,100,15,https://www.who.int/tools/flunet",
                      lines)
        self.assertIn("2026-10-04,100,12,https://www.who.int/tools/flunet",
                      lines)
        self.assertEqual(meta["imported_weeks"],4)
        self.assertTrue(meta["not_infection_cases"])
        self.assertFalse(meta["forecast_claim"])
        self.assertEqual(len(meta["raw_sha256"]),64)
        self.assertEqual(json.loads(self.out.with_suffix(".manifest.json").read_text())
                         ["metric_denominator"], "processed specimens (SPEC_PROCESSED_NB)")

    def test_reject_duplicate_country_week(self):
        self.write("Exampleland,2026-10-05,100,1\n"
                   "Exampleland,2026-10-05,200,1\n")
        with self.assertRaisesRegex(ValueError,"Duplicate"):
            loader.import_country(self.raw,"Exampleland","2026-10-25",self.out)

    def test_missing_week_never_interpolated(self):
        self.write("Exampleland,2026-09-28,100,10\n"
                   "Exampleland,2026-10-12,100,10\n"
                   "Exampleland,2026-10-19,100,10\n")
        with self.assertRaisesRegex(ValueError,"Fewer than four"):
            loader.import_country(self.raw,"Exampleland","2026-10-25",self.out)

    def test_reject_impossible_observation(self):
        self.write("Exampleland,2026-10-05,100,101\n")
        with self.assertRaisesRegex(ValueError,"Inconsistent"):
            loader.import_country(self.raw,"Exampleland","2026-10-25",self.out)

    def test_missing_data_not_silently_imputed(self):
        self.write("Exampleland,2026-09-21,100,8\n"
                   "Exampleland,2026-09-28,100,\n"
                   "Exampleland,2026-10-05,100,12\n"
                   "Exampleland,2026-10-12,120,12\n"
                   "Exampleland,2026-10-19,120,12\n"
                   "Exampleland,2026-10-26,120,12\n")
        meta=loader.import_country(self.raw,"Exampleland","2026-10-25",self.out)
        self.assertEqual(meta["excluded_missing_rows"],1)
        self.assertEqual(meta["imported_weeks"],4)
        self.assertNotIn("2026-09-27",self.out.read_text())

if __name__=="__main__": unittest.main()
