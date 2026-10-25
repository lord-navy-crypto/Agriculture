package io.github.lordnavycrypto.gugugaga.agriculture;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/** Crop variant that persists pollination until harvest. */
public final class PollinatedAgricultureCropBlock extends AgricultureCropBlock {
    public static final BooleanProperty POLLINATED = BooleanProperty.create("pollinated");

    public PollinatedAgricultureCropBlock(BlockBehaviour.Properties properties, String seedName, PollinationGroup group) {
        super(properties, seedName, group);
        if (group == PollinationGroup.NONE) {
            throw new IllegalArgumentException("Pollinated crop block requires a pollination group");
        }
        registerDefaultState(defaultBlockState().setValue(POLLINATED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POLLINATED);
    }

    @Override
    public boolean isPollinated(BlockState state) {
        return state.getValue(POLLINATED);
    }

    @Override
    protected void markPollinated(ServerLevel level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state.setValue(POLLINATED, true), 2);
    }
}
