package com.auroali.glyphcast.client.screen.widgets;

import com.auroali.glyphcast.client.screen.SpellSelectionScreen;
import com.auroali.glyphcast.common.capabilities.ISpellUser;
import com.auroali.glyphcast.common.network.server.ClearSpellSlotMessage;
import com.auroali.glyphcast.common.network.server.SetSlotSpellMessage;
import com.auroali.glyphcast.common.registry.GCNetwork;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.SpellSlot;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpellListWidget extends ObjectSelectionList<SpellListWidget.SpellListEntry> {

    final int width;
    final SpellSelectionScreen screen;
    ISpellUser user;

    public SpellListWidget(SpellSelectionScreen screen, int pWidth, int pHeight, int pY0, int pY1, int pItemHeight) {
        super(screen.getMinecraft(), pWidth, pHeight, pY0, pY1, pItemHeight);
        this.width = pWidth;
        this.screen = screen;
        this.setRenderBackground(false);
        this.setRenderHeader(false, 0);
        this.setRenderTopAndBottom(false);
    }

    public ISpellUser getUser() {
        return user;
    }

    public void setUser(ISpellUser user) {
        this.user = user;
    }


    @Override
    protected void renderBackground(PoseStack pPoseStack) {
        super.renderBackground(pPoseStack);
        GuiComponent.fill(pPoseStack, getLeft(), getTop(), getLeft() + width, getBottom(), -14540254);
        vLine(pPoseStack, width - 1, getTop(), getBottom(), -1);
        vLine(pPoseStack, 0, getTop(), getBottom(), -1);
    }

    @Override
    public int getRowWidth() {
        return width - 1;
    }

    @Override
    public void setSelected(@Nullable SpellListWidget.SpellListEntry pSelected) {
        if(pSelected == null)
            return;
        pSelected.selected = !pSelected.selected;
        if(pSelected.selected) {
            assignSpell(pSelected.spell);
        } else {
            clearSpell(pSelected.spell);
        }
    }

    void assignSpell(Spell spell) {
        boolean found = false;
        for (int i = 0; i < user.getSlots().size(); i++) {
            if (!user.getSlots().get(i).isEmpty())
                continue;
            GCNetwork.sendToServer(new SetSlotSpellMessage(i, spell));
            found = true;
            break;
        }
        if (!found) {
            unselectEntry(user.getSlots().get(user.getSlots().size() - 1));
            GCNetwork.sendToServer(new SetSlotSpellMessage(user.getSlots().size() - 1, spell));
        }
    }

    void clearSpell(Spell spell) {
        for(int i = 0; i < user.getSlots().size(); i++) {
            if(user.getSlots().get(i).getSpell() == spell)
                GCNetwork.sendToServer(new ClearSpellSlotMessage(i));
        }
    }

    void unselectEntry(SpellSlot slot) {
        children().forEach(e -> {
            if(e.spell == slot.getSpell())
                e.selected = false;
        });
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
        public boolean selected;
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
            if(selected)
                fill(pPoseStack, 0, pTop, pWidth, pTop + pHeight, -1);
            Minecraft.getInstance().font.draw(pPoseStack, spell.getName(), pLeft + 18, pTop + 5, -1);
            Minecraft.getInstance().font.drawShadow(pPoseStack, spell.getName(), pLeft + 18, pTop + 5, -1);
        }

        @Override
        public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_)
        {
            widget.setSelected(this);
            return false;
        }
    }
}
