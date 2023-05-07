package com.auroali.glyphcast.common.items;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.blocks.CondensedEnergyCauldron;
import com.auroali.glyphcast.common.registry.GCBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;

public class VialItem extends Item {

    private static final DecimalFormat FORMAT = new DecimalFormat("###");

    public VialItem() {
        super(new Properties().tab(GlyphCast.GLYPHCAST_TAB).stacksTo(1));
        CondensedEnergyCauldron.INTERACTIONS.put(this, (pBlockState, pLevel, pBlockPos, pPlayer, pHand, pStack) -> {
            if (pStack.getOrCreateTag().getDouble("Amount") >= 250 && pBlockState.getValue(LayeredCauldronBlock.LEVEL) < 3) {
                pStack.getOrCreateTag().putDouble("Amount", 0);
                pLevel.setBlockAndUpdate(pBlockPos, pBlockState.setValue(LayeredCauldronBlock.LEVEL, pBlockState.getValue(LayeredCauldronBlock.LEVEL) + 1));
                return InteractionResult.sidedSuccess(pLevel.isClientSide);
            }
            if (pBlockState.getValue(LayeredCauldronBlock.LEVEL) == 0 && pStack.getOrCreateTag().getDouble("Amount") < 250)
                return InteractionResult.PASS;
            LayeredCauldronBlock.lowerFillLevel(pBlockState, pLevel, pBlockPos);
            pStack.getOrCreateTag().putDouble("Amount", 250);
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        });
        CauldronInteraction.EMPTY.put(this, (pBlockState, pLevel, pBlockPos, pPlayer, pHand, pStack) -> {
            if (pStack.getOrCreateTag().getDouble("Amount") >= 250) {
                BlockState blockstate = GCBlocks.CONDENSED_ENERGY_CAULDRON.get()
                        .defaultBlockState()
                        .setValue(LayeredCauldronBlock.LEVEL, 1);
                pLevel.setBlockAndUpdate(pBlockPos, blockstate);
                pLevel.gameEvent(GameEvent.BLOCK_CHANGE, pBlockPos, GameEvent.Context.of(blockstate));
                pLevel.levelEvent(1047, pBlockPos, 0);
                pStack.getOrCreateTag().putDouble("Amount", 0);
                return InteractionResult.sidedSuccess(pLevel.isClientSide);
            }
            return InteractionResult.PASS;
        });
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
