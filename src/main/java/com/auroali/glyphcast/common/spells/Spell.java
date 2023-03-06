package com.auroali.glyphcast.common.spells;

import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public abstract class Spell {
    protected final GlyphSequence sequence;

    public Spell(GlyphSequence sequence) {
        this.sequence = sequence;
    }

    public boolean isSequence(GlyphSequence sequence) {
        return this.sequence.equals(sequence);
    }
    public abstract void activate(Level level, Player player);
}
