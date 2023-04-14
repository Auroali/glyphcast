package com.auroali.glyphcast.common.capabilities.chunk;

import com.auroali.glyphcast.common.registry.GCCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;

public interface IChunkEnergy extends INBTSerializable<CompoundTag> {
    double getEnergy(BlockPos pos);
    void setEnergy(BlockPos pos, double energy);
    void tick();
    double getMaxEnergy(BlockPos pos);

    static double getEnergyAt(Level level, BlockPos pos) {
        return level.getChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()))
                .getCapability(GCCapabilities.CHUNK_ENERGY)
                .map(cap -> cap.getEnergy(pos))
                .orElse(0.0);
    }

    static double getMaxEnergyAt(Level level, BlockPos pos) {
        return level.getChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()))
                .getCapability(GCCapabilities.CHUNK_ENERGY)
                .map(cap -> cap.getMaxEnergy(pos))
                .orElse(0.0);
    }

    /**
     * Drains the specified amount of energy from the chunk at 'pos'
     * @param level the level the chunk is in
     * @param pos the position of the chunk
     * @param amount the amount to drain
     * @param simulate if we should simulate draining instead of actually draining
     * @return the amount of energy successfully drained from the chunk
     */
    static double drainAt(Level level, BlockPos pos, double amount, boolean simulate) {
        if (!level.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ())))
            return 0.0;
        double amnt = level.getChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()))
                .getCapability(GCCapabilities.CHUNK_ENERGY)
                .map(e ->
                   e.getEnergy(pos) - amount >= 0 ? amount : e.getEnergy(pos)
                )
                .orElse(0.0);
        if(simulate)
            return amnt;
        int range = 15;
        for(int x = -range; x < range; x++) {
            for (int z = -range; z < range; z++) {
                final double dist = 1.0 - ((x * x) + (z * z)) / 450.0;
                final BlockPos transformed = new BlockPos(pos.getX() + x, pos.getY(), pos.getZ() + z);
                if (!level.hasChunk(SectionPos.blockToSectionCoord(transformed.getX()), SectionPos.blockToSectionCoord(transformed.getZ())))
                    continue;
                level.getChunk(SectionPos.blockToSectionCoord(pos.getX() + x), SectionPos.blockToSectionCoord(pos.getZ() + z))
                        .getCapability(GCCapabilities.CHUNK_ENERGY)
                        .ifPresent(e ->
                                e.setEnergy(transformed, (float) Math.max(e.getEnergy(transformed) - (amnt * dist), 0))
                        );
            }
        }
        return amnt;
    }
}
