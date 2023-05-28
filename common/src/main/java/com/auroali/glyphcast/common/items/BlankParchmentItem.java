package com.auroali.glyphcast.common.items;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.registry.GCItems;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BlankParchmentItem extends Item implements IGlyphWriteable {
    public BlankParchmentItem() {
        super(new Properties().stacksTo(16).tab(Glyphcast.GLYPHCAST_TAB));
    }

    @Override
    public ItemStack writeGlyphs(ItemStack stack, GlyphSequence sequence) {
        ItemStack stack1 = GCItems.PARCHMENT.get().withGlyphSequence(sequence);
        stack1.setCount(stack.getCount());

        return stack1;
    }
}
