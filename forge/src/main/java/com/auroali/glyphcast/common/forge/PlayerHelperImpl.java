package com.auroali.glyphcast.common.forge;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

public class PlayerHelperImpl {
    public static NonNullList<ItemStack> getAllEquipment(Player player) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        player.getArmorSlots().forEach(stacks::add);
        CuriosApi.getCuriosHelper().getEquippedCurios(player).ifPresent(handler -> {
            for(int i = 0; i < handler.getSlots(); i++)
                stacks.add(handler.getStackInSlot(i));
        });
        return stacks;
    }

    public static double getReachDistance(Player player) {
        return player.getReachDistance();
    }
}
