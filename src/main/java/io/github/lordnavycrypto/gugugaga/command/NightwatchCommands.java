package io.github.lordnavycrypto.gugugaga.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lordnavycrypto.gugugaga.ai.EncounterPolicy;
import io.github.lordnavycrypto.gugugaga.ai.NightwatchDirector;
import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * Quiet is available to every player; server-owned auto spawning is
 * restricted to operators and opt-in only.
 */
public final class NightwatchCommands {
    private NightwatchCommands(){}

    public static void register(RegisterCommandsEvent event) {
        // Brigadier merges the same root registered by OllamaCommands.
        event.getDispatcher().register(Commands.literal("gugugaga")
                .then(Commands.literal("quiet")
                        .then(Commands.literal("on").executes(ctx->quiet(ctx.getSource(),true)))
                        .then(Commands.literal("off").executes(ctx->quiet(ctx.getSource(),false)))
                        .then(Commands.literal("status").executes(ctx->quietStatus(ctx.getSource()))))
                .then(Commands.literal("encounters")
                        .requires(src->src.hasPermission(2))
                        .then(Commands.literal("status").executes(ctx->status(ctx.getSource())))
                        .then(Commands.literal("on").executes(ctx->toggle(ctx.getSource(),true)))
                        .then(Commands.literal("off").executes(ctx->toggle(ctx.getSource(),false)))));
    }

    private static int quiet(CommandSourceStack src,boolean quiet)
            throws CommandSyntaxException {
        var player=src.getPlayerOrException();
        NightwatchDirector.setQuiet(src.getServer(),player.getUUID(),quiet);
        src.sendSuccess(()->Component.literal(quiet
                ? "Gugu Gaga encounters disabled for your session."
                : "Gugu Gaga encounters allowed for your session (if server enabled)."),
                false);
        return 1;
    }

    private static int quietStatus(CommandSourceStack src)
            throws CommandSyntaxException {
        var player=src.getPlayerOrException();
        boolean quiet=NightwatchDirector.isQuiet(src.getServer(),player.getUUID());
        src.sendSuccess(()->Component.literal("Your Nightwatch quiet mode: "+quiet),
                false);
        return 1;
    }

    private static int status(CommandSourceStack src) {
        src.sendSuccess(()->Component.literal("Nightwatch encounters: enabled="
                +NightwatchConfig.ENCOUNTERS_ENABLED.get()
                +", minimum cooldown (ticks)="
                +EncounterPolicy.boundedCooldown(
                        NightwatchConfig.ENCOUNTER_COOLDOWN_TICKS.get())
                +", observer global cap="+EncounterPolicy.MAX_GLOBAL_OBSERVERS
                +", nearby cap="+EncounterPolicy.MAX_NEAR_PLAYER),false);
        return 1;
    }

    private static int toggle(CommandSourceStack src,boolean enabled) {
        NightwatchConfig.ENCOUNTERS_ENABLED.set(enabled);
        src.sendSuccess(()->Component.literal(enabled
                ? "Nightwatch enabled: rare nonviolent night-only encounters after cooldown."
                : "Nightwatch automatic encounters disabled. Existing summoned observers stay."),
                true);
        return 1;
    }
}
