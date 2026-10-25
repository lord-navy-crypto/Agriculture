package io.github.lordnavycrypto.gugugaga.client;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import io.github.lordnavycrypto.gugugaga.entity.GugugagaEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public final class ObserverRenderer extends MobRenderer<GugugagaEntity, ObserverModel<GugugagaEntity>> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(GuguGagaMod.MOD_ID, "observer"), "main");
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(GuguGagaMod.MOD_ID, "textures/entity/observer.png");

    public ObserverRenderer(EntityRendererProvider.Context context) {
        super(context, new ObserverModel<>(context.bakeLayer(LAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(GugugagaEntity entity) {
        return TEXTURE;
    }
}
