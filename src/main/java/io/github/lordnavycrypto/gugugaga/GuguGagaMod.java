package io.github.lordnavycrypto.gugugaga;

import io.github.lordnavycrypto.gugugaga.entity.GugugagaEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(GuguGagaMod.MOD_ID)
public final class GuguGagaMod {
    public static final String MOD_ID = "gugugaga";

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<GugugagaEntity>> OBSERVER =
            ENTITIES.register("observer", () -> EntityType.Builder
                    .of(GugugagaEntity::new, MobCategory.CREATURE)
                    .sized(0.95f, 1.50f).clientTrackingRange(8)
                    .updateInterval(3).build("gugugaga:observer"));

    public GuguGagaMod(IEventBus modBus) {
        ENTITIES.register(modBus);
        modBus.addListener(GuguGagaMod::onAttributes);
    }

    private static void onAttributes(EntityAttributeCreationEvent event) {
        event.put(OBSERVER.get(), GugugagaEntity.createAttributes().build());
    }
}
