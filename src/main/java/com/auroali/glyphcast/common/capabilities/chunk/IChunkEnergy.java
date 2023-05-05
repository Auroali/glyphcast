package com.auroali.glyphcast.common.capabilities.chunk;

import com.auroali.glyphcast.common.energy.Fracture;
import com.auroali.glyphcast.common.registry.GCCapabilities;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;

public interface IChunkEnergy extends INBTSerializable<CompoundTag> {

    static List<Fracture> getNearbyFractures(Level level, BlockPos pos, int range) {
        ImmutableList.Builder<Fracture> builder = new ImmutableList.Builder<>();
        int x = SectionPos.blockToSectionCoord(pos.getX());
        int z = SectionPos.blockToSectionCoord(pos.getZ());
        for (int cX = -range; cX <= range; cX++) {
            for (int cZ = -range; cZ <= range; cZ++) {
                if (!level.hasChunk(cX + x, cZ + z))
                    continue;

                builder.addAll(level.getChunk(cX + x, cZ + z).getCapability(GCCapabilities.CHUNK_ENERGY)
                        .map(IChunkEnergy::getFractures)
                        .orElse(List.of()));
            }
        }
        return builder.build();
    }

    static double getAverageFractureEnergy(Level level, BlockPos pos) {
        List<Fracture> fractures = getNearbyFractures(level, pos, 1);
        double amount = 0;
        int num = 0;
        for(Fracture f : fractures) {
            double dist = f.position().distToLowCornerSqr(pos.getX(), pos.getY(), pos.getZ());
            if(dist > 15*15)
                continue;
            amount += (1 - (dist / 255.0)) * f.energy();
            num++;
        }
        amount /= num;

        return amount;
    }

    void tick();

    List<Fracture> getFractures();
}
