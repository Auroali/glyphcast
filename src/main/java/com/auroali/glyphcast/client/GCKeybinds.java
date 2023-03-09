package com.auroali.glyphcast.client;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.client.screen.SpellSelectionScreen;
import com.auroali.glyphcast.common.network.server.SelectSpellSlotMessage;
import com.auroali.glyphcast.common.registry.GCNetwork;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GlyphCast.MODID, value = Dist.CLIENT)
public class GCKeybinds {
    public static final KeyMapping SPELL_SELECTION = new KeyMapping("key.glyphcast.spells", InputConstants.KEY_BACKSLASH, "key.categories.glyphcast");
    public static final KeyMapping SELECT_SPELLSLOT_1 = new KeyMapping("key.glyphcast.spells.1", KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, InputConstants.KEY_1, "key.categories.glyphcast");
    public static final KeyMapping SELECT_SPELLSLOT_2 = new KeyMapping("key.glyphcast.spells.2", KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, InputConstants.KEY_2, "key.categories.glyphcast");
    public static final KeyMapping SELECT_SPELLSLOT_3 = new KeyMapping("key.glyphcast.spells.3", KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, InputConstants.KEY_3, "key.categories.glyphcast");
    public static final KeyMapping SELECT_SPELLSLOT_4 = new KeyMapping("key.glyphcast.spells.4", KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, InputConstants.KEY_4, "key.categories.glyphcast");

    @SubscribeEvent
    public static void onInput(InputEvent event) {
        if(SPELL_SELECTION.isDown())
            SpellSelectionScreen.openScreen();
        if(SELECT_SPELLSLOT_1.isDown())
            GCNetwork.sendToServer(new SelectSpellSlotMessage(0));
        if(SELECT_SPELLSLOT_2.isDown())
            GCNetwork.sendToServer(new SelectSpellSlotMessage(1));
        if(SELECT_SPELLSLOT_3.isDown())
            GCNetwork.sendToServer(new SelectSpellSlotMessage(2));
        if(SELECT_SPELLSLOT_4.isDown())
            GCNetwork.sendToServer(new SelectSpellSlotMessage(3));
    }
}
