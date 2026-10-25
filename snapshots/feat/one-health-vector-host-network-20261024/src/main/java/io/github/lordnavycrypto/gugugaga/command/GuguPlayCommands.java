package io.github.lordnavycrypto.gugugaga.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lordnavycrypto.gugugaga.ai.HauntGameplayRuntime;
import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * Voluntary horror gameplay. Administrators authorize WORLD availability;
 * each survival player must join individually. No quests/endings here.
 */
public final class GuguPlayCommands {
    private GuguPlayCommands(){}
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("gugugaga")
                .then(Commands.literal("play")
                        .then(Commands.literal("join").executes(ctx->join(ctx.getSource())))
                        .then(Commands.literal("leave").executes(ctx->leave(ctx.getSource())))
                        .then(Commands.literal("status").executes(ctx->status(ctx.getSource())))
                        .then(Commands.literal("server")
                                .requires(src->src.hasPermission(2))
                                .then(Commands.literal("on").executes(ctx->server(ctx.getSource(),true)))
                                .then(Commands.literal("off").executes(ctx->server(ctx.getSource(),false)))))
                .then(Commands.literal("investigate")
                        .executes(ctx->investigate(ctx.getSource()))));
    }
    private static int join(CommandSourceStack src) throws CommandSyntaxException {
        ServerPlayer player=src.getPlayerOrException();
        if(!NightwatchConfig.PLAYABLE_LOOP_ENABLED.get()){
            player.sendSystemMessage(Component.literal(
                    "[Nightwatch] The world owner must run /gugugaga play server on first."));
            return 0;
        }
        HauntGameplayRuntime.join(player);
        return 1;
    }
    private static int leave(CommandSourceStack src) throws CommandSyntaxException {
        HauntGameplayRuntime.leave(src.getPlayerOrException());
        return 1;
    }
    private static int status(CommandSourceStack src) throws CommandSyntaxException {
        ServerPlayer player=src.getPlayerOrException();
        player.sendSystemMessage(Component.literal("[Nightwatch] "+
                HauntGameplayRuntime.status(player)));
        return 1;
    }
    private static int investigate(CommandSourceStack src) throws CommandSyntaxException {
        return HauntGameplayRuntime.investigate(src.getPlayerOrException())?1:0;
    }
    private static int server(CommandSourceStack src,boolean active) {
        NightwatchConfig.PLAYABLE_LOOP_ENABLED.set(active);
        // Enabling the game loop also allows the pre-existing 8-minute
        // sighting scheduler to introduce the first Gugu. Each player must
        // still join. Disabling this experimental mode shuts auto spawning
        // off, without forcibly despawning existing manually summoned mobs.
        NightwatchConfig.ENCOUNTERS_ENABLED.set(active);
        src.sendSuccess(()->Component.literal(
                active?"Five-act Nightwatch enabled. Every player must /gugugaga play join."
                        +" Witnesses follow the original 8-minute minimum sighting cooldown."
                    :"Five-act Nightwatch disabled. Automatic encounters also disabled."),
                true);
        return 1;
    }
}
