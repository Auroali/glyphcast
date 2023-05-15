package com.auroali.glyphcast.common.fabric;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PlayerHelperImpl {
    public static NonNullList<ItemStack> getAllEquipment(Player player) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        player.getArmorSlots().forEach(stacks::add);
        return stacks;
    }
}
