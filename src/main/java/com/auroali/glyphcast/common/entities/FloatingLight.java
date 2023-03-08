package com.auroali.glyphcast.common.entities;

import com.auroali.glyphcast.client.LightTracker;
import com.auroali.glyphcast.common.registry.GCEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FloatingLight extends Entity implements IEntityAdditionalSpawnData {

    UUID ownerUUID;
    Entity cachedOwner;

    /**
     * Returns an immutable list of all light entities owned by and currently orbiting the player
     * @param player the owning player
     * @return the list of orbiting lights
     */
    public static List<FloatingLight> getAllFollowing(Player player) {
        return player.level.getEntities(player, player.getBoundingBox().inflate(10),
                e -> e instanceof FloatingLight && ((FloatingLight) e).ownerUUID.equals(player.getUUID()))
                .stream().map(e -> (FloatingLight)e)
                .toList();
    }

    public static List<FloatingLight> getAllFollowing(Player player, ServerLevel level) {
        var it = level.getEntities().getAll().iterator();
        List<FloatingLight> entities = new ArrayList<>();
        while (it.hasNext()) {
            Entity entity = it.next();
            if(entity instanceof FloatingLight light && light.getOwner().getUUID().equals(player.getUUID()))
                entities.add(light);
        }
        return entities;
    }

    public FloatingLight(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public FloatingLight(Level pLevel, double x, double y, double z) {
        super(GCEntities.FLOATING_LIGHT.get(), pLevel);
        setPos(x, y, z);
    }

    @Override
    public void lerpTo(double pX, double pY, double pZ, float pYaw, float pPitch, int pPosRotationIncrements, boolean pTeleport) {
        this.setPos(pX, pY, pZ);
        this.setRot(pYaw, pPitch);
    }

    /**
     * Updates the entity motion clientside, called by packets from the server
     */
    @Override
    public void lerpMotion(double pX, double pY, double pZ) {
        super.lerpMotion(pX, pY, pZ);
    }

    @Override
    public void tick() {
        super.tick();
        if(getOwner() != null) {
            Vec3 target = getOwner().getEyePosition().add(0, 0.0, 0);
            double perc = position().distanceToSqr(target) > 400 ? 1.0f : 0.35f; //Math.max(Math.min(position().distanceTo(target) / 10, 1.0), 0.5f);
            Vec3 pos = position().lerp(target, perc);
            double i = 0.25 * Math.sin(0.05 * level.getGameTime());
            double j = 0.25 * Math.cos(0.05 * level.getGameTime());
            double k = 0.25 * Math.cos(0.05 * level.getGameTime() + Math.toRadians(getOwner().getYRot()));
            pos = pos.add(i, k, j);


            setPos(pos);
        }
        if(level.isClientSide) {
            LightTracker.update(this, 15);
            if(level.getGameTime() % 10 == 0 && random.nextDouble() > 0.5)
                spawnParticles();
        }

        //setPos(position().add(getDeltaMovement()));
    }

    void spawnParticles() {
        AABB bb = this.getBoundingBox().inflate(-0.15f);
        double x = bb.minX + (random.nextDouble() * (bb.maxX - bb.minX));
        double y = bb.minY + (random.nextDouble() * (bb.maxY - bb.minY));
        double z = bb.minZ + (random.nextDouble() * (bb.maxZ - bb.minZ));
        level.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0, 0);
    }
    public void setOwner(Entity entity) {
        this.ownerUUID = entity.getUUID();
        this.cachedOwner = entity;
    }

    public Entity getOwner() {
        if(cachedOwner != null && !cachedOwner.isRemoved())
            return cachedOwner;
        if(level instanceof ServerLevel serverLevel) {
            cachedOwner = serverLevel.getEntity(ownerUUID);
            return cachedOwner;
        }
        return null;
    }
    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        if(pCompound.contains("Owner"))
            ownerUUID = pCompound.getUUID("Owner");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        if(ownerUUID != null)
            pCompound.putUUID("Owner", ownerUUID);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        if(getOwner() != null) {
            buffer.writeInt(getOwner().getId());
        }
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        if(additionalData.isReadable())
            setOwner(level.getEntity(additionalData.readInt()));
    }
}
