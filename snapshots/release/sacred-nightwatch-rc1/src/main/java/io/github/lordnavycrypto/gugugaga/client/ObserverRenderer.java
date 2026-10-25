package io.github.lordnavycrypto.gugugaga.client;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import io.github.lordnavycrypto.gugugaga.entity.GugugagaEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/** GeckoLib v4 renderer; legacy layer retained temporarily for v0.10 model QA. */
public final class ObserverRenderer extends GeoEntityRenderer<GugugagaEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(GuguGagaMod.MOD_ID, "observer"), "main");
    // observer_blackened.png is picked dynamically in ObserverGeoModel.
    public ObserverRenderer(EntityRendererProvider.Context context) {
        super(context, new ObserverGeoModel());
    }
}
