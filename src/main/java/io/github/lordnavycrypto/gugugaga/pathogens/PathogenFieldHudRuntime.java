package io.github.lordnavycrypto.gugugaga.pathogens;

import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Optional, server-authoritative action-bar HUD. Disabled for every player
 * until that PLAYER explicitly runs '/gugugaga field hud on'.
 *
 * We use the native vanilla action bar, not custom payloads, textures,
 * pathogen blocks or client-only class loading on dedicated servers.
 */
public final class PathogenFieldHudRuntime {
    private PathogenFieldHudRuntime(){}
    private static final int HUD_INTERVAL=40; // Two seconds in nominal game time
    private static final WeakHashMap<MinecraftServer,Set<UUID>> ENABLED=
            new WeakHashMap<>();

    public static boolean isEnabled(MinecraftServer server,UUID player){
        var viewers=ENABLED.get(server);
        return viewers!=null && viewers.contains(player);
    }

    public static void setEnabled(ServerPlayer player,boolean enable){
        MinecraftServer server=player.serverLevel().getServer();
        if(enable)
            ENABLED.computeIfAbsent(server,ignored->new HashSet<>())
                    .add(player.getUUID());
        else {
            var viewers=ENABLED.get(server);
            if(viewers!=null){
                viewers.remove(player.getUUID());
                if(viewers.isEmpty())ENABLED.remove(server);
            }
        }
    }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event){
        if(!(event.getEntity() instanceof ServerPlayer player))return;
        setEnabled(player,false);
    }

    public static void onTick(ServerTickEvent.Post event){
        var server=event.getServer();
        if(!NightwatchConfig.PATHOGEN_FIELD_ENABLED.get() ||
                server.overworld().getGameTime()%HUD_INTERVAL!=0L)return;
        for(ServerPlayer player:server.getPlayerList().getPlayers()){
            if(!isEnabled(server,player.getUUID()))continue;
            var world=player.serverLevel();
            var at=PathogenSpatialFieldRuntime.cell(player.blockPosition());
            var saved=PathogenSpatialFieldSavedData.forLevel(world);
            int[] values=new int[PathogenHudTextPolicy.CHANNEL_COUNT];
            for(int i=0;i<values.length;i++)
                values[i]=saved.get(at,i);
            player.displayClientMessage(
                Component.literal(PathogenHudTextPolicy.format(at,values)),true);
        }
    }
}
