package com.auroali.glyphcast.common.items;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.capabilities.SpellUser;
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
        if(!pLevel.isClientSide) {
            SpellUser.get(pPlayer).ifPresent(user -> user.getSelectedSpell().activate(pLevel, pPlayer));

            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.success(stack);
    }
}
