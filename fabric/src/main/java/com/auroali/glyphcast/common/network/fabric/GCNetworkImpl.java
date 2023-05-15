package com.auroali.glyphcast.common.network.fabric;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;

public class GCNetworkImpl {
    public static Packet<?> spawnPacket(Entity entity) {
        return new ClientboundAddEntityPacket(entity);
    }
}
