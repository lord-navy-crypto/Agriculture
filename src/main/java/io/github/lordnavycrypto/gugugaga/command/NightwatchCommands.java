package io.github.lordnavycrypto.gugugaga.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lordnavycrypto.gugugaga.ai.EncounterPolicy;
import io.github.lordnavycrypto.gugugaga.ai.NightwatchDirector;
import io.github.lordnavycrypto.gugugaga.ai.WitnessJournal;
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
                .then(Commands.literal("journal")
                        .executes(ctx->journal(ctx.getSource()))
                        .then(Commands.literal("on").executes(ctx->journalToggle(ctx.getSource(),true)))
                        .then(Commands.literal("off").executes(ctx->journalToggle(ctx.getSource(),false)))
                        .then(Commands.literal("clear").executes(ctx->journalClear(ctx.getSource()))))
                .then(Commands.literal("encounters")
                        .requires(src->src.hasPermission(2))
                        .then(Commands.literal("status").executes(ctx->status(ctx.getSource())))
                        .then(Commands.literal("on").executes(ctx->toggle(ctx.getSource(),true)))
                        .then(Commands.literal("off").executes(ctx->toggle(ctx.getSource(),false))))
                .then(Commands.literal("atmosphere")
                        .requires(src->src.hasPermission(2))
                        .then(Commands.literal("status").executes(ctx->atmosphereStatus(ctx.getSource())))
                        .then(Commands.literal("on").executes(ctx->atmosphere(ctx.getSource(),true)))
                        .then(Commands.literal("off").executes(ctx->atmosphere(ctx.getSource(),false)))));
    }

    private static int atmosphereStatus(CommandSourceStack src) {
        src.sendSuccess(()->Component.literal("Nightwatch atmosphere: audio="
                + NightwatchConfig.HAUNTING_AUDIO_ENABLED.get()
                + ", particles=" + NightwatchConfig.HAUNTING_PARTICLES_ENABLED.get()
                + ". Both can be configured independently in gugugaga-server.toml."),
                false);
        return 1;
    }

    private static int atmosphere(CommandSourceStack src,boolean enabled) {
        NightwatchConfig.HAUNTING_AUDIO_ENABLED.set(enabled);
        NightwatchConfig.HAUNTING_PARTICLES_ENABLED.set(enabled);
        src.sendSuccess(()->Component.literal(enabled
                ? "Subtle Nightwatch sounds/particles enabled (no flash or loud effects)."
                : "Nightwatch atmospheric sounds/particles disabled; core AI unaffected."),
                true);
        return 1;
    }

    private static int journal(CommandSourceStack src)
            throws CommandSyntaxException {
        WitnessJournal.show(src.getPlayerOrException());
        return 1;
    }

    private static int journalToggle(CommandSourceStack src,boolean enabled)
            throws CommandSyntaxException {
        var player=src.getPlayerOrException();
        WitnessJournal.setEnabled(player,enabled);
        src.sendSuccess(()->Component.literal(enabled
                ? "Nightwatch journal recording resumed."
                : "Nightwatch journal recording paused. Old clues remain private."),false);
        return 1;
    }

    private static int journalClear(CommandSourceStack src)
            throws CommandSyntaxException {
        WitnessJournal.clear(src.getPlayerOrException());
        src.sendSuccess(()->Component.literal(
                "Your Nightwatch clues have been cleared. Your preference remains."),false);
        return 1;
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
