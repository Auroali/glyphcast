package com.auroali.glyphcast.client.renderer.entity;

import com.auroali.glyphcast.common.entities.FireEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class FireEntityRenderer extends EntityRenderer<FireEntity> {
    public FireEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.shadowStrength = 0.0f;
        this.shadowRadius = 0.0f;
    }

    @Override
    public void render(FireEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
    }

    @Override
    public ResourceLocation getTextureLocation(FireEntity pEntity) {
        return null;
    }
}
