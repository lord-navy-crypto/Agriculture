package io.github.lordnavycrypto.threshold.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

/**
 * A source-backed multi-destination passage.
 *
 * A route is drawn from relative gameplay weights using the world seed and
 * this doorway's coordinates. The same doorway therefore keeps its assigned
 * destination after reloads and cannot be rerolled by repeatedly clicking it.
 * Weights are implementation tuning, not claimed canonical probabilities.
 */
public final class ThresholdRouteBlock extends Block {
    public record RouteOption(
            ResourceKey<Level> destination,
            double x, double y, double z,
            String label,
            int weight) {
        public RouteOption {
            if (weight < 1) throw new IllegalArgumentException("Route weight must be positive");
        }
    }

    private final List<RouteOption> routes;
    private final int totalWeight;
    private final long routeSalt;

    public ThresholdRouteBlock(List<RouteOption> routes, long routeSalt, Properties properties) {
        super(properties);
        if (routes == null || routes.isEmpty()) {
            throw new IllegalArgumentException("At least one route is required");
        }
        this.routes = List.copyOf(routes);
        this.totalWeight = this.routes.stream().mapToInt(RouteOption::weight).sum();
        this.routeSalt = routeSalt;
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos,
            Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;

        RouteOption route = chooseRoute(serverPlayer.serverLevel(), pos);
        ServerLevel destination = serverPlayer.getServer().getLevel(route.destination());
        if (destination == null) {
            serverPlayer.displayClientMessage(
                    Component.literal("The passage leads beyond the currently connected map."), true);
            return InteractionResult.FAIL;
        }

        serverPlayer.teleportTo(destination, route.x(), route.y(), route.z(),
                serverPlayer.getYRot(), serverPlayer.getXRot());
        serverPlayer.displayClientMessage(Component.literal(route.label()), true);
        return InteractionResult.SUCCESS;
    }

    private RouteOption chooseRoute(ServerLevel source, BlockPos pos) {
        long mixed = mix(source.getSeed() ^ routeSalt, pos.asLong());
        int roll = (int) Math.floorMod(mixed, (long) totalWeight);
        for (RouteOption route : routes) {
            if (roll < route.weight()) return route;
            roll -= route.weight();
        }
        return routes.get(routes.size() - 1);
    }

    private static long mix(long seed, long position) {
        long value = seed ^ position ^ 0x9E3779B97F4A7C15L;
        value = (value ^ (value >>> 30)) * 0xBF58476D1CE4E5B9L;
        value = (value ^ (value >>> 27)) * 0x94D049BB133111EBL;
        return value ^ (value >>> 31);
    }
}
