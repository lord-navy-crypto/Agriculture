package io.github.lordnavycrypto.threshold.client;

import io.github.lordnavycrypto.threshold.ThresholdMod;
import io.github.lordnavycrypto.threshold.block.ThresholdCeilingVentBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public final class ThresholdVentModel extends GeoModel<ThresholdCeilingVentBlockEntity> {
    @Override
    public ResourceLocation getModelResource(ThresholdCeilingVentBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(ThresholdMod.MOD_ID, "geo/ceiling_vent.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ThresholdCeilingVentBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(ThresholdMod.MOD_ID, "textures/block/warehouse_wall.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ThresholdCeilingVentBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(ThresholdMod.MOD_ID, "animations/ceiling_vent.animation.json");
    }
}
