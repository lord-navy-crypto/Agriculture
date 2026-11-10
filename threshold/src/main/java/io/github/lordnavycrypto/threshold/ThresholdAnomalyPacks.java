package io.github.lordnavycrypto.threshold;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.neoforge.event.AddPackFindersEvent;

/**
 * Opt-in anomaly structures. OFF by default so existing worlds retain their
 * six established maze levels and cannot accidentally spawn giant towers.
 * Enabling this pack is a new-world experiment, never a migration.
 */
public final class ThresholdAnomalyPacks {
    private static final PackSource OPTIONAL_BUILT_IN =
            PackSource.create(PackSource.BUILT_IN::decorate, false);

    private ThresholdAnomalyPacks() {}

    public static void register(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.SERVER_DATA) return;
        event.addPackFinders(
                ResourceLocation.fromNamespaceAndPath(
                        "gugugaga", "resourcepacks/threshold_anomalies"),
                PackType.SERVER_DATA,
                Component.literal("Backroom Agriculture | Impossible Architecture (Experimental)"),
                OPTIONAL_BUILT_IN,
                false,
                Pack.Position.TOP
        );
    }
}
