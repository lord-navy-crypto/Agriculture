#!/usr/bin/env python3
"""Import one national WHO FluNet series into the mod's scientific data layer.

Offline, deterministic and read-only. Fields are **laboratory specimens**,
never confirmed infected individuals or infection probabilities.
No biological transmission parameters are inferred.
"""
from __future__ import annotations

import argparse
import csv
import datetime as dt
import hashlib
import json
from pathlib import Path

SOURCE_URL = "https://www.who.int/tools/flunet"
FIELD_NAMES = (
    "COUNTRY_AREA_TERRITORY",
    "ISO_WEEKSTARTDATE",
    "SPEC_PROCESSED_NB",
    "INF_ALL",
)


def import_country(raw_path: Path, country: str, retrieved: str,
                   output_path: Path, max_weeks: int = 52) -> dict:
    retrieved_on = dt.date.fromisoformat(retrieved)
    if retrieved_on > dt.date.today():
        raise ValueError("Retrieval date cannot be in the future")
    if not country.strip():
        raise ValueError("Country name is required")
    if max_weeks < 4 or max_weeks > 520:
        raise ValueError("max_weeks must be between 4 and 520")

    raw_bytes = raw_path.read_bytes()
    checksum = hashlib.sha256(raw_bytes).hexdigest()
    records: dict[dt.date, tuple[int, int]] = {}
    skipped_missing = 0
    matched = 0
    with raw_path.open("r", newline="", encoding="utf-8-sig") as handle:
        reader = csv.DictReader(handle)
        if not reader.fieldnames:
            raise ValueError("CSV is missing a header")
        column_map = {name.strip().upper(): name for name in reader.fieldnames}
        if set(FIELD_NAMES) - set(column_map):
            raise ValueError("Missing required WHO FluNet columns: "
                             + ", ".join(sorted(set(FIELD_NAMES)-set(column_map))))
        for row in reader:
            value = (row[column_map["COUNTRY_AREA_TERRITORY"]] or "").strip()
            if value.casefold() != country.casefold():
                continue
            matched += 1
            date_raw = (row[column_map["ISO_WEEKSTARTDATE"]] or "").strip()[:10]
            tests_raw = (row[column_map["SPEC_PROCESSED_NB"]] or "").strip()
            positive_raw = (row[column_map["INF_ALL"]] or "").strip()
            if not date_raw or not tests_raw or not positive_raw:
                skipped_missing += 1
                continue
            week_start = dt.date.fromisoformat(date_raw)
            if week_start.weekday() != 0:
                raise ValueError("Expected ISO week-start Monday")
            if week_start > retrieved_on:
                raise ValueError("Observation week cannot start after retrieval")
            if not tests_raw.isdigit() or not positive_raw.isdigit():
                raise ValueError(f"Non-integer specimen count for {week_start}")
            tested, positive = int(tests_raw), int(positive_raw)
            if tested <= 0 or positive > tested or tested > 1_000_000_000:
                raise ValueError(f"Inconsistent specimen counts for {week_start}")
            # Do not incorrectly sum country-week rows that might represent
            # different overlapping sentinel/non-sentinel reporting strata.
            if week_start in records:
                raise ValueError(f"Duplicate WHO country-week {week_start}; "
                                 "filter the export to one surveillance stratum")
            records[week_start] = (tested, positive)
    if not matched:
        raise ValueError(f"No FluNet observations for country {country!r}")
    if not records:
        raise ValueError("Country has no usable non-missing weekly observations")

    # Use the latest uninterrupted weekly run; never interpolate missing data.
    weeks = sorted(records)
    segment = [weeks[-1]]
    for i in range(len(weeks) - 2, -1, -1):
        if (segment[0] - weeks[i]).days != 7:
            break
        segment.insert(0, weeks[i])
    segment = segment[-max_weeks:]
    if len(segment) < 4:
        raise ValueError("Fewer than four consecutive observed reporting weeks")

    output_path.parent.mkdir(parents=True, exist_ok=True)
    with output_path.open("w", encoding="utf-8", newline="") as out:
        writer = csv.writer(out, lineterminator="\n")
        writer.writerow(("week_ending", "tests", "positives", "source_url"))
        for start in segment:
            tests, positives = records[start]
            writer.writerow((str(start + dt.timedelta(days=6)),
                             tests, positives, SOURCE_URL))
    metadata = {
        "dataset": "WHO FluNet laboratory virological surveillance",
        "country": country,
        "retrieved_on": str(retrieved_on),
        "source_url": SOURCE_URL,
        "raw_sha256": checksum,
        "metric_numerator": "positive laboratory specimens (INF_ALL)",
        "metric_denominator": "processed specimens (SPEC_PROCESSED_NB)",
        "not_infection_cases": True,
        "not_population_incidence": True,
        "matched_country_rows": matched,
        "excluded_missing_rows": skipped_missing,
        "first_week_start": str(segment[0]),
        "last_week_start": str(segment[-1]),
        "imported_weeks": len(segment),
        "excluded_noncontiguous_weeks": len(records)-len(segment),
        "forecast_claim": False,
    }
    output_path.with_suffix(".manifest.json").write_text(
        json.dumps(metadata, ensure_ascii=False, indent=2, sort_keys=True) + "\n",
        encoding="utf-8")
    return metadata


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--input", required=True, type=Path)
    parser.add_argument("--country", required=True)
    parser.add_argument("--retrieved-on", required=True,
                        help="The real retrieval date YYYY-MM-DD")
    parser.add_argument("--output", required=True, type=Path)
    parser.add_argument("--max-weeks", type=int, default=52)
    options = parser.parse_args()
    metadata = import_country(options.input, options.country,
                              options.retrieved_on, options.output,
                              options.max_weeks)
    print(json.dumps(metadata, indent=2, ensure_ascii=False))


if __name__ == "__main__":
    main()
