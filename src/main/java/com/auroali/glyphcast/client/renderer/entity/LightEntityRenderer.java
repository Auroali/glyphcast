package com.auroali.glyphcast.client.renderer.entity;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.client.GCRenderTypes;
import com.auroali.glyphcast.client.model.FloatingLightModel;
import com.auroali.glyphcast.common.entities.FireEntity;
import com.auroali.glyphcast.common.entities.LightEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class LightEntityRenderer extends EntityRenderer<LightEntity> {

    FloatingLightModel model;
    ResourceLocation TEXTURE = new ResourceLocation(GlyphCast.MODID, "textures/model/floating_light.png");
    public LightEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.shadowStrength = 0.0f;
        this.shadowRadius = 0.0f;
        pContext.bakeLayer(FloatingLightModel.LAYER_LOCATION);
        model = new FloatingLightModel(pContext.bakeLayer(FloatingLightModel.LAYER_LOCATION));
    }

    @Override
    public void render(LightEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.0f, -0.85f, 0.f);

        model.renderToBuffer(pPoseStack, pBuffer.getBuffer(model.renderType(getTextureLocation(pEntity))), 15728640, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        model.renderToBuffer(pPoseStack, pBuffer.getBuffer(GCRenderTypes.shadeless(getTextureLocation(pEntity))), 15728640, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);

        pPoseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(LightEntity pEntity) {
        return TEXTURE;
    }
}
