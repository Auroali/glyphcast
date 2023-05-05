package com.auroali.glyphcast.common.items;

import com.auroali.glyphcast.GlyphCast;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;

public class VialItem extends Item {

    private static final DecimalFormat FORMAT = new DecimalFormat("###");

    public VialItem() {
        super(new Properties().tab(GlyphCast.GLYPHCAST_TAB).stacksTo(16));
    }

    public double fill(ItemStack stack, double energy) {
        double current = stack.getOrCreateTag().getDouble("Amount");
        double newAmount = current + energy;
        if (newAmount > 250) {
            newAmount = 250;
            energy = (energy - (current + energy - 250)) * 100.0;
        }

        stack.getOrCreateTag().putDouble("Amount", newAmount);

        return energy;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("item.glyphcast.vial.tooltip", FORMAT.format(pStack.getOrCreateTag().getDouble("Amount")))
                .withStyle(ChatFormatting.BLUE)
                .withStyle(ChatFormatting.BOLD));
    }
}
