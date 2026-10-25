package io.github.lordnavycrypto.gugugaga.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lordnavycrypto.gugugaga.ai.FactionPolicy;
import io.github.lordnavycrypto.gugugaga.ai.StoryLedger;
import io.github.lordnavycrypto.gugugaga.ai.ThreatPolicy.Mood;
import io.github.lordnavycrypto.gugugaga.ai.WitnessJournal;
import io.github.lordnavycrypto.gugugaga.ai.WitnessJournalPolicy;
import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import io.github.lordnavycrypto.gugugaga.entity.GugugagaEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Comparator;

/**
 * Story choices are voluntary. Operators control hostile test states.
 * No chunk generation or long-term tracking of player position.
 */
public final class GuguStoryCommands {
    private GuguStoryCommands(){}

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("gugugaga")
                .then(Commands.literal("story")
                        .executes(ctx->story(ctx.getSource())))
                .then(Commands.literal("trust")
                        .then(Commands.literal("guardian")
                                .executes(ctx->trust(ctx.getSource(),FactionPolicy.Side.GUARDIAN)))
                        .then(Commands.literal("redactor")
                                .executes(ctx->trust(ctx.getSource(),FactionPolicy.Side.REDACTOR)))
                        .then(Commands.literal("neutral")
                                .executes(ctx->trust(ctx.getSource(),FactionPolicy.Side.UNDECIDED))))
                .then(Commands.literal("reassure")
                        .executes(ctx->reassure(ctx.getSource())))
                .then(Commands.literal("hostility")
                        .requires(src->src.hasPermission(2))
                        .then(Commands.literal("status")
                                .executes(ctx->hostilityStatus(ctx.getSource())))
                        .then(Commands.literal("on")
                                .executes(ctx->hostility(ctx.getSource(),true)))
                        .then(Commands.literal("off")
                                .executes(ctx->hostility(ctx.getSource(),false))))
                .then(Commands.literal("temper")
                        .requires(src->src.hasPermission(2))
                        .then(Commands.literal("calm")
                                .executes(ctx->temper(ctx.getSource(),Mood.CALM)))
                        .then(Commands.literal("angry")
                                .executes(ctx->temper(ctx.getSource(),Mood.ANGRY)))
                        .then(Commands.literal("corrupted")
                                .executes(ctx->temper(ctx.getSource(),Mood.CORRUPTED)))));
    }

    private static int story(CommandSourceStack src)
            throws CommandSyntaxException {
        StoryLedger.show(src.getPlayerOrException());
        return 1;
    }

    private static int trust(CommandSourceStack src,FactionPolicy.Side side)
            throws CommandSyntaxException {
        ServerPlayer player=src.getPlayerOrException();
        StoryLedger.choose(player,side);
        StoryLedger.show(player);
        return 1;
    }

    private static GugugagaEntity closest(ServerPlayer player,double radius) {
        return player.serverLevel().getEntitiesOfClass(
                        GugugagaEntity.class,player.getBoundingBox().inflate(radius))
                .stream().min(Comparator.comparingDouble(player::distanceToSqr))
                .orElse(null);
    }

    private static int reassure(CommandSourceStack src)
            throws CommandSyntaxException {
        ServerPlayer player=src.getPlayerOrException();
        if(WitnessJournalPolicy.discovered(WitnessJournal.evidenceBits(player))<3) {
            player.sendSystemMessage(Component.literal(
                    "You have not yet witnessed enough to calm the Guardian. /gugugaga journal"));
            return 0;
        }
        GugugagaEntity bird=closest(player,10);
        if(bird==null || !player.hasLineOfSight(bird)) {
            player.sendSystemMessage(Component.literal(
                    "No Gugu-Gaga is close enough for you to reassure."));
            return 0;
        }
        bird.setTemperament(Mood.CALM);
        player.sendSystemMessage(Component.literal(
                "The penguin becomes still. The Redactor's story was not the whole truth."));
        return 1;
    }

    private static int hostilityStatus(CommandSourceStack src) {
        src.sendSuccess(()->Component.literal(
                "Optional Gugu-Gaga hostility: "+NightwatchConfig.HOSTILITY_ENABLED.get()
                        +" (false means NO damage)."),false);
        return 1;
    }

    private static int hostility(CommandSourceStack src,boolean on) {
        NightwatchConfig.HOSTILITY_ENABLED.set(on);
        src.sendSuccess(()->Component.literal(on
                ? "Hostile penguin behavior enabled for this server; use /gugugaga temper angry."
                : "Hostile penguin behavior disabled; existing birds cannot chase/strike."),true);
        return 1;
    }

    private static int temper(CommandSourceStack src,Mood mood)
            throws CommandSyntaxException {
        ServerPlayer player=src.getPlayerOrException();
        GugugagaEntity bird=closest(player,24);
        if(bird==null) {
            src.sendFailure(Component.literal("No nearby Gugu-Gaga within 24 blocks."));
            return 0;
        }
        bird.setTemperament(mood);
        src.sendSuccess(()->Component.literal(
                "Nearest Gugu-Gaga mood="+mood
                +" (hostility enabled="+NightwatchConfig.HOSTILITY_ENABLED.get()
                +"). States automatically recover."),true);
        return 1;
    }
}
