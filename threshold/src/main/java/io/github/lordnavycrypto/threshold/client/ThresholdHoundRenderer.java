package io.github.lordnavycrypto.threshold.client;

import io.github.lordnavycrypto.threshold.entity.ThresholdHoundEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/** GeckoLib renderer for the Level 1 Hound. */
public final class ThresholdHoundRenderer extends GeoEntityRenderer<ThresholdHoundEntity> {
    public ThresholdHoundRenderer(EntityRendererProvider.Context context) {
        super(context, new ThresholdHoundModel());
        this.shadowRadius = 0.55F;
    }
}
