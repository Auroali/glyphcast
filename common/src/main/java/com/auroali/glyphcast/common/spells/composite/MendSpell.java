package com.auroali.glyphcast.common.spells.composite;


import com.auroali.glyphcast.common.items.IWandLike;
import com.auroali.glyphcast.common.spells.HoldSpell;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.spells.glyph.Ring;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public class MendSpell extends HoldSpell {
    public MendSpell() {
        super(new GlyphSequence(
                Ring.of(Glyph.LIGHT),
                Ring.of(Glyph.LIGHT, Glyph.EARTH, Glyph.LIGHT, Glyph.EARTH),
                Ring.of(Glyph.EARTH, Glyph.EARTH, Glyph.EARTH, Glyph.EARTH, Glyph.EARTH, Glyph.EARTH)
        ));
    }

    @Override
    protected void run(Spell.IContext ctx, int usedTicks) {
        ItemStack other = ctx.getOtherHandItem();
        if (usedTicks % 4 != 0 || ctx.player().totalExperience == 0 || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MENDING, other) == 0 || other.getDamageValue() == 0)
            return;

        ctx.player().giveExperiencePoints(-1);
        int toRepair = Math.min(2, other.getDamageValue());
        other.setDamageValue(other.getDamageValue() - toRepair);
    }

    @Override
    public boolean canCastSpell(ItemStack castingItem) {
        return castingItem.getItem() instanceof IWandLike;
    }

    @Override
    public double getCost() {
        return 0.5;
    }
}
