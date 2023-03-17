package com.auroali.glyphcast.common.items;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.client.screen.SpellWheelScreen;
import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.network.server.SetSlotSpellMessage;
import com.auroali.glyphcast.common.registry.GCItems;
import com.auroali.glyphcast.common.registry.GCNetwork;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class WandItem extends Item {
    public WandItem() {
        super(new Properties().stacksTo(1).tab(GlyphCast.GLYPHCAST_TAB));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        ItemStack other = pPlayer.getItemInHand(pUsedHand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        if(pPlayer.isCrouching() && other.is(GCItems.PARCHMENT.get())) {
            if(pLevel.isClientSide) {
                ISpellHolder holder = (ISpellHolder) other.getItem();
                holder.getSpell(other).ifPresent(spell -> {
                    SpellWheelScreen.openScreen(entry -> GCNetwork.sendToServer(new SetSlotSpellMessage(entry.index, spell)), false, true);
                });
            }
            return InteractionResultHolder.sidedSuccess(stack, !pLevel.isClientSide);
        }
        if(!pLevel.isClientSide) {
            SpellUser.get(pPlayer).ifPresent(user -> {
                if(user.getSelectedSpell() != null)
                    user.getSelectedSpell().activate(pLevel, pPlayer);
            });
        }
        return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide);
    }
}
