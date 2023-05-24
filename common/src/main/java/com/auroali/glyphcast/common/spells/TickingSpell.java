package com.auroali.glyphcast.common.spells;

import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * A spell that runs over several ticks until a condition is met
 *
 * @author Auroali
 */
public abstract class TickingSpell extends Spell {
    public TickingSpell(GlyphSequence sequence) {
        super(sequence);
    }

    @Override
    public void tryActivate(Level level, Player player, InteractionHand hand) {
        activate(new BasicContext(level, player, hand));
    }


    @Override
    public void activate(IContext ctx) {
        SpellUser.get(ctx.player()).ifPresent(user -> {
            CompoundTag tag = new CompoundTag();
            this.onActivate(ctx, tag);
            user.addTickingSpell(this, ctx.hand(), tag);
        });
    }

    /**
     * Called for every tick the spell is active
     *
     * @param ctx the spell context
     * @param tag the tag containing data for the spell
     * @return whether this spell should continue ticking or not
     */
    public abstract boolean tick(IContext ctx, int ticks, CompoundTag tag);

    /**
     * Called when this spell first activates,
     * should be used to set up the tag associated with this spell
     *
     * @param ctx the spell context
     * @param tag the tag containing data for the spell
     */
    public abstract void onActivate(IContext ctx, CompoundTag tag);

}
