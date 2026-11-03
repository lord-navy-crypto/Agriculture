package io.github.lordnavycrypto.gugugaga.client;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import io.github.lordnavycrypto.gugugaga.agriculture.GeckoKneadingBenchBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public final class GeckoKneadingBenchModel extends GeoModel<GeckoKneadingBenchBlockEntity> {
    @Override
    public ResourceLocation getModelResource(GeckoKneadingBenchBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(GuguGagaMod.MOD_ID,
                "geo/kneading_bench.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GeckoKneadingBenchBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(GuguGagaMod.MOD_ID,
                "textures/entity/kneading_bench.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GeckoKneadingBenchBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(GuguGagaMod.MOD_ID,
                "animations/kneading_bench.animation.json");
    }
}
