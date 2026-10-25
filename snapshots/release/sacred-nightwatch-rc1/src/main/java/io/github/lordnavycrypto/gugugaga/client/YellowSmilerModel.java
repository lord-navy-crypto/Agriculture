package io.github.lordnavycrypto.gugugaga.client;

import io.github.lordnavycrypto.gugugaga.entity.YellowSmilerEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * Original bright but unnervingly symmetrical mascot; never any copied
 * Verity texture/mesh. His stillness is intentional.
 */
public final class YellowSmilerModel extends HierarchicalModel<YellowSmilerEntity> {
    private final ModelPart root,head,leftArm,rightArm;

    public YellowSmilerModel(ModelPart root){
        this.root=root;
        this.head=root.getChild("head");
        this.leftArm=root.getChild("left_arm");
        this.rightArm=root.getChild("right_arm");
    }

    public static LayerDefinition createBodyLayer(){
        MeshDefinition mesh=new MeshDefinition();
        PartDefinition root=mesh.getRoot();
        root.addOrReplaceChild("body",CubeListBuilder.create()
                .texOffs(32,0).addBox(-4,-12,-2,8,12,4),
                PartPose.offset(0,22,0));
        root.addOrReplaceChild("head",CubeListBuilder.create()
                .texOffs(0,0).addBox(-5,-10,-5,10,10,10),
                PartPose.offset(0,10,0));
        root.addOrReplaceChild("left_arm",CubeListBuilder.create()
                .texOffs(32,20).addBox(0,-2,-2,3,12,4),
                PartPose.offset(4,12,0));
        root.addOrReplaceChild("right_arm",CubeListBuilder.create()
                .texOffs(32,20).addBox(-3,-2,-2,3,12,4),
                PartPose.offset(-4,12,0));
        root.addOrReplaceChild("left_leg",CubeListBuilder.create()
                .texOffs(0,32).addBox(-2,0,-2,4,10,4),
                PartPose.offset(2,22,0));
        root.addOrReplaceChild("right_leg",CubeListBuilder.create()
                .texOffs(0,32).addBox(-2,0,-2,4,10,4),
                PartPose.offset(-2,22,0));
        return LayerDefinition.create(mesh,64,64);
    }

    @Override public ModelPart root(){return root;}

    @Override public void setupAnim(YellowSmilerEntity entity,float limbSwing,
            float limbSwingAmount,float ageInTicks,float netHeadYaw,float headPitch){
        root.getAllParts().forEach(ModelPart::resetPose);
        // Too slow and too perfect: the face does not stop smiling even
        // as its body takes notice of the Lost Watcher.
        head.yRot=Mth.clamp(netHeadYaw*0.006f,-0.22f,0.22f);
        leftArm.xRot=(float)Math.sin(ageInTicks*0.020f)*0.018f;
        rightArm.xRot=-leftArm.xRot;
        if(entity.revealed()) {
            // Unmasked: still recognizably Yellow, but the bilateral
            // smile's symmetry is broken only through posture, not flash.
            head.zRot=-0.19f;
            head.xRot=-0.17f;
            head.yRot=Mth.clamp(netHeadYaw*0.004f,-0.11f,0.11f);
            leftArm.xRot=-0.27f;
            rightArm.xRot=-0.08f;
        }
    }
}
