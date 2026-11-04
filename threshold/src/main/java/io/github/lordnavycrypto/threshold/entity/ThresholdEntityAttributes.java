package io.github.lordnavycrypto.threshold.entity;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

/** Attribute registration for the non-combat observer and Level 1 Hound. */
public final class ThresholdEntityAttributes {
    private ThresholdEntityAttributes() {}

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        AttributeSupplier watcherAttributes = Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .build();
        event.put(ThresholdEntities.WATCHER.get(), watcherAttributes);
        event.put(ThresholdEntities.HOUND.get(), ThresholdHoundEntity.createAttributes().build());
        event.put(ThresholdEntities.PARTYGOER.get(), ThresholdPartygoerEntity.createAttributes().build());
    }
}
