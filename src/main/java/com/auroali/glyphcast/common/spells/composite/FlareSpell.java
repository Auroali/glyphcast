package com.auroali.glyphcast.common.spells.composite;

import com.auroali.glyphcast.common.entities.Flare;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.SpellStats;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.spells.glyph.Ring;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class FlareSpell extends Spell {
    public FlareSpell() {
        super(new GlyphSequence(Ring.of(Glyph.FIRE), Ring.of(Glyph.FIRE, Glyph.FIRE, Glyph.LIGHT, Glyph.LIGHT)));
    }

    @Override
    public double getCost() {
        return 20;
    }

    @Override
    public void activate(Level level, Player player, SpellStats stats) {
        Flare flare = new Flare(level, player.getX() + player.getLookAngle().x, player.getEyeY() - 0.25 + player.getLookAngle().y, player.getZ() + player.getLookAngle().z);
        flare.setDeltaMovement(player.getLookAngle().scale(1.25));
        level.addFreshEntity(flare);
        level.playSound(null, player, SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0f, 1.0f);
    }
}
