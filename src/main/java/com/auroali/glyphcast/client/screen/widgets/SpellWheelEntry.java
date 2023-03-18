package com.auroali.glyphcast.client.screen.widgets;

import com.auroali.glyphcast.client.render.GlyphRenderer;
import com.auroali.glyphcast.client.screen.SpellWheelScreen;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;

public class SpellWheelEntry extends GuiComponent implements Widget, GuiEventListener, NarratableEntry {

    public final int centerX;
    public final int centerY;
    public final int index;
    public final Spell spell;
    private final Glyph glyph;

    public final int posX;
    public final int posY;


    private final SpellWheelScreen screen;

    public SpellWheelEntry(SpellWheelScreen screen, int centerX, int centerY, int index, Spell spell) {
        this.screen = screen;
        this.centerX = centerX;
        this.centerY = centerY;
        this.index = index;
        this.spell = spell;

        double angle = 2*Math.PI * ((double)this.index / 9);

        this.posX = (int) (60 * Math.cos(angle));
        this.posY = (int) (60 * Math.sin(angle));

        if(spell == null || spell.getSequence().equals(GlyphSequence.EMPTY))
            this.glyph = null;
        else
            this.glyph = spell.getSequence().asList().stream().findFirst().orElse(null);
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
        if(spell != null)
            pNarrationElementOutput.add(NarratedElementType.HINT, spell.getName());
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.setShaderTexture(0, GlyphRenderer.GLYPHS);
        if(isMouseOver(pMouseX, pMouseY))
            screen.selectedEntry = this;

        if(spell == null || glyph == null)
            return;

        GlyphRenderer.drawBaseGlyph(pPoseStack, centerX + posX, centerY + posY, glyph);
    }


    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        double mouseAngle = Math.atan2(pMouseY - centerY, pMouseX - centerX);
        mouseAngle = mouseAngle < 0 ? mouseAngle + 2*Math.PI : mouseAngle;
        // TODO: Fix it not working when the angle wraps back around to 0
        double angle = 2*Math.PI * ((double)this.index / 9);
        double upper_bound = angle + 0.34906585039;
        double lower_bound = angle - 0.34906585039;

        return lower_bound < mouseAngle && mouseAngle < upper_bound;
    }

    @Override
    public NarrationPriority narrationPriority() {
        return spell != null ? NarrationPriority.HOVERED : NarrationPriority.NONE;
    }
}
