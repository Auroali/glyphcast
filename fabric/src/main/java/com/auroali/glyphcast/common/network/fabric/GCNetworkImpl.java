package com.auroali.glyphcast.common.network.fabric;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;

public class GCNetworkImpl {
    public static Packet<?> getEntitySpawnPacket(Entity entity) {
        return new ClientboundAddEntityPacket(entity);
    }

    public static Packet<?> getEntitySpawnPacket(Entity entity, int data) {
        return new ClientboundAddEntityPacket(entity, data);
    }
}
