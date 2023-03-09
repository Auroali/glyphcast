package com.auroali.glyphcast.client.screen;

import com.auroali.glyphcast.client.screen.widgets.SpellListWidget;
import com.auroali.glyphcast.common.items.tooltip.GlyphTooltipComponent;
import com.auroali.glyphcast.common.network.server.SetSlotSpellMessage;
import com.auroali.glyphcast.common.registry.GCNetwork;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.List;
import java.util.Optional;

public class SpellSelectionScreen extends Screen {

    SpellListWidget list;
    public static void openScreen() {
        SpellSelectionScreen screen = new SpellSelectionScreen();
        Minecraft.getInstance().setScreen(screen);
    }

    protected SpellSelectionScreen() {
        super(Component.literal("test"));
    }

    @Override
    protected void init() {
        this.list = new SpellListWidget(this, width / 2, height / 2, 20, 200, Minecraft.getInstance().font.lineHeight * 2 + 2);

        Button slot1 = new Button(width / 2,0,Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Component.translatable("gui.glyphcast.select_spell"), b -> selectSpell(0));
        Button slot2 = new Button(width / 2,Button.DEFAULT_HEIGHT,Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Component.translatable("gui.glyphcast.select_spell"), b -> selectSpell(1));
        Button slot3 = new Button(width / 2,Button.DEFAULT_HEIGHT * 2,Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Component.translatable("gui.glyphcast.select_spell"), b -> selectSpell(2));
        Button slot4 = new Button(width / 2,Button.DEFAULT_HEIGHT * 3,Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Component.translatable("gui.glyphcast.select_spell"), b -> selectSpell(3));
        addRenderableWidget(list);
        addRenderableWidget(slot1);
        addRenderableWidget(slot2);
        addRenderableWidget(slot3);
        addRenderableWidget(slot4);
    }

    void selectSpell(int slot) {
        SpellListWidget.SpellListEntry entry = list.getSelected();
        if(entry == null)
            return;

        GCNetwork.sendToServer(new SetSlotSpellMessage(slot, entry.spell));
        list.setSelected(null);
    }
    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        //this.list.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        SpellListWidget.SpellListEntry entry = list.getHoveredEntry();
        if(entry != null) {
            Optional<TooltipComponent> tooltipComponent = Optional.empty();
            if(!entry.spell.isSequence(GlyphSequence.EMPTY))
                tooltipComponent = Optional.of(new GlyphTooltipComponent(entry.spell.getSequence()));

            renderTooltip(pPoseStack, List.of(entry.spell.getName()), tooltipComponent, pMouseX, pMouseY);

        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
