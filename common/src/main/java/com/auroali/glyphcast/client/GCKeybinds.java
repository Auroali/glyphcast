package com.auroali.glyphcast.client;

import com.auroali.glyphcast.client.screen.SpellWheelScreen;
import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class GCKeybinds {
    public static final KeyMapping SPELL_SELECTION = new KeyMapping("key.glyphcast.spells", InputConstants.KEY_V, "key.categories.glyphcast");
    public static final KeyMapping QUICK_SELECT_0 = new KeyMapping("key.glyphcast.quick_select_0", InputConstants.KEY_Z, "key.categories.glyphcast");
    public static final KeyMapping QUICK_SELECT_1 = new KeyMapping("key.glyphcast.quick_select_1", InputConstants.KEY_X, "key.categories.glyphcast");
    public static final KeyMapping QUICK_SELECT_2 = new KeyMapping("key.glyphcast.quick_select_2", InputConstants.KEY_C, "key.categories.glyphcast");

    public static void register() {

        KeyMappingRegistry.register(SPELL_SELECTION);
        KeyMappingRegistry.register(QUICK_SELECT_0);
        KeyMappingRegistry.register(QUICK_SELECT_1);
        KeyMappingRegistry.register(QUICK_SELECT_2);
        ClientTickEvent.CLIENT_POST.register(minecraft -> {
            while (SPELL_SELECTION.consumeClick())
                SpellUser.get(Minecraft.getInstance().player).ifPresent(cap -> {
                    if (!cap.canOpenSpellWheel())
                        return;
                    SpellWheelScreen.openCombined();
                });
            while (QUICK_SELECT_0.consumeClick())
                SpellUser.get(Minecraft.getInstance().player).ifPresent(cap -> cap.quickSelectSlot(0));
            while (QUICK_SELECT_1.consumeClick())
                SpellUser.get(Minecraft.getInstance().player).ifPresent(cap -> cap.quickSelectSlot(1));
            while (QUICK_SELECT_2.consumeClick())
                SpellUser.get(Minecraft.getInstance().player).ifPresent(cap -> cap.quickSelectSlot(2));
        });
    }
}
