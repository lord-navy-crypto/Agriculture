package io.github.lordnavycrypto.gugugaga.pathogens;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

/**
 * Optional CI-only, real NeoForge server-side NBT round-trip.
 * Never writes to the actual world SavedData store. Enabled exclusively
 * by GUGU_FIELD_SELFTEST=1 in the repository's ephemeral CI server.
 */
public final class PathogenFieldServerSavedDataSmoke {
    private PathogenFieldServerSavedDataSmoke(){}
    public static final String PASS="GUGUGAGA_SPATIAL_SAVEDDATA_ROUNDTRIP_PASS";

    public static void onServerStarted(ServerStartedEvent event){
        if(!"1".equals(System.getenv("GUGU_FIELD_SELFTEST")))return;
        var registry=event.getServer().registryAccess();
        var scratch=PathogenSpatialFieldSavedData.load(new CompoundTag(),registry);
        int index=PathogenSpatialFieldMath.channel("potato_virus_y");
        var a=new PathogenSpatialFieldMath.Cell(-7,11);
        var b=new PathogenSpatialFieldMath.Cell(-6,11);
        var other=PathogenSpatialFieldMath.channel("lyssavirus_rabies");

        check(scratch.set(a,index,13),"seed virtual source");
        check(scratch.set(b,index,2),"seed virtual destination");
        check(scratch.set(a,other,9),"independent catalog channel");
        check(scratch.size()==2,"two sparse cells");
        var routes=scratch.routeCandidates(1);
        check(routes.size()==1,"budgeted route-source scan");

        // Real Minecraft/NeoForge HolderLookup-compatible serialization.
        var encoded=scratch.save(new CompoundTag(),registry);
        check(encoded.contains("Cells"),"actual NBT rows produced");
        check(encoded.getInt("RouteCursor")==1,"fairness cursor serialized");
        var loaded=PathogenSpatialFieldSavedData.load(encoded,registry);
        check(loaded.get(a,index)==13,"source value survives NBT read");
        check(loaded.get(b,index)==2,"destination survives NBT read");
        check(loaded.get(a,other)==9,"independent pathogen channel survives");
        check(loaded.routeCandidates(1).size()==1,"loaded cursor is usable");

        // Copy-on-save must protect the old scratch data.
        check(loaded.transfer(a,b,index,
                PathogenRoutePolicy15.Route.VIRTUAL_VECTOR,true),
                "game-only route between two loaded virtual cells");
        check(loaded.get(a,index)==12&&loaded.get(b,index)==3,
                "fast in-place transfer changes two game counters");
        check(scratch.get(a,index)==13&&scratch.get(b,index)==2,
                "separate SavedData instances never share arrays");

        var second=loaded.save(new CompoundTag(),registry);
        var restarted=PathogenSpatialFieldSavedData.load(second,registry);
        check(restarted.get(a,index)==12&&restarted.get(b,index)==3,
                "post-transfer numbers survive repeated NBT round-trip");
        check(restarted.get(a,other)==9,"other taxon unaffected");
        restarted.set(a,index,0);
        restarted.set(a,other,0);
        restarted.set(b,index,0);
        check(restarted.size()==0,"empty virtual cells reclaimed");

        // No call to world.getDataStorage(): this cannot dirty an actual
        // player's world or leave a test taxon label behind after startup.
        System.out.println(PASS+" channels=15 gameCells=2 persistedCursor=true");
    }

    private static void check(boolean result,String description){
        if(!result)throw new IllegalStateException(
                "GUGUGAGA_SPATIAL_SAVEDDATA_TEST_FAILED "+description);
    }
}
