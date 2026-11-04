package io.github.lordnavycrypto.threshold.client;

import io.github.lordnavycrypto.threshold.entity.ThresholdPartygoerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/** Reuses the dark silhouette presentation until the dedicated party mask asset is authored. */
public final class ThresholdPartygoerRenderer extends GeoEntityRenderer<ThresholdPartygoerEntity> {
    public ThresholdPartygoerRenderer(EntityRendererProvider.Context context) {
        super(context, new ThresholdPartygoerModel());
        this.shadowRadius = 0.25F;
    }
}
