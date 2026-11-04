package io.github.lordnavycrypto.threshold.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A real BlockEntity-backed ceiling fixture for GeckoLib's continuous
 * low-amplitude fan animation. This is presentation-only.
 */
public final class ThresholdCeilingVentBlock extends BaseEntityBlock {
    public ThresholdCeilingVentBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ThresholdCeilingVentBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
}
