package com.auroali.glyphcast.common.spells.composite;

import com.auroali.glyphcast.common.network.client.SpawnParticlesMessage;
import com.auroali.glyphcast.common.network.GCNetwork;
import com.auroali.glyphcast.common.registry.GCParticles;
import com.auroali.glyphcast.common.spells.HoldSpell;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.spells.glyph.Ring;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ShieldSpell extends HoldSpell {
    public ShieldSpell() {
        super(new GlyphSequence(Ring.of(), Ring.of(Glyph.EARTH, Glyph.EARTH, Glyph.EARTH, Glyph.EARTH)));
    }

    @Override
    protected void run(IContext ctx, int usedTicks) {
        AABB aabb = new AABB(ctx.player().position().x - 0.5, ctx.player().position().y - 0.5, ctx.player().position().z - 0.5, ctx.player().position().x + 0.5, ctx.player().position().y + 0.5, ctx.player().position().z + 0.5)
                .inflate(2.5);
        ctx.level().getEntities(ctx.player(), aabb, e -> true)
                .forEach(e -> {
                    Vec3 pushVec = e.position().subtract(ctx.player().position()).normalize().scale(0.4 + e.getDeltaMovement().length());
                    e.push(pushVec.x, pushVec.y, pushVec.z);
                    GCNetwork.CHANNEL.sendToNear(ctx.level(), e.position(), 16, new SpawnParticlesMessage(GCParticles.MAGIC_AMBIENCE.get(), 0.1, 6, e.position().add(0, e.getEyeHeight() / 2, 0), pushVec.normalize(), pushVec.length()));
                    if (e instanceof ServerPlayer sPlayer) {
                        sPlayer.connection.send(new ClientboundSetEntityMotionPacket(sPlayer));
                    }
                });
        if (usedTicks % 2 == 0)
            triggerEvent((byte) 0, PositionedContext.with(ctx, ctx.player().position()));

        ctx.playSound(SoundEvents.BEACON_AMBIENT, 0.5f);
    }

    @Override
    public void handleEvent(Byte id, PositionedContext ctx) {
        long time = ctx.level().getGameTime();
        double asin = Math.asin(Math.sin(0.5 * time / 10.0));
        double radius = 2 * (Math.sqrt(Math.abs(asin) * (Math.PI - Math.abs(asin))) / (Math.PI / 2));
        if (asin < 0)
            radius = -radius;

        for (int i = 0; i < 15; i++) {
            double x = (2 - Math.abs(radius)) * Math.sin(2 * Math.PI * i / 15.0);
            double z = (2 - Math.abs(radius)) * Math.cos(2 * Math.PI * i / 15.0);
            ctx.level().addParticle(GCParticles.MAGIC_PULSE.get(), ctx.start().x + x, ctx.start().y + 1 + radius, ctx.start().z + z, 0, 0, 0);
        }
    }

    @Override
    public double getCost() {
        return 1;
    }
}
