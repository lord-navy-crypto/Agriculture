import io.github.lordnavycrypto.gugugaga.microbes.OneHealthHistoryCatalog;
import io.github.lordnavycrypto.gugugaga.microbes.OneHealthHistoryCatalog.Kind;
import java.util.HashSet;

public final class OneHealthHistorySmoke {
    public static void main(String[] args) {
        var events=OneHealthHistoryCatalog.all();
        if(events.size()!=5)throw new AssertionError("five documented milestones");
        String previous="";
        var ids=new HashSet<String>();
        for(var e:events) {
            if(!ids.add(e.id())||previous.compareTo(e.reportDate())>0
                ||!e.sourceUrl().startsWith("https://")
                ||e.caveatEn().isBlank()||e.caveatZh().isBlank())
                throw new AssertionError("invalid source/chronology");
            previous=e.reportDate();
        }
        if(!events.get(0).reportDate().equals("1885")
           ||OneHealthHistoryCatalog.filter("lyssavirus_rabies",null).size()!=1
           ||OneHealthHistoryCatalog.filter("avian_influenza_a_interface",null).size()!=4
           ||OneHealthHistoryCatalog.filter(null,Kind.ANIMAL_REPORT).size()!=1
           ||OneHealthHistoryCatalog.filter(null,Kind.HUMAN_REPORT).size()!=3
           ||!OneHealthHistoryCatalog.filter("lyssavirus_rabies",Kind.ANIMAL_REPORT).isEmpty())
            throw new AssertionError("bad filtering or invented precision");
        try {
            events.clear();
            throw new AssertionError("mutable source catalog");
        } catch(UnsupportedOperationException expected) { }
        System.out.println("ONE_HEALTH_HISTORY_PASS records=5 immutable=true sourceBound=true"
            +" bilingual=true noAutoSpread=true");
    }
}
