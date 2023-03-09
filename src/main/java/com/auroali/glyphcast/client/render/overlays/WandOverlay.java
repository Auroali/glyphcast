package com.auroali.glyphcast.client.render.overlays;

import com.auroali.glyphcast.client.GCKeybinds;
import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.spells.SpellSlot;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class WandOverlay implements IGuiOverlay {
    public void renderSlot(PoseStack stack, SpellSlot slot) {
        int index = slot.getIndex();
        Component component = getComponent(index, slot.isEmpty() ? Component.empty() : slot.getSpell().getName());

        Minecraft.getInstance().font.draw(stack, component, (float) 5, (float) 5 + Minecraft.getInstance().font.lineHeight * slot.getIndex(), -1);
    }

    public Component getComponent(int index, Component component) {
        return switch (index) {
            case 1 -> Component.keybind(GCKeybinds.SELECT_SPELLSLOT_2.getName()).append(component);
            case 2 -> Component.keybind(GCKeybinds.SELECT_SPELLSLOT_3.getName()).append(component);
            case 3 -> Component.keybind(GCKeybinds.SELECT_SPELLSLOT_4.getName()).append(component);
            default -> Component.keybind(GCKeybinds.SELECT_SPELLSLOT_1.getName()).append(component);
        };
    }

    @Override
    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if(player == null)
            return;

        SpellUser.get(player).ifPresent(user -> user.getSlots().forEach(slot -> renderSlot(poseStack, slot)));
    }
}
