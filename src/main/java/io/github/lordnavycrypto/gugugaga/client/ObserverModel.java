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

public final class ObserverModel<T extends Entity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart leftWing;
    private final ModelPart rightWing;

    public ObserverModel(ModelPart root) {
        this.root = root;
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
        root.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 33).addBox(-5, -7, -4, 10, 8, 8),
                PartPose.offset(0, 9, 0));
        root.addOrReplaceChild("left_eye", CubeListBuilder.create()
                .texOffs(40, 0).addBox(1.5f, -4.3f, -4.6f, 2, 2, 1),
                PartPose.offset(0, 9, 0));
        root.addOrReplaceChild("right_eye", CubeListBuilder.create()
                .texOffs(40, 0).addBox(-3.5f, -4.3f, -4.6f, 2, 2, 1),
                PartPose.offset(0, 9, 0));
        root.addOrReplaceChild("beak", CubeListBuilder.create()
                .texOffs(40, 16).addBox(-2, -2.0f, -6.5f, 4, 2, 3),
                PartPose.offset(0, 9, 0));
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
        float sway = (float) Math.sin(limbSwing * 0.66f) * Math.min(limbSwingAmount, 1.0f) * 0.25f;
        leftWing.zRot = sway;
        rightWing.zRot = -sway;
    }
}
