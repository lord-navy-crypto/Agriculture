package io.github.lordnavycrypto.gugugaga.client;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import io.github.lordnavycrypto.gugugaga.entity.YellowSmilerEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public final class YellowSmilerRenderer extends MobRenderer<YellowSmilerEntity,YellowSmilerModel> {
    public static final ModelLayerLocation LAYER=new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(GuguGagaMod.MOD_ID,"yellow_smiler"),"main");
    private static final ResourceLocation TEXTURE=ResourceLocation.fromNamespaceAndPath(
            GuguGagaMod.MOD_ID,"textures/entity/yellow_smiler.png");

    private static final ResourceLocation EXPOSED =
            ResourceLocation.fromNamespaceAndPath(GuguGagaMod.MOD_ID,
                    "textures/entity/yellow_smiler_true.png");

    public YellowSmilerRenderer(EntityRendererProvider.Context context) {
        super(context,new YellowSmilerModel(context.bakeLayer(LAYER)),0.45f);
    }

    @Override public ResourceLocation getTextureLocation(YellowSmilerEntity entity) {
        return entity.revealed()?EXPOSED:TEXTURE;
    }
}
