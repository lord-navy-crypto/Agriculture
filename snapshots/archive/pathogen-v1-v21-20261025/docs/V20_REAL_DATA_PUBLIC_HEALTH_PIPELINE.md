# V20 — Real observations, public-health reporting scenarios, Minecraft viewer

## Exact scientific scope
V20 is the first operational **real-source-data → validated scientific observations → reproducible aggregate reporting scenario → Minecraft command** workflow.

It uses published **clinical laboratory specimens tested and positive**, not infection counts, prevalence, infected people, transmission coefficients, or environmental concentrations. No numerical values are invented. The reported **empirical Monte Carlo scenario is not a predictive disease model**, and none of these public health data affect vanilla animals, players, crops, game fields or any pathogen transmission routine.

## Concrete bundled real observations
Data resource: `src/main/resources/data/gugugaga/publichealth/us_fluview_clinical_2026_weeks38_41.csv`

Each dated record includes a separately checkable primary CDC report URL:

| CDC surveillance week ending | Tested specimens | Positive specimens | Official source |
| --- | ---: | ---: | --- |
| 2026-09-26 | 33,547 | 1,151 | https://www.cdc.gov/fluview/surveillance/2026-week-38.html |
| 2026-10-03 | 53,137 | 2,070 | https://www.cdc.gov/fluview/surveillance/2026-week-40.html (retrospective week-39 figures, revised) |
| 2026-10-10 | 45,331 | 2,270 | https://www.cdc.gov/fluview/surveillance/2026-week-40.html |
| 2026-10-17 | 51,490 | 2,918 | https://www.cdc.gov/fluview/surveillance/2026-week-41.html |

The October 3 row intentionally takes the **later report's revised week-39 column**. Published surveillance data are preliminary and can be revised as reports arrive. Data are *clinical lab specimens*, not public-health lab subtyping counts and not flu cases. **Do not claim these sample counts represent all influenza infections in the United States.**

## How the in-game workflow works
Run `/gugugaga field surveillance` as a player. The read-only command:
1. Loads the packaged, source-attributed series; if a successfully imported WHO resource was packaged, uses that instead of the CDC sample.
2. Checks the CSV schema, source URLs, weekly continuity, positive <= tested and 520-row cap.
3. Displays the latest observed test-positive fraction, a pooled four-week fraction, source URL and an **illustrative, deterministic 256-draw empirical reporting scenario**.
4. The empirical scenario draws one of the *observed last four weekly test-positive fractions* with replacement, then scales to the **latest observed number of tests**; it shows 5th/50th/95th percentile illustrative positive-specimen numbers. It is not a prediction of future real transmission, epidemic trajectory, infection count or patient probability.

No source data are used as real-pathogen coefficients. The reader is a self-contained Java 21 class.

## Import official WHO FluNet CSV without fabricating values
WHO primary download page: https://www.who.int/tools/flunet
Official surveillance data page: https://www.who.int/teams/global-influenza-programme/surveillance-and-monitoring/influenza-surveillance-outputs

1. Download the WHO FluNet CSV from the primary website, preserving the original file.
2. Run the local, offline importer:

```bash
python3 tools/publichealth/import_who_flunet.py \
  --input /path/to/VIW_FNT.csv \
  --country "Selected WHO country name" \
  --retrieved-on 2026-10-25 \
  --output src/main/resources/data/gugugaga/publichealth/imported_weekly_observations.csv
```

3. Inspect the `imported_weekly_observations.manifest.json` (SHA-256 of raw source, retrieval date, country, source URL, field definitions, exclusions and date range). Commit the data deliberately only after verifying data-use terms, then rebuild the mod.

The WHO importer handles the published fields `COUNTRY_AREA_TERRITORY`, `ISO_WEEKSTARTDATE`, `SPEC_PROCESSED_NB`, `INF_ALL`. It rejects duplicate country-week records (they can indicate overlapping surveillance strata), impossible or negative values, future weeks, fewer than 4 consecutive observations and unknown schemas. Missing weeks are never interpolated and values are never estimated. It picks the latest uninterrupted run (max 52 weeks by default).

Use one verified surveillance stratum. The raw WHO export may contain separate site/reporting strata; **do not simply sum overlapping records**. Always verify the current WHO dictionary before processing a new export.

## Validation and limitations
- `qa/java/PublicHealthSurveillanceSmoke.java`: real bundled CDC figures, dates, source provenance, 256-draw reproducibility, bounds, malformed data, missing weeks, mistaken "cases" language.
- `qa/test_v20_public_health_pipeline.py`: WHO import, provenance hash, missing-data behavior, impossible values, duplicate-week rejection.
- Java test is mandatory in GitHub Actions. Python test runs in existing source integrity unittest discovery. NeoForge compilation, true server and client startup and two-process SavedData restart remain required.
- No real-world predictive accuracy, statistical calibration or bias correction has been demonstrated. The resampling percentiles cover only the empirical historical-scenario spread, **not true surveillance measurement uncertainty or an infection forecast**.
- The existing V19 Minecraft habitat observation is a separate real *game-world* observation; it is not evidence of real-world weather, temperatures, pathogenic fitness or transport.
- No additional NBT schema, automatic download, online tracking, infection state, new entities/blocks, or multiplayer broadcast was added.

**Keep PR #44 Draft. Do not merge on code/tests alone; require exact HEAD CI and in-game visual checks.**
