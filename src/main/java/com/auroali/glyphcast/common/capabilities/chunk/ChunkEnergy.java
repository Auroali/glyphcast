package com.auroali.glyphcast.common.capabilities.chunk;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.PlayerHelper;
import com.auroali.glyphcast.common.energy.Fracture;
import com.auroali.glyphcast.common.network.client.ClientPacketHandler;
import com.auroali.glyphcast.common.network.client.SpawnParticlesMessage;
import com.auroali.glyphcast.common.network.client.SyncChunkEnergyMessage;
import com.auroali.glyphcast.common.registry.GCNetwork;
import com.auroali.glyphcast.common.registry.GCParticles;
import com.auroali.glyphcast.common.registry.tags.GCItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChunkEnergy implements IChunkEnergy {
    final Level level;
    final ChunkPos pos;
    public List<Fracture> fractures;
    public boolean needsSync;
    public long lastTickTime;

    public ChunkEnergy(LevelChunk chunk) {
        this.level = chunk.getLevel();
        this.pos = chunk.getPos();
    }

    void calculateMaxEnergy() {
        if (level instanceof ServerLevel serverLevel) {
            ProfilerFiller profiler = level.getServer().getProfiler();
            profiler.push("glyphcast chunk energy gen");
            RandomSource source = new LegacyRandomSource(serverLevel.getSeed());
            PerlinSimplexNoise noise = new PerlinSimplexNoise(source, List.of(1));
            fractures = new ArrayList<>();

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    double noiseV = (2 + noise.getValue((SectionPos.sectionToBlockCoord(pos.x) + x) / 16.0, (SectionPos.sectionToBlockCoord(pos.z) + z) / 16.0, true)) / 2;
                    double maxVal = Math.max(115, 250.0 * noiseV);
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

        double fractureEnergy = Math.max(250, 350 * source.nextDouble());

        GlyphCast.LOGGER.debug("Generated fracture at X: {} Z: {}", SectionPos.sectionToBlockCoord(pos.x) + x, SectionPos.sectionToBlockCoord(pos.z) + z);
        fractures.add(new Fracture(
                worldX,
                72,
                worldZ,
                fractureEnergy,
                fractureEnergy
        ));
    }


    @Override
    public CompoundTag serializeNBT() {
        if (failedValidation())
            calculateMaxEnergy();
        CompoundTag tag = new CompoundTag();

        ListTag fracturesList = new ListTag();

        fractures.forEach(f -> {
            CompoundTag nbt = new CompoundTag();
            nbt.putDouble("Energy", f.energy());
            nbt.putDouble("MaxEnergy", f.maxEnergy());
            nbt.put("Pos", NbtUtils.writeBlockPos(f.position()));
            fracturesList.add(nbt);
        });

        tag.put("Fractures", fracturesList);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (!nbt.contains("Fractures")) {
            calculateMaxEnergy();
            return;
        }

        fractures = new ArrayList<>();

        ListTag tag = nbt.getList("Fractures", Tag.TAG_COMPOUND);
        for (int i = 0; i < tag.size(); i++) {
            CompoundTag fractureNbt = tag.getCompound(i);
            BlockPos pos = NbtUtils.readBlockPos(fractureNbt.getCompound("Pos"));
            double energy = fractureNbt.getDouble("Energy");
            double maxEnergy = fractureNbt.getDouble("MaxEnergy");

            fractures.add(new Fracture(
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    energy,
                    maxEnergy
            ));
        }

        needsSync = true;
    }

    public boolean failedValidation() {
        return fractures == null;
    }

    @Override
    public void tick() {
        if (lastTickTime == level.getGameTime())
            return;

        lastTickTime = level.getGameTime();
        if (failedValidation()) {
            calculateMaxEnergy();
            return;
        }

        if (level.isClientSide) {
            spawnFractureParticles();
            return;
        }

        fractures.forEach(f -> {
            if (f.energy() < f.maxEnergy()) {
                f.setEnergy(f.energy() + 0.02);
                needsSync = true;
            }
        });

        if (needsSync && level.getGameTime() % 15 == 0)
            syncEnergy();
    }

    // i know OnlyIn is bad but i *really* didn't want to move this into another class
    @OnlyIn(Dist.CLIENT)
    public void spawnFractureParticles() {
        fractures.forEach(f -> {
            if(PlayerHelper.hasItemInHand(net.minecraft.client.Minecraft.getInstance().player, GCItemTags.WAND_ENERGY_GAUGE_OVERLAY))
                level.addParticle(GCParticles.FRACTURE.get(),
                        f.position().getX() + 0.5,
                        f.position().getY() + 0.5,
                        f.position().getZ() + 0.5,
                        0,
                        0,
                        0);
        });
    }

    @Override
    public List<Fracture> getFractures() {
        if (fractures == null)
            return Collections.emptyList();
        return Collections.unmodifiableList(fractures);
    }

    void syncEnergy() {
        needsSync = false;
        if (!level.isClientSide)
            GCNetwork.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunk(pos.x, pos.z)), new SyncChunkEnergyMessage(pos, fractures));
    }
}
