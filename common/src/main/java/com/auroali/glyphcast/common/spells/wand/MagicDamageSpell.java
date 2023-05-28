package com.auroali.glyphcast.common.spells.wand;

import com.auroali.glyphcast.common.PlayerHelper;
import com.auroali.glyphcast.common.damage.GCDamageSources;
import com.auroali.glyphcast.common.registry.GCParticles;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.spells.glyph.Ring;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MagicDamageSpell extends Spell {
    public MagicDamageSpell() {
        super(new GlyphSequence(Ring.of(Glyph.WAND)));
    }

    @Override
    public double getCost() {
        return 12;
    }

    @Override
    public void activate(IContext ctx) {
        double maxDist = PlayerHelper.getReachDistance(ctx.player()) * 1.5;
        EntityHitResult result = ctx.clipEntityWithCollision(maxDist, e -> !e.isRemoved());
        if (result == null) {
            triggerEvent((byte) 0, PositionedContext.with(ctx, new Vec3(getDist(ctx.player(), maxDist), 0, 0)));
            return;
        }

        result.getEntity().hurt(GCDamageSources.magic(ctx.player()), (float) 9);
        double dist = Math.ceil(ctx.player().getEyePosition().distanceTo(result.getLocation()));
        triggerEvent((byte) 0, PositionedContext.with(ctx, new Vec3(dist, 0, 0)));
    }

    double getDist(Player player, double max) {
        BlockHitResult result = player.level.clip(new ClipContext(player.getEyePosition(), player.getEyePosition().add(player.getLookAngle().scale(max)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        return result.getType() != HitResult.Type.BLOCK ? max : player.getEyePosition().distanceTo(result.getLocation());
    }

    @Override
    public void handleEvent(Byte id, PositionedContext ctx) {
        spawnEffects(ctx, ctx.start().x);
    }

    void spawnEffects(IContext ctx, double maxDist) {
        for (int i = 2; i < (int) maxDist * 2; i++) {
            Vec3 pos = ctx.player().getEyePosition().add(ctx.player().getLookAngle().scale(maxDist * (i / (maxDist * 2))));
            ctx.level().addParticle(GCParticles.MAGIC_PULSE.get(), pos.x, pos.y, pos.z, ctx.player().getLookAngle().x * 0.1, ctx.player().getLookAngle().y * 0.1, ctx.player().getLookAngle().z * 0.1);
        }
    }
}
