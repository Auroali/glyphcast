package com.auroali.glyphcast.client;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.client.screen.SpellWheelScreen;
import com.auroali.glyphcast.common.network.server.SelectSpellSlotMessage;
import com.auroali.glyphcast.common.registry.GCNetwork;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GlyphCast.MODID, value = Dist.CLIENT)
public class GCKeybinds {
    public static final KeyMapping SPELL_SELECTION = new KeyMapping("key.glyphcast.spells", InputConstants.KEY_V, "key.categories.glyphcast");

    @SubscribeEvent
    public static void onInput(InputEvent event) {
        if(SPELL_SELECTION.isDown())
            SpellWheelScreen.openScreen(entry -> GCNetwork.sendToServer(new SelectSpellSlotMessage(entry.index)), true, false);
    }
}
