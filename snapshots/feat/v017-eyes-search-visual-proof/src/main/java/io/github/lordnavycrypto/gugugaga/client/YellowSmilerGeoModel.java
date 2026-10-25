package io.github.lordnavycrypto.gugugaga.client;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import io.github.lordnavycrypto.gugugaga.entity.YellowSmilerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public final class YellowSmilerGeoModel extends GeoModel<YellowSmilerEntity> {
    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(GuguGagaMod.MOD_ID, path);
    }
    @Override public ResourceLocation getModelResource(YellowSmilerEntity entity) {
        return id("geo/yellow_smiler.geo.json");
    }
    @Override public ResourceLocation getTextureResource(YellowSmilerEntity entity) {
        return id(entity.revealed() ? "textures/entity/yellow_smiler_true.png" : "textures/entity/yellow_smiler.png");
    }
    @Override public ResourceLocation getAnimationResource(YellowSmilerEntity entity) {
        return id("animations/yellow_smiler.animation.json");
    }
}
