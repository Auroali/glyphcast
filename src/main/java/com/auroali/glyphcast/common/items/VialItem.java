package com.auroali.glyphcast.common.items;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.blocks.CondensedEnergyCauldron;
import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.registry.GCBlocks;
import com.auroali.glyphcast.common.registry.GCItems;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.NonNullList;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
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
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        return pPlayer.getItemInHand(pUsedHand).getOrCreateTag().getDouble("Amount") > 0
                ? ItemUtils.startUsingInstantly(pLevel, pPlayer, pUsedHand)
                : InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        if (pLivingEntity instanceof Player player) {
            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, pStack);
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.isCreative()) {
                pStack.shrink(1);
            }

            pLivingEntity.gameEvent(GameEvent.DRINK);

            SpellUser.get(player).ifPresent(user -> user.setEnergy(user.getEnergy() + pStack.getOrCreateTag().getDouble("Amount") / 4));
            return !player.isCreative() ? new ItemStack(GCItems.VIAL.get()) : pStack;
        }
        return pStack;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.DRINK;
    }

    @Override
    public void fillItemCategory(CreativeModeTab pCategory, NonNullList<ItemStack> pItems) {
        super.fillItemCategory(pCategory, pItems);
        if (allowedIn(pCategory)) {
            ItemStack stack = new ItemStack(this);
            stack.getOrCreateTag().putDouble("Amount", 250);
            pItems.add(stack);
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("item.glyphcast.vial.tooltip", FORMAT.format(pStack.getOrCreateTag().getDouble("Amount")))
                .withStyle(ChatFormatting.BLUE)
                .withStyle(ChatFormatting.BOLD));
    }
}
