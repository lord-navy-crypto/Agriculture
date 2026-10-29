package io.github.lordnavycrypto.gugugaga.agriculture;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.Block;

/**
 * Timed visual state for manual kitchen equipment.
 *
 * The ACTIVE flag switches the station's baked model to a distinct working
 * frame, then automatically resets after 10 ticks. It never stores inventory
 * or produces items on its own. All actual ingredient conversion remains
 * server-authoritative inside KitchenProcessingBlock.
 */
public final class KitchenV3AnimatedBlock extends KitchenProcessingBlock {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public KitchenV3AnimatedBlock(Kind kind, BlockBehaviour.Properties properties) {
        super(kind, properties);
        registerDefaultState(stateDefinition.any().setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    public void startWork(ServerLevel level, BlockPos pos) {
        BlockState current = level.getBlockState(pos);
        if (!current.is(this)) {
            return;
        }
        level.setBlock(pos, current.setValue(ACTIVE, true), Block.UPDATE_CLIENTS);
        level.scheduleTick(pos, this, 10);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(ACTIVE)) {
            level.setBlock(pos, state.setValue(ACTIVE, false), Block.UPDATE_CLIENTS);
        }
    }
}
