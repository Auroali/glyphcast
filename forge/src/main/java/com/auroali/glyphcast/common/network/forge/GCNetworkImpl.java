package com.auroali.glyphcast.common.network.forge;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkHooks;

@SuppressWarnings("unused")
public class GCNetworkImpl {
    public static Packet<?> getEntitySpawnPacket(Entity entity) {
        return NetworkHooks.getEntitySpawningPacket(entity);
    }

    public static Packet<?> getEntitySpawnPacket(Entity entity, int data) {
        // this is probably a bad idea
        return new ClientboundAddEntityPacket(entity, data);
    }
}
