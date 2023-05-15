package com.auroali.glyphcast.client.screen;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.client.GCKeybinds;
import com.auroali.glyphcast.client.screen.widgets.SpellWheelEntry;
import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.items.IWandLike;
import com.auroali.glyphcast.common.network.server.SelectSpellSlotMessage;
import com.auroali.glyphcast.common.network.GCNetwork;
import com.auroali.glyphcast.common.spells.SpellSlot;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class SpellWheelScreen extends Screen {
    public static final ResourceLocation WHEEL = new ResourceLocation(Glyphcast.MODID, "textures/gui/spell_wheel.png");
    final List<SpellSlot> slots;
    final boolean closeOnRelease;
    final boolean closeOnClick;
    final Consumer<SpellWheelEntry> onClose;
    final Consumer<SpellWheelScreen> onRightClick;
    public SpellWheelEntry selectedEntry;

    protected SpellWheelScreen(List<SpellSlot> slots, Consumer<SpellWheelEntry> onClose, Consumer<SpellWheelScreen> rightClick, boolean closeOnRelease, boolean closeOnClick) {
        super(GameNarrator.NO_TITLE);
        this.slots = slots;
        this.onClose = onClose;
        this.onRightClick = rightClick;
        this.closeOnRelease = closeOnRelease;
        this.closeOnClick = closeOnClick;
    }

    public static void openCombined() {
        openWithModifiable(e -> {
            if (e.visible) GCNetwork.CHANNEL.sendToServer(new SelectSpellSlotMessage(e.slotIndex));
        }, true, false);
    }

    /**
     * Opens the spell wheel screen
     *
     * @param onClose        runs on the selected spell entry when the screen closes
     * @param closeOnRelease if the screen should close when the SPELL_SELECTION key bind is released
     * @param closeOnClick   if the screen should close when the player left clicks
     * @see GCKeybinds
     */
    public static void openWithModifiable(Consumer<SpellWheelEntry> onClose, boolean closeOnRelease, boolean closeOnClick) {
        SpellUser.get(Minecraft.getInstance().player).ifPresent(user -> openScreenWith(user.getManuallyAssignedSlots(), onClose, s ->
                        openWithDefault(onClose, closeOnRelease, closeOnClick)
                , closeOnRelease, closeOnClick));
    }

    /**
     * Version of openScreen that uses the default slots instead of the modifiable ones
     *
     * @param onClose        runs on the selected spell entry when the screen closes
     * @param closeOnRelease if the screen should close when the SPELL_SELECTION key bind is released
     * @param closeOnClick   if the screen should close when the player left clicks
     * @see GCKeybinds
     */
    public static void openWithDefault(Consumer<SpellWheelEntry> onClose, boolean closeOnRelease, boolean closeOnClick) {
        SpellUser.get(Minecraft.getInstance().player).ifPresent(user -> openScreenWith(user.getDefaultSlots(), onClose, s ->
                        openWithModifiable(onClose, closeOnRelease, closeOnClick)
                , closeOnRelease, closeOnClick));
    }

    /**
     * Opens the spell wheel screen
     *
     * @param slots          the spell slots to populate the list with
     * @param onClose        runs on the selected spell entry when the screen closes
     * @param rightClick     runs when the user right clicks
     * @param closeOnRelease if the screen should close when the SPELL_SELECTION key bind is released
     * @param closeOnClick   if the screen should close when the player left clicks
     * @see GCKeybinds
     */
    public static void openScreenWith(List<SpellSlot> slots, Consumer<SpellWheelEntry> onClose, Consumer<SpellWheelScreen> rightClick, boolean closeOnRelease, boolean closeOnClick) {
        SpellWheelScreen screen = new SpellWheelScreen(slots, onClose, rightClick, closeOnRelease, closeOnClick);
        Minecraft.getInstance().setScreen(screen);
    }

    @Override
    protected void init() {
        selectedEntry = null;
        ItemStack castingItem = minecraft.player.getMainHandItem().getItem() instanceof IWandLike ? minecraft.player.getMainHandItem()
                : minecraft.player.getOffhandItem().getItem() instanceof IWandLike ? minecraft.player.getOffhandItem()
                : ItemStack.EMPTY;
        for (int i = 0; i < slots.size(); i++) {
            SpellWheelEntry entry = new SpellWheelEntry(this, castingItem, width / 2, height / 2, i, slots.get(i).getIndex(), slots.get(i).getSpell());
            addRenderableWidget(entry);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        if (closeOnRelease && GCKeybinds.SPELL_SELECTION.matches(pKeyCode, pScanCode))
            onClose();
        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (closeOnClick && pButton == 0)
            onClose();
        if (pButton == 1)
            onRightClick.accept(this);
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public void onClose() {
        if (selectedEntry != null && onClose != null) {
            onClose.accept(selectedEntry);
        }
        super.onClose();
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        if (selectedEntry == null)
            return;


        blit(pPoseStack, selectedEntry.centerX + selectedEntry.posX - 17, selectedEntry.centerY + selectedEntry.posY - 17, 0, 64, 34, 34);

        if (selectedEntry.visible && selectedEntry.spell != null)
            renderTooltip(pPoseStack, List.of(selectedEntry.spell.getName(), selectedEntry.spell.getSpellDescription()), Optional.empty(), selectedEntry.centerX + selectedEntry.posX, selectedEntry.centerY + selectedEntry.posY);
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
