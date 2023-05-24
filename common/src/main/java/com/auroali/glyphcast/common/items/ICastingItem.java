package com.auroali.glyphcast.common.items;

import com.auroali.glyphcast.common.wands.CastingTrait;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface ICastingItem {
    List<CastingTrait> getTraits(ItemStack stack);
}
