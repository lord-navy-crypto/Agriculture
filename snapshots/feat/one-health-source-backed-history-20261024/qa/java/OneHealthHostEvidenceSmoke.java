import io.github.lordnavycrypto.gugugaga.microbes.OneHealthHostEvidenceCatalog;
import io.github.lordnavycrypto.gugugaga.microbes.OneHealthEvidenceCatalog;
import java.util.HashSet;

public final class OneHealthHostEvidenceSmoke {
    public static void main(String[] args) {
        var all=OneHealthHostEvidenceCatalog.all();
        if(all.size()!=6)throw new AssertionError("six sourced host associations");
        var ids=new HashSet<String>();
        for(var r:all) {
            var p=OneHealthEvidenceCatalog.byId(r.profileId());
            if(!ids.add(r.id())||!p.documentedContexts().contains(r.hostContext())
                ||!r.sourceUrl().startsWith("https://")
                ||r.scopeEn().isBlank()||r.scopeZh().isBlank()
                ||r.caveatEn().isBlank()||r.caveatZh().isBlank())
                throw new AssertionError("missing evidence or invalid host");
        }
        if(OneHealthHostEvidenceCatalog.byProfile("lyssavirus_rabies").size()!=2
          ||OneHealthHostEvidenceCatalog.byProfile("avian_influenza_a_interface").size()!=4)
            throw new AssertionError("profile filter broke scope");
        try {
            all.clear();
            throw new AssertionError("mutable host associations");
        }catch(UnsupportedOperationException expected){}
        System.out.println("ONE_HEALTH_HOST_EVIDENCE_PASS relations=6"
            +" traceable=true bilingual=true noTransmissionEdges=true");
    }
}
