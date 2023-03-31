package com.auroali.glyphcast.common.spells.composite;

import com.auroali.glyphcast.common.entities.Flare;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.spells.glyph.Ring;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class FlareSpell extends Spell {
    public FlareSpell() {
        super(new GlyphSequence(Ring.of(Glyph.FIRE), Ring.of(Glyph.FIRE, Glyph.FIRE, Glyph.LIGHT, Glyph.LIGHT)));
    }

    @Override
    public double getCost() {
        return 20;
    }

    @Override
    public void activate(IContext ctx) {
        Flare flare = new Flare(ctx.level(), ctx.player().getX() + ctx.player().getLookAngle().x, ctx.player().getEyeY() - 0.25 + ctx.player().getLookAngle().y, ctx.player().getZ() + ctx.player().getLookAngle().z);
        flare.setDeltaMovement(ctx.player().getLookAngle().scale(1.25));
        ctx.level().addFreshEntity(flare);
        ctx.level().playSound(null, ctx.player(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0f, 1.0f);
    }
}
