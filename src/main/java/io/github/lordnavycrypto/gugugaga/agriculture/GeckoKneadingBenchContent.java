package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** GeckoLib block-entity registrations for the animated workstation prototype. */
public final class GeckoKneadingBenchContent {
    private GeckoKneadingBenchContent() {}

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, GuguGagaMod.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeckoKneadingBenchBlockEntity>>
            DOUGH_BENCH = BLOCK_ENTITY_TYPES.register("dough_bench",
            () -> BlockEntityType.Builder.of(GeckoKneadingBenchBlockEntity::new,
                    KitchenV3Content.DOUGH_BENCH.get()).build(null));
}
