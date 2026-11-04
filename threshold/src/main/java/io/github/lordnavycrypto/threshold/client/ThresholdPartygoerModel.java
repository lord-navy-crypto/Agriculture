package io.github.lordnavycrypto.threshold.client;

import io.github.lordnavycrypto.threshold.ThresholdMod;
import io.github.lordnavycrypto.threshold.entity.ThresholdPartygoerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public final class ThresholdPartygoerModel extends GeoModel<ThresholdPartygoerEntity> {
    @Override
    public ResourceLocation getModelResource(ThresholdPartygoerEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(ThresholdMod.MOD_ID, "geo/watcher.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ThresholdPartygoerEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(ThresholdMod.MOD_ID, "textures/entity/watcher.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ThresholdPartygoerEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(ThresholdMod.MOD_ID, "animations/watcher.animation.json");
    }
}
