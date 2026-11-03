package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Three GeckoLib workstation block-entity types sharing one animation implementation. */
public final class GeckoKitchenStationContent {
    private GeckoKitchenStationContent() {}

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, GuguGagaMod.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeckoKitchenStationBlockEntity>>
            DOUGH_BENCH = register("dough_bench",
            () -> KitchenV3Content.DOUGH_BENCH.get(),
            GeckoKitchenStationBlockEntity.Profile.KNEAD);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeckoKitchenStationBlockEntity>>
            SKILLET_STATION = register("skillet_station",
            () -> KitchenV3Content.SKILLET_STATION.get(),
            GeckoKitchenStationBlockEntity.Profile.SKILLET);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeckoKitchenStationBlockEntity>>
            BAMBOO_STEAMER = register("bamboo_steamer",
            () -> KitchenV3Content.BAMBOO_STEAMER.get(),
            GeckoKitchenStationBlockEntity.Profile.STEAMER);

    private static DeferredHolder<BlockEntityType<?>, BlockEntityType<GeckoKitchenStationBlockEntity>>
    register(String id, Supplier<? extends Block> block,
             GeckoKitchenStationBlockEntity.Profile profile) {
        return BLOCK_ENTITY_TYPES.register(id, () -> BlockEntityType.Builder.of(
                (pos, state) -> new GeckoKitchenStationBlockEntity(profile, pos, state),
                block.get()).build(null));
    }

    public static BlockEntityType<GeckoKitchenStationBlockEntity> typeFor(
            GeckoKitchenStationBlockEntity.Profile profile) {
        return switch (profile) {
            case KNEAD -> DOUGH_BENCH.get();
            case SKILLET -> SKILLET_STATION.get();
            case STEAMER -> BAMBOO_STEAMER.get();
        };
    }
}
