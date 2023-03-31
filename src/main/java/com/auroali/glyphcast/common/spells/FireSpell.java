package com.auroali.glyphcast.common.spells;

import com.auroali.glyphcast.common.entities.FireSpellProjectile;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.spells.glyph.Ring;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class FireSpell extends Spell {
    public FireSpell() {
        super(new GlyphSequence(Ring.of(Glyph.FIRE)));
    }

    @Override
    public double getCost() {
        return 10;
    }

    @Override
    public void activate(IContext ctx) {
        FireSpellProjectile fire = new FireSpellProjectile(ctx.level(), ctx.player().getX() + ctx.player().getLookAngle().x, ctx.player().getEyeY() - 0.25 + ctx.player().getLookAngle().y, ctx.player().getZ() + ctx.player().getLookAngle().z);
        fire.setOwner(ctx.player());
        fire.setDeltaMovement(ctx.player().getLookAngle().scale(1.25));
        fire.getEntityData().set(FireSpellProjectile.DAMAGE, (float)(8 * ctx.stats().fireAffinity()));
        ctx.level().addFreshEntity(fire);
        ctx.level().playSound(null, ctx.player(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0f, 1.0f);
    }
}
