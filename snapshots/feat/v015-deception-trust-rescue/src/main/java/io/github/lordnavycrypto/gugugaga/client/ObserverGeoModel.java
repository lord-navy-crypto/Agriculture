package io.github.lordnavycrypto.gugugaga.client;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import io.github.lordnavycrypto.gugugaga.entity.GugugagaEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public final class ObserverGeoModel extends GeoModel<GugugagaEntity> {
    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(GuguGagaMod.MOD_ID, path);
    }
    @Override public ResourceLocation getModelResource(GugugagaEntity entity) {
        return id("geo/observer.geo.json");
    }
    @Override public ResourceLocation getTextureResource(GugugagaEntity entity) {
        return id(entity.corruptionStage() >= 3 ? "textures/entity/observer_blackened.png" : "textures/entity/observer.png");
    }
    @Override public ResourceLocation getAnimationResource(GugugagaEntity entity) {
        return id("animations/observer.animation.json");
    }
}
