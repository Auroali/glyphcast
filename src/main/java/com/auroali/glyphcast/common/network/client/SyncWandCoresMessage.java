package com.auroali.glyphcast.common.network.client;

import com.auroali.glyphcast.common.network.NetworkMessage;
import com.auroali.glyphcast.common.registry.GCWandCores;
import com.auroali.glyphcast.common.wands.WandCore;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SyncWandCoresMessage extends NetworkMessage {
    Map<ResourceLocation, WandCore> coreMap;
    public SyncWandCoresMessage() {
        coreMap = GCWandCores.KEY_MAP;
    }
    @SuppressWarnings("deprecation")
    public SyncWandCoresMessage(FriendlyByteBuf buf) {
        coreMap = new HashMap<>();
        while(buf.isReadable()) {
            coreMap.put(buf.readResourceLocation(), buf.readWithCodec(WandCore.CODEC));
        }
    }
    @Override
    @SuppressWarnings("deprecation")
    public void encode(FriendlyByteBuf buf) {
        for(Map.Entry<ResourceLocation, WandCore> entry : coreMap.entrySet()) {
            buf.writeResourceLocation(entry.getKey());
            buf.writeWithCodec(WandCore.CODEC, entry.getValue());
        }
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            GCWandCores.KEY_MAP.clear();
            GCWandCores.VALUE_MAP.clear();
            for(Map.Entry<ResourceLocation, WandCore> entry : coreMap.entrySet()) {
                GCWandCores.KEY_MAP.put(entry.getKey(), entry.getValue());
                GCWandCores.VALUE_MAP.put(entry.getValue(), entry.getKey());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
