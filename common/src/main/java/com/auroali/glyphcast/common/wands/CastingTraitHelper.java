package com.auroali.glyphcast.common.wands;

import com.auroali.glyphcast.common.items.ICastingItem;
import net.minecraft.world.item.ItemStack;

public class CastingTraitHelper {
    public boolean hasTrait(ItemStack stack, CastingTrait trait) {
        if(stack.getItem() instanceof ICastingItem castingItem) {
            return castingItem.getTraits(stack).contains(trait);
        }
        return false;
    }
}
