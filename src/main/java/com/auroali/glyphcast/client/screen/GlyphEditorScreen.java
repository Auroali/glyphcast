package com.auroali.glyphcast.client.screen;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.network.server.WriteParchmentMessage;
import com.auroali.glyphcast.common.registry.GCNetwork;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GlyphEditorScreen extends Screen {
    public static final ResourceLocation GLYPHS = new ResourceLocation(GlyphCast.MODID, "textures/gui/glyphs/glyph_icons.png");
    public static final ResourceLocation PARCHMENT = new ResourceLocation(GlyphCast.MODID, "textures/gui/parchment.png");
    public static final Component SAVE_LABEL = Component.translatable("gui.glyphcast.save_glyphs");
    public static final Component CLOSE_LABEL = Component.translatable("gui.cancel");

    public static void openScreen(Player player, ItemStack stack) {
        GlyphEditorScreen editor = new GlyphEditorScreen(player.getInventory().findSlotMatchingItem(stack));
        Minecraft.getInstance().setScreen(editor);
    }
    int slot;
    List<Glyph> glyphs = new ArrayList<>();
    public GlyphEditorScreen(int slot) {
        super(GameNarrator.NO_TITLE);
        this.slot = slot;
    }

    @Override
    public void init() {
        int centerX = width / 2;
        int centerY = height / 2;
        int topLeftX = centerX - 90;
        int topLeftY = centerY - 78;
        int bottomLeftY = centerY + 62;
        Button fire = new AddGlyphButton(Glyph.FIRE, topLeftX,topLeftY, (b) -> addGlyph(Glyph.FIRE));
        Button light = new AddGlyphButton(Glyph.LIGHT, topLeftX,topLeftY + 18, (b) -> addGlyph(Glyph.LIGHT));
        Button ice = new AddGlyphButton(Glyph.ICE, topLeftX,bottomLeftY - 18, (b) -> addGlyph(Glyph.ICE));
        Button earth = new AddGlyphButton(Glyph.EARTH, topLeftX,bottomLeftY, (b) -> addGlyph(Glyph.EARTH));
        Button save = new Button(centerX - 104, centerY + 92,100,20, SAVE_LABEL, (b) -> saveGlyphSequence());
        Button exit = new Button(centerX + 4, centerY + 92,100,20, CLOSE_LABEL, (b) -> Minecraft.getInstance().setScreen(null));
        addRenderableWidget(fire);
        addRenderableWidget(light);
        addRenderableWidget(ice);
        addRenderableWidget(earth);
        addRenderableWidget(save);
        addRenderableWidget(exit);
    }

    void addGlyph(Glyph glyph) {
        if(glyphs.size() >= 37)
            return;

        glyphs.add(glyph);
    }
    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, GLYPHS);
        if(glyphs.size() == 0)
            return;

        drawAllGlyphs(pPoseStack);
    }

    @Override
    public void renderBackground(PoseStack poseStack) {
        super.renderBackground(poseStack);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, PARCHMENT);
        int posX = (width - 200) / 2;
        int posY = (height - 176) / 2;
        this.blit(poseStack, posX, posY, 0, 0, 200, 230);
    }

    private void drawAllGlyphs(PoseStack pPoseStack) {
        drawBaseGlyph(pPoseStack, glyphs.get(0));

        int dist = 25;
        int maxGlyphsInRing = 6;
        int size = Math.min(glyphs.size() - 1, maxGlyphsInRing);
        int indexOffset = 0;
        for(int i = 1; i < glyphs.size(); i++) {
            renderGlyph(pPoseStack, i - indexOffset, size, dist, glyphs.get(i));
            // We've exceeded the amount of glyphs allowed in this ring, so we move on to the next
            if((i - indexOffset) % maxGlyphsInRing == 0) {
                dist += 20;
                indexOffset += maxGlyphsInRing;
                maxGlyphsInRing += 6;
                size = Math.min(Math.max(1, glyphs.size() - indexOffset), maxGlyphsInRing);
            }
        }
    }

    private void drawBaseGlyph(PoseStack pPoseStack, Glyph glyph) {
        int centerX = width / 2;
        int centerY = height / 2;
        int texOffsetX = 8;
        int texOffsetY = 8;
        this.blit(pPoseStack, centerX - texOffsetX, centerY - texOffsetY, 0, 32, 16, 16);
        drawGlyphIcon(pPoseStack, glyph, centerX - texOffsetX, centerY - texOffsetY);
    }

    void drawGlyphIcon(PoseStack stack, Glyph glyph, int x, int y) {
        this.blit(stack, x, y, 32, 16 * glyph.ordinal(), 16, 16);
    }
    void saveGlyphSequence() {
        if(glyphs.size() == 0)
            return;
        GlyphSequence sequence = new GlyphSequence(glyphs);
        GCNetwork.sendToServer(new WriteParchmentMessage(slot, sequence));
        Minecraft.getInstance().setScreen(null);
    }

    /**
     * Renders a glyph around a circle centered in the center of the screen
     * @param index the index of the glyph
     * @param distance the distance from the center of the screen
     * @param glyph the glyph ro render
     */
    void renderGlyph(PoseStack stack, int index, int size, int distance, Glyph glyph) {
        // There is only the base glyph which is at the center of the screen,
        // therefore we shouldn't be rendering any others.
        if(glyphs.size() <= 1)
            return;
        // Get the angle this glyph will be at
        double angle = (2*Math.PI) * ((double) index / (double) size);
        // Convert the angle to screen space coordinates
        int x = (int) (distance * Math.cos(angle - Math.PI / 3));
        int y = (int) (distance * Math.sin(angle - Math.PI / 3));

        int centerX = width / 2;
        int centerY = height / 2;
        int texOffsetX = 8;
        int texOffsetY = 8;
        this.blit(stack, centerX + x - texOffsetX, centerY + y - texOffsetY, 0, 32, 16, 16);
        drawGlyphIcon(stack, glyph, centerX + x - texOffsetX, centerY + y - texOffsetY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}
