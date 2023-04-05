package com.auroali.glyphcast.common.capabilities.chunk;

import com.auroali.glyphcast.common.registry.GCCapabilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.WorldgenRandom;

public class ChunkEnergy implements IChunkEnergy {
    double energy = 1000;
    double maxEnergy = 0;
    double rechargeCooldown = 0;
    int rechargeRate = 0;

    final Level level;
    final ChunkPos pos;

    public ChunkEnergy(LevelChunk chunk) {
        this.level = chunk.getLevel();
        this.pos = chunk.getPos();
        calculateMaxEnergy();
    }

    void calculateMaxEnergy() {
        if(level instanceof ServerLevel serverLevel) {
            RandomSource source = WorldgenRandom.seedSlimeChunk(pos.x, pos.z, serverLevel.getSeed(), 907234411L);
            maxEnergy = Math.min(250 * source.nextDouble(), 50);
            energy = maxEnergy;
            rechargeRate = source.nextInt(1, 3);
        }
    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("Energy", energy);
        tag.putDouble("MaxEnergy", maxEnergy);
        tag.putDouble("RechargeCooldown", rechargeCooldown);
        tag.putInt("RechargeRate", rechargeRate);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        energy = nbt.getDouble("Energy");
        rechargeCooldown = nbt.getDouble("RechargeCooldown");
        maxEnergy = nbt.getDouble("MaxEnergy");
        rechargeRate = nbt.getInt("RechargeRate");
        // We have an invalid value, so we should recalculate the max energy
        if(maxEnergy <= 0 || rechargeRate <= 0)
            calculateMaxEnergy();
    }

    @Override
    public double getEnergy() {
        return energy;
    }

    @Override
    public void setEnergy(double energy) {
        this.energy = Math.min(energy, maxEnergy);
    }

    @Override
    public void startRechargeCooldown() {
        rechargeCooldown = 80;
    }

    @Override
    public void tick() {
        for(int x = -1; x <= 1; x++) {
            for(int z = -1; z <= 1; z++) {
                tryRechargeNearby(pos.x + x, pos.z + z);
            }
        }


        if(energy >= maxEnergy) {
            energy = maxEnergy;
            return;
        }

        if(rechargeCooldown > 0) {
            rechargeCooldown--;
            return;
        }

        energy += rechargeRate / 20.0;
    }

    @Override
    public double getMaxEnergy() {
        return maxEnergy;
    }

    public void tryRechargeNearby(int x, int z) {
        if(!level.hasChunk(x, z) || (pos.x == x && pos.z == z))
            return;

        level.getChunk(x, z).getCapability(GCCapabilities.CHUNK_ENERGY).ifPresent(energy -> {
            if(energy.getEnergy() >= getEnergy() || energy.getEnergy() >= energy.getMaxEnergy())
                return;

            double diff = Math.min(getEnergy() - energy.getEnergy(), energy.getMaxEnergy() / 2);
            double cooldown = energy instanceof ChunkEnergy e ? e.rechargeCooldown : 0;
            energy.setEnergy(energy.getEnergy() + diff / ((1 - cooldown / 80) * 720 + 360));
            this.rechargeCooldown = 1;
        });
    }
}
