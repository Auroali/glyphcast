package com.auroali.glyphcast.common.spells;

import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import net.minecraft.nbt.CompoundTag;

/**
 * A spell that runs until the player stops holding right-click with a wand
 */
public abstract class HoldSpell extends TickingSpell {
    public HoldSpell(GlyphSequence sequence) {
        super(sequence);
    }

    protected abstract void run(IContext ctx, int usedTicks);

    @Override
    public boolean tick(IContext ctx, int ticks, CompoundTag tag) {
        if (!canDrainEnergy(ctx, getCost()))
            return false;

        drainEnergy(ctx, getCost());
        run(ctx, ticks);
        return ctx.isWand();
    }

    @Override
    public void onActivate(IContext ctx, CompoundTag tag) {
    }
}
