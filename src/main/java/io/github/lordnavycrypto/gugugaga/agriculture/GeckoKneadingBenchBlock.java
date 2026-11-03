package io.github.lordnavycrypto.gugugaga.agriculture;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * GeckoLib-backed rendering for the existing dough bench.
 *
 * Processing, tool checks, item consumption and the finite ACTIVE state remain
 * in KitchenProcessingBlock/KitchenV3AnimatedBlock. Only world rendering is
 * moved to the GeoBlockEntity renderer.
 */
public final class GeckoKneadingBenchBlock extends KitchenV3AnimatedBlock implements EntityBlock {
    public GeckoKneadingBenchBlock(BlockBehaviour.Properties properties) {
        super(KitchenProcessingBlock.Kind.KNEAD, properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GeckoKneadingBenchBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
}
