package com.auroali.glyphcast.common.capabilities.chunk;

import com.auroali.glyphcast.common.registry.GCCapabilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public class ChunkEnergy implements IChunkEnergy {
    double energy = 1000;

    final Level level;
    final ChunkPos pos;

    public ChunkEnergy(LevelChunk chunk) {
        this.level = chunk.getLevel();
        this.pos = chunk.getPos();
    }
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("Energy", energy);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        energy = nbt.getDouble("Energy");
    }

    @Override
    public double getEnergy() {
        return energy;
    }

    @Override
    public void setEnergy(double energy) {
        this.energy = energy;
    }

    @Override
    public void tick() {
        for(int x = -1; x <= 1; x++) {
            for(int z = -1; z <= 1; z++) {
                tryRechargeNearby(pos.x + x, pos.z + z);
            }
        }

        energy += 0.05;
    }

    public void tryRechargeNearby(int x, int z) {
        if(!level.hasChunk(x, z) || (pos.x == x && pos.z == z))
            return;

        level.getChunk(x, z).getCapability(GCCapabilities.CHUNK_ENERGY).ifPresent(energy -> {
            if(energy.getEnergy() >= getEnergy())
                return;

            double diff = getEnergy() - energy.getEnergy();
            energy.setEnergy(energy.getEnergy() + diff / 4);
        });
    }
}
