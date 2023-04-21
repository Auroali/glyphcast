package com.auroali.glyphcast.client.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class TextureButton extends Button {
    final ResourceLocation texture;
    final int uOffset;
    final int vOffset;

    public TextureButton(ResourceLocation texture, int pX, int pY, int width, int height, int uOffset, int vOffset, OnPress pOnPress) {
        super(pX, pY, width, height, GameNarrator.NO_TITLE, pOnPress);
        this.texture = texture;
        this.uOffset = uOffset;
        this.vOffset = vOffset;
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (!this.visible)
            return;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, texture);
        this.blit(pPoseStack, x, y, isMouseOver(pMouseX, pMouseY) ? 16 : 0, 32, 16, 16);
        this.blit(pPoseStack, x, y, uOffset, vOffset, width, height);
    }
}
