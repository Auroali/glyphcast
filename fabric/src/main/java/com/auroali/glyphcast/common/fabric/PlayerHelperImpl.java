package com.auroali.glyphcast.common.fabric;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PlayerHelperImpl {
    public static NonNullList<ItemStack> getAllEquipment(Player player) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        player.getArmorSlots().forEach(stacks::add);
        TrinketsApi.getTrinketComponent(player).ifPresent(trinketComponent ->
                trinketComponent.getAllEquipped()
                        .stream()
                        .map(Tuple::getB)
                        .forEach(stacks::add)
        );
        return stacks;
    }

    public static double getReachDistance(Player player) {
        return ReachEntityAttributes.getReachDistance(player, 5.0);
    }
}
