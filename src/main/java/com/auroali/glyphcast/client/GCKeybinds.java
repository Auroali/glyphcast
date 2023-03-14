package com.auroali.glyphcast.client;

import com.auroali.glyphcast.GlyphCast;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GlyphCast.MODID, value = Dist.CLIENT)
public class GCKeybinds {
//    public static final KeyMapping SPELL_SELECTION = new KeyMapping("key.glyphcast.spells", InputConstants.KEY_BACKSLASH, "key.categories.glyphcast");
//    public static final KeyMapping SELECT_SPELLSLOT_1 = new KeyMapping("key.glyphcast.spells.1", KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, InputConstants.KEY_1, "key.categories.glyphcast");
//    public static final KeyMapping SELECT_SPELLSLOT_2 = new KeyMapping("key.glyphcast.spells.2", KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, InputConstants.KEY_2, "key.categories.glyphcast");
//    public static final KeyMapping SELECT_SPELLSLOT_3 = new KeyMapping("key.glyphcast.spells.3", KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, InputConstants.KEY_3, "key.categories.glyphcast");
//    public static final KeyMapping SELECT_SPELLSLOT_4 = new KeyMapping("key.glyphcast.spells.4", KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, InputConstants.KEY_4, "key.categories.glyphcast");
//    public static final KeyMapping SELECT_SPELLSLOT_5 = new KeyMapping("key.glyphcast.spells.5", KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, InputConstants.KEY_5, "key.categories.glyphcast");
//    public static final KeyMapping SELECT_SPELLSLOT_6 = new KeyMapping("key.glyphcast.spells.6", KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, InputConstants.KEY_6, "key.categories.glyphcast");
//    public static final KeyMapping SELECT_SPELLSLOT_7 = new KeyMapping("key.glyphcast.spells.7", KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, InputConstants.KEY_7, "key.categories.glyphcast");
//    public static final KeyMapping SELECT_SPELLSLOT_8 = new KeyMapping("key.glyphcast.spells.8", KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, InputConstants.KEY_8, "key.categories.glyphcast");
//    public static final KeyMapping SELECT_SPELLSLOT_9 = new KeyMapping("key.glyphcast.spells.9", KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, InputConstants.KEY_9, "key.categories.glyphcast");

    @SubscribeEvent
    public static void onInput(InputEvent event) {}
}
