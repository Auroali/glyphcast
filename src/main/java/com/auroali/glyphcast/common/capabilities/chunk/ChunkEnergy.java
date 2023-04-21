package com.auroali.glyphcast.common.capabilities.chunk;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.energy.Fracture;
import com.auroali.glyphcast.common.network.client.ClientPacketHandler;
import com.auroali.glyphcast.common.network.client.SpawnParticlesMessage;
import com.auroali.glyphcast.common.network.client.SyncChunkEnergyMessage;
import com.auroali.glyphcast.common.registry.GCNetwork;
import com.auroali.glyphcast.common.registry.GCParticles;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChunkEnergy implements IChunkEnergy {
    final Level level;
    final ChunkPos pos;
    public double[] values;
    public double[] maxValues;
    public List<Fracture> fractures;
    public boolean needsSync;

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
        return 16 * 16;
    }

    void calculateMaxEnergy() {
        if (level instanceof ServerLevel serverLevel) {
            ProfilerFiller profiler = level.getServer().getProfiler();
            profiler.push("glyphcast chunk energy gen");
            RandomSource source = new LegacyRandomSource(serverLevel.getSeed());
            PerlinSimplexNoise noise = new PerlinSimplexNoise(source, List.of(1));
            values = new double[getDataSize()];
            maxValues = new double[getDataSize()];
            fractures = new ArrayList<>();

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    double noiseV = (2 + noise.getValue((SectionPos.sectionToBlockCoord(pos.x) + x) / 16.0, (SectionPos.sectionToBlockCoord(pos.z) + z) / 16.0, true)) / 2;
                    double maxVal = Math.max(115, 250.0 * noiseV);
                    int index = posToIndex(x, z);
                    values[index] = maxVal;
                    maxValues[index] = maxVal;
                    if (maxVal > 350) {
                        profiler.push("glyphcast fracture gen");
                        tryGenerateFracture(serverLevel, x, z);
                        profiler.pop();
                    }
                }
            }

            profiler.pop();
        }
    }

    void tryGenerateFracture(ServerLevel level, int x, int z) {

        int worldX = SectionPos.sectionToBlockCoord(pos.x) + x;
        int worldZ = SectionPos.sectionToBlockCoord(pos.z) + z;
        RandomSource source = new LegacyRandomSource((67234L * worldX) ^ worldX + (23514L * worldZ) ^ worldZ + level.getSeed() ^ 12342L);
        if (source.nextInt(128) != 0)
            return;

        GlyphCast.LOGGER.debug("Generated fracture at X: {} Z: {}", SectionPos.sectionToBlockCoord(pos.x) + x, SectionPos.sectionToBlockCoord(pos.z) + z);
        fractures.add(new Fracture(worldX, 72, worldZ));
        maxValues[posToIndex(x, z)] = maxValues[posToIndex(x, z)] * 1.25;
        values[posToIndex(x, z)] = maxValues[posToIndex(x, z)];
    }


    @Override
    public CompoundTag serializeNBT() {
        if (failedValidation())
            calculateMaxEnergy();
        CompoundTag tag = new CompoundTag();
        writeValues(tag);
        writeMaxValues(tag);

        BlockPos.CODEC.listOf().encodeStart(NbtOps.INSTANCE, fractures.stream().map(Fracture::position).toList())
                .resultOrPartial(GlyphCast.LOGGER::error)
                .ifPresent(t -> tag.put("Fractures", t));
        return tag;
    }

    private void writeMaxValues(CompoundTag tag) {
        Codec.DOUBLE.listOf().encodeStart(NbtOps.INSTANCE, Arrays.stream(maxValues).boxed().toList())
                .resultOrPartial(GlyphCast.LOGGER::error)
                .ifPresent(t -> tag.put("MaxValues", t));
    }

    private void writeValues(CompoundTag tag) {
        Codec.DOUBLE.listOf().encodeStart(NbtOps.INSTANCE, Arrays.stream(values).boxed().toList())
                .resultOrPartial(GlyphCast.LOGGER::error)
                .ifPresent(t -> tag.put("Values", t));
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (!nbt.contains("MaxValues") || !nbt.contains("Values") || !nbt.contains("Fractures")) {
            calculateMaxEnergy();
            return;
        }
        Codec.DOUBLE.listOf().parse(NbtOps.INSTANCE, nbt.get("MaxValues"))
                .resultOrPartial(GlyphCast.LOGGER::error)
                .ifPresent(l -> {
                    values = new double[l.size()];
                    int i = 0;
                    for (Double f : l) {
                        values[i++] = f;
                    }
                });
        Codec.DOUBLE.listOf().parse(NbtOps.INSTANCE, nbt.get("Values"))
                .resultOrPartial(GlyphCast.LOGGER::error)
                .ifPresent(l -> {
                    maxValues = new double[l.size()];
                    int i = 0;
                    for (Double f : l) {
                        maxValues[i++] = f;
                    }
                });
        BlockPos.CODEC.listOf().parse(NbtOps.INSTANCE, nbt.get("Fractures"))
                .resultOrPartial(GlyphCast.LOGGER::error)
                .ifPresent(l -> {
                    fractures = new ArrayList<>();
                    l.forEach(b -> fractures.add(new Fracture(b.getX(), b.getY(), b.getZ())));
                });
        if (failedValidation()) {
            calculateMaxEnergy();
        }
    }

    public boolean failedValidation() {
        return fractures == null || values == null || maxValues == null || values.length != getDataSize() || maxValues.length != getDataSize() || values.length != maxValues.length;

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

        if (values[i] == Math.min(energy, maxValues[i]))
            return;

        values[i] = Math.min(energy, maxValues[i]);
        needsSync = true;
    }

    @Override
    public void tick() {
        if (failedValidation()) {
            calculateMaxEnergy();
            return;
        }

        if (level.isClientSide) {
            spawnFractureParticles();
            return;
        }

        for (int i = 0; i < getDataSize(); i++) {
            if (values[i] == maxValues[i])
                continue;

            needsSync = true;
            values[i] = Math.min(maxValues[i], values[i] + maxValues[i] / 1200);
        }

        if (needsSync && level.getGameTime() % 15 == 0)
            syncEnergy();
    }

    public void spawnFractureParticles() {
        fractures.forEach(f -> {
            if (level.random.nextInt(15) != 0)
                return;
            ClientPacketHandler.spawnParticles(
                    new SpawnParticlesMessage(GCParticles.MAGIC_AMBIENCE.get(), 1.0, level.random.nextInt(1, 3), new Vec3(f.position().getX() + 0.5, f.position().getY() + 0.5, f.position().getZ() + 0.5), Vec3.ZERO, 0.5, 1.2)
            );
        });
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

    @Override
    public List<Fracture> getFractures() {
        return Collections.unmodifiableList(fractures);
    }

    void syncEnergy() {
        if (!level.isClientSide)
            GCNetwork.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunk(pos.x, pos.z)), new SyncChunkEnergyMessage(pos, values, maxValues, fractures));
    }
}
