package com.auroali.glyphcast.common.items.equipment;

import com.auroali.glyphcast.Glyphcast;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CrystalAmuletItem extends Item implements IMaxEnergyModifier {
    public CrystalAmuletItem() {
        super(new Properties().stacksTo(1).tab(Glyphcast.GLYPHCAST_TAB));
    }

    @Override
    public Type getEnergyModType() {
        return Type.ADDITION;
    }

    @Override
    public double getEnergyMod(ItemStack stack) {
        return 50;
    }
}
