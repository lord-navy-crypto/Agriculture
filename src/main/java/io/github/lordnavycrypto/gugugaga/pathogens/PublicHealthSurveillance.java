package io.github.lordnavycrypto.gugugaga.pathogens;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.SplittableRandom;

/**
 * Data-driven, NATIONAL-AGGREGATE laboratory surveillance statistics.
 * Values count TESTED and POSITIVE SPECIMENS, never infected people.
 * The Monte Carlo empirical resampling scenario is NOT a mechanistic
 * transmission model, a forecast, a dose model, or a risk assessment.
 * Source attribution travels with each dated observation.
 */
public final class PublicHealthSurveillance {
    private PublicHealthSurveillance() {}
    public static final String BUNDLED_RESOURCE =
        "/data/gugugaga/publichealth/us_fluview_clinical_2026_weeks38_41.csv";
    private static final int MAX_ROWS = 520;
    public record Observation(LocalDate weekEnding,long tested,long positive,String sourceUrl) {
        public Observation {
            Objects.requireNonNull(weekEnding);
            Objects.requireNonNull(sourceUrl);
            if (tested < 1 || tested > 1_000_000_000L
                    || positive < 0 || positive > tested
                    || !sourceUrl.startsWith("https://")
                    || sourceUrl.contains(",") || sourceUrl.contains("\n"))
                throw new IllegalArgumentException("Invalid sourced specimen observation");
        }
        public double positivity() { return positive / (double)tested; }
    }
    public record Series(List<Observation> weeks) {
        public Series {
            Objects.requireNonNull(weeks);
            weeks=List.copyOf(weeks);
            if (weeks.isEmpty() || weeks.size()>MAX_ROWS)
                throw new IllegalArgumentException("Unexpected weekly dataset length");
            for (int i=1;i<weeks.size();i++) {
                if (ChronoUnit.DAYS.between(
                        weeks.get(i-1).weekEnding(),weeks.get(i).weekEnding())!=7)
                    throw new IllegalArgumentException(
                            "Weekly observations must be contiguous and ordered");
            }
        }
        public Observation latest() { return weeks.get(weeks.size()-1); }
        public long sumTested() {
            long n=0; for(var row:weeks)n=Math.addExact(n,row.tested());
            return n;
        }
        public long sumPositive() {
            long n=0; for(var row:weeks)n=Math.addExact(n,row.positive());
            return n;
        }
        public double pooledPositivity() { return sumPositive()/(double)sumTested(); }
    }
    public record Scenario(long baselineTestCount,int draws,long p05,long median,long p95) {
        public Scenario {
            if (baselineTestCount<1||draws<1||p05<0||p05>median
                    ||median>p95||p95>baselineTestCount)
                throw new IllegalArgumentException("Invalid scenario");
        }
    }
    public static Series parse(Reader reader) throws IOException {
        Objects.requireNonNull(reader);
        var rows=new ArrayList<Observation>();
        BufferedReader br=new BufferedReader(reader);
        String header=br.readLine();
        if (!"week_ending,tests,positives,source_url".equals(header))
            throw new IOException("Expected sourced weekly specimen CSV columns");
        String line;
        int lineNumber=1;
        while ((line=br.readLine())!=null) {
            lineNumber++;
            if (line.isBlank())continue;
            if (rows.size()>=MAX_ROWS)
                throw new IOException("Surveillance series exceeds resource cap");
            String[] fields=line.split(",",-1);
            if(fields.length!=4)
                throw new IOException("Malformed week entry at line "+lineNumber);
            try {
                rows.add(new Observation(LocalDate.parse(fields[0]),
                        Long.parseLong(fields[1]),Long.parseLong(fields[2]),
                        fields[3]));
            } catch (RuntimeException malformed) {
                throw new IOException("Invalid surveillance value at line "+lineNumber,
                        malformed);
            }
        }
        try {return new Series(rows);}
        catch(RuntimeException invalid){throw new IOException(invalid.getMessage(),invalid);}
    }
    public static Series loadBundled() throws IOException {
        try (InputStream in=PublicHealthSurveillance.class
                .getResourceAsStream(BUNDLED_RESOURCE)) {
            if(in==null)throw new IOException("No bundled sourced surveillance CSV");
            return parse(new InputStreamReader(in,StandardCharsets.UTF_8));
        }
    }

    /**
     * Resample four latest OBSERVED weekly laboratory positivity fractions.
     * For each simulated reporting scenario, hold the observed latest WEEKLY
     * TEST COUNT fixed. No infections, susceptible hosts, reproductive
     * ratios, spread mechanism or environment-derived disease coefficients.
     * This is a descriptive uncertainty/scenario exercise, NOT a forecast.
     */
    public static Scenario resampledReportingScenario(Series series,long seed) {
        Objects.requireNonNull(series);
        if(series.weeks().size()<4)
            throw new IllegalArgumentException("Need four consecutive observed weeks");
        final int draws=256;
        long tests=series.latest().tested();
        var history=series.weeks();
        var random=new SplittableRandom(seed);
        List<Long> results=new ArrayList<>(draws);
        for(int i=0;i<draws;i++) {
            Observation selected=history.get(
                    history.size()-4+random.nextInt(4));
            results.add(Math.round(tests*selected.positivity()));
        }
        Collections.sort(results);
        return new Scenario(tests,draws,
                results.get(12),results.get(draws/2),results.get(243));
    }
    public static String summary(Series series) {
        Observation week=series.latest();
        Scenario scenario=resampledReportingScenario(series,20261025L);
        return String.format(Locale.ROOT,
            "OBSERVED %s: %,d positive/%,d tested specimens (%.2f%%); "
            +"four-week pooled %.2f%%; source %s; "
            +"ILLUSTRATIVE empirical resampling for %,d tests: p05 %,d, "
            +"median %,d, p95 %,d positive specimens. "
            +"NOT cases, infections, forecast or transmission probability.",
            week.weekEnding(),week.positive(),week.tested(),
            100*week.positivity(),100*series.pooledPositivity(),
            week.sourceUrl(),scenario.baselineTestCount(),scenario.p05(),
            scenario.median(),scenario.p95());
    }
}
