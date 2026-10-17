package io.github.lordnavycrypto.gugugaga.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.Entity;
import io.github.lordnavycrypto.gugugaga.ai.ObserverBrain;
import io.github.lordnavycrypto.gugugaga.ai.ThreatPolicy;
import io.github.lordnavycrypto.gugugaga.entity.GugugagaEntity;
import net.minecraft.util.Mth;

public final class ObserverModel<T extends Entity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart leftWing;
    private final ModelPart rightWing;

    public ObserverModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.leftWing = root.getChild("left_wing");
        this.rightWing = root.getChild("right_wing");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-5, -12, -4, 10, 12, 8),
                PartPose.offset(0, 21, 0));
        root.addOrReplaceChild("belly", CubeListBuilder.create()
                .texOffs(40, 0).addBox(-4, -8, -4.6f, 8, 8, 1),
                PartPose.offset(0, 21, 0));
        // Eyes and beak are children of the head so the observer's gaze
        // can track players instead of its detached facial pieces drifting.
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 33).addBox(-5, -7, -4, 10, 8, 8),
                PartPose.offset(0, 9, 0));
        head.addOrReplaceChild("left_eye", CubeListBuilder.create()
                .texOffs(40, 0).addBox(1.5f, -4.3f, -4.6f, 2, 2, 1),
                PartPose.ZERO);
        head.addOrReplaceChild("right_eye", CubeListBuilder.create()
                .texOffs(40, 0).addBox(-3.5f, -4.3f, -4.6f, 2, 2, 1),
                PartPose.ZERO);
        head.addOrReplaceChild("beak", CubeListBuilder.create()
                .texOffs(40, 16).addBox(-2, -2.0f, -6.5f, 4, 2, 3),
                PartPose.ZERO);
        root.addOrReplaceChild("left_wing", CubeListBuilder.create()
                .texOffs(0, 16).addBox(0, -9, -2.5f, 2, 8, 5),
                PartPose.offset(4.7f, 21, 0));
        root.addOrReplaceChild("right_wing", CubeListBuilder.create()
                .texOffs(0, 16).addBox(-2, -9, -2.5f, 2, 8, 5),
                PartPose.offset(-4.7f, 21, 0));
        root.addOrReplaceChild("left_foot", CubeListBuilder.create()
                .texOffs(40, 16).addBox(1, -1, -5, 3, 2, 6),
                PartPose.offset(0, 23, 0));
        root.addOrReplaceChild("right_foot", CubeListBuilder.create()
                .texOffs(40, 16).addBox(-4, -1, -5, 3, 2, 6),
                PartPose.offset(0, 23, 0));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        root.getAllParts().forEach(ModelPart::resetPose);
        float sway=(float)Math.sin(limbSwing*0.66f)
                *Math.min(limbSwingAmount,1.0f)*0.25f;
        float breath=(float)Math.sin(ageInTicks*0.075f)*0.035f;
        // Deliberate slow gaze, never fast spins, screen effects or flashing.
        head.yRot=Mth.clamp(netHeadYaw*0.011f,-0.52f,0.52f)+breath;
        head.xRot=Mth.clamp(headPitch*0.008f,-0.20f,0.25f);
        if(entity instanceof GugugagaEntity observer) {
            ObserverBrain.Phase phase=observer.phase();
            ThreatPolicy.Mood mood=observer.mood();
            // Preserve the goofy, recognizable beak and silhouette. Horror
            // comes from a face that is almost still and subtly asymmetric.
            // No giant eyes, sudden scale jumps, flashing or render effects.
            if(mood==ThreatPolicy.Mood.ANGRY) {
                head.zRot=0.085f;
                head.yRot=Mth.clamp(head.yRot*0.55f,-0.21f,0.21f);
            } else if(mood==ThreatPolicy.Mood.CORRUPTED) {
                head.zRot=-0.16f;
                head.yRot=Mth.clamp(head.yRot*0.35f,-0.13f,0.13f);
                head.xRot-=0.13f;
            }
            float lift=switch(phase) {
                case APPROACH -> 0.34f;
                case RETREAT -> 0.55f;
                case OBSERVE -> 0.06f;
                case DORMANT -> 0.0f;
            };
            leftWing.zRot=sway+lift;
            rightWing.zRot=-sway-lift;
            if(mood==ThreatPolicy.Mood.ANGRY) {
                leftWing.zRot+=0.18f;
                rightWing.zRot-=0.10f;
            } else if(mood==ThreatPolicy.Mood.CORRUPTED) {
                // Uneven, very slow wing posture reads as "wrong" even on
                // bright/cartoon source textures without a jumpscare.
                leftWing.zRot+=0.41f;
                rightWing.zRot-=0.06f;
            }
            if(phase==ObserverBrain.Phase.OBSERVE) {
                // It is never perfectly still, but its tiny movement makes
                // the player question whether it just looked at them.
                head.yRot+=(float)Math.sin(ageInTicks*0.025f)*0.075f;
            }
        } else {
            leftWing.zRot=sway;
            rightWing.zRot=-sway;
        }
    }
}
