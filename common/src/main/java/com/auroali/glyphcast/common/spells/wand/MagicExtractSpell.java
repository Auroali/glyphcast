package com.auroali.glyphcast.common.spells.wand;

import com.auroali.glyphcast.common.PlayerHelper;
import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.entities.FractureEntity;
import com.auroali.glyphcast.common.registry.GCItems;
import com.auroali.glyphcast.common.registry.GCParticles;
import com.auroali.glyphcast.common.spells.HoldSpell;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.spells.glyph.Ring;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class MagicExtractSpell extends HoldSpell {
    public MagicExtractSpell() {
        super(new GlyphSequence(Ring.of(Glyph.WAND)));
    }

    @Override
    protected void run(IContext ctx, int usedTicks) {
        Vec3 start = ctx.player().getEyePosition();
        Vec3 end = ctx.player().getEyePosition().add(ctx.player().getLookAngle().scale(PlayerHelper.getReachDistance(ctx.player())));

        FractureEntity fracture = clipFracture(ctx, start, end);
        if (fracture == null || fracture.getEnergy() < 5)
            return;

        double drainedAmount = fracture.drain(16 * 0.05);

        Vec3 fracturePos = new Vec3(fracture.position().x, fracture.position().y, fracture.position().z);
        triggerEvent((byte) 0, PositionedContext.with(ctx, fracturePos));

        if (!ctx.getOtherHandItem().is(GCItems.VIAL.get())) {
            handleRechargePlayer(ctx, drainedAmount);
            return;
        }

        fillVial(ctx, drainedAmount);
    }

    private void fillVial(IContext ctx, double amountToDrain) {
        GCItems.VIAL.get().fill(ctx.getOtherHandItem(), amountToDrain);
    }

    public void handleRechargePlayer(IContext ctx, double amount) {
        SpellUser.get(ctx.player()).ifPresent(user -> {
            user.setEnergy(user.getEnergy() + amount);
        });
    }

    @Override
    public void handleEvent(Byte id, PositionedContext ctx) {
        for (int i = 0; i < 2; i++) {
            Vec3 start = ctx.start().add(ctx.level().random.nextFloat() * 0.25, ctx.level().random.nextFloat() * 0.25, ctx.level().random.nextFloat() * 0.25);
            Vec3 end = ctx.player().getEyePosition().add(0, -0.25, 0);
            Vec3 direction = end.subtract(start).normalize().scale(start.distanceTo(end) / 10);
            ctx.level().addParticle(GCParticles.MAGIC_PULSE.get(), start.x, start.y, start.z, direction.x, direction.y, direction.z);
        }
    }

    private FractureEntity clipFracture(IContext ctx, Vec3 start, Vec3 end) {
        double d0 = Double.MAX_VALUE;
        FractureEntity fracture = null;

        List<FractureEntity> fractures = FractureEntity.getNear(ctx.level(), ctx.player().blockPosition(), PlayerHelper.getReachDistance(ctx.player()));
        for (FractureEntity f : fractures) {
            AABB aabb = new AABB(f.blockPosition()).inflate(0.5);
            Optional<Vec3> pos = aabb.clip(start, end);
            if (pos.isPresent()) {
                double d1 = start.distanceToSqr(pos.get());
                if (d1 < d0) {
                    fracture = f;
                    d0 = d1;
                }
            }
        }
        return fracture;
    }

    @Override
    public double getCost() {
        return 0;
    }
}
