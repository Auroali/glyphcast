package com.auroali.glyphcast.common.items;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class WandItem extends Item {
    public WandItem() {
        super(new Properties().stacksTo(1).tab(GlyphCast.GLYPHCAST_TAB));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        if(!pLevel.isClientSide) {
            GlyphSequence s = new GlyphSequence(Glyph.FIRE);
            Optional<Spell> spell = s.findSpell();
            spell.ifPresent(spell_ -> spell_.activate(pLevel, pPlayer));
            return spell.isPresent() ? InteractionResultHolder.consume(stack) : InteractionResultHolder.pass(stack);
        }
        return InteractionResultHolder.success(stack);
    }
}
