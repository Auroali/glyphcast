package com.auroali.glyphcast.common.spells;

import com.auroali.glyphcast.common.entities.FireSpellProjectile;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.spells.glyph.Ring;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class FireSpell extends Spell {
    public FireSpell() {
        super(new GlyphSequence(Ring.of(Glyph.FIRE)));
    }

    @Override
    public double getCost() {
        return 10;
    }

    @Override
    public void activate(Level level, Player player, SpellStats stats) {
        FireSpellProjectile fire = new FireSpellProjectile(level, player.getX() + player.getLookAngle().x, player.getEyeY() - 0.25 + player.getLookAngle().y, player.getZ() + player.getLookAngle().z);
        fire.setOwner(player);
        fire.setDeltaMovement(player.getLookAngle().scale(1.25));
        fire.getEntityData().set(FireSpellProjectile.DAMAGE, (float)(8 * stats.fireAffinity()));
        level.addFreshEntity(fire);
        level.playSound(null, player, SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0f, 1.0f);
    }
}
