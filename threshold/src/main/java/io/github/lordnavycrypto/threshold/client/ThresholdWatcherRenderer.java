package io.github.lordnavycrypto.threshold.client;

import io.github.lordnavycrypto.threshold.entity.ThresholdWatcherEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/** A distant, dark silhouette; it has no combat presentation or nameplate. */
public final class ThresholdWatcherRenderer extends GeoEntityRenderer<ThresholdWatcherEntity> {
    public ThresholdWatcherRenderer(EntityRendererProvider.Context context) {
        super(context, new ThresholdWatcherModel());
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(ThresholdWatcherEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        // The Watcher is intentionally charcoal-dark. Full-bright sampling keeps
        // its silhouette and eye details readable even in Level 0's darkest rooms.
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, LightTexture.FULL_BRIGHT);
    }
}
