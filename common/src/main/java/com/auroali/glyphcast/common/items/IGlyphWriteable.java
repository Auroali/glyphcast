package com.auroali.glyphcast.common.items;

import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import net.minecraft.world.item.ItemStack;

public interface IGlyphWriteable {
    ItemStack writeGlyphs(ItemStack stack, GlyphSequence sequence);
}
