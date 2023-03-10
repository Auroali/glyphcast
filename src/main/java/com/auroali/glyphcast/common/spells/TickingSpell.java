package com.auroali.glyphcast.common.spells;

import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public abstract class TickingSpell extends Spell {
    public TickingSpell(GlyphSequence sequence) {
        super(sequence);
    }

    @Override
    public void activate(Level level, Player player) {
        SpellUser.get(player).ifPresent(user -> {
            CompoundTag tag = new CompoundTag();
            this.onActivate(level, player, tag);
            user.addTickingSpell(this, tag);
        });
    }

    /**
     * Called for every tick the spell is active
     * @param level the level this spell is running in
     * @param player the player casting this spell
     * @param tag the tag containing data for the spell
     * @return whether this spell should continue ticking or not
     */
    public abstract boolean tick(Level level, Player player, int ticks, CompoundTag tag);

    /**
     * Called when this spell first activates,
     * should be used to set up the tag associated with this spell
     * @param level the level this spell is running in
     * @param player the player casting this spell
     * @param tag the tag containing data for the spell
     */
    public abstract void onActivate(Level level, Player player, CompoundTag tag);

}
