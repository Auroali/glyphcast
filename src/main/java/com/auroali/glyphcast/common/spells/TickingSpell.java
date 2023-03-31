package com.auroali.glyphcast.common.spells;

import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import net.minecraft.nbt.CompoundTag;

public abstract class TickingSpell extends Spell {
    public TickingSpell(GlyphSequence sequence) {
        super(sequence);
    }

    @Override
    public void activate(IContext ctx) {
        SpellUser.get(ctx.player()).ifPresent(user -> {
            CompoundTag tag = new CompoundTag();
            this.onActivate(ctx, tag);
            user.addTickingSpell(this, ctx.stats(), tag);
        });
    }

    /**
     * Called for every tick the spell is active
     * @param ctx the spell context
     * @param tag the tag containing data for the spell
     * @return whether this spell should continue ticking or not
     */
    public abstract boolean tick(IContext ctx, int ticks, CompoundTag tag);

    /**
     * Called when this spell first activates,
     * should be used to set up the tag associated with this spell
     * @param ctx the spell context
     * @param tag the tag containing data for the spell
     */
    public abstract void onActivate(IContext ctx, CompoundTag tag);

}
