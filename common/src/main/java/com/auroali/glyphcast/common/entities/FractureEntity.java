package com.auroali.glyphcast.common.entities;

import com.auroali.glyphcast.common.network.GCNetwork;
import com.auroali.glyphcast.common.registry.GCEntityDataSerializers;
import com.auroali.glyphcast.common.registry.GCParticles;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Optional;

public class FractureEntity extends Entity {
    public static final double MAX_ENERGY = 300.0;
    public static final EntityDataAccessor<Double> ENERGY = SynchedEntityData.defineId(FractureEntity.class, GCEntityDataSerializers.DOUBLE);
    public FractureEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public static List<FractureEntity> getNear(Level level, BlockPos pos, double range) {
        AABB aabb = new AABB(pos).inflate(range);
        ImmutableList.Builder<FractureEntity> fractures = new ImmutableList.Builder<>();
        level.getEntities((Entity) null, aabb, e -> e instanceof FractureEntity).forEach(e -> {
            fractures.add((FractureEntity) e);
        });
        return fractures.build();
    }

    public static double getAverageEnergyAt(Level level, BlockPos pos, double range) {
        List<FractureEntity> fractures = getNear(level, pos, range);
        double amount = 0.0;
        for (FractureEntity entity : fractures) {
            double dist = entity.blockPosition().distToLowCornerSqr(pos.getX(), pos.getY(), pos.getZ());
            amount += Math.min(1, Math.max(1 - dist / (range * range), 0)) * entity.getEnergy();
        }
        return amount / fractures.size();
    }

    public static Optional<FractureEntity> getAt(Level level, BlockPos pos) {
        List<FractureEntity> entities = level.getEntitiesOfClass(FractureEntity.class, new AABB(pos));
        if (entities.size() == 0)
            return Optional.empty();
        return Optional.of(entities.get(0));
    }

    public double drain(double amount) {
        return drain(amount, false);
    }
    public double drain(double amount, boolean simulate) {
        double current = this.entityData.get(ENERGY);
        double drained = current - amount < 0 ? current : amount;
        if(!simulate)
            this.entityData.set(ENERGY, current - drained);
        return drained;
    }

    public double getEnergy() {
        return this.entityData.get(ENERGY);
    }

    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide && level.getGameTime() % 2 == 0)
            level.addParticle(GCParticles.FRACTURE.get(), position().x, position().y, position().z, 0, 0, 0);
        if(this.entityData.get(ENERGY) < MAX_ENERGY) {
            this.entityData.set(ENERGY, this.entityData.get(ENERGY) + 0.05);
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ENERGY, MAX_ENERGY);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        this.entityData.set(ENERGY, compoundTag.getDouble("Energy"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putDouble("Energy", this.entityData.get(ENERGY));
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return GCNetwork.getEntitySpawnPacket(this);
    }
}
