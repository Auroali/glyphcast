package com.auroali.glyphcast.common.spells.composite;

import com.auroali.glyphcast.common.spells.TickingSpell;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.spells.glyph.Ring;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class FlareSpell extends TickingSpell {
    public FlareSpell() {
        super(new GlyphSequence(Ring.of(Glyph.FIRE), Ring.of(Glyph.FIRE, Glyph.FIRE, Glyph.LIGHT, Glyph.LIGHT)));
    }

    @Override
    public boolean tick(Level level, Player player, int ticks, CompoundTag tag) {
        return false;
    }

    @Override
    public void onActivate(Level level, Player player, CompoundTag tag) {

    }
}
