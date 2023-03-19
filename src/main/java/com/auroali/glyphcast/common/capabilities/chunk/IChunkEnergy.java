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

    void tick();

    static double drainAt(Level level, ChunkPos pos, double amount, boolean simulate) {
        return level.getChunk(pos.x, pos.z).getCapability(GCCapabilities.CHUNK_ENERGY).map(energy -> {
            double finalAmount = energy.getEnergy() - amount;
            double drainAmount = amount;
            if(finalAmount < 0) {
                finalAmount = 0;
                drainAmount = energy.getEnergy();
            }
            if(!simulate)
                energy.setEnergy(finalAmount);
            return drainAmount;
        }).orElse(0.0);
    }

    static double drainAt(Level level, BlockPos pos, double amount, boolean simulate) {
        return drainAt(level, new ChunkPos(pos), amount, simulate);
    }
}
