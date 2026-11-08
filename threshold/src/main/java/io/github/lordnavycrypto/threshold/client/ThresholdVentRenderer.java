package io.github.lordnavycrypto.threshold.client;

import io.github.lordnavycrypto.threshold.block.ThresholdCeilingVentBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/** All animated vent geometry is client-only, with no server-side renderer classes. */
public final class ThresholdVentRenderer extends GeoBlockRenderer<ThresholdCeilingVentBlockEntity> {
    public ThresholdVentRenderer() {
        super(new ThresholdVentModel());
    }
}
