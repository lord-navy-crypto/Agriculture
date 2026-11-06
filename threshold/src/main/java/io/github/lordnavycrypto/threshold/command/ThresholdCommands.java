package io.github.lordnavycrypto.threshold.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lordnavycrypto.threshold.dimension.ThresholdDimensions;
import io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks;
import io.github.lordnavycrypto.threshold.entity.ThresholdEntities;
import io.github.lordnavycrypto.threshold.entity.ThresholdWatcherEntity;
import io.github.lordnavycrypto.threshold.entity.ThresholdHoundEntity;
import io.github.lordnavycrypto.threshold.entity.ThresholdPartygoerEntity;
import io.github.lordnavycrypto.threshold.entity.ThresholdEntityDirector;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
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
                                        "Level 0 — The Lobby / Isolation")))
                        .then(Commands.literal("level_1")
                                .executes(context -> travel(
                                        context, ThresholdDimensions.LEVEL_1, 4.5, 64.0, 4.5,
                                        "Level 1 — The Habitable Zone")))
                        .then(Commands.literal("level_2")
                                .executes(context -> travel(
                                        context, ThresholdDimensions.LEVEL_2, 4.5, 64.0, 4.5,
                                        "Level 2 — Abandoned Utility Halls")))
                        .then(Commands.literal("level_3")
                                .executes(context -> travel(
                                        context, ThresholdDimensions.LEVEL_3, 4.5, 64.0, 4.5,
                                        "Level 3 — Electrical Station")))
                        .then(Commands.literal("level_4")
                                .executes(context -> travel(
                                        context, ThresholdDimensions.LEVEL_4, 4.5, 64.0, 4.5,
                                        "Level 4 — Abandoned Office")))
                        .then(Commands.literal("level_5")
                                .executes(context -> travel(
                                        context, ThresholdDimensions.LEVEL_5, 4.5, 64.0, 4.5,
                                        "Level 5 — Terror Hotel")))
                        .then(Commands.literal("overworld")
                                .executes(ThresholdCommands::returnToOverworld)))
                .then(Commands.literal("whereami")
                        .executes(ThresholdCommands::whereAmI))
                .then(Commands.literal("summon")
                        .then(Commands.literal("watcher").executes(ThresholdCommands::summonWatcher))
                        .then(Commands.literal("watcher_observer_test").executes(ThresholdCommands::summonWatcherObserverTest))
                        .then(Commands.literal("watcher_passive").executes(ThresholdCommands::summonWatcherPassive))
                        .then(Commands.literal("watcher_aggressive").executes(ThresholdCommands::summonWatcherAggressive))
                        .then(Commands.literal("watcher_hunt").executes(ThresholdCommands::summonWatcherHunt))
                        .then(Commands.literal("hound").executes(ThresholdCommands::summonHound))
                        .then(Commands.literal("partygoer").executes(ThresholdCommands::summonPartygoer)))
                .then(Commands.literal("watcher")
                        .then(Commands.literal("status").executes(ThresholdCommands::watcherStatus))
                        .then(Commands.literal("mode")
                                .then(Commands.literal("passive").executes(
                                        context -> setNearbyWatcherMode(context, ThresholdWatcherEntity.BehaviorMode.PASSIVE)))
                                .then(Commands.literal("aggressive").executes(
                                        context -> setNearbyWatcherMode(context, ThresholdWatcherEntity.BehaviorMode.AGGRESSIVE)))))
                .then(Commands.literal("repair_floor")
                        .executes(ThresholdCommands::repairFloor)));
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

    /**
     * Repairs the legacy carpet-under-wall seam in already generated Level 0
     * chunks. Bounded to loaded chunks in a five-by-five region around the
     * operator; no worldgen and no edits to plain carpet or player inventories.
     */
    private static int repairFloor(CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        if (!player.level().dimension().equals(ThresholdDimensions.LEVEL_0)) {
            context.getSource().sendFailure(
                    Component.literal("Use this command inside Threshold Level 0."));
            return 0;
        }
        ServerLevel level = player.serverLevel();
        int centerChunkX = player.blockPosition().getX() >> 4;
        int centerChunkZ = player.blockPosition().getZ() >> 4;
        int fixed = 0;
        int chunks = 0;
        for (int cx = centerChunkX - 2; cx <= centerChunkX + 2; cx++) {
            for (int cz = centerChunkZ - 2; cz <= centerChunkZ + 2; cz++) {
                if (!level.hasChunk(cx, cz)) continue;
                chunks++;
                for (int x = cx << 4; x < (cx << 4) + 16; x++) {
                    for (int z = cz << 4; z < (cz << 4) + 16; z++) {
                        BlockPos base = new BlockPos(x, 64, z);
                        BlockState lower = level.getBlockState(base);
                        if (!lower.is(Blocks.YELLOW_CARPET)
                                && !lower.is(Blocks.RED_CARPET)
                                && !lower.is(Blocks.GRAY_CARPET)) continue;
                        BlockState upper = level.getBlockState(base.above());
                        if (isThresholdWall(upper)) {
                            level.setBlock(base, upper, 3);
                            fixed++;
                        }
                    }
                }
            }
        }
        final int repaired = fixed;
        final int inspected = chunks;
        context.getSource().sendSuccess(
                () -> Component.literal("Repaired " + repaired
                        + " wall footings in " + inspected + " loaded chunks."), false);
        return Command.SINGLE_SUCCESS;
    }

    private static boolean isThresholdWall(BlockState state) {
        return state.is(ThresholdEnvironmentBlocks.FADED_WALLPAPER.get())
                || state.is(ThresholdEnvironmentBlocks.STAINED_WALLPAPER.get())
                || state.is(ThresholdEnvironmentBlocks.TRANSITION_WALL.get())
                || state.is(Blocks.YELLOW_CONCRETE)
                || state.is(Blocks.GRAY_CONCRETE)
                || state.is(Blocks.RED_TERRACOTTA);
    }

    private static int summonWatcher(CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ServerLevel level = player.serverLevel();
        if (ThresholdEntityDirector.hasNearbyWatcher(level, player)) {
            context.getSource().sendFailure(Component.literal(
                    "Two Watchers are already active in this local encounter area."));
            return 0;
        }
        int nearbyWatchers = ThresholdEntityDirector.countNearbyWatchers(level, player);
        BlockPos pos = ThresholdEntityDirector.findWatcherShowcasePosition(level, player);
        if (pos == null) {
            context.getSource().sendFailure(Component.literal(
                    "No clear floor position is available in front of you. Move into an open hallway and retry."));
            return 0;
        }
        ThresholdWatcherEntity watcher = ThresholdEntities.WATCHER.get().create(level);
        if (watcher == null) return 0;
        float yaw = (float) (Math.atan2(player.getZ() - pos.getZ(), player.getX() - pos.getX())
                * (180.0D / Math.PI)) - 90.0F;
        watcher.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, yaw, 0.0F);
        watcher.setFlankerRole(nearbyWatchers > 0);
        watcher.enableShowcaseMode();
        if (!level.addFreshEntity(watcher)) {
            context.getSource().sendFailure(Component.literal("The Watcher could not be placed here."));
            return 0;
        }
        context.getSource().sendSuccess(() -> Component.literal(
                "Harmless Watcher preview spawned. Two entities maximum are allowed in one encounter area."), false);
        return Command.SINGLE_SUCCESS;
    }

    /** Compatibility alias: the former hunt command now creates an aggressive capture-mode Watcher. */
    private static int summonWatcherHunt(CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException {
        return summonWatcherAggressive(context);
    }

    private static int summonWatcherObserverTest(CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException {
        return summonWatcherMode(context, ThresholdWatcherEntity.BehaviorMode.PASSIVE, true);
    }

    private static int summonWatcherPassive(CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException {
        return summonWatcherMode(context, ThresholdWatcherEntity.BehaviorMode.PASSIVE);
    }

    private static int summonWatcherAggressive(CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException {
        return summonWatcherMode(context, ThresholdWatcherEntity.BehaviorMode.AGGRESSIVE);
    }

    private static int summonWatcherMode(
            CommandContext<CommandSourceStack> context,
            ThresholdWatcherEntity.BehaviorMode mode
    ) throws CommandSyntaxException {
        return summonWatcherMode(context, mode, false);
    }

    private static int summonWatcherMode(
            CommandContext<CommandSourceStack> context,
            ThresholdWatcherEntity.BehaviorMode mode,
            boolean observerTest
    ) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ServerLevel level = player.serverLevel();
        if (ThresholdEntityDirector.hasNearbyWatcher(level, player)) {
            context.getSource().sendFailure(Component.literal(
                    "Two Watchers are already active in this local encounter area."));
            return 0;
        }
        int nearbyWatchers = ThresholdEntityDirector.countNearbyWatchers(level, player);
        BlockPos pos = ThresholdEntityDirector.findShowcasePosition(level, player, 8, 16, 2);
        if (pos == null) {
            context.getSource().sendFailure(Component.literal(
                    "No safe floor position is available in front of you. Move into an open hallway and retry."));
            return 0;
        }
        ThresholdWatcherEntity watcher = ThresholdEntities.WATCHER.get().create(level);
        if (watcher == null) return 0;
        watcher.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, player.getYRot(), 0.0F);
        watcher.setFlankerRole(nearbyWatchers > 0);
        if (observerTest) {
            watcher.enableObserverTestMode();
        } else if (mode == ThresholdWatcherEntity.BehaviorMode.PASSIVE) {
            watcher.enablePassiveTestMode();
        } else {
            watcher.enableAggressiveTestMode();
        }
        if (!level.addFreshEntity(watcher)) {
            context.getSource().sendFailure(Component.literal("The Watcher could not be placed here."));
            return 0;
        }
        String spawnLabel = observerTest ? "OBSERVER TEST Watcher" : mode.name() + " Watcher";
        context.getSource().sendSuccess(() -> Component.literal(
                spawnLabel + " spawned and will persist until manually removed. Use /threshold watcher status to inspect it."), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int setNearbyWatcherMode(
            CommandContext<CommandSourceStack> context,
            ThresholdWatcherEntity.BehaviorMode mode
    ) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ServerLevel level = player.serverLevel();
        var watchers = level.getEntitiesOfClass(
                ThresholdWatcherEntity.class, player.getBoundingBox().inflate(42.0D));
        ThresholdWatcherEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (ThresholdWatcherEntity watcher : watchers) {
            double distance = player.distanceToSqr(watcher);
            if (distance < nearestDistance) {
                nearest = watcher;
                nearestDistance = distance;
            }
        }
        if (nearest == null) {
            context.getSource().sendFailure(Component.literal("No Watchers are active nearby."));
            return 0;
        }
        nearest.setBehaviorMode(mode);
        int watcherId = nearest.getId();
        context.getSource().sendSuccess(() -> Component.literal(
                "Watcher #" + watcherId + " switched to " + mode.name() + " mode."), false);
        return Command.SINGLE_SUCCESS;
    }

    /** Reports only local Watcher state and decaying last-observation confidence. */
    private static int watcherStatus(CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ServerLevel level = player.serverLevel();
        var watchers = level.getEntitiesOfClass(
                ThresholdWatcherEntity.class, player.getBoundingBox().inflate(42.0D));
        if (watchers.isEmpty()) {
            context.getSource().sendFailure(Component.literal("No Watchers are active nearby."));
            return 0;
        }
        for (ThresholdWatcherEntity watcher : watchers) {
            long confidence = Math.round(watcher.getObservationConfidence() * 100.0D);
            long age = watcher.getObservationAgeTicks();
            String report = "Watcher #" + watcher.getId()
                    + " mode=" + watcher.getBehaviorModeName()
                    + " test=" + watcher.isTestEncounterMode()
                    + " observerTest=" + watcher.isObserverTestMode()
                    + " role=" + watcher.getObservationStyleName()
                    + " state=" + watcher.getEncounterStateName()
                    + " memory=" + confidence + "% age=" + age + "t";
            context.getSource().sendSuccess(() -> Component.literal(report), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int summonHound(CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ServerLevel level = player.serverLevel();
        BlockPos pos = ThresholdEntityDirector.findShowcasePosition(level, player, 4, 10, 2);
        if (pos == null) {
            context.getSource().sendFailure(Component.literal(
                    "No safe floor position is available in front of you. Move into an open area and retry."));
            return 0;
        }
        ThresholdHoundEntity hound = ThresholdEntities.HOUND.get().create(level);
        if (hound == null) return 0;
        hound.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, player.getYRot(), 0.0F);
        hound.setTarget(player);
        if (!level.addFreshEntity(hound)) {
            context.getSource().sendFailure(Component.literal("The Hound could not be placed here."));
            return 0;
        }
        context.getSource().sendSuccess(() -> Component.literal(
                "Hound test summon created in " + level.dimension().location() + "."), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int summonPartygoer(CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ServerLevel level = player.serverLevel();
        BlockPos pos = ThresholdEntityDirector.findShowcasePosition(level, player, 10, 18, 2);
        if (pos == null) {
            context.getSource().sendFailure(Component.literal(
                    "No safe floor position is available in front of you. Move into an open area and retry."));
            return 0;
        }
        ThresholdPartygoerEntity partygoer = ThresholdEntities.PARTYGOER.get().create(level);
        if (partygoer == null) return 0;
        partygoer.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D,
                player.getYRot() + 180.0F, 0.0F);
        partygoer.setTarget(player);
        if (!level.addFreshEntity(partygoer)) {
            context.getSource().sendFailure(Component.literal("The Partygoer could not be placed here."));
            return 0;
        }
        context.getSource().sendSuccess(() -> Component.literal(
                "Partygoer test summon created in " + level.dimension().location()
                        + ". It will stalk, pursue, and attack."), false);
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
