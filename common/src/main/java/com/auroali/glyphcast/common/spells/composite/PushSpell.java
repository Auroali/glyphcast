package com.auroali.glyphcast.common.spells.composite;

import com.auroali.glyphcast.common.network.client.SpawnParticlesMessage;
import com.auroali.glyphcast.common.network.GCNetwork;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.spells.glyph.Ring;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class PushSpell extends Spell {
    public PushSpell() {
        super(new GlyphSequence(Ring.of(Glyph.FIRE), Ring.of(Glyph.ICE, Glyph.ICE)));
    }

    @Override
    public double getCost() {
        return 10;
    }

    @Override
    public void activate(IContext ctx) {
        SpawnParticlesMessage msg = new SpawnParticlesMessage(ParticleTypes.CAMPFIRE_COSY_SMOKE, 0.05, 15, ctx.player().getEyePosition().add(ctx.player().getLookAngle().scale(0.5)), ctx.player().getLookAngle(), 0.15);
        GCNetwork.CHANNEL.sendToNear(ctx.level(), ctx.player().getEyePosition(), 16, msg);
        if (ctx.player().isCrouching())
            useOnSelf(ctx.player());
        else
            useOnTargetEntity(ctx.player());
    }

    private void useOnSelf(Player player) {
        Vec3 pushVec = player.getLookAngle().scale(-1);
        player.push(pushVec.x, pushVec.y, pushVec.z);
        if (player instanceof ServerPlayer sPlayer) {
            sPlayer.connection.send(new ClientboundSetEntityMotionPacket(sPlayer));
        }
    }

    private void useOnTargetEntity(Player player) {
        EntityHitResult result = clipEntityFromPlayer(player, 4.0f, entity -> entity instanceof LivingEntity && !entity.isRemoved());
        if (result == null)
            return;

        Entity entity = result.getEntity();
        Vec3 pushVec = player.getLookAngle().add(0, 0.15, 0).normalize().scale(1.2);
        entity.push(pushVec.x, pushVec.y, pushVec.z);
        if (entity instanceof ServerPlayer sPlayer) {
            sPlayer.connection.send(new ClientboundSetEntityMotionPacket(sPlayer));
        }
    }
}
