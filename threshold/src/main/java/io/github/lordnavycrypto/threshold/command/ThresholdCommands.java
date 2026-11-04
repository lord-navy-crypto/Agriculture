package io.github.lordnavycrypto.threshold.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lordnavycrypto.threshold.dimension.ThresholdDimensions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class ThresholdCommands {
    private ThresholdCommands() {
    }

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("threshold")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("travel")
                        .then(Commands.literal("level_0")
                                .executes(context -> travel(
                                        context, ThresholdDimensions.LEVEL_0, 4.5, 65.0, 4.5,
                                        "Level 0 — Isolation")))
                        .then(Commands.literal("level_1")
                                .executes(context -> travel(
                                        context, ThresholdDimensions.LEVEL_1, 4.5, 64.0, 4.5,
                                        "Level 1 — The Vulnerable Shelter")))
                        .then(Commands.literal("overworld")
                                .executes(ThresholdCommands::returnToOverworld)))
                .then(Commands.literal("whereami")
                        .executes(ThresholdCommands::whereAmI)));
    }

    private static int travel(
            CommandContext<CommandSourceStack> context,
            net.minecraft.resources.ResourceKey<Level> dimensionKey,
            double x, double y, double z, String label
    ) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ServerLevel destination = context.getSource().getServer().getLevel(dimensionKey);

        if (destination == null) {
            context.getSource().sendFailure(Component.literal(
                    "Threshold dimension is not loaded: " + dimensionKey.location()));
            return 0;
        }

        player.teleportTo(destination, x, y, z, player.getYRot(), player.getXRot());
        context.getSource().sendSuccess(() -> Component.literal("Entered " + label + "."), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int returnToOverworld(CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ServerLevel overworld = context.getSource().getServer().getLevel(Level.OVERWORLD);
        if (overworld == null) {
            context.getSource().sendFailure(Component.literal("Overworld is unavailable."));
            return 0;
        }

        BlockPos spawn = overworld.getSharedSpawnPos();
        player.teleportTo(
                overworld, spawn.getX() + 0.5, spawn.getY() + 1.0, spawn.getZ() + 0.5,
                player.getYRot(), player.getXRot()
        );
        context.getSource().sendSuccess(() -> Component.literal("Returned to the Overworld."), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int whereAmI(CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String id = player.level().dimension().location().toString();
        context.getSource().sendSuccess(
                () -> Component.literal("Current dimension: " + id), false);
        return Command.SINGLE_SUCCESS;
    }
}
