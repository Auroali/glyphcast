package com.auroali.glyphcast.client.screen.widgets;

import com.auroali.glyphcast.client.render.GlyphRenderer;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;

public class AddGlyphButton extends Button {
    final Glyph glyph;
    public AddGlyphButton(Glyph glyph, int pX, int pY, OnPress pOnPress) {
        super(pX, pY, 16, 16, glyph.component(), pOnPress);
        this.glyph = glyph;
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, GlyphRenderer.GLYPHS);
        this.blit(pPoseStack, x, y, isMouseOver(pMouseX, pMouseY) ? 16 : 0, 32, 16, 16);
        this.blit(pPoseStack, x, y, 32, 16 * glyph.ordinal(), 16, 16);
    }
}
