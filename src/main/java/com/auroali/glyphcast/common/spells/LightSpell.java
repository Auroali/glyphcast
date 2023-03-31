package com.auroali.glyphcast.common.spells;

import com.auroali.glyphcast.common.entities.FloatingLight;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.spells.glyph.Ring;
import net.minecraft.world.entity.Entity;

public class LightSpell extends Spell {
    public LightSpell() {
        super(new GlyphSequence(Ring.of(Glyph.LIGHT)));
    }

    @Override
    public double getCost() {
        return 5;
    }

    @Override
    public void activate(IContext ctx) {
        var entities = FloatingLight.getAllFollowing(ctx.player());
        if(entities.size() > 0) {
            entities.forEach(e -> e.remove(Entity.RemovalReason.KILLED));
            return;
        }

        FloatingLight entity = new FloatingLight(ctx.level(), ctx.player().getX(), ctx.player().getEyeY(), ctx.player().getZ());
        entity.setOwner(ctx.player());
        entity.getEntityData().set(FloatingLight.BRIGHTNESS, (int) Math.max(8, Math.min(15, 15 * ctx.stats().lightAffinity())));
        ctx.level().addFreshEntity(entity);
    }
}
