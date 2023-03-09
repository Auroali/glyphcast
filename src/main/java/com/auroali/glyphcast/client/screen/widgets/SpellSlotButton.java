package com.auroali.glyphcast.client.screen.widgets;

import com.auroali.glyphcast.client.screen.SpellSelectionScreen;
import com.auroali.glyphcast.common.capabilities.ISpellUser;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

public class SpellSlotButton extends Button {
    final ISpellUser user;
    final SpellListWidget list;
    public final int slot;

    public SpellSlotButton(int slot, ISpellUser user, SpellListWidget list, int pX, int pY, OnPress pOnPress) {
        super(pX, pY, 16, 16, Component.empty(), pOnPress);
        this.user = user;
        this.list = list;
        this.slot = slot;
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, SpellSelectionScreen.SELECTION_ICONS);
        this.blit(pPoseStack, x, y, 24, shouldShowPrompt(pMouseX, pMouseY) ? 16 : getIcon(), 16, 16);
    }

    int getIcon() {
        return user.getSlots().get(slot).isEmpty() ? 0 : 32;
    }

    boolean shouldShowPrompt(int mouseX, int mouseY) {
        return isMouseOver(mouseX, mouseY) && list.getSelected() != null;
    }
}
