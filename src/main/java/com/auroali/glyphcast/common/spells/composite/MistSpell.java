package com.auroali.glyphcast.common.spells.composite;

import com.auroali.glyphcast.common.network.client.SpawnParticlesMessage;
import com.auroali.glyphcast.common.registry.GCNetwork;
import com.auroali.glyphcast.common.spells.TickingSpell;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.spells.glyph.Ring;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MistSpell extends TickingSpell {
    public MistSpell() {
        super(new GlyphSequence(Ring.of(), Ring.of(Glyph.FIRE, Glyph.ICE)));
    }

    @Override
    public boolean tick(Level level, Player player, int ticks, CompoundTag tag) {
        if(!canDrainEnergy(player, 20))
            return false;

        drainEnergy(player, 20);
        Vec3 originPos = new Vec3(tag.getDouble("PosX"),tag.getDouble("PosY"), tag.getDouble("PosZ"));
        double radius = tag.getDouble("Radius");
        AABB bounds = new AABB(originPos, originPos).inflate(radius);
        if(ticks % 10 == 0) {
            BlockPos.betweenClosedStream(bounds).forEach(pos -> {
                Vec3 particlePos = new Vec3(pos.getX(), pos.getY(), pos.getZ());
                SpawnParticlesMessage msg = new SpawnParticlesMessage(ParticleTypes.CAMPFIRE_COSY_SMOKE, 2, 1, particlePos, Vec3.ZERO, 0.04, 0.07);
                GCNetwork.sendToNear(level, particlePos, 32, msg);
            });
        }
        level.getEntities(player, bounds, e -> e instanceof LivingEntity).forEach(entity -> {
            LivingEntity living = (LivingEntity) entity;
            living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 2, true, false, false));
        });
        return ticks < 600;
    }

    @Override
    public void onActivate(Level level, Player player, CompoundTag tag) {
        tag.putDouble("PosX", player.getX());
        tag.putDouble("PosY", player.getY());
        tag.putDouble("PosZ", player.getZ());
        tag.putDouble("Radius", 2.5);
    }
}
