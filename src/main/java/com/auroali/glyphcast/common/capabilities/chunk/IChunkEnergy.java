package com.auroali.glyphcast.common.capabilities.chunk;

import com.auroali.glyphcast.common.registry.GCCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;

public interface IChunkEnergy extends INBTSerializable<CompoundTag> {
    double getEnergy();
    void setEnergy(double energy);
    void startRechargeCooldown();
    void tick();
    double getMaxEnergy();

    static double getEnergyAt(Level level, ChunkPos pos) {
        if(!level.hasChunk(pos.x, pos.z))
            return 0;
        return level.getChunk(pos.x, pos.z).getCapability(GCCapabilities.CHUNK_ENERGY).map(IChunkEnergy::getEnergy).orElse(0.0);
    }

    static double getMaxEnergyAt(Level level, ChunkPos pos) {
        if(!level.hasChunk(pos.x, pos.z))
            return 0;
        return level.getChunk(pos.x, pos.z).getCapability(GCCapabilities.CHUNK_ENERGY).map(IChunkEnergy::getMaxEnergy).orElse(0.0);
    }

    static double getEnergyAt(Level level, BlockPos pos) {
        return getEnergyAt(level, new ChunkPos(pos));
    }

    static double getMaxEnergyAt(Level level, BlockPos pos) {
        return getMaxEnergyAt(level, new ChunkPos(pos));
    }

    /**
     * Drains the specified amount of energy from the chunk at 'pos'
     * @param level the level the chunk is in
     * @param pos the position of the chunk
     * @param amount the amount to drain
     * @param simulate if we should simulate draining instead of actually draining
     * @return the amount of energy successfully drained from the chunk
     */
    static double drainAt(Level level, ChunkPos pos, double amount, boolean simulate) {
        if(!level.hasChunk(pos.x, pos.z))
            return 0;
        return level.getChunk(pos.x, pos.z).getCapability(GCCapabilities.CHUNK_ENERGY).map(energy -> {
            double finalAmount = energy.getEnergy() - amount;
            double drainAmount = amount;
            if(finalAmount < 0) {
                finalAmount = 0;
                drainAmount = energy.getEnergy();
            }
            if(!simulate) {
                energy.setEnergy(finalAmount);
                energy.startRechargeCooldown();
            }
            return drainAmount;
        }).orElse(0.0);
    }

    /**
     * Drains the specified amount of energy from the chunk at 'pos'
     * @param level the level the chunk is in
     * @param pos the position of the chunk
     * @param amount the amount to drain
     * @param simulate if we should simulate draining instead of actually draining
     * @return the amount of energy successfully drained from the chunk
     * @see com.auroali.glyphcast.common.capabilities.chunk.IChunkEnergy#drainAt(Level, ChunkPos, double, boolean)
     */
    static double drainAt(Level level, BlockPos pos, double amount, boolean simulate) {
        return drainAt(level, new ChunkPos(pos), amount, simulate);
    }
}
