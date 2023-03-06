package com.auroali.glyphcast.common.entities;

import com.auroali.glyphcast.client.LightTracker;
import com.auroali.glyphcast.common.registry.GCEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

import java.util.UUID;

public class LightEntity extends Entity implements IEntityAdditionalSpawnData {

    UUID ownerUUID;
    Entity cachedPlayer;

    public LightEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public LightEntity(Level pLevel, double x, double y, double z) {
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
            double perc = 0.35f; //Math.max(Math.min(position().distanceTo(target) / 10, 1.0), 0.5f);
            Vec3 pos = position().lerp(target, perc);
            double i = 0.25 * Math.sin(0.05 * level.getGameTime());
            double j = 0.25 * Math.cos(0.05 * level.getGameTime());
            double k = 0.25 * Math.cos(0.05 * level.getGameTime() + Math.toRadians(getOwner().getYRot()));
            pos = pos.add(i, k, j);


            setPos(pos);
        }
        if(level.isClientSide) {
            LightTracker.update(this);
        }

        //setPos(position().add(getDeltaMovement()));
    }

    public void setOwner(Entity entity) {
        this.ownerUUID = entity.getUUID();
        this.cachedPlayer = entity;
    }

    public Entity getOwner() {
        if(cachedPlayer != null && !cachedPlayer.isRemoved())
            return cachedPlayer;
        if(level instanceof ServerLevel serverLevel) {
            cachedPlayer = (Player) serverLevel.getEntity(ownerUUID);
            return cachedPlayer;
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
