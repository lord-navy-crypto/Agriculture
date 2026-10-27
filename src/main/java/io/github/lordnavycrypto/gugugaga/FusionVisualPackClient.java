package io.github.lordnavycrypto.gugugaga;

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
 * Adds the fully bundled visual overlay as a separate built-in client resource pack.
 *
 * If Fusion is absent, never register this pack: its fusion:model loaders and
 * random .png.mcmeta atlases must not replace vanilla-compatible game resources.
 * No Fusion code is linked and no optional mod is shipped inside our JAR.
 */
@EventBusSubscriber(modid = GuguGagaMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class FusionVisualPackClient {
    private FusionVisualPackClient() {}

    @SubscribeEvent
    public static void registerBuiltInFusionPack(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) {
            return;
        }
        if (!ModList.get().isLoaded("fusion")) {
            return;
        }

        // The folder is included in the mod JAR by processResources and has
        // its own pack.mcmeta and assets/ root. It is NOT default mod assets.
        event.addPackFinders(
                ResourceLocation.fromNamespaceAndPath(GuguGagaMod.MOD_ID, "resourcepacks/fusion"),
                PackType.CLIENT_RESOURCES,
                Component.literal("Gugu Gaga Agriculture — Fusion Visuals (Built-in)"),
                PackSource.BUILT_IN,
                true,
                Pack.Position.TOP
        );
    }
}
