package com.auroali.glyphcast.common.network.client;

import com.auroali.glyphcast.common.network.NetworkMessage;
import com.auroali.glyphcast.common.registry.GCWandCaps;
import com.auroali.glyphcast.common.wands.WandCap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SyncWandCapsMessage extends NetworkMessage {
    final Map<ResourceLocation, WandCap> map;
    public SyncWandCapsMessage() {
        map = GCWandCaps.KEY_MAP;
    }
    @SuppressWarnings("deprecation")
    public SyncWandCapsMessage(FriendlyByteBuf buf) {
        map = new HashMap<>();
        while(buf.isReadable()) {
            map.put(buf.readResourceLocation(), buf.readWithCodec(WandCap.CODEC));
        }
    }
    @Override
    @SuppressWarnings("deprecation")
    public void encode(FriendlyByteBuf buf) {
        for(Map.Entry<ResourceLocation, WandCap> entry : map.entrySet()) {
            buf.writeResourceLocation(entry.getKey());
            buf.writeWithCodec(WandCap.CODEC, entry.getValue());
        }
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            GCWandCaps.KEY_MAP.clear();
            GCWandCaps.VALUE_MAP.clear();
            for(Map.Entry<ResourceLocation, WandCap> entry : map.entrySet()) {
                GCWandCaps.KEY_MAP.put(entry.getKey(), entry.getValue());
                GCWandCaps.VALUE_MAP.put(entry.getValue(), entry.getKey());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
