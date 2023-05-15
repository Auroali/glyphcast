package com.auroali.glyphcast.common.items.equipment;

import net.minecraft.world.item.ItemStack;

public interface IMaxEnergyModifier {
    Type getEnergyModType();

    double getEnergyMod(ItemStack stack);

    enum Type {
        ADDITION,
        MULTIPLIER
    }
}

