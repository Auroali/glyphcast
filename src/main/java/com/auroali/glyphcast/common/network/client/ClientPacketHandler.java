package com.auroali.glyphcast.common.network.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public class ClientPacketHandler {
    public static void spawnParticles(SpawnParticlesMessage msg) {
        ClientLevel level = Minecraft.getInstance().level;
        if(level == null)
            return;
        RandomSource rand = level.getRandom();
        for(int i = 0; i < msg.count; i++) {
            Vec3 newDir = msg.direction.normalize().scale(msg.maxSpeed * rand.nextFloat());
            level.addParticle(ParticleTypes.SNOWFLAKE, msg.pos.x, msg.pos.y, msg.pos.z, newDir.x, newDir.y, newDir.z);
        }
    }
}
