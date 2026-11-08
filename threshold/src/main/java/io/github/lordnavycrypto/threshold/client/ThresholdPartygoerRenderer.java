package io.github.lordnavycrypto.threshold.client;

import io.github.lordnavycrypto.threshold.entity.ThresholdPartygoerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/** Uses its own geometry, animation and smiling-mask texture; it is visually separate from the Level 0 Observer. */
public final class ThresholdPartygoerRenderer extends GeoEntityRenderer<ThresholdPartygoerEntity> {
    public ThresholdPartygoerRenderer(EntityRendererProvider.Context context) {
        super(context, new ThresholdPartygoerModel());
        this.shadowRadius = 0.25F;
    }
}
