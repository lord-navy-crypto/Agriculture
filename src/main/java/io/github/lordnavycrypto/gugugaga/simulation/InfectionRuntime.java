package io.github.lordnavycrypto.gugugaga.simulation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Animal;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Runs the new Minecraft-only infection engine on existing vanilla animals.
 * One simulation frame per 20 game ticks, at most 64 nearby animals per world,
 * and never creates or edits pathogen Blocks or mobs. This is a working
 * gameplay state machine, not an organism-specific disease model.
 *
 * V0.1 uses memory-only state: saved-world persistence is NOT implemented.
 */
public final class InfectionRuntime {
    private InfectionRuntime() {}
    private static final int INTERVAL=20;
    private static final int MAX_VISUALS_PER_WORLD=8;
    private static final InfectionEngine.Config CONFIG=InfectionEngine.DEFAULT;

    /** Server identities are weakly held, no static growing global entity sets. */
    private static final Map<MinecraftServer,Map<String,Map<UUID,InfectionEngine.Host>>>
            STATES=new WeakHashMap<>();

    private static String key(ServerLevel world){
        return world.dimension().location().toString();
    }
    private static Map<UUID,InfectionEngine.Host> read(ServerLevel world){
        var dimensions=STATES.get(world.getServer());
        return dimensions==null?Map.of():
                dimensions.getOrDefault(key(world),Map.of());
    }
    public static void seed(ServerLevel world,Animal selected){
        var dimension=STATES.computeIfAbsent(world.getServer(),server->new HashMap<>());
        var tracked=dimension.computeIfAbsent(key(world),ignored->new HashMap<>());
        if(tracked.size()>=CONFIG.maxHosts()&&!tracked.containsKey(selected.getUUID()))
            throw new IllegalStateException("Minecraft infection test at 64-animal cap");
        tracked.put(selected.getUUID(),new InfectionEngine.Host(
                selected.getUUID(),InfectionEngine.Stage.INFECTIOUS,0,
                selected.getX(),selected.getY(),selected.getZ()));
    }
    public static void clear(ServerLevel world){
        var dimensions=STATES.get(world.getServer());
        if(dimensions!=null)dimensions.remove(key(world));
    }
    public static String status(ServerLevel world){
        int susceptible=0,exposed=0,infectious=0,recovered=0;
        for(var host:read(world).values()){
            switch(host.stage()){
                case SUSCEPTIBLE -> susceptible++;
                case EXPOSED -> exposed++;
                case INFECTIOUS -> infectious++;
                case RECOVERED -> recovered++;
            }
        }
        return "Minecraft-only active animals: S="+susceptible+" E="+exposed
                +" I="+infectious+" R="+recovered
                +" | memory-only; transitions every "+INTERVAL+" game ticks";
    }
    public static void onServerStopped(ServerStoppedEvent event){
        STATES.remove(event.getServer());
    }
    public static void onTick(ServerTickEvent.Post event){
        MinecraftServer server=event.getServer();
        for(ServerLevel world:server.getAllLevels()){
            if(world.getGameTime()%INTERVAL!=0)continue;
            // Only observe existing loaded animals near active players.
            LinkedHashMap<UUID,Animal> sampled=new LinkedHashMap<>();
            for(var participant:world.players()){
                if(!(participant instanceof ServerPlayer player))continue;
                List<Animal> animals=world.getEntitiesOfClass(Animal.class,
                        player.getBoundingBox().inflate(12),Animal::isAlive);
                animals.sort(Comparator.comparing(Animal::getUUID));
                for(Animal animal:animals){
                    if(sampled.size()>=CONFIG.maxHosts())break;
                    sampled.putIfAbsent(animal.getUUID(),animal);
                }
                if(sampled.size()>=CONFIG.maxHosts())break;
            }
            if(sampled.isEmpty())continue;
            var old=read(world);
            List<InfectionEngine.Host> observed=new ArrayList<>(sampled.size());
            for(var item:sampled.entrySet()){
                Animal animal=item.getValue();
                var previous=old.get(item.getKey());
                observed.add(previous==null
                    ?new InfectionEngine.Host(item.getKey(),
                        InfectionEngine.Stage.SUSCEPTIBLE,0,
                        animal.getX(),animal.getY(),animal.getZ())
                    :previous.at(animal.getX(),animal.getY(),animal.getZ()));
            }
            var frame=InfectionEngine.advance(observed,CONFIG);
            STATES.computeIfAbsent(server,ignored->new HashMap<>())
                    .put(key(world),new HashMap<>(frame.hosts()));

            // Bounded vanilla particles are a visual of the actual GAME state.
            int visuals=0;
            for(var item:frame.hosts().entrySet()){
                if(visuals>=MAX_VISUALS_PER_WORLD)break;
                if(item.getValue().stage()!=InfectionEngine.Stage.INFECTIOUS)continue;
                Animal animal=sampled.get(item.getKey());
                if(animal==null||!animal.isAlive())continue;
                world.sendParticles(ParticleTypes.ENCHANT,
                    animal.getX(),animal.getY()+animal.getBbHeight()+0.2,
                    animal.getZ(),1,0.12,0.12,0.12,0);
                visuals++;
            }
        }
    }
}
