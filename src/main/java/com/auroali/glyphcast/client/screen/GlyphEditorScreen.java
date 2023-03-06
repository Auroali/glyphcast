package com.auroali.glyphcast.client.screen;

import com.auroali.glyphcast.common.network.server.WriteParchmentMessage;
import com.auroali.glyphcast.common.registry.GCNetwork;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class GlyphEditorScreen extends Screen {
    public static final Component SAVE_LABEL = Component.translatable("gui.glyphcast.save_glyphs");
    int slot;
    List<Glyph> glyphs = new ArrayList<>();
    public GlyphEditorScreen(int slot) {
        super(GameNarrator.NO_TITLE);
        this.slot = slot;
    }

    @Override
    public void init() {
        Button fire = new Button(0,0,80,20, Glyph.FIRE.component(), (b) -> glyphs.add(Glyph.FIRE));
        Button light = new Button(0,20,80,20, Glyph.LIGHT.component(), (b) -> glyphs.add(Glyph.LIGHT));
        Button ice = new Button(0,40,80,20, Glyph.ICE.component(), (b) -> glyphs.add(Glyph.ICE));
        Button earth = new Button(0,60,80,20, Glyph.EARTH.component(), (b) -> glyphs.add(Glyph.EARTH));
        Button save = new Button(0,80,80,20, SAVE_LABEL, (b) -> saveGlyphSequence());
        addRenderableWidget(fire);
        addRenderableWidget(light);
        addRenderableWidget(ice);
        addRenderableWidget(earth);
        addRenderableWidget(save);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {

        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    void saveGlyphSequence() {
        if(glyphs.size() == 0)
            return;
        GlyphSequence sequence = new GlyphSequence(glyphs);
        GCNetwork.sendToServer(new WriteParchmentMessage(slot, sequence));
        Minecraft.getInstance().setScreen(null);
    }
    @Override
    public boolean isPauseScreen() {
        return false;
    }

}
