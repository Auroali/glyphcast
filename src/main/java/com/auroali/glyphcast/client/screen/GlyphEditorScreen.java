package com.auroali.glyphcast.client.screen;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.client.render.GlyphRenderer;
import com.auroali.glyphcast.client.screen.widgets.AddGlyphButton;
import com.auroali.glyphcast.client.screen.widgets.TextureButton;
import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.network.server.WriteParchmentMessage;
import com.auroali.glyphcast.common.registry.GCNetwork;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.spells.glyph.Ring;
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
    public static final ResourceLocation PARCHMENT = new ResourceLocation(GlyphCast.MODID, "textures/gui/parchment.png");
    public static final Component SAVE_LABEL = Component.translatable("gui.glyphcast.save_glyphs");
    public static final Component CLOSE_LABEL = Component.translatable("gui.cancel");

    public static void openScreen(Player player, ItemStack stack) {
        GlyphEditorScreen editor = new GlyphEditorScreen(player.getInventory().findSlotMatchingItem(stack));
        Minecraft.getInstance().setScreen(editor);
    }

    final int slot;
    final List<List<Glyph>> glyphs = new ArrayList<>();
    public GlyphEditorScreen(int slot) {
        super(GameNarrator.NO_TITLE);
        this.slot = slot;
        this.glyphs.add(new ArrayList<>());
    }

    @Override
    public void init() {
        int centerX = width / 2;
        int centerY = height / 2;
        int topLeftX = centerX - 90;
        int topLeftY = centerY - 78;
        int bottomRightX = centerX + 74;
        int bottomLeftY = centerY + 62;

        SpellUser.get(Minecraft.getInstance().player).ifPresent(user -> {
            Button fire = new AddGlyphButton(Glyph.FIRE, user, topLeftX, topLeftY, (b) -> addGlyph(Glyph.FIRE));
            Button light = new AddGlyphButton(Glyph.LIGHT, user, topLeftX, topLeftY + 18, (b) -> addGlyph(Glyph.LIGHT));
            Button ice = new AddGlyphButton(Glyph.ICE, user, topLeftX, bottomLeftY - 18, (b) -> addGlyph(Glyph.ICE));
            Button earth = new AddGlyphButton(Glyph.EARTH, user, topLeftX, bottomLeftY, (b) -> addGlyph(Glyph.EARTH));
            addRenderableWidget(fire);
            addRenderableWidget(light);
            addRenderableWidget(ice);
            addRenderableWidget(earth);
        });

        Button addRing = new TextureButton(GlyphRenderer.GLYPHS, bottomRightX, bottomLeftY, 16, 16, 0, 48, (b) -> addRing());
        Button save = new Button(centerX - 104, centerY + 92, 100, 20, SAVE_LABEL, (b) -> saveGlyphSequence());
        Button exit = new Button(centerX + 4, centerY + 92, 100, 20, CLOSE_LABEL, (b) -> Minecraft.getInstance().setScreen(null));
        addRenderableWidget(save);
        addRenderableWidget(exit);
        addRenderableWidget(addRing);
    }

    void addGlyph(Glyph glyph) {
        if(glyphs.size() == 0 || glyphs.get(glyphs.size() - 1).size() >= getMaxPerRing())
            return;

        glyphs.get(glyphs.size() - 1).add(glyph);
    }

    void addRing() {
        if(glyphs.size() >= 3)
            return;

        glyphs.add(new ArrayList<>());
    }

    int getMaxPerRing() {
        return switch (glyphs.size()) {
            case 1 -> 1;
            case 2 -> 6;
            case 3 -> 11;
            default -> 0;
        };
    }
    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        GlyphRenderer.drawAllGlyphs(pPoseStack, width / 2, height / 2, glyphs);
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

    void saveGlyphSequence() {
        if(glyphs.size() == 0)
            return;
        List<Ring> rings = new ArrayList<>();
        glyphs.forEach(r -> rings.add(Ring.of(r)));
        GlyphSequence sequence = new GlyphSequence(rings);
        GCNetwork.sendToServer(new WriteParchmentMessage(slot, sequence));
        onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}
