package io.github.lordnavycrypto.threshold;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.neoforge.event.AddPackFindersEvent;

/**
 * Complete six-floor anomaly structure pool. Enabled by default for new
 * worlds, but remains user-disableable; existing world data is never rewritten.
 */
public final class ThresholdAnomalyPacks {
    private static final PackSource OPTIONAL_BUILT_IN =
            PackSource.create(PackSource.BUILT_IN::decorate, true);

    private ThresholdAnomalyPacks() {}

    public static void register(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.SERVER_DATA) return;
        event.addPackFinders(
                ResourceLocation.fromNamespaceAndPath(
                        "gugugaga", "resourcepacks/threshold_anomalies"),
                PackType.SERVER_DATA,
                Component.literal("Backroom Agriculture | Impossible Architecture (Experimental, enabled by default)"),
                OPTIONAL_BUILT_IN,
                false,
                Pack.Position.TOP
        );
    }
}
