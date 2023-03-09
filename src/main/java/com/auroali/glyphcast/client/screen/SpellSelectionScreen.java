package com.auroali.glyphcast.client.screen;

import com.auroali.glyphcast.client.screen.widgets.SpellListWidget;
import com.auroali.glyphcast.common.network.server.SelectSpellMessage;
import com.auroali.glyphcast.common.registry.GCNetwork;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

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
        this.list = new SpellListWidget(this.minecraft, width / 2, height / 2, 20, 200, Minecraft.getInstance().font.lineHeight * 2 + 2);
        this.list.setLeftPos(6);

        Button button = new Button(0,0,Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Component.translatable("gui.glyphcast.select_spell"), b -> selectSpell());
        addRenderableWidget(list);
        addRenderableWidget(button);
    }

    void selectSpell() {
        SpellListWidget.SpellListEntry entry = list.getSelected();
        if(entry == null)
            return;

        GCNetwork.sendToServer(new SelectSpellMessage(entry.spell));
        minecraft.setScreen(null);
    }
    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        //this.list.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
