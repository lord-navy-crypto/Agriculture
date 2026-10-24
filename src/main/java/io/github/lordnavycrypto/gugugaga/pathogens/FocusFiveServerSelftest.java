package io.github.lordnavycrypto.gugugaga.pathogens;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

/**
 * Actual Minecraft HolderLookup NBT codec check for BOTH SavedData schemas.
 * Enabled only in ephemeral CI. This is memory-only scratch data, never world
 * infection, pathogen replication, or an operation on an existing user save.
 */
public final class FocusFiveServerSelftest {
    private FocusFiveServerSelftest(){}
    public static final String PASS="GUGUGAGA_FIVE_FOCUS_NATIVE_NBT_PASS";
    private static void yes(boolean result,String reason){
        if(!result)throw new IllegalStateException("FOCUS_FIVE_NBT_FAILURE: "+reason);
    }
    public static void onServerStarted(ServerStartedEvent event){
        if(!"1".equals(System.getenv("GUGU_FIELD_SELFTEST")))return;
        var registry=event.getServer().registryAccess();
        var core=PathogenSpatialFieldSavedData.load(new CompoundTag(),registry);
        var extra=EcologyExtendedSavedData.load(new CompoundTag(),registry);
        var at=new PathogenSpatialFieldMath.Cell(-101,83);
        for(var profile:FocusFiveProfiles.FIVE){
            boolean success=profile.store()==FocusFiveProfiles.Store.ORIGINAL_15
                    ?core.set(at,profile.channel(),12)
                    :extra.set(at,profile.channel(),12);
            yes(success,"all five focus labels can occupy same game area");
        }
        yes(core.size()==1&&extra.size()==1,"independent old/new game files");
        var c2=PathogenSpatialFieldSavedData.load(
                core.save(new CompoundTag(),registry),registry);
        var e2=EcologyExtendedSavedData.load(
                extra.save(new CompoundTag(),registry),registry);
        int[] coreValues=new int[15],extraValues=new int[8];
        for(int i=0;i<15;i++)coreValues[i]=c2.get(at,i);
        for(int i=0;i<8;i++)extraValues[i]=e2.get(at,i);
        for(var profile:FocusFiveProfiles.FIVE){
            yes(FocusFiveReadout.gameValue(profile,coreValues,extraValues)==12,
                    "Minecraft native NBT preserved "+profile.id());
        }
        yes(FocusFiveReadout.compatible("minecraft:chicken",true,
                coreValues,extraValues).size()==2,"chicken two-reference display");
        yes(FocusFiveReadout.compatible("minecraft:wheat",false,
                coreValues,extraValues).size()==1,"wheat from extension file");
        yes(FocusFiveReadout.compatible("minecraft:bee",true,
                coreValues,extraValues).size()==1,"bee from extension file");
        yes(FocusFiveReadout.compatible("minecraft:player",true,
                coreValues,extraValues).isEmpty(),"no person infection status");
        yes(e2.set(at,EcologyExtendedTaxa.index("varroa_destructor"),0),
                "clear game art example");
        yes(core.get(at,PathogenSpatialFieldMath.channel("potato_virus_y"))==12,
                "extension value changes never alter original field");
        System.out.println(PASS+
            " fiveFocus=true independentNBT=true noNewPathogenBlocks=true");
    }
}
