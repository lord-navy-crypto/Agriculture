package io.github.lordnavycrypto.gugugaga.client;

import io.github.lordnavycrypto.gugugaga.entity.RedactorEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * Original dual-form block model. One logical entity, with synchronized
 * mask state. Lapis looks like a small harmless smiling mineral block;
 * true Redactor is a tall fractured archivist silhouette.
 */
public final class RedactorModel extends HierarchicalModel<RedactorEntity> {
    private final ModelPart root;
    private final ModelPart lapis;
    private final ModelPart revealed;
    private final ModelPart mask;
    private final ModelPart leftArm;
    private final ModelPart rightArm;

    public RedactorModel(ModelPart root) {
        this.root=root;
        this.lapis=root.getChild("lapis");
        this.revealed=root.getChild("revealed");
        this.mask=revealed.getChild("mask");
        this.leftArm=revealed.getChild("left_arm");
        this.rightArm=revealed.getChild("right_arm");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh=new MeshDefinition();
        PartDefinition root=mesh.getRoot();
        PartDefinition lapis=root.addOrReplaceChild("lapis",CubeListBuilder.create()
                .texOffs(0,0).addBox(-5,-11,-5,10,10,10),
                PartPose.offset(0,23,0));
        lapis.addOrReplaceChild("lapis_face",CubeListBuilder.create()
                .texOffs(0,32).addBox(-4,-9,-5.25f,8,7,0.5f),
                PartPose.ZERO);
        PartDefinition revealed=root.addOrReplaceChild("revealed",CubeListBuilder.create()
                .texOffs(32,0).addBox(-3,-17,-2,6,17,4),
                PartPose.offset(0,24,0));
        revealed.addOrReplaceChild("mask",CubeListBuilder.create()
                .texOffs(0,0).addBox(-4,-7,-3,8,8,6),
                PartPose.offset(0,-17,0));
        revealed.addOrReplaceChild("left_arm",CubeListBuilder.create()
                .texOffs(32,22).addBox(-1,-2,-1.5f,2,15,3),
                PartPose.offset(4,-14,0));
        revealed.addOrReplaceChild("right_arm",CubeListBuilder.create()
                .texOffs(32,22).addBox(-1,-2,-1.5f,2,15,3),
                PartPose.offset(-4,-14,0));
        return LayerDefinition.create(mesh,64,64);
    }

    @Override
    public ModelPart root(){return root;}

    @Override
    public void setupAnim(RedactorEntity entity,float limbSwing,
            float limbSwingAmount,float ageInTicks,float netHeadYaw,float headPitch){
        root.getAllParts().forEach(ModelPart::resetPose);
        boolean trueForm=entity.revealed();
        lapis.visible=!trueForm;
        revealed.visible=trueForm;
        if(trueForm){
            mask.yRot=Mth.clamp(netHeadYaw*0.006f,-0.18f,0.18f);
            mask.zRot=-0.09f;
            leftArm.xRot=(float)Math.sin(ageInTicks*0.032f)*0.08f;
            rightArm.xRot=-(float)Math.sin(ageInTicks*0.024f)*0.05f;
        }else {
            lapis.yRot=(float)Math.sin(ageInTicks*0.021f)*0.045f;
        }
    }
}
