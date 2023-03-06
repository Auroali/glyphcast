package com.auroali.glyphcast.common.spells;

import com.auroali.glyphcast.common.entities.FireEntity;
import com.auroali.glyphcast.common.entities.LightEntity;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class LightSpell extends Spell {
    public LightSpell() {
        super(new GlyphSequence(Glyph.LIGHT));
    }

    @Override
    public void activate(Level level, Player player) {
        AABB bounds = player.getBoundingBox().inflate(10.0f);

        var entities = level.getEntities(player, bounds, e -> e instanceof LightEntity && ((LightEntity) e).getOwner() == player);
        if(entities.size() > 0) {
            entities.forEach(e -> e.remove(Entity.RemovalReason.KILLED));
            return;
        }

        LightEntity entity = new LightEntity(level, player.getX(), player.getEyeY(), player.getZ());
        entity.setOwner(player);
        level.addFreshEntity(entity);
    }
}
