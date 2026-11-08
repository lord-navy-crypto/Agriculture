package io.github.lordnavycrypto.gugugaga.client;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import io.github.lordnavycrypto.gugugaga.agriculture.GeckoKitchenStationBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/** Resolves the rig, texture and animation set from the station's immutable profile. */
public final class GeckoKitchenStationModel extends GeoModel<GeckoKitchenStationBlockEntity> {
    @Override
    public ResourceLocation getModelResource(GeckoKitchenStationBlockEntity animatable) {
        return asset(animatable, "geo/", ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GeckoKitchenStationBlockEntity animatable) {
        return asset(animatable, "textures/entity/", ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(GeckoKitchenStationBlockEntity animatable) {
        return asset(animatable, "animations/", ".animation.json");
    }

    private ResourceLocation asset(GeckoKitchenStationBlockEntity animatable,
                                   String folder, String suffix) {
        return ResourceLocation.fromNamespaceAndPath(GuguGagaMod.MOD_ID,
                folder + animatable.profile().assetId() + suffix);
    }
}
