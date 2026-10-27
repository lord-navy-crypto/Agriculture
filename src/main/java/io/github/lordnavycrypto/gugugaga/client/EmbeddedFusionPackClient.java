package io.github.lordnavycrypto.gugugaga.client;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddPackFindersEvent;

/**
 * The optional Fusion visual layer ships inside the Agriculture JAR.
 *
 * With Fusion installed, its overlays activate automatically; without Fusion,
 * they are not registered, so vanilla-compatible core models stay in use.
 * The nested pack is never required on a dedicated server.
 */
@EventBusSubscriber(modid = GuguGagaMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class EmbeddedFusionPackClient {
    private EmbeddedFusionPackClient() {}

    @SubscribeEvent
    public static void addEmbeddedPack(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES || !ModList.get().isLoaded("fusion")) {
            return;
        }
        event.addPackFinders(
                ResourceLocation.fromNamespaceAndPath(GuguGagaMod.MOD_ID, "resourcepacks/gugugaga_fusion"),
                PackType.CLIENT_RESOURCES,
                Component.literal("Gugu Gaga Agriculture: Fusion Enhanced Visuals"),
                PackSource.BUILT_IN,
                true,
                Pack.Position.TOP
        );
    }
}
