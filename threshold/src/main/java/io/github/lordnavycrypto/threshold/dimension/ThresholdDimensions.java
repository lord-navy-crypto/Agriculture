package io.github.lordnavycrypto.threshold.dimension;

import io.github.lordnavycrypto.threshold.ThresholdMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public final class ThresholdDimensions {
    public static final ResourceKey<Level> LEVEL_0 = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(ThresholdMod.MOD_ID, "level_0")
    );

    public static final ResourceKey<Level> LEVEL_1 = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(ThresholdMod.MOD_ID, "level_1")
    );

    public static final ResourceKey<Level> LEVEL_2 = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(ThresholdMod.MOD_ID, "level_2")
    );

    public static final ResourceKey<Level> LEVEL_3 = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(ThresholdMod.MOD_ID, "level_3")
    );

    public static final ResourceKey<Level> LEVEL_4 = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(ThresholdMod.MOD_ID, "level_4")
    );

    private ThresholdDimensions() {
    }
}
