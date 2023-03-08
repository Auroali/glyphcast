package com.auroali.glyphcast.client.render;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class GlyphClientTooltipComponent implements ClientTooltipComponent {
    final List<Glyph> glyphs;

    public static final ResourceLocation BACKGROUND = new ResourceLocation(GlyphCast.MODID, "textures/gui/tooltip/glyph_tooltip_bg.png");
    public GlyphClientTooltipComponent(GlyphSequence sequence) {
        this.glyphs = sequence.asList();
    }

    @Override
    public int getHeight() {
        return 100;
    }

    @Override
    public int getWidth(Font pFont) {
        return 100;
    }

    @Override
    public void renderText(Font pFont, int pX, int pY, Matrix4f pMatrix4f, MultiBufferSource.BufferSource pBufferSource) {
        ClientTooltipComponent.super.renderText(pFont, pX, pY, pMatrix4f, pBufferSource);
    }

    @Override
    public void renderImage(Font pFont, int pMouseX, int pMouseY, PoseStack pPoseStack, ItemRenderer pItemRenderer, int pBlitOffset) {
        RenderSystem.setShaderTexture(0, BACKGROUND);
        pPoseStack.pushPose();
        pPoseStack.scale(0.5f,0.5f,0.5f);
        pMouseX *= 2;
        pMouseY *= 2;
        GuiComponent.blit(pPoseStack, pMouseX, pMouseY, pBlitOffset, 0, 0, 200, 200, 256, 256);
        GlyphRenderer.drawAllGlyphs(pPoseStack, pMouseX + 100, pMouseY + 100, glyphs);
        pPoseStack.popPose();
    }
}
