package com.auroali.glyphcast.common.spells.composite;

import com.auroali.glyphcast.common.PlayerHelper;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.spells.glyph.Ring;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class PushSpell extends Spell {
    public PushSpell() {
        super(new GlyphSequence(Ring.of(Glyph.FIRE), Ring.of(Glyph.ICE)));
    }

    @Override
    public double getCost() {
        return 4;
    }

    @Override
    public void activate(Spell.IContext ctx) {
        EntityHitResult result = ctx.clipEntityWithCollision(PlayerHelper.getReachDistance(ctx.player()), entity -> entity instanceof LivingEntity && !entity.isRemoved());
        if (result == null)
            return;

        Entity entity = result.getEntity();
        Vec3 pushVec = ctx.player().getLookAngle().add(0, 0.15, 0).normalize().scale(1.2);
        entity.push(pushVec.x, pushVec.y, pushVec.z);
        triggerEvent((byte)0, PositionedContext.withRange(ctx, entity.getBoundingBox().getCenter(), pushVec));
        if (entity instanceof ServerPlayer sPlayer) {
            sPlayer.connection.send(new ClientboundSetEntityMotionPacket(sPlayer));
        }
    }

    @Override
    public void handleEvent(Byte id, PositionedContext ctx) {
        Vec3 entityPos = ctx.start();
        Vec3 direction = ctx.end().normalize();

        for(int i = 0; i < 10; i++) {
            double xSpeed = direction.x * 0.07 + (ctx.level().random.nextGaussian() * 0.07);
            double ySpeed = direction.y * 0.07 + (ctx.level().random.nextGaussian() * 0.07);
            double zSpeed = direction.z * 0.07 + (ctx.level().random.nextGaussian() * 0.07);
            ctx.level().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, entityPos.x, entityPos.y, entityPos.z, xSpeed, ySpeed, zSpeed);
        }
    }
}
