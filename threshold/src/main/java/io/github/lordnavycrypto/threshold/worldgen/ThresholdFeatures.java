package io.github.lordnavycrypto.threshold.worldgen;

import io.github.lordnavycrypto.threshold.ThresholdMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ThresholdFeatures {
    public static final DeferredRegister<Feature<?>> REGISTER =
            DeferredRegister.create(Registries.FEATURE, ThresholdMod.MOD_ID);

    static {
        REGISTER.register("room_layout", ThresholdRoomLayoutFeature::new);
        REGISTER.register("industrial_vent", ThresholdIndustrialVentFeature::new);
    }

    private ThresholdFeatures() {
    }
}
