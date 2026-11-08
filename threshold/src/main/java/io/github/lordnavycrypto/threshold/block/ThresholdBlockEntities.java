package io.github.lordnavycrypto.threshold.block;

import io.github.lordnavycrypto.threshold.ThresholdMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ThresholdBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ThresholdMod.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ThresholdCeilingVentBlockEntity>> CEILING_VENT =
            TYPES.register("ceiling_vent",
                    () -> BlockEntityType.Builder.of(ThresholdCeilingVentBlockEntity::new,
                            ThresholdEnvironmentBlocks.CEILING_VENT.get()).build(null));

    private ThresholdBlockEntities() {}
}
