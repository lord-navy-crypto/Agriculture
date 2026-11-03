package io.github.lordnavycrypto.gugugaga.agriculture;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Shared GeckoLib rendering adapter for the three v3 kitchen workstations.
 * All processing rules, tool checks and finite ACTIVE timing stay in the
 * existing KitchenProcessingBlock/KitchenV3AnimatedBlock hierarchy.
 */
public final class GeckoKitchenStationBlock extends KitchenV3AnimatedBlock implements EntityBlock {
    private final GeckoKitchenStationBlockEntity.Profile profile;

    public GeckoKitchenStationBlock(Kind kind,
                                    GeckoKitchenStationBlockEntity.Profile profile,
                                    BlockBehaviour.Properties properties) {
        super(kind, properties);
        this.profile = profile;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GeckoKitchenStationBlockEntity(profile, pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
}
