package com.auroali.glyphcast.common.spells.composite;

import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.spells.glyph.Ring;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class PushSpell extends Spell {
    public PushSpell() {
        super(new GlyphSequence(Ring.of(Glyph.FIRE), Ring.of(Glyph.ICE, Glyph.ICE)));
    }

    @Override
    public void activate(Level level, Player player) {
        if(!canDrainEnergy(player, 25))
            return;
        if(player.isCrouching())
            useOnSelf(player);
        else
            useOnTargetEntity(player);
    }

    private void useOnSelf(Player player) {
        Vec3 pushVec = player.getLookAngle().scale(-1);
        player.push(pushVec.x, pushVec.y, pushVec.z);
        if(player instanceof ServerPlayer sPlayer) {
            sPlayer.connection.send(new ClientboundSetEntityMotionPacket(sPlayer));
        }
    }

    private void useOnTargetEntity(Player player) {
        EntityHitResult result = clipEntityFromPlayer(player, player.getReachDistance(), entity -> entity instanceof LivingEntity && !entity.isRemoved()); //clipEntity(level, player, player.getEyePosition(), player.getLookAngle(), entity -> entity instanceof LivingEntity && !entity.isRemoved(), player.getReachDistance());
        if(result == null)
            return;

        Entity entity = result.getEntity();
        Vec3 pushVec = player.getLookAngle().scale(0.75);
        entity.push(pushVec.x, pushVec.y, pushVec.z);
        if(entity instanceof ServerPlayer sPlayer) {
            sPlayer.connection.send(new ClientboundSetEntityMotionPacket(sPlayer));
        }
    }
}
