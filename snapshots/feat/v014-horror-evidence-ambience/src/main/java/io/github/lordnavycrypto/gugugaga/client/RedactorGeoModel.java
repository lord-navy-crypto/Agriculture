package io.github.lordnavycrypto.gugugaga.client;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import io.github.lordnavycrypto.gugugaga.entity.RedactorEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public final class RedactorGeoModel extends GeoModel<RedactorEntity> {
    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(GuguGagaMod.MOD_ID, path);
    }
    @Override public ResourceLocation getModelResource(RedactorEntity entity) {
        return id(entity.revealed() ? "geo/redactor_true.geo.json" : "geo/redactor_lapis.geo.json");
    }
    @Override public ResourceLocation getTextureResource(RedactorEntity entity) {
        return id(entity.revealed() ? "textures/entity/redactor_true.png" : "textures/entity/lapis_mask.png");
    }
    @Override public ResourceLocation getAnimationResource(RedactorEntity entity) {
        return id("animations/redactor.animation.json");
    }
}
