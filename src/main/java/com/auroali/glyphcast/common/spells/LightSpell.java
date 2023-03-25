package com.auroali.glyphcast.common.spells;

import com.auroali.glyphcast.common.entities.FloatingLight;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.spells.glyph.Ring;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class LightSpell extends Spell {
    public LightSpell() {
        super(new GlyphSequence(Ring.of(Glyph.LIGHT)));
    }

    @Override
    public double getCost() {
        return 5;
    }

    @Override
    public void activate(Level level, Player player, SpellStats stats) {
        AABB bounds = player.getBoundingBox().inflate(10.0f);

        var entities = level.getEntities(player, bounds, e -> e instanceof FloatingLight && ((FloatingLight) e).getOwner() == player);
        if(entities.size() > 0) {
            entities.forEach(e -> e.remove(Entity.RemovalReason.KILLED));
            return;
        }

        FloatingLight entity = new FloatingLight(level, player.getX(), player.getEyeY(), player.getZ());
        entity.setOwner(player);
        entity.getEntityData().set(FloatingLight.BRIGHTNESS, (int) Math.max(8, Math.min(15, 15 * stats.lightAffinity())));
        level.addFreshEntity(entity);
    }
}
