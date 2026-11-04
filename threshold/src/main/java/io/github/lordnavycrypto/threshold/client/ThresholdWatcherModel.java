package io.github.lordnavycrypto.threshold.client;

import io.github.lordnavycrypto.threshold.ThresholdMod;
import io.github.lordnavycrypto.threshold.entity.ThresholdWatcherEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public final class ThresholdWatcherModel extends GeoModel<ThresholdWatcherEntity> {
    @Override
    public ResourceLocation getModelResource(ThresholdWatcherEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(ThresholdMod.MOD_ID, "geo/watcher.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ThresholdWatcherEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(ThresholdMod.MOD_ID, "textures/entity/watcher.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ThresholdWatcherEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(ThresholdMod.MOD_ID, "animations/watcher.animation.json");
    }
}
