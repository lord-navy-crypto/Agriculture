package io.github.lordnavycrypto.gugugaga.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import io.github.lordnavycrypto.gugugaga.entity.GugugagaEntity;
import io.github.lordnavycrypto.gugugaga.entity.RedactorEntity;
import io.github.lordnavycrypto.gugugaga.entity.YellowSmilerEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import java.util.Comparator;

/** Operator-only experimental character controls. No real admin escalation. */
public final class GuguAdvancedCommands {
    private GuguAdvancedCommands(){}
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("gugugaga")
                .then(Commands.literal("mimic")
                        .requires(src->src.hasPermission(2))
                        .then(Commands.literal("status").executes(ctx->mimicStatus(ctx.getSource())))
                        .then(Commands.literal("hostility")
                                .then(Commands.literal("on").executes(ctx->mimicHostility(ctx.getSource(),true)))
                                .then(Commands.literal("off").executes(ctx->mimicHostility(ctx.getSource(),false))))
                        .then(Commands.literal("reveal").executes(ctx->mimicForm(ctx.getSource(),true)))
                        .then(Commands.literal("disguise").executes(ctx->mimicForm(ctx.getSource(),false))))
                .then(Commands.literal("smiler")
                        .requires(src->src.hasPermission(2))
                        .then(Commands.literal("reveal").executes(ctx->smiler(ctx.getSource(),true)))
                        .then(Commands.literal("mask").executes(ctx->smiler(ctx.getSource(),false))))
                .then(Commands.literal("blacken")
                        .requires(src->src.hasPermission(2))
                        .executes(ctx->blacken(ctx.getSource()))));
    }
    private static <T extends Entity> T nearest(ServerPlayer player,
            Class<T> type) {
        return player.serverLevel().getEntitiesOfClass(type,
                player.getBoundingBox().inflate(32.0),Entity::isAlive)
                .stream().min(Comparator.comparingDouble(player::distanceToSqr))
                .orElse(null);
    }
    private static int mimicStatus(CommandSourceStack src) {
        src.sendSuccess(()->Component.literal(
                "Redactor/Lapis attack opt-in="+
                NightwatchConfig.REDACTOR_HOSTILITY_ENABLED.get()
                +". No player attacks unless explicitly enabled."),false);
        return 1;
    }
    private static int mimicHostility(CommandSourceStack src,boolean enabled) {
        NightwatchConfig.REDACTOR_HOSTILITY_ENABLED.set(enabled);
        src.sendSuccess(()->Component.literal(
                "Redactor attack="+enabled+". This never bypasses Yellow flight priority."),true);
        return 1;
    }
    private static int mimicForm(CommandSourceStack src,boolean reveal)
            throws CommandSyntaxException {
        RedactorEntity redactor=nearest(src.getPlayerOrException(),RedactorEntity.class);
        if(redactor==null) {
            src.sendFailure(Component.literal("No Redactor within 32 blocks."));
            return 0;
        }
        redactor.setRevealed(reveal);
        src.sendSuccess(()->Component.literal(
                reveal?"Lapis mask withdrawn.":"Lapis disguise restored."),false);
        return 1;
    }
    private static int smiler(CommandSourceStack src,boolean reveal)
            throws CommandSyntaxException {
        YellowSmilerEntity entity=nearest(src.getPlayerOrException(),
                YellowSmilerEntity.class);
        if(entity==null) {
            src.sendFailure(Component.literal("No Yellow Smiler within 32 blocks."));
            return 0;
        }
        entity.setRevealed(reveal);
        src.sendSuccess(()->Component.literal(
                reveal?"The Smiler's mask is gone.":"The Smiler has put his smile back on."),false);
        return 1;
    }
    private static int blacken(CommandSourceStack src)
            throws CommandSyntaxException {
        GugugagaEntity bird=nearest(src.getPlayerOrException(),GugugagaEntity.class);
        if(bird==null) {
            src.sendFailure(Component.literal("No Gugu-Gaga within 32 blocks."));
            return 0;
        }
        bird.testBlackenedCorruption();
        src.sendSuccess(()->Component.literal(
                "Final corruption stage preview: automatically reversible."),false);
        return 1;
    }
}
