import io.github.lordnavycrypto.gugugaga.pathogens.PublicHealthSurveillance;
import java.io.StringReader;
import java.io.IOException;

public final class PublicHealthSurveillanceSmoke {
    static int checks=0;
    static void ok(boolean value,String why){
        checks++;if(!value)throw new AssertionError(why);
    }
    static void reject(String csv,String reason)throws Exception{
        try {
            PublicHealthSurveillance.parse(new StringReader(csv));
            throw new AssertionError("Invalid CSV accepted: "+reason);
        } catch(IOException expected){checks++;}
    }
    public static void main(String[] args) throws Exception {
        var series=PublicHealthSurveillance.loadBundled();
        ok(series.weeks().size()==4,"four sourced weeks in example");
        ok(series.latest().weekEnding().toString().equals("2026-10-17"),
                "end of CDC FluView 2026 week41");
        ok(series.latest().tested()==51490 && series.latest().positive()==2918,
                "actual CDC clinical samples, not infected people");
        ok(series.weeks().get(1).tested()==53137
                && series.weeks().get(1).positive()==2070,
                "week39 uses week40 retrospective revised clinical result");
        ok(series.latest().positivity()>0.056 && series.latest().positivity()<0.058,
                "latest observed positive fraction not fictional game level");
        long testSum=0,posSum=0;
        for(var row:series.weeks()){
            testSum+=row.tested();posSum+=row.positive();
            ok(row.sourceUrl().startsWith("https://www.cdc.gov/"),
                    "each observation retains dated official provenance");
        }
        ok(testSum==series.sumTested()&&posSum==series.sumPositive(),
                "denominator and numerator provenance accounting");
        var a=PublicHealthSurveillance.resampledReportingScenario(series,42L);
        var b=PublicHealthSurveillance.resampledReportingScenario(series,42L);
        ok(a.equals(b),"fixed-seed reproducibility");
        ok(a.p05()<=a.median() && a.median()<=a.p95(),
                "scenario uncertainty quantiles ordered");
        ok(a.p95()<=series.latest().tested(),
                "positive specimen scenarios cannot exceed tests");
        ok(a.draws()==256,"bounded independent scenario draws");
        String summary=PublicHealthSurveillance.summary(series);
        ok(summary.contains("OBSERVED") && summary.contains("NOT cases"),
                "never mislabel laboratory positives as cases or incidence");
        ok(summary.contains("ILLUSTRATIVE empirical resampling"),
                "scenario is explicitly non-predictive");

        String header="week_ending,tests,positives,source_url\n";
        String source="https://www.cdc.gov/fluview/surveillance/2026-week-40.html";
        reject(header+"2026-10-10,3,4,"+source+"\n","more positives than tested");
        reject(header+"2026-10-10,-1,0,"+source+"\n","negative denominator");
        reject(header+"2026-10-10,2,1,http://invalid.test\n",
                "non-HTTPS provenance");
        reject(header+"2026-10-10,2,1,"+source+"\n"
                +"2026-10-10,2,1,"+source+"\n","duplicate week");
        reject(header+"2026-10-10,2,1,"+source+"\n"
                +"2026-10-24,2,1,"+source+"\n","silent gap disallowed");
        reject("week,foo\n2026-10-10,2,1,"+source+"\n","wrong schema");
        System.out.println("V20_SOURCED_PUBLIC_HEALTH_PASS checks="+checks
                +" provenance=true empiricalOnly=true notInfectionCases=true");
    }
}
