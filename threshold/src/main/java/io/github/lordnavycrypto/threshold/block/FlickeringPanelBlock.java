package io.github.lordnavycrypto.threshold.block;

import io.github.lordnavycrypto.threshold.dimension.ThresholdDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
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
        boolean level0 = level.dimension().equals(ThresholdDimensions.LEVEL_0);
        boolean level1 = level.dimension().equals(ThresholdDimensions.LEVEL_1);
        if (!level0 && !level1) return;

        // Level 0 normally blinks briefly. Panels mounted below a black-concrete
        // ceiling belong to a blackout pocket: they fail more often and recover slowly.
        // Level 1 warehouse faults retain their independent, longer-dark profile.
        boolean blackoutPocket = level0 && level.getBlockState(pos.below()).is(Blocks.BLACK_CONCRETE);
        int offChance = level0 ? (blackoutPocket ? 8 : 18) : 8;
        int restoreChance = level0 ? (blackoutPocket ? 12 : 2) : 8;
        if (state.getValue(LIT)) {
            if (random.nextInt(offChance) == 0) {
                level.setBlock(pos, state.setValue(LIT, false), 2);
            }
        } else if (random.nextInt(restoreChance) == 0) {
            level.setBlock(pos, state.setValue(LIT, true), 2);
        }
    }
}
