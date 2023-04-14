package com.auroali.glyphcast.common.capabilities.chunk;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.network.client.SyncChunkEnergyMessage;
import com.auroali.glyphcast.common.registry.GCNetwork;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraftforge.network.PacketDistributor;

import java.util.Arrays;
import java.util.List;

public class ChunkEnergy implements IChunkEnergy {
    public double[] values;
    public double[] maxValues;
    final Level level;
    final ChunkPos pos;

    public ChunkEnergy(LevelChunk chunk) {
        this.level = chunk.getLevel();
        this.pos = chunk.getPos();
    }

    int posToIndex(int x, int z) {
        return x * 16 + z;
    }

    boolean isOutOfBounds(int y) {
        return y <= level.getMinBuildHeight() || y >= level.getMaxBuildHeight();
    }

    int getDataSize() {
        return 16*16;
    }
    void calculateMaxEnergy() {
        if(level instanceof ServerLevel serverLevel) {
            ProfilerFiller profiler = level.getServer().getProfiler();
            profiler.push("glyphcast chunk energy gen");
            RandomSource source = new LegacyRandomSource(serverLevel.getSeed());
            PerlinSimplexNoise noise = new PerlinSimplexNoise(source, List.of(1));
            values = new double[getDataSize()];
            maxValues = new double[getDataSize()];
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    double noiseV = (2 + noise.getValue(SectionPos.sectionToBlockCoord(pos.x) + x, SectionPos.sectionToBlockCoord(pos.z) + z, true)) / 2;
                    float maxVal = (float) (250 * noiseV);
                    int index = posToIndex(x, z);
                    values[index] = maxVal;
                    maxValues[index] = maxVal;
                }
            }

            profiler.pop();
        }
    }


    @Override
    public CompoundTag serializeNBT() {
        if(failedValidation())
            calculateMaxEnergy();
        CompoundTag tag = new CompoundTag();
        writeValues(tag);
        writeMaxValues(tag);

        return tag;
    }

    private void writeMaxValues(CompoundTag tag) {

        Codec.DOUBLE.listOf().encodeStart(NbtOps.INSTANCE, Arrays.stream(maxValues).boxed().toList())
                .resultOrPartial(GlyphCast.LOGGER::error)
                .ifPresent(t -> tag.put("maxVals", t));
    }

    private void writeValues(CompoundTag tag) {
        Codec.DOUBLE.listOf().encodeStart(NbtOps.INSTANCE, Arrays.stream(values).boxed().toList())
                .resultOrPartial(GlyphCast.LOGGER::error)
                .ifPresent(t -> tag.put("vals", t));
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if(!nbt.contains("maxVals") || !nbt.contains("vals")) {
            calculateMaxEnergy();
            return;
        }
        Codec.DOUBLE.listOf().parse(NbtOps.INSTANCE, nbt.get("maxVals"))
                .resultOrPartial(GlyphCast.LOGGER::error)
                .ifPresent(l -> {
                    values = new double[l.size()];
                    int i = 0;
                    for(Double f : l) {
                        values[i++] = f;
                    }
                });
        Codec.DOUBLE.listOf().parse(NbtOps.INSTANCE, nbt.get("vals"))
                .resultOrPartial(GlyphCast.LOGGER::error)
                .ifPresent(l -> {
                    maxValues = new double[l.size()];
                    int i = 0;
                    for(Double f : l) {
                        maxValues[i++] = f;
                    }
                });
        if (failedValidation()) {
            calculateMaxEnergy();
        }
    }

    boolean failedValidation() {
        return values == null || maxValues == null || values.length != getDataSize() || maxValues.length != getDataSize() || values.length != maxValues.length;
    }

    @Override
    public double getEnergy(BlockPos pos) {
        if (isOutOfBounds(pos.getY()))
            return 0;
        int localX = Math.abs(pos.getX() - SectionPos.blockToSectionCoord(pos.getX()) * 16);
        int localZ = Math.abs(pos.getZ() - SectionPos.blockToSectionCoord(pos.getZ()) * 16);
        int i = posToIndex(localX, localZ);
        return values != null ? values[i] : 0.f;
    }

    @Override
    public void setEnergy(BlockPos pos, double energy) {
        if (isOutOfBounds(pos.getY()))
            return;
        if (values == null || maxValues == null)
            return;
        int localX = Math.abs(pos.getX() - SectionPos.blockToSectionCoord(pos.getX()) * 16);
        int localZ = Math.abs(pos.getZ() - SectionPos.blockToSectionCoord(pos.getZ()) * 16);
        int i = posToIndex(localX, localZ);
        values[i] = Math.min(energy, maxValues[i]);
        syncEnergy();
    }

    @Override
    public void startRechargeCooldown() {
    }

    @Override
    public void tick() {
        if(failedValidation())
            calculateMaxEnergy();

        boolean needsSync = false;
        for(int i = 0; i < getDataSize(); i++) {
            if(values[i] == maxValues[i])
                continue;

            needsSync = true;
            values[i] = Math.min(maxValues[i], values[i] + maxValues[i] / 1200);
        }

        if(needsSync)
            syncEnergy();
    }

    @Override
    public double getMaxEnergy(BlockPos pos) {
        if (isOutOfBounds(pos.getY()))
            return 0;
        int localX = Math.abs(pos.getX() - SectionPos.blockToSectionCoord(pos.getX()) * 16);
        int localZ = Math.abs(pos.getZ() - SectionPos.blockToSectionCoord(pos.getZ()) * 16);
        int i = posToIndex(localX, localZ);
        return maxValues != null ? maxValues[i] : 0.f;
    }

    void syncEnergy() {
        if (!level.isClientSide)
            GCNetwork.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunk(pos.x, pos.z)), new SyncChunkEnergyMessage(pos, values, maxValues));
    }
}
