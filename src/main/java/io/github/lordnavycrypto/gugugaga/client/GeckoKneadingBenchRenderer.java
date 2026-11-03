package io.github.lordnavycrypto.gugugaga.client;

import io.github.lordnavycrypto.gugugaga.agriculture.GeckoKneadingBenchBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public final class GeckoKneadingBenchRenderer extends GeoBlockRenderer<GeckoKneadingBenchBlockEntity> {
    public GeckoKneadingBenchRenderer(BlockEntityRendererProvider.Context context) {
        super(new GeckoKneadingBenchModel());
    }
}
