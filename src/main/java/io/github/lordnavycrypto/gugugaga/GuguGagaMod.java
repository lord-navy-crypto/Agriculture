package io.github.lordnavycrypto.gugugaga;

import io.github.lordnavycrypto.gugugaga.entity.GugugagaEntity;
import io.github.lordnavycrypto.gugugaga.command.OllamaCommands;
import io.github.lordnavycrypto.gugugaga.command.NightwatchCommands;
import io.github.lordnavycrypto.gugugaga.ai.NightwatchDirector;
import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
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

    public GuguGagaMod(IEventBus modBus, ModContainer container) {
        ENTITIES.register(modBus);
        modBus.addListener(GuguGagaMod::onAttributes);
        container.registerConfig(ModConfig.Type.SERVER, NightwatchConfig.SPEC);
        NeoForge.EVENT_BUS.addListener(OllamaCommands::register);
        NeoForge.EVENT_BUS.addListener(NightwatchCommands::register);
        NeoForge.EVENT_BUS.addListener(NightwatchDirector::onTick);
    }

    private static void onAttributes(EntityAttributeCreationEvent event) {
        event.put(OBSERVER.get(), GugugagaEntity.createAttributes().build());
    }
}
