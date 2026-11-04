package io.github.lordnavycrypto.threshold.client;

import io.github.lordnavycrypto.threshold.entity.ThresholdWatcherEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/** A distant, dark silhouette; it has no combat presentation or nameplate. */
public final class ThresholdWatcherRenderer extends GeoEntityRenderer<ThresholdWatcherEntity> {
    public ThresholdWatcherRenderer(EntityRendererProvider.Context context) {
        super(context, new ThresholdWatcherModel());
        this.shadowRadius = 0.0F;
    }
}
