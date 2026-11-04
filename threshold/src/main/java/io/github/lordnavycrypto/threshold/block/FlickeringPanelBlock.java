package io.github.lordnavycrypto.threshold.block;

import io.github.lordnavycrypto.threshold.dimension.ThresholdDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * Light fluctuations are environmental and bounded by ordinary random ticks;
 * they do not create mobs, damage players, or modify other world locations.
 */
public final class FlickeringPanelBlock extends Block {
    public static final BooleanProperty LIT = BooleanProperty.create("lit");

    public FlickeringPanelBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(LIT, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.dimension().equals(ThresholdDimensions.LEVEL_0)) return;
        if (state.getValue(LIT)) {
            if (random.nextInt(16) == 0) {
                level.setBlock(pos, state.setValue(LIT, false), 2);
            }
        } else if (random.nextInt(2) == 0) {
            level.setBlock(pos, state.setValue(LIT, true), 2);
        }
    }
}
