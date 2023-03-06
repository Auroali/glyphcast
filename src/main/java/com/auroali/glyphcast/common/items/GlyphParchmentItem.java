package com.auroali.glyphcast.common.items;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class GlyphParchmentItem extends Item {

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
        stack.getOrCreateTag().put("glyphcast:glyphs", sequence.serialize());
        return stack;
    }

    public GlyphParchmentItem() {
        super(new Properties().stacksTo(16).tab(GlyphCast.GLYPHCAST_TAB));
    }

    /**
     * TODO: Eventually this should be replaced with a direct spell lookup with the spell's id being saved instead of it's glyph sequence
     */
    public GlyphSequence getGlyphsFromStack(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if(tag.contains("glyphcast:glyphs"))
            return GlyphSequence.fromTag(tag.getCompound("glyphcast:glyphs"));
        return null;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        if(!pLevel.isClientSide) {
            GlyphSequence sequence = getGlyphsFromStack(stack);
            if(sequence == null)
                return InteractionResultHolder.pass(stack);

            Optional<Spell> spell = sequence.findSpell();
            spell.ifPresent(spell_ -> spell_.activate(pLevel, pPlayer));

            if(spell.isPresent() && !pPlayer.isCreative())
                stack.shrink(1);
            return spell.isPresent() ? InteractionResultHolder.consume(stack) : InteractionResultHolder.pass(stack);
        }
        return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide);
    }
}
