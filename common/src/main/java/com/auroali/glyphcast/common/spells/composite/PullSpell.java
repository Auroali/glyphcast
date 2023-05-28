package com.auroali.glyphcast.common.spells.composite;

import com.auroali.glyphcast.common.PlayerHelper;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.spells.glyph.Ring;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class PullSpell extends Spell {
    public PullSpell() {
        super(new GlyphSequence(Ring.of(Glyph.ICE), Ring.of(Glyph.FIRE)));
    }

    @Override
    public double getCost() {
        return 4;
    }

    @Override
    public void activate(IContext ctx) {
        EntityHitResult result = ctx.clipEntityWithCollision(PlayerHelper.getReachDistance(ctx.player()) * 2, e -> !e.isRemoved());
        if(result == null)
            return;

        Vec3 pos = ctx.player().getEyePosition().add(ctx.player().getLookAngle()
                .scale(2.5)
                .scale(result.getEntity().getBoundingBox().getSize() / 2)
                .add(0, -0.25, 0)
        );
        triggerEvent((byte)0, PositionedContext.withRange(ctx, result.getEntity().position(), pos));

        if(result.getEntity() instanceof ServerPlayer player) {
            player.teleportTo((ServerLevel) ctx.level(), pos.x, pos.y, pos.z, player.getYRot(), player.getXRot());
            return;
        }

        result.getEntity().moveTo(pos);

        if (result.getEntity() instanceof LivingEntity entity) {
            entity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 13, 0, true, true));
        }
    }

    @Override
    public void handleEvent(Byte id, PositionedContext ctx) {
        Vec3 startPos = ctx.start();
        Vec3 endPos = ctx.end();
        Vec3 dir = endPos.subtract(startPos);
        double dist = startPos.distanceToSqr(endPos);
        int distBlocks = (int) dist;
        for(int i = 0; i < distBlocks; i++) {
            ctx.level().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    startPos.x + dir.x *  (i / dist),
                    startPos.y + dir.y *  (i / dist),
                    startPos.z + dir.z *  (i / dist),
                    0,
                    0,
                    0
            );
        }
    }
}
