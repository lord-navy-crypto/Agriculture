package io.github.lordnavycrypto.threshold.client;

import io.github.lordnavycrypto.threshold.ThresholdMod;
import io.github.lordnavycrypto.threshold.entity.ThresholdHoundEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public final class ThresholdHoundModel extends GeoModel<ThresholdHoundEntity> {
    @Override
    public ResourceLocation getModelResource(ThresholdHoundEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(ThresholdMod.MOD_ID, "geo/hound.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ThresholdHoundEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(ThresholdMod.MOD_ID, "textures/entity/hound.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ThresholdHoundEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(ThresholdMod.MOD_ID, "animations/hound.animation.json");
    }
}
