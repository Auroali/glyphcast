package com.auroali.glyphcast.client.screen.widgets;

import com.auroali.glyphcast.client.screen.SpellSelectionScreen;
import com.auroali.glyphcast.common.spells.Spell;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;

import java.util.List;

public class SpellListWidget extends ObjectSelectionList<SpellListWidget.SpellListEntry> {

    final int width;
    final SpellSelectionScreen screen;
    public SpellListWidget(SpellSelectionScreen screen, int pWidth, int pHeight, int pY0, int pY1, int pItemHeight) {
        super(screen.getMinecraft(), pWidth, pHeight, pY0, pY1, pItemHeight);
        this.width = pWidth;
        this.screen = screen;
        this.setRenderBackground(false);
        this.setRenderHeader(false, 0);
        this.setRenderTopAndBottom(false);
    }

    @Override
    protected void renderBackground(PoseStack pPoseStack) {
        super.renderBackground(pPoseStack);
        GuiComponent.fill(pPoseStack, getLeft(), getTop(), getLeft() + width, getBottom(), -14540254);
        vLine(pPoseStack, width, getTop(), getBottom(), -1);
    }

    @Override
    public int getRowWidth() {
        return width;
    }

    @Override
    protected int getScrollbarPosition()
    {
        return this.width - 6;
    }

    public SpellListEntry getHoveredEntry() {
        return getHovered();
    }

    public void setSpells(List<Spell> spells) {
        clearEntries();
        spells.forEach(spell -> addEntry(new SpellListEntry(this, spell)));
    }
    public static class SpellListEntry extends ObjectSelectionList.Entry<SpellListEntry> {

        public final Spell spell;
        final Font font;
        final SpellListWidget widget;
        public SpellListEntry(SpellListWidget widget, Spell spell) {
            this.widget = widget;
            this.spell = spell;
            this.font = Minecraft.getInstance().font;
        }

        @Override
        public Component getNarration() {
            return spell.getName();
        }

        @Override
        public void render(PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            Minecraft.getInstance().font.draw(pPoseStack, spell.getName(), pLeft + 18, pTop + 5, -1);
        }

        @Override
        public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_)
        {
            widget.setSelected(this);
            return false;
        }
    }
}
