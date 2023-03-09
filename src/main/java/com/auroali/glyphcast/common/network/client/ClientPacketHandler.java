package com.auroali.glyphcast.common.network.client;

import com.auroali.glyphcast.common.capabilities.SpellUser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public class ClientPacketHandler {
    public static void spawnParticles(SpawnParticlesMessage msg) {
        ClientLevel level = Minecraft.getInstance().level;
        if(level == null)
            return;
        RandomSource rand = level.getRandom();
        for(int i = 0; i < msg.count; i++) {
            double spreadX = rand.nextGaussian() * msg.spread;
            double spreadY = rand.nextGaussian() * msg.spread;
            double spreadZ = rand.nextGaussian() * msg.spread;
            Vec3 newDir = msg.direction.normalize().add(spreadX, spreadY, spreadZ).normalize().scale(msg.maxSpeed * rand.nextFloat());
            level.addParticle(msg.particle, msg.pos.x, msg.pos.y, msg.pos.z, newDir.x, newDir.y, newDir.z);
        }
    }

    public static void syncSpellUserData(CompoundTag tag) {
        SpellUser.get(Minecraft.getInstance().player).ifPresent(user -> user.deserializeNBT(tag));
    }
}
