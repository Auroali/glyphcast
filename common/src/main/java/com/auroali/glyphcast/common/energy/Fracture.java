package com.auroali.glyphcast.common.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

public class Fracture {
    protected final ChunkPos chunkPos;
    protected final BlockPos blockPos;
    protected final double maxEnergy;
    protected double energy;

    public Fracture(int x, int y, int z, double energy, double maxEnergy) {
        this.blockPos = new BlockPos(x, y, z);
        this.chunkPos = new ChunkPos(blockPos);
        this.energy = energy;
        this.maxEnergy = maxEnergy;
    }

    public double energy() {
        return energy;
    }

    public double maxEnergy() {
        return maxEnergy;
    }

    public void setEnergy(double energy) {
        this.energy = Math.max(0, Math.min(energy, maxEnergy));
    }

    public BlockPos position() {
        return blockPos;
    }

    public double drain(double amount, boolean simulate) {
        if (energy - amount < 0)
            amount = energy;

        if (!simulate)
            this.energy -= amount;
        return amount;
    }

    public ChunkPos chunkPosition() {
        return chunkPos;
    }
}
