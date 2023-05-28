package com.auroali.glyphcast.common.items;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.client.screen.SpellWheelScreen;
import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.network.GCNetwork;
import com.auroali.glyphcast.common.network.server.SetSlotSpellMessage;
import com.auroali.glyphcast.common.registry.GCItems;
import com.auroali.glyphcast.common.spells.HoldSpell;
import com.auroali.glyphcast.common.wands.CastingTrait;
import com.auroali.glyphcast.common.wands.WandCore;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class WandItem extends Item implements IPointItem, IWandLike, ICastingItem {
    private final List<CastingTrait> traits;
    public WandItem(List<CastingTrait> traits) {
        super(new Properties().stacksTo(1).tab(Glyphcast.GLYPHCAST_TAB).durability(250));
        this.traits = traits;
    }

    private static void openSpellWheelEditor(Level pLevel, Player pPlayer, ItemStack other) {
        if (pLevel.isClientSide) {
            ISpellHolder holder = (ISpellHolder) other.getItem();
            holder.getSpell(other).ifPresent(spell ->
                    SpellUser.get(pPlayer).ifPresent(user ->
                            SpellWheelScreen.openScreenWith(user.getManuallyAssignedSlots(), entry -> GCNetwork.CHANNEL.sendToServer(new SetSlotSpellMessage(entry.index, spell)), s -> {
                            }, false, true)
                    )
            );
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);

        String coreKey = getCore(pStack).map(core ->
            core.item().getDescriptionId()
        ).orElse("wand_core.none");

        pTooltipComponents.add(Component.translatable("item.glyphcast.wand.tooltip", Component.translatable(coreKey).withStyle(ChatFormatting.BLUE).withStyle(ChatFormatting.BOLD)).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));

        List<CastingTrait> traits = getTraits(pStack);
        traits.forEach(trait ->
            pTooltipComponents.add(trait.getTranslationComponent())
        );
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity, int pTimeCharged) {
        if (pLivingEntity instanceof Player player) {
            SpellUser.get(player)
                    .ifPresent(cap ->
                            cap.getTickingSpells().removeIf(data -> {
                                if(data.getSpell() instanceof HoldSpell) {
                                    cap.getCooldownManager().addCooldown(data.getSpell(), data.getSpell().getCooldown());
                                    return true;
                                }
                                return false;
                            })
                    );
        }
    }

    @Override
    public void fillItemCategory(CreativeModeTab pCategory, NonNullList<ItemStack> pItems) {
        if (this.allowedIn(pCategory)) {
            ItemStack stack = new ItemStack(this);
            setCore(stack, new ResourceLocation(Glyphcast.MODID, "petal"));
            pItems.add(stack);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        ItemStack other = pPlayer.getItemInHand(pUsedHand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        if (pPlayer.isCrouching() && other.is(GCItems.PARCHMENT.get())) {
            openSpellWheelEditor(pLevel, pPlayer, other);
            return InteractionResultHolder.sidedSuccess(stack, !pLevel.isClientSide);
        }
        if (!pLevel.isClientSide) {
            activateSpell(pLevel, pPlayer, pUsedHand, stack);
        }
        return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide);
    }

    private void activateSpell(Level pLevel, Player pPlayer, InteractionHand hand, ItemStack stack) {
        SpellUser.get(pPlayer).ifPresent(user -> {
            if (user.getSelectedSpell() != null && !user.getCooldownManager().isOnCooldown(user.getSelectedSpell())) {
                user.getSelectedSpell().tryActivate(pLevel, pPlayer, hand);
                if (user.getSelectedSpell() instanceof HoldSpell)
                    pPlayer.startUsingItem(hand);
                else
                    user.getCooldownManager().addCooldown(user.getSelectedSpell(), user.getSelectedSpell().getCooldown());

                stack.hurtAndBreak(1, pPlayer, (p_35997_) -> {
                    p_35997_.broadcastBreakEvent(hand);
                });
            }
        });
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 7200;
    }

    @Override
    public void transform(PoseStack stack) {
        stack.mulPose(Quaternion.fromXYZ((float) (-Math.PI / 2.3), 0, 0));
        stack.translate(0, 0.15, -0.2);
    }

    @Override
    public List<CastingTrait> getTraits(ItemStack stack) {
        return Stream.concat(
                traits.stream(),
                getCore(stack).map(WandCore::traits).orElseGet(Collections::emptyList).stream()
        ).toList();
    }
}
