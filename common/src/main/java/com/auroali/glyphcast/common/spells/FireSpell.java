package com.auroali.glyphcast.common.spells;

import com.auroali.glyphcast.common.entities.FireSpellProjectile;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.spells.glyph.Ring;
import net.minecraft.sounds.SoundEvents;

public class FireSpell extends HoldSpell {
    public FireSpell() {
        super(new GlyphSequence(Ring.of(Glyph.FIRE)));
    }

    @Override
    public double getCost() {
        return 1;
    }

    @Override
    protected void run(IContext ctx, int usedTicks) {
        if (usedTicks % 2 != 0)
            return;
        FireSpellProjectile fire = new FireSpellProjectile(ctx.level(), ctx.player().getX() + ctx.player().getLookAngle().x, ctx.player().getEyeY() - 0.25 + ctx.player().getLookAngle().y, ctx.player().getZ() + ctx.player().getLookAngle().z);
        fire.setOwner(ctx.player());
        fire.setDeltaMovement(ctx.player().getLookAngle().scale(1.25));
        fire.getEntityData().set(FireSpellProjectile.DAMAGE, (float) (6 * ctx.stats().fireAffinity()));
        ctx.level().addFreshEntity(fire);
        ctx.playSound(SoundEvents.FIRECHARGE_USE, 0.3f);
    }
}
