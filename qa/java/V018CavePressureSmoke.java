import io.github.lordnavycrypto.gugugaga.ai.CaveThreatSoundPolicy;
public final class V018CavePressureSmoke{
 static void ok(boolean v,String m){if(!v)throw new AssertionError(m);}
 public static void main(String[] a){
   ok(CaveThreatSoundPolicy.classify(true,true,true,4)==CaveThreatSoundPolicy.Pressure.IMMEDIATE,"close");
   ok(CaveThreatSoundPolicy.classify(true,true,true,81)==CaveThreatSoundPolicy.Pressure.NEAR,"near");
   ok(CaveThreatSoundPolicy.classify(true,true,true,225)==CaveThreatSoundPolicy.Pressure.DISTANT,"distant");
   ok(CaveThreatSoundPolicy.classify(true,true,false,4)==CaveThreatSoundPolicy.Pressure.NONE,"no fake monster");
   ok(CaveThreatSoundPolicy.classify(false,true,true,4)==CaveThreatSoundPolicy.Pressure.NONE,"optout");
   ok(CaveThreatSoundPolicy.classify(true,false,true,4)==CaveThreatSoundPolicy.Pressure.NONE,"not cave");
   for(int n=0;n<100000;n++){
     var p=CaveThreatSoundPolicy.classify(true,true,true,(n%24)*(n%24));
     float v=CaveThreatSoundPolicy.volume(p);
     ok(v>=0&&v<=CaveThreatSoundPolicy.MAX_VOLUME,"volume");
     ok(CaveThreatSoundPolicy.pitch(p)>=0.5f,"pitch");
     if(n!=0&&n%CaveThreatSoundPolicy.PERIOD_TICKS==0)
       ok(CaveThreatSoundPolicy.due(n),"cadence");
   }
   System.out.println("GUGUGAGA_V018_CAVE_PRESSURE_PASS 100000");
 }
}
