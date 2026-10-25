package io.github.lordnavycrypto.gugugaga.client;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import io.github.lordnavycrypto.gugugaga.entity.RedactorEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public final class RedactorRenderer extends MobRenderer<RedactorEntity,RedactorModel> {
    public static final ModelLayerLocation LAYER=new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(GuguGagaMod.MOD_ID,"redactor"),"main");
    private static final ResourceLocation MASK=ResourceLocation.fromNamespaceAndPath(
            GuguGagaMod.MOD_ID,"textures/entity/lapis_mask.png");
    private static final ResourceLocation TRUE=ResourceLocation.fromNamespaceAndPath(
            GuguGagaMod.MOD_ID,"textures/entity/redactor_true.png");

    public RedactorRenderer(EntityRendererProvider.Context context) {
        super(context,new RedactorModel(context.bakeLayer(LAYER)),0.48f);
    }

    @Override public ResourceLocation getTextureLocation(RedactorEntity entity) {
        return entity.revealed()?TRUE:MASK;
    }
}
