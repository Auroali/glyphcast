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
    public void activate(Level level, Player player) {
        if(!canDrainEnergy(player, 10))
            return;
        drainEnergy(player, 10);
        FireSpellProjectile fire = new FireSpellProjectile(level, player.getX() + player.getLookAngle().x, player.getEyeY() - 0.25 + player.getLookAngle().y, player.getZ() + player.getLookAngle().z);
        fire.setOwner(player);
        fire.setDeltaMovement(player.getLookAngle().scale(1.25));
        level.addFreshEntity(fire);
        level.playSound(null, player, SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0f, 1.0f);
    }
}
