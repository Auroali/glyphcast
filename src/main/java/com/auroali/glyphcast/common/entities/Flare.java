package com.auroali.glyphcast.common.entities;

import com.auroali.glyphcast.client.LightTracker;
import com.auroali.glyphcast.common.network.client.ClientPacketHandler;
import com.auroali.glyphcast.common.network.client.SpawnParticlesMessage;
import com.auroali.glyphcast.common.registry.GCEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Flare extends Projectile {
    int lifetime = 0;
    public Flare(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public Flare(Level level, double x, double y, double z) {
        super(GCEntities.FLARE.get(), level);
        setPos(x, y, z);
    }

    @Override
    public void tick() {
        super.tick();

        if(lifetime > 25) {
            explode();
            return;
        }

        spawnParticles();

        move(MoverType.SELF, getDeltaMovement());
        lifetime++;
    }

    public void explode() {
        if(level.isClientSide) {
            return;
        }

        level.broadcastEntityEvent(this, (byte) 17);
        discard();
    }

    private void spawnParticles() {
        if(level.isClientSide) {
            LightTracker.update(this, lifetime > 150 ? 15 : 12);
            double speed = getDeltaMovement().length() / 5;
            ClientPacketHandler.spawnParticles(new SpawnParticlesMessage(ParticleTypes.SOUL_FIRE_FLAME, 0.0, 4, position().add(0, 0.0125, 0),getDeltaMovement(), speed));
            double x = 0.25 * Math.cos(lifetime);
            double y = 0.25 * Math.sin(lifetime);
            double z = 0.25 * -Math.cos(lifetime);
            ClientPacketHandler.spawnParticles(new SpawnParticlesMessage(ParticleTypes.END_ROD, 0.0, 1, position().add(x, y, z), Vec3.ZERO, 0));
        }
    }

    public void handleEntityEvent(byte pId) {
        if(pId != 17 || !level.isClientSide) {
            super.handleEntityEvent(pId);
            return;
        }

        spawnBurst();
    }

    public void spawnBurst() {
        for(int i = 0; i < 200; i++) {
            double xSpeed = 0.25 * random.nextGaussian();
            double ySpeed = 0.15 * random.nextGaussian();
            double zSpeed = 0.25 * random.nextGaussian();
            level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, true, getX(), getY(), getZ(), xSpeed, ySpeed, zSpeed);
            level.addParticle(ParticleTypes.END_ROD, true, getX(), getY(), getZ(), xSpeed, ySpeed, zSpeed);
        }
    }

    @Override
    protected void defineSynchedData() {

    }
}
