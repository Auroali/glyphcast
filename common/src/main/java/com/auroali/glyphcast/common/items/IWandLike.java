package com.auroali.glyphcast.common.items;

import com.auroali.glyphcast.common.registry.GCWandCores;
import com.auroali.glyphcast.common.wands.WandCore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface IWandLike {
    default void setCore(ItemStack stack, ResourceLocation core) {
        stack.getOrCreateTag().putString("WandCore", core.toString());
    }

    default Optional<WandCore> getCore(ItemStack stack) {
        ResourceLocation location = new ResourceLocation(stack.getOrCreateTag().getString("WandCore"));
        return Optional.ofNullable(GCWandCores.getValue(location));
    }


}
