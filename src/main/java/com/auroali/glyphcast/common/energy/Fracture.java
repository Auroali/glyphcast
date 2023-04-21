package com.auroali.glyphcast.common.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

public class Fracture {
    protected final ChunkPos chunkPos;
    protected final BlockPos blockPos;

    public Fracture(int x, int y, int z) {
        this.blockPos = new BlockPos(x, y, z);
        this.chunkPos = new ChunkPos(blockPos);
    }

    public BlockPos position() {
        return blockPos;
    }

    public ChunkPos chunkPosition() {
        return chunkPos;
    }
}
