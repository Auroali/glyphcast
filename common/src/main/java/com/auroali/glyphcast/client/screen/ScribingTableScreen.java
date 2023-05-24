package com.auroali.glyphcast.client.screen;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.client.render.GlyphRenderer;
import com.auroali.glyphcast.common.menu.CarvingMenu;
import com.auroali.glyphcast.common.menu.ScribingMenu;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class ScribingTableScreen extends AbstractContainerScreen<ScribingMenu> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Glyphcast.MODID, "textures/gui/container/scribing_menu.png");

    public ScribingTableScreen(ScribingMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        titleLabelY = titleLabelY - 1;
        titleLabelX = titleLabelX - 1;
        this.imageWidth = 218;
        this.imageHeight = 196;
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        int centerX = width / 2;
        int centerY = (height - 22) / 2;
        pPoseStack.pushPose();
        float scaleFactor = 0.75f;
        pPoseStack.scale(scaleFactor, scaleFactor, scaleFactor);
        GlyphRenderer.drawAllGlyphs(pPoseStack, (int) (centerX / scaleFactor), (int) (centerY / scaleFactor), List.of(List.of(Glyph.FIRE), List.of(Glyph.EARTH, Glyph.ICE), List.of(Glyph.LIGHT, Glyph.WAND)));
        pPoseStack.popPose();
        this.renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        this.blit(pPoseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}
