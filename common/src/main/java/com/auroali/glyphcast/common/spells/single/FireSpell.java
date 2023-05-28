package com.auroali.glyphcast.common.spells.single;

import com.auroali.glyphcast.common.entities.FireSpellProjectile;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.spells.glyph.Ring;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;

public class FireSpell extends Spell {
    public FireSpell() {
        super(new GlyphSequence(Ring.of(Glyph.FIRE)));
    }

    @Override
    public double getCost() {
        return 2;
    }

    @Override
    public void activate(IContext ctx) {
        Vec3 startPos = ctx.player().getEyePosition().add(ctx.player().getLookAngle().scale(0.1));
        FireSpellProjectile projectile = new FireSpellProjectile(ctx.level(), startPos.x, startPos.y, startPos.z);
        projectile.setDeltaMovement(ctx.player().getLookAngle().add(ctx.player().getDeltaMovement()));
        ctx.level().addFreshEntity(projectile);
        ctx.playSound(SoundEvents.FLINTANDSTEEL_USE, 1.0f);
    }
}
