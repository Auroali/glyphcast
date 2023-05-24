//package com.auroali.glyphcast.client.model.entity;// Made with Blockbench 4.6.5
//// Exported for Minecraft version 1.17 or later with Mojang mappings
//// Paste this class into your mod and generate all required imports
//
//
//import com.auroali.glyphcast.Glyphcast;
//import com.auroali.glyphcast.common.entities.StaffEntity;
//import com.mojang.blaze3d.vertex.PoseStack;
//import com.mojang.blaze3d.vertex.VertexConsumer;
//import net.minecraft.client.model.EntityModel;
//import net.minecraft.client.model.geom.ModelLayerLocation;
//import net.minecraft.client.model.geom.ModelPart;
//import net.minecraft.client.model.geom.PartPose;
//import net.minecraft.client.model.geom.builders.*;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.util.Mth;
//
//public class StaffCatModel extends EntityModel<StaffEntity> {
//    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
//    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Glyphcast.MODID, "staffcatmodel"), "main");
//    private final ModelPart head;
//    private final ModelPart front_left_leg;
//    private final ModelPart front_right_leg;
//    private final ModelPart back_left_leg;
//    private final ModelPart back_right_leg;
//    private final ModelPart body;
//    private final ModelPart tail;
//
//    public StaffCatModel(ModelPart root) {
//        this.head = root.getChild("head");
//        this.front_left_leg = root.getChild("front_left_leg");
//        this.front_right_leg = root.getChild("front_right_leg");
//        this.back_left_leg = root.getChild("back_left_leg");
//        this.back_right_leg = root.getChild("back_right_leg");
//        this.body = root.getChild("body");
//        this.tail = root.getChild("tail");
//    }
//
//    public static LayerDefinition createBodyLayer() {
//        MeshDefinition meshdefinition = new MeshDefinition();
//        PartDefinition partdefinition = meshdefinition.getRoot();
//
//        PartDefinition back_left_leg = partdefinition.addOrReplaceChild("back_left_leg", CubeListBuilder.create().texOffs(0, 25).addBox(-1.0F, 2.0F, -0.25F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
//                .texOffs(8, 25).addBox(-1.0F, -2.0F, -2.25F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 19.0F, 2.5F));
//
//        PartDefinition back_right_leg = partdefinition.addOrReplaceChild("back_right_leg", CubeListBuilder.create().texOffs(0, 25).addBox(-1.0F, 2.0F, -0.25F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
//                .texOffs(8, 25).addBox(-1.0F, -2.0F, -2.25F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 19.0F, 2.5F));
//
//        PartDefinition front_left_leg = partdefinition.addOrReplaceChild("front_left_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.5F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.4F, 18.5F, -3.0F));
//
//        PartDefinition front_right_leg = partdefinition.addOrReplaceChild("front_right_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.5F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.4F, 18.5F, -3.0F));
//
//        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(1, 0).addBox(-2.0F, -7.75F, -5.25F, 4.0F, 5.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
//
//        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(15, 16).addBox(1.5F, -4.25F, -1.25F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
//                .texOffs(15, 16).addBox(-2.5F, -4.25F, -1.25F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
//                .texOffs(0, 16).addBox(-2.5F, -3.25F, -4.25F, 5.0F, 4.0F, 5.0F, new CubeDeformation(0.0F))
//                .texOffs(19, 7).addBox(-1.5F, -1.25F, -5.25F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.5F, -4.5F));
//
//        PartDefinition tail = partdefinition.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0F, 17.0F, 4.5F));
//
//        PartDefinition cube_r1 = tail.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(21, 2).addBox(-0.5F, 1.042F, 3.3495F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.25F, 0.0F, 0.9599F, 0.0F, 0.0F));
//
//        PartDefinition cube_r2 = tail.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(21, 2).addBox(-0.5F, -0.558F, -0.1005F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.25F, 0.0F, 0.5236F, 0.0F, 0.0F));
//
//        return LayerDefinition.create(meshdefinition, 64, 64);
//    }
//
//    @Override
//    public void prepareMobModel(StaffEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick) {
//        back_right_leg.y = 19.0F;
//        back_left_leg.y = 19.0F;
//        back_right_leg.z = 2.5F;
//        back_left_leg.z = 2.5F;
//
//        front_left_leg.z = -3.0F;
//        front_right_leg.z = -3.0F;
//
//        back_left_leg.xRot = 0.0f;
//        back_right_leg.xRot = 0;
//        front_right_leg.xRot = 0;
//        front_left_leg.xRot = 0;
//        back_left_leg.yRot = 0;
//        back_right_leg.yRot = 0;
//
//        body.y = 24.0F;
//        body.z = 0.0F;
//
//        head.z = -4.5f;
//        head.y = 16.5F;
//        body.xRot = 0.0f;
//
//        tail.y = 17.0F;
//        tail.z = 4.5f;
//    }
//
//    @Override
//    public void setupAnim(StaffEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
//        head.xRot = (float) Math.toRadians(headPitch);
//        head.yRot = (float) Math.toRadians(netHeadYaw);
//
//        if (Math.abs(entity.getDeltaMovement().length()) < 0.01) {
//            setupSitting(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
//            return;
//        }
//
//        back_right_leg.xRot = Mth.cos(limbSwing * 0.64323f) * 1.4f * limbSwingAmount;
//        back_left_leg.xRot = Mth.cos(limbSwing * 0.64323f + (float) Math.PI) * 1.4f * limbSwingAmount;
//        front_left_leg.xRot = Mth.cos(limbSwing * 0.64323f + (float) Math.PI) * 1.4f * limbSwingAmount;
//        front_right_leg.xRot = Mth.cos(limbSwing * 0.64323f) * 1.4f * limbSwingAmount;
//    }
//
//    public void setupSitting(StaffEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
//        body.xRot = -1.134464014F;
//
//        back_left_leg.xRot = (float) (-Math.PI / 2.0);
//        back_right_leg.xRot = (float) (-Math.PI / 2.0);
//        back_left_leg.yRot = (float) (12 * -Math.PI / 180.0);
//        back_right_leg.yRot = (float) (12 * Math.PI / 180.0);
//        back_right_leg.y = 23.0f;
//        back_right_leg.z = 2.5f;
//        back_left_leg.z = 2.5f;
//        back_left_leg.y = 23.0f;
//
//        front_left_leg.z = -2.0F;
//        front_right_leg.z = -2.0F;
//
//        body.z = -3.5f;
//        body.y = 21.5f;
//
//        head.z = -0.7f;
//        head.y = 14.5f;
//
//        tail.y = 22.0F;
//        tail.z = 5;
//    }
//
//    @Override
//    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
//        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
//        front_left_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
//        front_right_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
//        back_left_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
//        back_right_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
//        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
//        tail.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
//    }
//}