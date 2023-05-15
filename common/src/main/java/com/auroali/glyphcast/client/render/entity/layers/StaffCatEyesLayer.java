package com.auroali.glyphcast.client.render.entity.layers;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.client.render.entity.StaffEntityRenderer;
import com.auroali.glyphcast.common.entities.StaffEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class StaffCatEyesLayer extends RenderLayer<StaffEntity, EntityModel<StaffEntity>> {
    private static final RenderType EYES = RenderType.eyes(new ResourceLocation(Glyphcast.MODID, "textures/entity/staff/cat_eyes.png"));
    private final StaffEntityRenderer parent;

    public StaffCatEyesLayer(RenderLayerParent<StaffEntity, EntityModel<StaffEntity>> pRenderer) {
        super(pRenderer);
        this.parent = (StaffEntityRenderer) pRenderer;
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, StaffEntity pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        VertexConsumer consumer = pBuffer.getBuffer(EYES);
        parent.getCurrentModel(pLivingEntity).renderToBuffer(pPoseStack, consumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
