package com.auroali.glyphcast.client.screen.widgets;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.client.screen.SpellSelectionScreen;
import com.auroali.glyphcast.common.spells.Spell;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.widget.ModListWidget;

public class SpellListWidget extends ObjectSelectionList<SpellListWidget.SpellListEntry> {

    final int width;
    public SpellListWidget(Minecraft pMinecraft, int pWidth, int pHeight, int pY0, int pY1, int pItemHeight) {
        super(pMinecraft, pWidth, pHeight, pY0, pY1, pItemHeight);
        this.width = pWidth;
        GlyphCast.SPELL_REGISTRY.get().getValues().forEach(spell -> addEntry(new SpellListEntry(this, spell)));
    }

    @Override
    public int getRowWidth() {
        return width;
    }

    @Override
    protected int getScrollbarPosition()
    {
        return this.width;
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
            renderIcon(pPoseStack, spell, pTop, pLeft);
            Minecraft.getInstance().font.draw(pPoseStack, spell.getName(), pLeft + 18, pTop + 5, -1);
        }

        public void renderIcon(PoseStack stack, Spell spell, int top, int left) {
            ResourceLocation id = GlyphCast.SPELL_REGISTRY.get().getKey(spell);
            if(id == null)
                return;

            ResourceLocation texture = new ResourceLocation(id.getNamespace(), "textures/spell/%s.png".formatted(id.getPath()));
            RenderSystem.setShaderTexture(0, texture);
            blit(stack, left, top, 0, 0, 16, 16, 16, 16);
        }

        @Override
        public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_)
        {
            widget.setSelected(this);
            return false;
        }
    }
}
