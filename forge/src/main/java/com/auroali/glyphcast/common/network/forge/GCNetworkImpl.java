package com.auroali.glyphcast.common.network.forge;

import com.auroali.glyphcast.common.network.NetworkChannel;
import com.auroali.glyphcast.common.network.client.*;
import com.auroali.glyphcast.common.network.server.ClearSpellSlotMessage;
import com.auroali.glyphcast.common.network.server.SelectSpellSlotMessage;
import com.auroali.glyphcast.common.network.server.SetSlotSpellMessage;
import com.auroali.glyphcast.common.network.server.WriteParchmentMessage;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkHooks;

@SuppressWarnings("unused")
public class GCNetworkImpl {
    public static Packet<?> getEntitySpawnPacket(Entity entity) {
        return NetworkHooks.getEntitySpawningPacket(entity);
    }
}
