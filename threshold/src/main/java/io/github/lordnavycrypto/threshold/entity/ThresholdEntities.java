package io.github.lordnavycrypto.threshold.entity;

import io.github.lordnavycrypto.threshold.ThresholdMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Entity registry. Natural spawn locations are controlled by ThresholdEntityDirector;
 * operator summons are deliberately allowed in every dimension. */
public final class ThresholdEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, ThresholdMod.MOD_ID);

    /** Naturally appears throughout Levels 0-5; it is aggressive in Level 0 and passive elsewhere. */
    public static final DeferredHolder<EntityType<?>, EntityType<ThresholdWatcherEntity>> WATCHER =
            ENTITY_TYPES.register("watcher", () -> EntityType.Builder
                    .of(ThresholdWatcherEntity::new, MobCategory.MISC)
                    .sized(0.72F, 2.65F)
                    .clientTrackingRange(32)
                    .updateInterval(4)
                    .noSave()
                    .build("threshold:watcher"));

    /** A hostile Backrooms predator that naturally appears only in Level 1. */
    public static final DeferredHolder<EntityType<?>, EntityType<ThresholdHoundEntity>> HOUND =
            ENTITY_TYPES.register("hound", () -> EntityType.Builder
                    .of(ThresholdHoundEntity::new, MobCategory.MONSTER)
                    .sized(0.95F, 1.05F)
                    .clientTrackingRange(12)
                    .updateInterval(3)
                    .build("threshold:hound"));

    /** A hostile predator with natural night encounters in Level 1 and the Overworld. */
    public static final DeferredHolder<EntityType<?>, EntityType<ThresholdPartygoerEntity>> PARTYGOER =
            ENTITY_TYPES.register("partygoer", () -> EntityType.Builder
                    .of(ThresholdPartygoerEntity::new, MobCategory.MONSTER)
                    .sized(0.65F, 2.35F)
                    .clientTrackingRange(32)
                    .updateInterval(2)
                    .build("threshold:partygoer"));

    private ThresholdEntities() {}
}
