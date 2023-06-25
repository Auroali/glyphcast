package com.auroali.glyphcast.common.wands;

import com.auroali.glyphcast.common.items.ICastingItem;
import com.auroali.glyphcast.common.registry.GCCastingTraits;
import net.minecraft.world.item.ItemStack;

public class CastingTraitHelper {
    public static boolean hasTrait(ItemStack stack, CastingTrait trait) {
        if(stack.getItem() instanceof ICastingItem castingItem) {
            return castingItem.getTraits(stack).contains(trait);
        }
        return false;
    }

    public static double calculateFinalCost(ItemStack stack, double amount) {
        if(hasTrait(stack, GCCastingTraits.ANCIENT.get()))
            amount *= 1.5;

        return amount;
    }
}
