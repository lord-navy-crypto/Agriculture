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
                        .then(Commands.literal("help").executes(ctx->help(ctx.getSource())))
                        .then(Commands.literal("listen").executes(ctx->listen(ctx.getSource())))
                        .then(Commands.literal("listen-panel").executes(ctx->listenFromPanel(ctx.getSource())))
                        .then(Commands.literal("aid")
                                .then(Commands.literal("on").executes(ctx->aid(ctx.getSource(),true)))
                                .then(Commands.literal("off").executes(ctx->aid(ctx.getSource(),false))))
                        .then(Commands.literal("scares")
                                .then(Commands.literal("on").executes(ctx->scares(ctx.getSource(),true)))
                                .then(Commands.literal("off").executes(ctx->scares(ctx.getSource(),false))))
                        .then(Commands.literal("test")
                                .requires(src->src.hasPermission(2))
                                .then(Commands.literal("witness")
                                        .executes(ctx->testScene(ctx.getSource(),false)))
                                .then(Commands.literal("triad")
                                        .executes(ctx->testScene(ctx.getSource(),true))))
                        .then(Commands.literal("server")
                                .requires(src->src.hasPermission(2))
                                .then(Commands.literal("on").executes(ctx->server(ctx.getSource(),true)))
                                .then(Commands.literal("off").executes(ctx->server(ctx.getSource(),false)))))
                .then(Commands.literal("investigate")
                        .executes(ctx->investigate(ctx.getSource()))));
    }
    private static int testScene(CommandSourceStack src,boolean allThree)
            throws CommandSyntaxException {
        return HauntGameplayRuntime.startOperatorTestScene(
                src.getPlayerOrException(),allThree)?1:0;
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
    private static int listen(CommandSourceStack src) throws CommandSyntaxException {
        return HauntGameplayRuntime.listen(src.getPlayerOrException())?1:0;
    }
    private static int listenFromPanel(CommandSourceStack src) throws CommandSyntaxException {
        return HauntGameplayRuntime.listenFromPanel(src.getPlayerOrException())?1:0;
    }
    private static int help(CommandSourceStack src) throws CommandSyntaxException {
        return HauntGameplayRuntime.requestSmilerHelp(src.getPlayerOrException())?1:0;
    }
    private static int aid(CommandSourceStack src,boolean enabled)
            throws CommandSyntaxException {
        HauntGameplayRuntime.setSmilerAid(src.getPlayerOrException(),enabled);
        return 1;
    }

    private static int scares(CommandSourceStack src,boolean enabled)
            throws CommandSyntaxException {
        HauntGameplayRuntime.setScaresAllowed(src.getPlayerOrException(),enabled);
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
        if(active)io.github.lordnavycrypto.gugugaga.ai.E2EGameplayProof.record("SERVER_ENABLED");
        // Enabling the game loop also allows the pre-existing 8-minute
        // sighting scheduler to introduce the first Gugu. Each player must
        // still join. Disabling this experimental mode shuts auto spawning
        // off, without forcibly despawning existing manually summoned mobs.
        NightwatchConfig.ENCOUNTERS_ENABLED.set(active);
        src.sendSuccess(()->Component.literal(
                active?"Nightwatch enabled. Survival players join the horror loop automatically at night; press H for controls."
                    :"Nightwatch disabled for this world. Existing entities remain in place."),
                true);
        return 1;
    }
}
