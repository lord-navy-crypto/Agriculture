package io.github.lordnavycrypto.threshold.entity;

import io.github.lordnavycrypto.threshold.ThresholdMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Entity registry for the two intentionally different horror layers. */
public final class ThresholdEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, ThresholdMod.MOD_ID);

    /** A non-combat visual presence used only by Level 0. */
    public static final DeferredHolder<EntityType<?>, EntityType<ThresholdWatcherEntity>> WATCHER =
            ENTITY_TYPES.register("watcher", () -> EntityType.Builder
                    .of(ThresholdWatcherEntity::new, MobCategory.MISC)
                    .sized(0.72F, 2.65F)
                    .clientTrackingRange(32)
                    .updateInterval(2)
                    .noSave()
                    .build("threshold:watcher"));

    /** A real hostile Backrooms predator, restricted to Level 1 by the director. */
    public static final DeferredHolder<EntityType<?>, EntityType<ThresholdHoundEntity>> HOUND =
            ENTITY_TYPES.register("hound", () -> EntityType.Builder
                    .of(ThresholdHoundEntity::new, MobCategory.MONSTER)
                    .sized(0.95F, 1.05F)
                    .clientTrackingRange(12)
                    .updateInterval(3)
                    .build("threshold:hound"));

    /** A non-lethal Overworld lure that leads players into Level 0. */
    public static final DeferredHolder<EntityType<?>, EntityType<ThresholdPartygoerEntity>> PARTYGOER =
            ENTITY_TYPES.register("partygoer", () -> EntityType.Builder
                    .of(ThresholdPartygoerEntity::new, MobCategory.CREATURE)
                    .sized(0.65F, 2.35F)
                    .clientTrackingRange(32)
                    .updateInterval(2)
                    .build("threshold:partygoer"));

    private ThresholdEntities() {}
}
