package com.auroali.glyphcast.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;

public class GlyphTooltipComponent implements ClientTooltipComponent {
    @Override
    public int getHeight() {
        return 32;
    }

    @Override
    public int getWidth(Font pFont) {
        return 32;
    }

    @Override
    public void renderText(Font pFont, int pX, int pY, Matrix4f pMatrix4f, MultiBufferSource.BufferSource pBufferSource) {
        ClientTooltipComponent.super.renderText(pFont, pX, pY, pMatrix4f, pBufferSource);
    }

    @Override
    public void renderImage(Font pFont, int pMouseX, int pMouseY, PoseStack pPoseStack, ItemRenderer pItemRenderer, int pBlitOffset) {
        ClientTooltipComponent.super.renderImage(pFont, pMouseX, pMouseY, pPoseStack, pItemRenderer, pBlitOffset);
    }
}
