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

    public static void register() {
        KeyMappingRegistry.register(SPELL_SELECTION);
        ClientTickEvent.CLIENT_POST.register(minecraft -> {
            while (SPELL_SELECTION.consumeClick())
                SpellUser.get(Minecraft.getInstance().player).ifPresent(cap -> {
                    if (!cap.canOpenSpellWheel())
                        return;
                    SpellWheelScreen.openCombined();
                });
        });
    }
}
