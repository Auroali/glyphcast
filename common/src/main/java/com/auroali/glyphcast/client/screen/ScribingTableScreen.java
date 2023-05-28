package com.auroali.glyphcast.client.screen;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.client.render.GlyphRenderer;
import com.auroali.glyphcast.client.screen.widgets.AddGlyphButton;
import com.auroali.glyphcast.client.screen.widgets.TextureButton;
import com.auroali.glyphcast.common.capabilities.ISpellUser;
import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.menu.ScribingMenu;
import com.auroali.glyphcast.common.network.GCNetwork;
import com.auroali.glyphcast.common.network.server.menu.ScribingEraseButtonPress;
import com.auroali.glyphcast.common.network.server.menu.ScribingGlyphButtonPress;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ScribingTableScreen extends AbstractContainerScreen<ScribingMenu> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Glyphcast.MODID, "textures/gui/container/scribing_menu.png");
    private List<List<Glyph>> glyphs;
    public ScribingTableScreen(ScribingMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        titleLabelY = titleLabelY - 1;
        titleLabelX = titleLabelX - 1;

        inventoryLabelX = 28;
        inventoryLabelY = 162;
        this.imageWidth = 218;
        this.imageHeight = 196;

        this.glyphs = Collections.emptyList();
    }

    @Override
    protected void init() {
        super.init();
        int rightPos = (this.width + this.imageWidth) / 2;
        int bottomPos = (this.height + this.imageHeight) / 2;
        SpellUser.get(Minecraft.getInstance().player).ifPresent(user -> {
            this.addRenderableWidget(new AddGlyphButton(
                    Glyph.FIRE,
                    user,
                    leftPos + 38,
                    topPos + 18,
                    button -> GCNetwork.CHANNEL.sendToServer(new ScribingGlyphButtonPress(Glyph.FIRE))
            ));
            this.addRenderableWidget(new AddGlyphButton(
                    Glyph.LIGHT,
                    user,
                    leftPos + 38,
                    topPos + 38,
                    button -> GCNetwork.CHANNEL.sendToServer(new ScribingGlyphButtonPress(Glyph.LIGHT))
            ));


            this.addRenderableWidget(new AddGlyphButton(
                    Glyph.ICE,
                    user,
                    leftPos + 38,
                    bottomPos - 78,
                    button -> GCNetwork.CHANNEL.sendToServer(new ScribingGlyphButtonPress(Glyph.ICE))
            ));
            this.addRenderableWidget(new AddGlyphButton(
                    Glyph.EARTH,
                    user,
                    leftPos + 38,
                    bottomPos - 58,
                    button -> GCNetwork.CHANNEL.sendToServer(new ScribingGlyphButtonPress(Glyph.EARTH))
            ));

            if(hasMoreThanOneGlyph(user))
            this.addRenderableWidget(new TextureButton(
                    GlyphRenderer.GLYPHS,
                    rightPos - 54,
                    bottomPos - 78,
                    16,
                    16,
                    0,
                    48,
                    b -> GCNetwork.CHANNEL.sendToServer(new ScribingGlyphButtonPress(Glyph.WAND))
            ));

        });
        this.addRenderableWidget(new TextureButton(
                GlyphRenderer.GLYPHS,
                rightPos - 54,
                bottomPos - 58,
                16,
                16,
                80,
                0,
                b -> GCNetwork.CHANNEL.sendToServer(new ScribingEraseButtonPress())
        ));
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
        GlyphRenderer.drawAllGlyphs(pPoseStack, (int) (centerX / scaleFactor), (int) (centerY / scaleFactor), glyphs);
        pPoseStack.popPose();
        this.renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        this.blit(pPoseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    public void setGlyphs(List<List<Glyph>> glyphs) {
        this.glyphs = glyphs;
    }

    boolean hasMoreThanOneGlyph(ISpellUser user) {
        return Arrays.stream(Glyph.values()).filter(user::hasDiscoveredGlyph).count() > 2;
    }
}
