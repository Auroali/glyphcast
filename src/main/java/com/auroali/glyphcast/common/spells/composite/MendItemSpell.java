package com.auroali.glyphcast.common.spells.composite;

import com.auroali.glyphcast.common.items.IWandLike;
import com.auroali.glyphcast.common.spells.HoldSpell;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.spells.glyph.Ring;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;

public class MendItemSpell extends HoldSpell {
    public MendItemSpell() {
        super(new GlyphSequence(Ring.of(Glyph.LIGHT), Ring.of(Glyph.LIGHT, Glyph.FIRE, Glyph.LIGHT, Glyph.FIRE), Ring.of(Glyph.EARTH, Glyph.EARTH)));
    }

    @Override
    protected void run(IContext ctx, int usedTicks) {
        ItemStack other = ctx.getOtherHandItem();
        if (usedTicks % 4 != 0 || ctx.player().totalExperience == 0 || other.getEnchantmentLevel(Enchantments.MENDING) == 0 || other.getDamageValue() == 0)
            return;

        ctx.player().giveExperiencePoints((int) (-1 / Math.min(1.0, ctx.stats().lightAffinity())));
        int toRepair = (int) Math.min(other.getXpRepairRatio(), other.getDamageValue());
        other.setDamageValue(other.getDamageValue() - toRepair);
    }

    @Override
    public boolean canCastSpell(ItemStack castingItem) {
        return castingItem.getItem() instanceof IWandLike;
    }

    @Override
    public double getCost() {
        return 0;
    }
}
