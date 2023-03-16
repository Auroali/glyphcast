package com.auroali.glyphcast.client.screen;

import com.auroali.glyphcast.client.GCKeybinds;
import com.auroali.glyphcast.client.render.GlyphRenderer;
import com.auroali.glyphcast.common.capabilities.ISpellUser;
import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.spells.SpellSlot;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.Ring;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.util.LazyOptional;

import java.util.List;

public class SpellWheelScreen extends Screen {
    final ISpellUser user;

    public static void openScreen() {
        SpellWheelScreen screen = new SpellWheelScreen();
        Minecraft.getInstance().setScreen(screen);
    }

    protected SpellWheelScreen() {
        super(GameNarrator.NO_TITLE);
        this.user = SpellUser.get(Minecraft.getInstance().player).orElse(new SpellUser(Minecraft.getInstance().player));
    }

    @Override
    protected void init() {

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        if(GCKeybinds.SPELL_SELECTION.matches(pKeyCode, pScanCode))
            onClose();
        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        int centerH = width / 2;
        int centerV = height / 2;
        for(int i = 0; i < user.getSlots().size(); i++)
            renderSlot(pPoseStack, centerH, centerV, i, 40);

        System.out.println(Math.toDegrees(Math.atan2(pMouseY - centerH, pMouseX - centerV)));
    }

    public void renderSlot(PoseStack stack, int offsetH, int offsetV, int index, int dist) {
        SpellSlot slot = user.getSlots().get(index);
        if(slot.isEmpty())
            return;

        double angle = 2*Math.PI * ((double)index / 9);

        int posX = (int) (dist * Math.sin(angle));
        int posY = (int) (dist * Math.cos(angle));

        stack.pushPose();
        stack.scale(0.25f, 0.25f, 0.25f);
        posX *= 4;
        posY *= 4;
        offsetH *= 4;
        offsetV *= 4;
        List<List<Glyph>> glyphs = slot.getSpell().getSequence().getRings().stream().map(Ring::asList).toList();
        GlyphRenderer.drawAllGlyphs(stack, offsetH + posX, offsetV + posY, glyphs);
        stack.popPose();
    }

}
