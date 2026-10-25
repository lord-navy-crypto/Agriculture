package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Four distinct entity IDs in the v0.2 pollinator prototype.
 * They currently reuse vanilla Bee AI/model/hive behavior; true nesting AI comes later.
 */
public final class BeeWildlife {
    private BeeWildlife() {}

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, GuguGagaMod.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<Bee>> BUMBLEBEE =
            bee("bumblebee", 0.75F, 0.65F);
    public static final DeferredHolder<EntityType<?>, EntityType<Bee>> MASON_BEE =
            bee("mason_bee", 0.55F, 0.48F);
    public static final DeferredHolder<EntityType<?>, EntityType<Bee>> SQUASH_BEE =
            bee("squash_bee", 0.65F, 0.55F);
    public static final DeferredHolder<EntityType<?>, EntityType<Bee>> LEAFCUTTER_BEE =
            bee("leafcutter_bee", 0.60F, 0.50F);

    private static DeferredHolder<EntityType<?>, EntityType<Bee>> bee(String name, float width, float height) {
        return ENTITIES.register(name, () -> EntityType.Builder.of(Bee::new, MobCategory.CREATURE)
                .sized(width, height).clientTrackingRange(8)
                .build("gugugaga:" + name));
    }

    public static void onAttributes(EntityAttributeCreationEvent event) {
        event.put(BUMBLEBEE.get(), Bee.createAttributes().build());
        event.put(MASON_BEE.get(), Bee.createAttributes().build());
        event.put(SQUASH_BEE.get(), Bee.createAttributes().build());
        event.put(LEAFCUTTER_BEE.get(), Bee.createAttributes().build());
    }

    public static void onSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        place(event, BUMBLEBEE.get());
        place(event, MASON_BEE.get());
        place(event, SQUASH_BEE.get());
        place(event, LEAFCUTTER_BEE.get());
    }

    private static void place(RegisterSpawnPlacementsEvent event, EntityType<Bee> type) {
        event.register(type, SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, level, reason, pos, random) ->
                        level.getBlockState(pos.below()).is(Blocks.GRASS_BLOCK)
                                && level.getRawBrightness(pos, 0) > 8,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
}
