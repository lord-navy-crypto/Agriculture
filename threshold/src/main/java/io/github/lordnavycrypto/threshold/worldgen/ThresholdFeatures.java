package io.github.lordnavycrypto.threshold.worldgen;

import io.github.lordnavycrypto.threshold.ThresholdMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Locale;

public final class ThresholdFeatures {
    public static final DeferredRegister<Feature<?>> REGISTER =
            DeferredRegister.create(Registries.FEATURE, ThresholdMod.MOD_ID);

    static {
        REGISTER.register("room_layout", ThresholdRoomLayoutFeature::new);
        REGISTER.register("industrial_vent", ThresholdIndustrialVentFeature::new);

        // Port the 10 deterministic regional GeoWonder forms.
        for (ThresholdGeoWonderRegionFeature.Kind kind : ThresholdGeoWonderRegionFeature.Kind.values()) {
            String id = "geo_wonder_" + kind.name().toLowerCase(Locale.ROOT);
            REGISTER.register(id, () -> new ThresholdGeoWonderRegionFeature(kind));
        }

        // Port the seven additional EarthWonder forms. COOLED_LAVA_FIELD is
        // shared with GeoWonder and is not registered twice.
        for (ThresholdEarthWonderFeature.Kind kind : ThresholdEarthWonderFeature.Kind.values()) {
            if (kind == ThresholdEarthWonderFeature.Kind.COOLED_LAVA_FIELD) continue;
            String id = "earth_wonder_" + kind.name().toLowerCase(Locale.ROOT);
            REGISTER.register(id, () -> new ThresholdEarthWonderFeature(kind));
        }

        for (ThresholdGroundMicrostructureFeature.Kind kind : ThresholdGroundMicrostructureFeature.Kind.values()) {
            String id = "ground_microstructure_" + kind.name().toLowerCase(Locale.ROOT);
            REGISTER.register(id, () -> new ThresholdGroundMicrostructureFeature(kind));
        }

        for (ThresholdVanillaHabitatFeature.Habitat habitat : ThresholdVanillaHabitatFeature.Habitat.values()) {
            String id = "vanilla_habitat_" + habitat.name().toLowerCase(Locale.ROOT);
            REGISTER.register(id, () -> new ThresholdVanillaHabitatFeature(habitat));
        }

        for (ThresholdGrandEcologyFeature.Kind kind : ThresholdGrandEcologyFeature.Kind.values()) {
            String id = "grand_ecology_" + kind.name().toLowerCase(Locale.ROOT);
            REGISTER.register(id, () -> new ThresholdGrandEcologyFeature(kind));
        }
    }

    private ThresholdFeatures() {
    }
}
