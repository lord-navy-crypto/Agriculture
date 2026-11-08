package io.github.lordnavycrypto.gugugaga.client;

import io.github.lordnavycrypto.gugugaga.agriculture.GeckoKitchenStationBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public final class GeckoKitchenStationRenderer extends GeoBlockRenderer<GeckoKitchenStationBlockEntity> {
    public GeckoKitchenStationRenderer(BlockEntityRendererProvider.Context context) {
        super(new GeckoKitchenStationModel());
    }
}
