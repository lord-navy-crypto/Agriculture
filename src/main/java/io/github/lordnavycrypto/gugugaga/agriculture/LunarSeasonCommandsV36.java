package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.minecraft.commands.Commands;

/**
 * In-game, read-only seasonal status. No client payload, privileged world
 * edits, global tick handler or world-data migration required.
 */
@EventBusSubscriber(modid=GuguGagaMod.MOD_ID)
public final class LunarSeasonCommandsV36 {
    private LunarSeasonCommandsV36() {}

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("agseason")
                .executes(context -> {
                    ServerPlayer player=context.getSource().getPlayerOrException();
                    var level=player.serverLevel();
                    BlockPos pos=player.blockPosition();
                    long day=LunarSeasonsV36.absoluteDay(level);
                    var habitat=LunarSeasonsV36.habitat(
                            LunarSeasonsV36.biomeId(level,pos));
                    var local=LunarSeasonsV36.localSeason(day,habitat);
                    var world=LunarSeasonsV36.worldSeason(day);
                    int moon=LunarSeasonsV36.moonPhase(day);
                    int seasonDay=LunarSeasonsV36.localDayOfSeason(day,habitat);
                    int length=LunarSeasonsV36.seasonLengths(habitat)[local.ordinal()];
                    context.getSource().sendSuccess(() -> Component.translatable(
                            "message.gugugaga.v36_calendar",
                            LunarSeasonsV36.year(day),LunarSeasonsV36.yearDay(day)+1,
                            Component.translatable("season.gugugaga."+world.name().toLowerCase(
                                    java.util.Locale.ROOT)),
                            Component.translatable("season.gugugaga."+local.name().toLowerCase(
                                    java.util.Locale.ROOT)),
                            seasonDay,length,moon,
                            Component.translatable("moon.gugugaga."+moon)),
                            false);
                    return 1;
                }));
    }
}
