package com.auroali.glyphcast.common;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class PlayerHelper {
    public static <T> boolean hasItemInHand(Player player, Class<T> tClass) {
        return tClass.isInstance(player.getMainHandItem().getItem()) || tClass.isInstance(player.getOffhandItem().getItem());
    }

    public static boolean hasItemInHand(Player player, TagKey<Item> tag) {
        return player.getMainHandItem().is(tag) || player.getOffhandItem().is(tag);
    }

    public static boolean hasItemInHand(Player player, Item item) {
        return player.getMainHandItem().is(item) || player.getOffhandItem().is(item);
    }

    public static ItemStack getHeldItem(Player player, TagKey<Item> tag) {
        return player.getMainHandItem().is(tag) ? player.getMainHandItem()
                : player.getOffhandItem().is(tag) ? player.getOffhandItem()
                : ItemStack.EMPTY;
    }

    @ExpectPlatform
    public static double getReachDistance(Player player) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static NonNullList<ItemStack> getAllEquipment(Player player) {
        throw new AssertionError();
    }
}
