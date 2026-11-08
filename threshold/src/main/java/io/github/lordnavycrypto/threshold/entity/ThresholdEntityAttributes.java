package io.github.lordnavycrypto.threshold.entity;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

/** Attribute registration for the six-level Distant Watcher, Level 1 Hound and Level 1/Overworld Partygoer. */
public final class ThresholdEntityAttributes {
    private ThresholdEntityAttributes() {}

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        AttributeSupplier watcherAttributes = Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 22.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.34D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5D)
                .add(Attributes.FOLLOW_RANGE, 64.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.35D)
                .build();
        event.put(ThresholdEntities.WATCHER.get(), watcherAttributes);
        event.put(ThresholdEntities.HOUND.get(), ThresholdHoundEntity.createAttributes().build());
        event.put(ThresholdEntities.PARTYGOER.get(), ThresholdPartygoerEntity.createAttributes().build());
    }
}
