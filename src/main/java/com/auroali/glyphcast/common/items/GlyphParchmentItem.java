package com.auroali.glyphcast.common.items;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class GlyphParchmentItem extends Item implements ISpellHolder {

    @Override
    public void fillItemCategory(CreativeModeTab pCategory, NonNullList<ItemStack> pItems) {
        if (this.allowedIn(pCategory)) {
            pItems.add(withGlyphSequence(new GlyphSequence(Glyph.FIRE)));
            pItems.add(withGlyphSequence(new GlyphSequence(Glyph.LIGHT)));
            pItems.add(withGlyphSequence(new GlyphSequence(Glyph.ICE)));
            pItems.add(withGlyphSequence(new GlyphSequence(Glyph.EARTH)));
        }
    }

    public ItemStack withGlyphSequence(GlyphSequence sequence) {
        ItemStack stack = new ItemStack(this);
        this.writeSequence(stack, sequence);
        return stack;
    }

    public GlyphParchmentItem() {
        super(new Properties().stacksTo(16).tab(GlyphCast.GLYPHCAST_TAB));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        if(!pLevel.isClientSide) {
            Optional<Spell> spell = getSpell(stack);
            spell.ifPresent(spell_ -> spell_.activate(pLevel, pPlayer));

            if(spell.isPresent() && !pPlayer.isCreative())
                stack.shrink(1);
            return spell.isPresent() ? InteractionResultHolder.consume(stack) : InteractionResultHolder.pass(stack);
        }
        return InteractionResultHolder.success(stack);
    }
}
