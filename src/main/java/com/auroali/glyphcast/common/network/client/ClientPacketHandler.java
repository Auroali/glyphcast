package com.auroali.glyphcast.common.network.client;

import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.capabilities.chunk.ChunkEnergy;
import com.auroali.glyphcast.common.registry.GCCapabilities;
import com.auroali.glyphcast.common.spells.Spell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ClientPacketHandler {
    public static void spawnParticles(SpawnParticlesMessage msg) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null)
            return;
        RandomSource rand = level.getRandom();
        for (int i = 0; i < msg.count; i++) {
            double spreadX = rand.nextGaussian() * msg.spread;
            double spreadY = rand.nextGaussian() * msg.spread;
            double spreadZ = rand.nextGaussian() * msg.spread;
            double speed = Math.max(msg.minSpeed, msg.maxSpeed * rand.nextFloat());
            Vec3 newDir = msg.direction.normalize().add(spreadX, spreadY, spreadZ).normalize().scale(speed);
            level.addParticle(msg.particle, msg.pos.x, msg.pos.y, msg.pos.z, newDir.x, newDir.y, newDir.z);
        }
    }

    public static void syncSpellUserData(CompoundTag tag) {
        SpellUser.get(Minecraft.getInstance().player).ifPresent(user -> user.deserializeNBT(tag));
    }

    public static void handleChunkEnergy(SyncChunkEnergyMessage msg) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || !level.hasChunk(msg.pos.x, msg.pos.z))
            return;

        level.getChunk(msg.pos.x, msg.pos.z).getCapability(GCCapabilities.CHUNK_ENERGY).ifPresent(e -> {
            if (e instanceof ChunkEnergy energy) {
                energy.values = msg.values;
                energy.maxValues = msg.maxValues;
                energy.fractures = msg.fractures;
            }
        });
    }

    public static void triggerSpellEvent(Byte id, Spell spell, Spell.IContext ctx) {
        if (ctx instanceof Spell.PositionedContext posCtx)
            spell.handleEvent(id, posCtx);
    }

    public static Entity fromId(int id) {
        if (Minecraft.getInstance().level == null)
            return null;
        return Minecraft.getInstance().level.getEntity(id);
    }
}
