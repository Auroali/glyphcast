package com.auroali.glyphcast.client.screen;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.client.GCKeybinds;
import com.auroali.glyphcast.client.screen.widgets.SpellWheelEntry;
import com.auroali.glyphcast.common.capabilities.ISpellUser;
import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class SpellWheelScreen extends Screen {
    public static final ResourceLocation WHEEL = new ResourceLocation(GlyphCast.MODID, "textures/gui/spell_wheel.png");
    final ISpellUser user;

    public SpellWheelEntry selectedEntry;

    final boolean closeOnRelease;
    final boolean closeOnClick;
    final Consumer<SpellWheelEntry> consumer;

    /**
     * Opens the spell wheel screen
     * @param consumer runs on the selected spell entry when the screen closes
     * @param closeOnRelease if the screen should close when the SPELL_SELECTION key bind is released
     * @param closeOnClick if the screen should close when the player left clicks
     * @see com.auroali.glyphcast.client.GCKeybinds
     */
    public static void openScreen(Consumer<SpellWheelEntry> consumer, boolean closeOnRelease, boolean closeOnClick) {
        SpellWheelScreen screen = new SpellWheelScreen(consumer, closeOnRelease, closeOnClick);
        Minecraft.getInstance().setScreen(screen);
    }

    protected SpellWheelScreen(Consumer<SpellWheelEntry> consumer, boolean closeOnRelease, boolean closeOnClick) {
        super(GameNarrator.NO_TITLE);
        this.user = SpellUser.get(Minecraft.getInstance().player).orElse(new SpellUser(Minecraft.getInstance().player));
        this.consumer = consumer;
        this.closeOnRelease = closeOnRelease;
        this.closeOnClick = closeOnClick;
    }

    @Override
    protected void init() {
        selectedEntry = null;
        for(int i = 0; i < user.getSlots().size(); i++) {
            SpellWheelEntry entry = new SpellWheelEntry(this, width / 2, height / 2, i, user.getSlots().get(i).getSpell());
            addRenderableWidget(entry);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        if(closeOnRelease && GCKeybinds.SPELL_SELECTION.matches(pKeyCode, pScanCode))
            onClose();
        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if(closeOnClick && pButton == 0)
            onClose();
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public void onClose() {
        if(selectedEntry != null) {
            consumer.accept(selectedEntry);
        }
        super.onClose();
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        if(selectedEntry == null)
            return;


        blit(pPoseStack, selectedEntry.centerX + selectedEntry.posX - 17, selectedEntry.centerY + selectedEntry.posY - 17, 0, 64, 34, 34);

        if(selectedEntry.spell != null)
            renderTooltip(pPoseStack, selectedEntry.spell.getName(), selectedEntry.centerX + selectedEntry.posX, selectedEntry.centerY + selectedEntry.posY);
    }

    @Override
    public void renderBackground(PoseStack pPoseStack) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, WHEEL);

        int posX = (width - 154) / 2;
        int posY = (height - 154) / 2;
        blit(pPoseStack, posX, posY, 0, 0, 154, 154);
    }
}
